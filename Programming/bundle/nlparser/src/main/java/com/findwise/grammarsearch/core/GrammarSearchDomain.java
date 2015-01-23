package com.findwise.grammarsearch.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.agfjord.grammar.SolrGrammarSuggester;
import org.agfjord.grammar.SolrNameSuggester;
import org.agfjord.grammar.Templating;
import org.agfjord.server.result.NameResult;
import org.agfjord.server.result.TreeResult;

/**
 * Replacement for Parser
 * @author per.fredelius
 */
public class GrammarSearchDomain<T> {
    
    private final static Integer maxNumOfSuggestions = 10;
    private final static String placeholderPrefix = "{{";
    private final static String placeholderSuffix = "}}";
    private final static Integer nr_of_additional_suggestions = 5;
    
    // Move into GrammarSearchDomain
    private SolrGrammarSuggester grammarSuggester;
    private SolrNameSuggester nameSuggester;
    private GrammarSearchClient<T> grammarSearchClient;
    
    
    public GrammarSearchDomain(
            SolrNameSuggester nameSuggester,
            SolrGrammarSuggester grammarSuggester,
            GrammarSearchClient<T> grammarSearchClient) {
        
        this.grammarSearchClient = grammarSearchClient;
    }
    
    
    
    
    // Returning a list of interpretations
    // Each interpretation has a list of potential names found in the natural language question
    // Move into GrammarSearchDomain
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
    // Move into GrammarSearchDomain
    public List<String> suggestSentences(String nlQuestion)
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
    /*
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
    }*/
    
    
    // Move into GrammarSearchDomain
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

    public Object performQuery(String question) {
        return grammarSearchClient.performQuery(question);
    }
    
    // Move into GrammarSearchDomain
    class MissingCounts {

        public final Map<String, Integer> counts;
        public final Integer total;

        public MissingCounts(Map<String, Integer> counts, Integer total) {
            this.counts = counts;
            this.total = total;
        }
    }
    
    // Move into GrammarSearchDomain
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
    
    
    // Move into GrammarSearchDomain
    private String fillTemplate(String template, NameResult nameInQuestion) {
        String type = nameInQuestion.getType();
        return  defTempl.replaceFirst(template, type, nameInQuestion.getName());
    }
    // Move into GrammarSearchDomain
    private String fillTemplate(String template, List<NameResult> namesInQuestion) {
        for (NameResult nameInQuestion : namesInQuestion) {
            template = fillTemplate(template, nameInQuestion);
        }
        return template;
    }
}
