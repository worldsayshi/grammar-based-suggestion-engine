package org.agfjord.grammar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.lang.IllegalStateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import org.agfjord.domain.AbstractSyntaxTree;
import org.agfjord.domain.Query;
import org.agfjord.server.result.NameResult;
import org.agfjord.server.result.Question;
import org.agfjord.server.result.TreeResult;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.grammaticalframework.pgf.Concr;
import org.grammaticalframework.pgf.ExprProb;
import org.grammaticalframework.pgf.NercLiteralCallback;
import org.grammaticalframework.pgf.PGF;
import org.grammaticalframework.pgf.PGFError;
import org.grammaticalframework.pgf.ParseError;

/*
 Authors: Martin Agfjord, Per Fredelius
*/
public class Parser {

    private final static String placeholderPrefix = "{{";
    private final static String placeholderSuffix = "}}";
    
	private PGF gr;
    final String solr_url; 
	
    private SolrGrammarSuggester grammarSuggester;
    private SolrNameSuggester nameSuggester;
	//final private Properties prop = new Properties();
    
    // Maximum nr of variants on the Abs trees
    Integer max_nr_of_trees = 10;
    Integer nr_of_additional_suggestions = 5;
    
	static Logger log = Logger.getLogger(
			Parser.class.getName());

	public Parser() {
        solr_url = System.getProperty("solr.base.url");
        if(null==solr_url){
            throw new IllegalStateException("Could not initialize parser: solr.base.url variable was not set!");
        }
        
        nameSuggester = new SolrNameSuggester(solr_url);
        grammarSuggester = new SolrGrammarSuggester(solr_url);
        
		try {
			URL url = this.getClass().getClassLoader().getResource("Instrucs.pgf");
			gr = PGF.readPGF(url.openStream());
		}
		catch (PGFError e) { 
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*try {
			URL url = this.getClass().getClassLoader().getResource("config.properties");
			prop.load(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		gr.getLanguages().get("InstrucsEngRGL").addLiteral("Symb", new NercLiteralCallback());
		gr.getLanguages().get("InstrucsEngConcat").addLiteral("Symb", new NercLiteralCallback());
		gr.getLanguages().get("InstrucsSweRGL").addLiteral("Symb", new NercLiteralCallback());
	}

	public String closestQuestion(String nlQuestion){
		String[] words = nlQuestion.split("\\s+");
		int cut = -1;
		for(int i=words.length-1; i > 0; i--){
			if(words[i].equals("and") || words[i].equals("or")){
				cut = i;
				break;
			}
		}
		StringBuilder closestQuestion = new StringBuilder();
		for(int i=0; i < cut; i++){
			closestQuestion.append(words[i] + " ");
		}
		closestQuestion.deleteCharAt(closestQuestion.length()-1);
		return nlQuestion;
	}
	
	/*
	 * Parse a string @question in a language @parseLang. Retrieve all distinct abstract syntax trees
	 * and their distinct linearizations. I think there exists a bug in GF which causes a NLP-sentence
	 * to be parsed into multiple equal abstract syntax trees, the same with linearizations. drbean @ #gf @ freenode
	 * told me this bug existed.
	 * 
	 */
	public List<AbstractSyntaxTree> parse(String question, String parseLang) throws ParseError {
		Iterable<ExprProb> exprProbs;
		// Map is used to only have distinct asts, Set is used to only have distinct linearizations
		Map<String,Set<Query>> astQuery = new HashMap<String,Set<Query>>();
		exprProbs = gr.getLanguages().get(parseLang).parse(gr.getStartCat(), question);
		for(String key : gr.getLanguages().keySet()){
			Concr lang = gr.getLanguages().get(key);
			for(ExprProb exprProb : exprProbs) {
				Set<Query> qs = astQuery.get(exprProb.getExpr().toString());
				if(qs == null){
					qs = new TreeSet<Query>(comparator);
					astQuery.put(exprProb.getExpr().toString(), qs);
				}
				qs.add(new Query(lang.linearize(exprProb.getExpr()), lang.getName()));	
			}
		}
		List<AbstractSyntaxTree> asts = new ArrayList<AbstractSyntaxTree>();
		for(String key : astQuery.keySet()){
			asts.add(new AbstractSyntaxTree(key, astQuery.get(key)));
		}
		return asts;
	}
	
	Comparator<Query> comparator = new Comparator<Query>(){
        public int compare(Query a, Query b){
            return a.getLanguage().compareTo(b.getLanguage());
        }
    };

	
	
	public List<NameResult> parseQuestionIntoNameResults(String nlQuestion) 
            throws SolrGrammarSuggester.GrammarLookupFailure, SolrNameSuggester.NameLookupFailed {
		List<NameResult> names = new ArrayList<>();
		
		String[] words = nlQuestion.split("\\s+");

		// Find all names with their corresponding types in the question.
		// Add type and names to the hashmap names.
		for(String word : words){
            
            boolean isGrammarWord = grammarSuggester.checkIfGrammarWord(word);
			
			// Check if current word exists in a generated query
			// E.g. word = 'people' or 'who' or 'and' ...
			if(!isGrammarWord){
                
				//List<NameResult> nameResults = rsp.getBeans(NameResult.class);
                List<NameResult> nameResults = nameSuggester.suggestNameResolution(word);
				// Assume a word can only be represented by one name in the index
				// i.e. we only care about best match (the first)
				if(!nameResults.isEmpty()){
					NameResult nameResult = nameResults.get(0);
					nameResult.setOriginalName(word);
					// Change the word into its type + index
					// E.g. 'Java' ==> 'Skill0'
					names.add(nameResult);
				}
			}
		}
		return names;
	}
	
    public String replaceNames(String nlQuestion, List<NameResult> names, String replaceWith) {
        for ( NameResult nameResult:names) {
            String toReplace, replacement;
            if (replaceWith.equals("types")) { // We replace name with type
                toReplace = nameResult.getOriginalName();
                replacement = nameResult.getType();
            } else { // We replace type with name
                toReplace = nameResult.getType();
                replacement = nameResult.getName();
            }
            nlQuestion = nlQuestion.replace(toReplace, replacement);
        }
        return nlQuestion;
    }
    
    /**
	 * Complete a string into a valid question. Psuedocode of the algorithm:
	 * 1. Determine the words of the string which are names
	 * 2. Replace those names with their type in the index, e.g. Java ==> Skill0
	 * 3. Ask the index of questions similar to the string
	 * 4. Change the types back into their names (not the original if misspelled)
	 * 5. If the question consists of more names than what is inputed, ask index
	 *    of more names and add them to the question
	 * 6. Return all questions
	 * 
	 * @param nlQuestion - A question in a natural language
	 * @param parseLang - A natural language
	 * @return Valid questions
	 * @throws SolrServerException
	 */
    public List<String> completeSentenceBreadthFirst(String nlQuestion, String parseLang) 
            throws SolrGrammarSuggester.GrammarLookupFailure, SolrNameSuggester.NameLookupFailed{
        List<String> questions = new ArrayList<String>();
        
        
        
        List<NameResult> namesInQuestion = parseQuestionIntoNameResults(nlQuestion);
		nlQuestion = replaceNames(nlQuestion, namesInQuestion, "types");
        
        //
        List<TreeResult> templateLinearizationDocs = grammarSuggester.suggestRules(nlQuestion,parseLang,namesInQuestion);
        
        for(TreeResult templateLinearizationDoc : templateLinearizationDocs) {
            
            
            MissingCounts missingCounts = countMissingName(
                    namesInQuestion,templateLinearizationDoc);
            if(missingCounts.total>1){
                continue;
            } else if (missingCounts.total<1) {
                // Maybe just add the suggestion?
                List<String> suggestions = createSuggestionsForLinearization2(
                        namesInQuestion,
                        templateLinearizationDoc,
                        new ArrayList<NameResult>());
                if(suggestions.size()>1){
                    throw new InternalError("Error: This should not happen!");
                }
                questions.addAll(suggestions);
                continue;
            }
            String missingNameType = missingCounts.counts.keySet().iterator().next();
            List<NameResult> additionalNames = nameSuggester.suggestNames(
                    missingNameType,namesInQuestion,nr_of_additional_suggestions+1);
            
            // Now do something with missing counts
            
            // Find names that were not found in the current question
            /*Map<String, List<NameResult>> namesNotInQuestion
                    = findNamesNotInQuestion2(namesInQuestion,
                            templateLinearizationDoc);*/
            
            // For each linearization template, create suggestions
            // using various combination of names
            List<String> suggestions
                    = createSuggestionsForLinearization2(
                    namesInQuestion,
                    templateLinearizationDoc,
                    additionalNames);
            
            questions.addAll(suggestions);
        }
        
        return questions;
    }

    private List<String> createSuggestionsForLinearization2(
            List<NameResult> namesInQuestion, 
            TreeResult templateLinearizationDoc, 
            List<NameResult> additionalNames) {
        
        
        List<String> suggestions = new ArrayList<>();
        // We only care about one of the linearizations when it comes to
        // suggesting
        String suggestion = templateLinearizationDoc.getLinearizations().get(0);
        Templating templ = new Templating(placeholderPrefix, placeholderSuffix);
        for (NameResult nameInQuestion : namesInQuestion) {
            String type = nameInQuestion.getType();
            suggestion = templ.replaceFirst(suggestion, type, nameInQuestion.getName());
        }
        for (NameResult additionalName : additionalNames) {
            String type = additionalName.getType();
            String suggestionForAdditionalName = suggestion;
            suggestionForAdditionalName = templ.replaceFirst(suggestionForAdditionalName, type, additionalName.getName());
            suggestions.add(suggestionForAdditionalName);
        }
        
        return suggestions;
    }

    /*public Map<String, List<NameResult>> findNamesNotInQuestion(
            Map<String, List<NameResult>> namesInQuestion, 
            TreeResult templateLinearizationDoc,
            Map<String, String> queryForNamesNotInQuestion) {
        
        SolrQuery namesQuery = new SolrQuery();
        namesQuery.addSort("score", ORDER.desc);
        namesQuery.addSort("length", ORDER.asc);
        
        Map<String,List<NameResult>> namesNotInQuestion = new HashMap<>();
        for(String nameType : namesInQuestion.keySet()){
            
            int nameCountT = templateLinearizationDoc
                    .getNameCounts()
                    .get(nameType+"_i");
            String queryForNamesNotInQuestionT = queryForNamesNotInQuestion.get(nameType);
            List<NameResult> nameResultsT = namesInQuestion.get(nameType);
            
            SolrQuery namesQueryT = namesQuery.getCopy();
            int numberOfPlaceholdersOfType = nameCountT - nameResultsT.size();
            int numberOfWantedSuggestions = numberOfPlaceholdersOfType>0?
                    numberOfPlaceholdersOfType + nr_of_additional_suggestions : 0;
            namesQueryT.setRows(numberOfWantedSuggestions);
            namesQueryT.setQuery("*:*");
            namesQueryT.setFilterQueries(queryForNamesNotInQuestionT);
            
            // TODO We may be able to run queries before this method instead??
            QueryResponse namesRespT = namesServer.query(namesQueryT);
            
            // We rotate the list of names *not in the question* until we have wrapped
            // around. We should stop whenever we have a certain amount of suggestions.
            
            // Names in the current type that are not found in the question
            List<NameResult> namesNotInQuestionT
                    = namesRespT.getBeans(NameResult.class);
            namesNotInQuestion.put(nameType,namesNotInQuestionT);
        }
        return namesNotInQuestion;
    }*/

    class MissingCounts {
        public final Map<String, Integer> counts;
        public final Integer total;
        public MissingCounts (Map<String, Integer> counts,Integer total) {
            this.counts=counts;
            this.total=total;
        }
    }
    private MissingCounts countMissingName(
            List<NameResult> namesInQuestion, 
            TreeResult templateLinearizationDoc) {
        // We only care about one of the linearizations when it comes to
        // suggesting
        String linearization = templateLinearizationDoc.getLinearizations().get(0);
        
        
        for(NameResult nameRes:namesInQuestion){
            Templating templ = new Templating(placeholderPrefix, 
                    placeholderSuffix, nameRes.getType(), nameRes.getName());
            linearization = templ.replaceFirst(linearization);
        }
        List<String> typeNames = new Templating(placeholderPrefix, 
                placeholderSuffix).listVariables(linearization);
        Map<String, Integer> counts = new HashMap<>();
        int total = 0;
        for (String typeName : typeNames) {
            if(counts.containsKey(typeName)){
                total++;
                counts.put(typeName,counts.get(typeName)+1);
            } else {
                total++;
                counts.put(typeName,1);
            }
        }
        return new MissingCounts(counts, total);
    }
    
    public List<Question> createSuggestionsForLinearization(
            Map<String, List<NameResult>> namesInQuestion, 
            TreeResult templateLinearizationDoc,
             Map<String, List<NameResult>> namesNotInQuestion
            // Map<String, String> queryForNamesNotInQuestion
        ) {
        // We only care about one of the linearizations when it comes to
        // suggesting
        String linearization = templateLinearizationDoc.getLinearizations().get(0);
        List<Question> suggestions = new ArrayList<>();
        /*SolrQuery namesQuery = new SolrQuery();
        namesQuery.addSort("score", ORDER.desc);
        namesQuery.addSort("length", ORDER.asc);*/
        
        // Put the names that we think were in the original sentence
        // into the new linerization
        for(String nameType : namesInQuestion.keySet()){
            List<NameResult> namesInQuestionT = namesInQuestion.get(nameType);
            
            for (int i = 0; i < namesInQuestionT.size(); i++) {
                NameResult nameInQuestionT = namesInQuestionT.get(i);
                linearization = linearization.replace(nameInQuestionT.getType()+i, nameInQuestionT.getName());
            }
            
        }
        
        // How many placeholders for each type remain?
        Map<String,Integer> placeholderCount = getNumberOfFreePlaceholders(
                templateLinearizationDoc,
                namesInQuestion);
        int totalPlaceHolderCount = 0;
        for(Integer placeholderCount_:placeholderCount.values()){
            totalPlaceHolderCount+=placeholderCount_;
        }
        if(totalPlaceHolderCount>1){
            // We only want suggestions with one new name suggestion
            return new ArrayList();
        } else if (totalPlaceHolderCount==0){
            Question question = new Question();
            question.setLinearizations(new ArrayList<>(Arrays.asList(linearization)));
            suggestions.add(question);
            return suggestions;
        }
        for(String nameType : namesNotInQuestion.keySet()){
            List<NameResult> namesNotInQuestionT = namesNotInQuestion.get(nameType);
            if(namesNotInQuestionT.size()>0){
                for(NameResult nameR : namesNotInQuestionT){
                    String name = nameR.getName();
                    //String name = namesNotInQuestionT.get(0).getName();
                    String linearizationFilled = linearization.replaceFirst(nameType+"\\d{1}", name);
                    Question question = new Question();
                    question.setLinearizations(new ArrayList<>(Arrays.asList(linearizationFilled)));
                    suggestions.add(question);
                }
            }
        }
        return suggestions;
    }

    public Map<String, String> createQueryForExcludingNames(Map<String, List<NameResult>> namesInQuestion) {
        // For each name already used, create a query excluding it
        // and sort the queries so that we have them separated by type
        Map<String,String> queryForNamesNotInQuestion = new HashMap<>();
        for(String nameType : namesInQuestion.keySet()){
            String queryForNamesNotInQuestionT = "type:" + nameType;
            List<NameResult> namesInQuestionT = namesInQuestion.get(nameType);
            for (int i = 0; i < namesInQuestionT.size(); i++) {
                NameResult nameInQuestionT = namesInQuestionT.get(i);
                queryForNamesNotInQuestionT += " -name:" + nameInQuestionT.getName();
            }
            queryForNamesNotInQuestion.put(nameType, queryForNamesNotInQuestionT);
        }
        return queryForNamesNotInQuestion;
    }

	public List<String> replaceAll(List<String> list, CharSequence target, CharSequence replacement){
		for(int i=0; i < list.size(); i++){
			list.set(i, list.get(i).replace(target, replacement));
		}
		return list;
	}


    /**
     * Shift the elements of a list once to the left and append the 
     * shifted element to the end.
     */
    private <T> List<T> shiftOnce(List<T> listToShift) {
        if(listToShift.isEmpty()){return listToShift;}
        // LinkedList should be better for this particular use case but since 
        // we also copy the list I'm not sure...
        List<T> shiftedList = new LinkedList<>(listToShift);
        T shiftedElem = shiftedList.remove(0);
        shiftedList.add(shiftedElem);
        return shiftedList;
    }

    private Map<String, Integer> getNumberOfFreePlaceholders(
            TreeResult templateLinearizationDoc,
            Map<String, List<NameResult>> namesInQuestion) {
        HashMap<String, Integer> placeHolderCount = new HashMap<String, Integer>();
        for(String nameType : namesInQuestion.keySet()){
            placeHolderCount.put(nameType, 
                    templateLinearizationDoc.getNameCounts().get(nameType+"_i")
                        - namesInQuestion.get(nameType).size());
        }
        return placeHolderCount;
    }

    private Map<String, List<NameResult>> cloneNamesMap(Map<String, List<NameResult>> namesInQuestion) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private int numberOfPermutations(Map<String, Integer> placeholderCount) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    

}
