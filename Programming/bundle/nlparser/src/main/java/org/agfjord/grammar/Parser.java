package org.agfjord.grammar;

import com.findwise.crescent.model.StopLocation;
import com.findwise.crescent.model.TripList;
import com.findwise.crescent.rest.VasttrafikRestClient;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.agfjord.domain.AbstractSyntaxTree;
import org.agfjord.domain.Query;
import org.agfjord.server.result.NameResult;
import org.agfjord.server.result.TreeResult;
import org.apache.log4j.Logger;
import org.grammaticalframework.pgf.Concr;
import org.grammaticalframework.pgf.Expr;
import org.grammaticalframework.pgf.ExprProb;
import org.grammaticalframework.pgf.NercLiteralCallback;
import org.grammaticalframework.pgf.PGF;
import org.grammaticalframework.pgf.PGFError;
import org.grammaticalframework.pgf.ParseError;


/*
 Authors: Martin Agfjord, Per Fredelius
 */
public class Parser {

    private final static Integer maxNumOfSuggestions = 10;
    private final static String placeholderPrefix = "{{";
    private final static String placeholderSuffix = "}}";

    private PGF gr;
    final String solr_url;
    
    VasttrafikRestClient vasttrafikclient = new VasttrafikRestClient();

    private SolrGrammarSuggester grammarSuggester;
    private SolrNameSuggester nameSuggester;
	//final private Properties prop = new Properties();

    Integer nr_of_additional_suggestions = 5;

    static Logger log = Logger.getLogger(
            Parser.class.getName());

    public Parser() {
        solr_url = System.getProperty("solr.base.url");
        if (null == solr_url) {
            throw new IllegalStateException("Could not initialize parser: solr.base.url variable was not set!");
        }

        nameSuggester = new SolrNameSuggester(solr_url);
        grammarSuggester = new SolrGrammarSuggester(solr_url);

        try {
            URL url = this.getClass().getClassLoader().getResource("Vasttrafik.pgf");
            gr = PGF.readPGF(url.openStream());
        } catch (PGFError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //gr.getLanguages().get("VasttrafikSolr").addLiteral("Symb", new NercLiteralCallback());
        gr.getLanguages().get("VasttrafikEngConcat").addLiteral("Symb", new NercLiteralCallback());
        //gr.getLanguages().get("InstrucsEngRGL").addLiteral("Symb", new NercLiteralCallback());
        //gr.getLanguages().get("InstrucsEngConcat").addLiteral("Symb", new NercLiteralCallback());
        //gr.getLanguages().get("InstrucsSweRGL").addLiteral("Symb", new NercLiteralCallback());
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
        Map<String, Set<Query>> astQuery = new HashMap<String, Set<Query>>();
        exprProbs = gr.getLanguages().get(parseLang).parse(gr.getStartCat(), question);
        for (String key : gr.getLanguages().keySet()) {
            Concr lang = gr.getLanguages().get(key);
            for (ExprProb exprProb : exprProbs) {
                Set<Query> qs = astQuery.get(exprProb.getExpr().toString());
                if (qs == null) {
                    qs = new TreeSet<Query>(comparator);
                    astQuery.put(exprProb.getExpr().toString(), qs);
                }
                qs.add(new Query(lang.linearize(exprProb.getExpr()), lang.getName()));
            }
        }
        List<AbstractSyntaxTree> asts = new ArrayList<AbstractSyntaxTree>();
        for (String key : astQuery.keySet()) {
            asts.add(new AbstractSyntaxTree(key, astQuery.get(key)));
        }
        return asts;
    }
    
    List<Expr> parseToExpression(String question, String parseLang) throws ParseError {
        Iterable<ExprProb> exprProbs;
        List<Expr> expressions = new ArrayList<>();
        exprProbs = gr.getLanguages()
                .get(parseLang)
                .parse(
                        gr.getStartCat(), question);
        for (ExprProb exprProb : exprProbs) {
            Expr expr = exprProb.getExpr();
            expressions.add(expr);
        }
        return expressions;
    }
    
    public TripList performVasttrafikQuery (String question) throws ParseError {
        List<Expr> exprs = parseToExpression(question,"VasttrafikEngConcat");
        if(exprs.isEmpty()){
            throw new Error("TODO");
        }
        Expr expr = exprs.get(0);
        Concr apiLang = gr.getLanguages().get("VasttrafikApi");
        String apiLinearization = apiLang.linearize(expr);
        VasttrafikQuery q = parseVasttrafikApi(apiLinearization);
        if(q.from==null || q.to==null){
            // Not complete query - no route
            TripList tripList = new TripList();
            tripList.setTrip(new ArrayList());
            return tripList;
        }
        StopLocation from = vasttrafikclient.getBestMatchStop(q.from);
        StopLocation to = vasttrafikclient.getBestMatchStop(q.to);
        return vasttrafikclient.findConnections(from, to, null);
    }
    
    private VasttrafikQuery parseVasttrafikApi (String apiLinearization) {
        String[] rows = apiLinearization.split(";");
        Map<String,String> map = new HashMap<>();
        for (String row : rows) {
            String[] cols = row.split(":");
            if(cols.length==2){
                map.put(cols[0].trim(), cols[1].trim());
            }
        }
        return new VasttrafikQuery(map.get("from"),map.get("to"));
    }
    
    public class VasttrafikQuery {
        public final String from;
        public final String to;
        public VasttrafikQuery (String from, String to) {
            this.from=from;
            this.to=to;
        }
        
    }

    Comparator<Query> comparator = new Comparator<Query>() {
        public int compare(Query a, Query b) {
            return a.getLanguage().compareTo(b.getLanguage());
        }
    };

    // Returning a list of interpretations
    // Each interpretation has a list of potential names found in the natural language question
    public List<List<NameResult>> interpretNamesOfNLQuestion(String nlQuestion, int maxNumOfInterpretations)
            throws SolrGrammarSuggester.GrammarLookupFailure, SolrNameSuggester.NameLookupFailed {
        List<List<NameResult>> interpretations = new ArrayList<>();

        String[] words = nlQuestion.split("\\s+");

		// Find all names with their corresponding types in the question.
        // Add type and names to the hashmap names.
        for (String word : words) {

            // Check if current word exists in a generated query
            // We don't do namelookups for words that exist statically in the
            // grammar. 
            // E.g. word = 'people' or 'who' or 'and' ...
            // This seems like an unnecessary limitation?
            boolean isGrammarWord = grammarSuggester.checkIfGrammarWord(word);

            if (isGrammarWord) {
                continue;
            }

            //List<NameResult> nameResults = rsp.getBeans(NameResult.class);
            List<NameResult> nameResults = nameSuggester.suggestNameResolution(word);
            // Assume a word can only be represented by one name in the index
            // i.e. we only care about best match (the first)
            if (nameResults.isEmpty()) {
                continue;
            } /*else if (nameResults.size()==1) {
             NameResult nameResult = nameResults.get(0);

             nameResult.setOriginalName(word);
             // Change the word into its type + index
             // E.g. 'Java' ==> 'Skill0'
             interpretation.add(nameResult);
             }*/ else {
                if (interpretations.isEmpty()) {
                    List<List<NameResult>> new_interpretations = new ArrayList<>();
                    for (NameResult nameResult : nameResults) {
                        nameResult.setOriginalName(word);
                        new_interpretations.add(new ArrayList<>(Arrays.asList(nameResult)));
                    }
                    interpretations = new_interpretations;
                } else {
                    List<List<NameResult>> new_interpretations = new ArrayList<>();
                    for (List<NameResult> interpretation : interpretations) {
                        if(new_interpretations.size()>=maxNumOfInterpretations){
                            break;
                        }
                        for (int i = 0; i < nameResults.size(); i++) {
                            NameResult altName = nameResults.get(i);
                            altName.setOriginalName(word);
                            ArrayList<NameResult> new_interpretation = new ArrayList<>(interpretation);
                            new_interpretation.add(altName);
                            new_interpretations.add(new_interpretation);
                        }
                    }
                    interpretations = new_interpretations;
                }
            }
        }
        return interpretations;
    }

    

    /**
     * Complete a string into a valid question. Psuedocode of the algorithm: 1.
     * Determine the words of the string which are names 2. Replace those names
     * with their type in the index, e.g. Java ==> Skill0 3. Ask the index of
     * questions similar to the string 4. Change the types back into their names
     * (not the original if misspelled) 5. If the question consists of more
     * names than what is inputed, ask index of more names and add them to the
     * question 6. Return all questions
     *
     * @param nlQuestion - A question in a natural language
     * @param parseLang - A natural language
     * @return Valid questions
     * @throws org.agfjord.grammar.SolrGrammarSuggester.GrammarLookupFailure
     * @throws org.agfjord.grammar.SolrNameSuggester.NameLookupFailed
     */
    public List<String> completeSentenceBreadthFirst(String nlQuestion, String parseLang)
            throws SolrGrammarSuggester.GrammarLookupFailure, SolrNameSuggester.NameLookupFailed {
        //List<String> questions = new ArrayList<>();

        List<List<NameResult>> interpretations = interpretNamesOfNLQuestion(nlQuestion,maxNumOfSuggestions);
        
        // If the names are not unambiguously resolved, 
        // give suggestions based on the name interpretations
        /*if(interpretations.size()>1){
            for (List<NameResult> namesInQuestion : interpretations) {
                // Produce a template using the names we have found
                String templateCandidate = templateCandidate(nlQuestion, namesInQuestion);
                String interpretation = fillTemplate(templateCandidate,namesInQuestion);
                
                //String interpretation = replaceNames(nlQuestion, namesInQuestion, "types");
                //interpretation = replaceNames(interpretation, namesInQuestion, "names");
                questions.add(interpretation);
            }
        } else if (interpretations.isEmpty()) {
            return new ArrayList<>();
        }*/
        
        // Form suggestions based on the first interpretation
        //List<NameResult> namesInQuestion = interpretations.get(0);
        int suggestionCount = 0;
        List<Iterator<String>> suggestionsByInterpretations = new ArrayList<>();
        
        for (List<NameResult> namesInQuestion : interpretations) {
            
        
            String interpretation = templateCandidate(nlQuestion, namesInQuestion);

            List<TreeResult> templateLinearizationDocs = grammarSuggester.suggestRules(interpretation, parseLang, namesInQuestion);

            for (TreeResult templateLinearizationDoc : templateLinearizationDocs) {

                MissingCounts missingCounts = countMissingName(
                        namesInQuestion, templateLinearizationDoc);
                // We only care about one of the linearizations when it comes to
                // suggesting
                String linearization = templateLinearizationDoc.getLinearizations().get(0);
                if (missingCounts.total > 1) {
                    continue;
                } else if (missingCounts.total < 1) {
                    List<String> suggestions = createSuggestionsForLinearization(
                            namesInQuestion,
                            linearization,
                            new ArrayList<NameResult>());
                    if (suggestions.size() > 1) {
                        throw new InternalError(
                                "Multiple suggestions for single template without unknowns");
                    }
                    //questions.addAll(suggestions);
                    suggestionCount += suggestions.size();
                    suggestionsByInterpretations.add(suggestions.iterator());
                } else {
                    String missingNameType = missingCounts.counts.keySet().iterator().next();
                    List<NameResult> additionalNames = nameSuggester.suggestNames(
                            missingNameType, namesInQuestion, nr_of_additional_suggestions + 1);

                    List<String> suggestions
                            = createSuggestionsForLinearization(
                                    namesInQuestion,
                                    linearization,
                                    additionalNames);
                    
                    //questions.addAll(suggestions);
                    suggestionCount += suggestions.size();
                    suggestionsByInterpretations.add(suggestions.iterator());
                }
                /*if(questions.size()>=maxNumOfSuggestions){
                    break suggestionLoop;
                }*/
            }
        }
        List<String> finalSuggestions = new ArrayList<>();
        int i = 0;
        while(finalSuggestions.size()<=maxNumOfSuggestions){
            boolean depletedIterators = true;
            for (Iterator<String> suggestions : suggestionsByInterpretations) {
                if(suggestions.hasNext()){
                    depletedIterators = false;
                    String suggestion = suggestions.next();
                    finalSuggestions.add(suggestion);
                }
            }
            if(depletedIterators){
                break;
            }
        }
        return finalSuggestions;
    }
    
    private static <T> List<List<T>> transpose(List<List<T>> table) {
        List<List<T>> ret = new ArrayList<>();
        final int N = table.get(0).size();
        for (int i = 0; i < N; i++) {
            List<T> col = new ArrayList<>();
            for (List<T> row : table) {
                col.add(row.get(i));
            }
            ret.add(col);
        }
        return ret;
    }
    
    private static <T> List<T> flatten(List<List<T>> list) {
        List<T> flatList = new ArrayList<>();
        for (List<T> l : list) {
            flatList.addAll(l);
        }
        return flatList;
    }

    private List<String> createSuggestionsForLinearization(
            List<NameResult> namesInQuestion,
            String linearization,
            List<NameResult> additionalNames) {

        List<String> suggestions = new ArrayList<>();
        
        linearization = fillTemplate(linearization, namesInQuestion);
        
        int nrUnknowns = defTempl.listVariables(linearization).size();
        if(nrUnknowns>additionalNames.size()){
            throw new InternalError("No suggestions for unknown template variables.");
        }
        if(nrUnknowns==0){
            return Arrays.asList(linearization);
        }
        
        for (NameResult additionalName : additionalNames) {
            String type = additionalName.getType();
            String suggestionForAdditionalName = linearization;
            //suggestionForAdditionalName = defTempl.replaceFirst(suggestionForAdditionalName, type, additionalName.getName());
            //suggestions.add(suggestionForAdditionalName);
            suggestions.add(fillTemplate(suggestionForAdditionalName, additionalName));
        }

        return suggestions;
    }
    private static final Templating defTempl = new Templating(placeholderPrefix, placeholderSuffix);

    private String templateCandidate(String nlQuestion, List<NameResult> namesInQuestion) {
        for (NameResult nameResult : namesInQuestion) {
            String toReplace, replacement;
            toReplace = nameResult.getOriginalName();
            replacement = placeholderPrefix+nameResult.getType()+placeholderSuffix;
            nlQuestion = nlQuestion.replace(toReplace, replacement);
        }
        return nlQuestion;
    }
    /*
    // TODO Use Templating class
    public String replaceNames(String nlQuestion, List<NameResult> names, String replaceWith) {
        for (NameResult nameResult : names) {
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
    */

    private String fillTemplate(String template, NameResult nameInQuestion) {
        String type = nameInQuestion.getType();
        return  defTempl.replaceFirst(template, type, nameInQuestion.getName());
    }
    private String fillTemplate(String template, List<NameResult> namesInQuestion) {
        for (NameResult nameInQuestion : namesInQuestion) {
            template = fillTemplate(template, nameInQuestion);
        }
        return template;
    }

    class MissingCounts {

        public final Map<String, Integer> counts;
        public final Integer total;

        public MissingCounts(Map<String, Integer> counts, Integer total) {
            this.counts = counts;
            this.total = total;
        }
    }

    private MissingCounts countMissingName(
            List<NameResult> namesInQuestion,
            TreeResult templateLinearizationDoc) {
        // We only care about one of the linearizations when it comes to
        // suggesting
        String linearization = templateLinearizationDoc.getLinearizations().get(0);

        for (NameResult nameRes : namesInQuestion) {
            Templating templ = new Templating(placeholderPrefix,
                    placeholderSuffix, nameRes.getType(), nameRes.getName());
            linearization = templ.replaceFirst(linearization);
        }
        List<String> typeNames = new Templating(placeholderPrefix,
                placeholderSuffix).listVariables(linearization);
        Map<String, Integer> counts = new HashMap<>();
        int total = 0;
        for (String typeName : typeNames) {
            if (counts.containsKey(typeName)) {
                total++;
                counts.put(typeName, counts.get(typeName) + 1);
            } else {
                total++;
                counts.put(typeName, 1);
            }
        }
        return new MissingCounts(counts, total);
    }
}
