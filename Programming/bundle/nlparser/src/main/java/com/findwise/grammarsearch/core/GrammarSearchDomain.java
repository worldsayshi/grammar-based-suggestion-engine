package com.findwise.grammarsearch.core;

import com.findwise.grammarsearch.core.SolrGrammarSuggester.GrammarLookupFailure;
import com.findwise.grammarsearch.web.SuggestionParams;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.findwise.grammarsearch.util.Templating;
import java.util.*;
import java.util.Map.Entry;
import org.agfjord.server.result.NameResult;
import org.agfjord.server.result.TreeResult;
import org.grammaticalframework.pgf.ParseError;

/**
 * Replacement for Parser
 *
 * @author M. Agfjord & per.fredelius
 */
public class GrammarSearchDomain<T> {

    private final static String placeholderPrefix = "{{";
    private final static String placeholderSuffix = "}}";
    private String absGrammarName;
    private SolrGrammarSuggester grammarSuggester;
    private SolrNameSuggester nameSuggester;
    private GrammarSearchClient<T> grammarSearchClient;
    private StringWordCountComparator stringComparator = new StringWordCountComparator();

    public GrammarSearchDomain(
            String absGrammarName,
            SolrNameSuggester nameSuggester,
            SolrGrammarSuggester grammarSuggester,
            GrammarSearchClient<T> grammarSearchClient) {
        this.absGrammarName = absGrammarName;
        this.nameSuggester = nameSuggester;
        this.grammarSuggester = grammarSuggester;
        this.grammarSearchClient = grammarSearchClient;
    }

    // Returning a list of interpretations
    // Each template has a list of potential names found in the natural language question
    public Interpretations interpretNamesOfNLQuestion(String nlQuestion, int maxNumOfInterpretations)
            throws SolrGrammarSuggester.GrammarLookupFailure, SolrNameSuggester.NameLookupFailed {

        Map<String, WordType> wordTypes = new HashMap<>();
        List<Interpretation> namesInterpretations = new ArrayList<>();

        String[] words = nlQuestion.split("\\s+");

        boolean anyKnownName = false;

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
                wordTypes.put(word, WordType.Grammar);
                continue;
            }

            //List<NameResult> nameResults = rsp.getBeans(NameResult.class);
            List<NameResult> nameResults = nameSuggester.suggestNameResolution(word);
            // Assume a word can only be represented by one name in the index
            // i.e. we only care about best match (the first)
            if (nameResults.isEmpty()) {
                wordTypes.put(word, WordType.Unknown);
                continue;
            } /*
             * else if (nameResults.size()==1) { NameResult nameResult =
             * nameResults.get(0);
             *
             * nameResult.setOriginalName(word); // Change the word into its
             * type + index // E.g. 'Java' ==> 'Skill0'
             * template.add(nameResult); }
             */ else {

                wordTypes.put(word, WordType.Name);
                anyKnownName = true;

                if (namesInterpretations.isEmpty()) {
                    List<Interpretation> new_interpretations = new ArrayList<>();
                    for (NameResult nameResult : nameResults) {
                        nameResult.setOriginalName(word);
                        new_interpretations.add(new Interpretation(new ArrayList<>(Arrays.asList(nameResult))));
                    }
                    namesInterpretations = new_interpretations;
                }
                else {
                    List<Interpretation> new_interpretations = new ArrayList<>();
                    for (Interpretation interpretation : namesInterpretations) {
                        if (new_interpretations.size() >= maxNumOfInterpretations) {
                            break;
                        }
                        for (int i = 0; i < nameResults.size(); i++) {
                            NameResult altName = nameResults.get(i);
                            altName.setOriginalName(word);
                            ArrayList<NameResult> new_interpretation = new ArrayList<>(interpretation.getNameTypes());
                            new_interpretation.add(altName);
                            new_interpretations.add(new Interpretation(new_interpretation));
                        }
                    }
                    namesInterpretations = new_interpretations;
                }
            }
        }

        //there was no known names -> return one "empty" template
        if (!anyKnownName) {
            namesInterpretations.add(new Interpretation(new ArrayList<NameResult>()));
        }

        return new Interpretations(namesInterpretations, wordTypes);
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
    public List<String> suggestSentences(String nlQuestion, String concreteLang, SuggestionParams params)
            throws Exception, SolrGrammarSuggester.GrammarLookupFailure, SolrNameSuggester.NameLookupFailed {
        List<String> questions = new LinkedList<>();

        nlQuestion = nlQuestion.toLowerCase();

        boolean continu = params.isEnableContinue() && nlQuestion.endsWith(params.getContinueHint());
        boolean giveAlters = params.isEnableAlter() && !nlQuestion.endsWith(params.getContinueHint());


        Interpretations interpretations = interpretNamesOfNLQuestion(nlQuestion, params.getMaxInterpretations());

        // If the names are not unambiguously resolved, 
        // give suggestions based on the name interpretations
        /*
         * if(interpretations.size()>1){ for (List<NameResult> namesInQuestion :
         * interpretations) { // Produce a template using the names we have
         * found String templateCandidate = templateCandidate(nlQuestion,
         * namesInQuestion); String template =
         * fillTemplate(templateCandidate,namesInQuestion);
         *
         * //String template = replaceNames(nlQuestion, namesInQuestion,
         * "types"); //interpretation = replaceNames(template, namesInQuestion,
         * "names"); questions.add(template); } } else if
         * (interpretations.isEmpty()) { return new ArrayList<>(); }
         */

        // Form suggestions based on the first template
        //List<NameResult> namesInQuestion = interpretations.get(0);
        //List<Iterator<String>> suggestionsByInterpretations = new ArrayList<>();



        for (Interpretation current : interpretations.getInterpretations()) {
            List<NameResult> namesInQuestion = current.getNameTypes();

            String template = templateCandidate(nlQuestion, namesInQuestion);

            //SuggestionBehaviors currentBehavior = chooseBehavior(nlQuestion, template, concreteLang, namesInQuestion, params);

            List<TreeResult> templateLinearizationDocs = grammarSuggester.suggestRules(template, concreteLang, namesInQuestion);

            for (TreeResult templateLinearizationDoc : templateLinearizationDocs) {

                NameTypeCounts missingCounts = countMissingName(
                        namesInQuestion, templateLinearizationDoc);
                // We only care about one of the linearizations when it comes to
                // suggesting

                //MARCIN TODO: WYBRAC ODPOWIEDNIE LINEARIZATION (napisac komentarz co i jak)
                //MARCIN TODO: tak sugerowac names, zeby sie nie powtarzaly (ew parametr) (jest parametr, napisac komentarze)
                //MARCIN TODO: tak przebudowac, zeby najpierw byly te ktore dodaja najmniej info
                //MARCIN TODO: napisz ze getBest moze zwrocic null (jezeli wszystkie traca informacje)
                //MARCIN TODO: konfigurowalne number of additional suggestions
                //MARCIN TODO: sortowanie wszystkiego od najkrotszych
                //MARCIN TODO: trol trol case

                String linearization = getBestLinearization(templateLinearizationDoc.getLinearizations(), current, interpretations.getWordTypes());

                if (linearization == null) {
                    continue;
                }

                if (missingCounts.total < 1) {
                    List<String> suggestions = createSuggestionsForLinearization(
                            namesInQuestion,
                            linearization,
                            Collections.EMPTY_LIST);
                    if (suggestions.size() > 1) {
                        throw new InternalError(
                                "Multiple suggestions for single template without unknowns");
                    }
                    questions.addAll(suggestions);
                }
                else {
                    Iterator<String> missingCountsIterator = missingCounts.counts.keySet().iterator();

                    List<List<NameResult>> additionalNames = new ArrayList<>();

                    while (missingCountsIterator.hasNext()) {

                        String missingNameType = missingCountsIterator.next();

                        List<NameResult> forbiddenNames = new LinkedList<>(namesInQuestion);

                        for (int i = 0; i < missingCounts.counts.get(missingNameType); i++) {

                            List<NameResult> currentNames = nameSuggester.suggestNames(
                                    absGrammarName,
                                    missingNameType, forbiddenNames, params.getMaxAdditionalSuggestedNames() + 1);

                            additionalNames.add(currentNames);

                            if (params.getNoRepetitionTypes().contains(missingNameType.toLowerCase())) {
                                forbiddenNames.addAll(currentNames);
                            }
                        }
                    }

                    List<String> suggestions = createSuggestionsForLinearization(
                            namesInQuestion,
                            linearization,
                            additionalNames);

                    questions.addAll(suggestions);
                }

            }
        }

        Collections.sort(questions, stringComparator);

        return questions.subList(0, Math.min(questions.size(), params.getMaxSuggestions()));
    }

//    private SuggestionBehaviors chooseBehavior(String nlQuestion, String template, String concreteLang, List<NameResult> namesInQuestion, SuggestionParams params) throws GrammarLookupFailure {
//        List<TreeResult> rules = grammarSuggester.suggestRules(template, concreteLang, namesInQuestion, 1);
//        
//        if(rules.isEmpty() ){
//            return SuggestionBehaviors.DoNotSuggest;
//        }
//        
//        if(getBestLinearization(template, rules.get(0).getLinearizations()))
//        tutaj dalej jazda
//        
//    }
    private class StringWordCountComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            int diff = o1.split("\\s+").length - o2.split("\\s+").length;

            if (diff == 0) {
                diff = o1.length() - o2.length();
            }

            return diff;
        }
    }

    
    private String getBestLinearization(List<String> linearizations, Interpretation interpretation, Map<String, WordType> originalWords){
        
        Suggestion best = null;
        
        for(String current : linearizations){
            Suggestion suggestion = buildSuggestion(current, interpretation, originalWords);
            
            if(suggestion != null && 
                    (best == null || 
                    best.getAlteredGammarWordsCount() > suggestion.getAlteredGammarWordsCount() ||
                    best.getAdditionalNamesCount() + best.getAddtionalGrammarWords() > suggestion.getAdditionalNamesCount() + suggestion.getAddtionalGrammarWords())){
                best = suggestion;
            }
        }
        
        if(best == null){
            return null;
        }
        
        return best.getText();
    }
    
//    private String getBestLinearization(String interpretation, List<String> linearizations) throws Exception {
//        List<String> words = Arrays.asList(interpretation.toLowerCase().split("\\s+"));
//
//        String best = null;
//        int bestLength = Integer.MAX_VALUE;
//
//        for (String current : linearizations) {
//            
//            
//            
//            if (Arrays.asList(current.toLowerCase().split("\\s+")).containsAll(words) && bestLength > current.length()) {
//                best = current;
//                bestLength = current.length();
//            }
//        }
//
//        return best;
//    }

    private Suggestion buildSuggestion(String linearization, Interpretation interpretation, Map<String, WordType> originalWords) {
        String[] words = linearization.split("\\s+");


        Map<String, Integer> namesMissing = new HashMap(interpretation.getNameTypeCounts().counts);
        
        int addNamesCount = 0;

        Set<String> wordsNotMatched = new HashSet<>(originalWords.keySet());
        for (Entry<String, WordType> entry : originalWords.entrySet()) {
            if (entry.getValue() == WordType.Name) {
                wordsNotMatched.remove(entry.getKey());
            }
        }
        int addGrammarWordsCount = 0;


        for (String word : words) {

            //name word
            if (word.startsWith(placeholderPrefix) && word.endsWith(placeholderSuffix)) {
                String type = word.substring(2, word.length() - 2);
                if (namesMissing.containsKey(type)) {
                    Integer count = namesMissing.get(type);
                    if (count > 1) {
                        namesMissing.put(type, count - 1);
                    }
                    else{
                        namesMissing.remove(type);
                    }
                }
                else{
                    addNamesCount ++ ;
                }
            }
            //grammar word
            else {
                String lowWord = word.toLowerCase();
                
                if(wordsNotMatched.contains(lowWord)){
                    wordsNotMatched.remove(lowWord);
                }
                else{
                    addGrammarWordsCount ++;
                }
            }
        }
        
        //MARCIN TODO: tylko w okreslonych trybach
        if(!wordsNotMatched.isEmpty()){
            return null;
        }
        
        int grammarWordsAltered = wordsNotMatched.size();
        int grammarWordsAdded = addGrammarWordsCount - grammarWordsAltered;
        
        if(grammarWordsAdded < 0){
            //losing valid information when applying this linearization
            return null;
        }
        
        for(Entry<String,Integer> entry : namesMissing.entrySet()){
            if(entry.getValue() !=0){
                //losing valid information when applying this linearization
                return null;
            }
        }
              
        return new Suggestion(linearization, false, addNamesCount, grammarWordsAdded, grammarWordsAltered);
    }

//    private Suggestion getSuggestionTemplate(String currentInterpretation, TreeResult templateDoc, Map<String,WordType> originalWords){
//        List<Suggestion> suggestionsFromLinearizations = new LinkedList<>();
//        
//        for(String linearization : templateDoc.getLinearizations()){
//            
//            int grammarCount = 0;
//            int nameCount = 0;
//            
//            List<String> currentWords = Arrays.asList(linearization.toLowerCase().split("\\s+"));
//            
//            for(String)
//        }
//        
//        return new Suggestion(absGrammarName, false, addNames, addGrammar, alterNames, alterGrammar)
//    }
    private List<String> createSuggestionsForLinearization(
            List<NameResult> namesInQuestion,
            String linearization,
            List<List<NameResult>> additionalNames) {

        List<String> suggestions = new ArrayList<>();

        linearization = fillTemplate(linearization, namesInQuestion);

        int nrUnknowns = defTempl.listVariables(linearization).size();
        if (nrUnknowns > additionalNames.size()) {
            throw new InternalError("No suggestions for unknown template variables.");
        }
        if (nrUnknowns == 0) {
            return Arrays.asList(linearization);
        }

        int index = 0;
        while (true) {
            String currentSuggestion = linearization;
            boolean allNamesFilled = true;

            for (List<NameResult> currentNames : additionalNames) {

                if (currentNames.size() <= index) {
                    allNamesFilled = false;
                    break;
                }

                currentSuggestion = fillTemplate(currentSuggestion, currentNames.get(index));
            }

            if (allNamesFilled) {
                suggestions.add(currentSuggestion);
                index++;
            }
            else {
                break;
            }
        }

        return suggestions;
    }
    private static final Templating defTempl = new Templating(placeholderPrefix, placeholderSuffix);

    private String templateCandidate(String nlQuestion, List<NameResult> namesInQuestion) {
        for (NameResult nameResult : namesInQuestion) {
            String toReplace, replacement;
            toReplace = nameResult.getOriginalName();
            replacement = placeholderPrefix + nameResult.getType() + placeholderSuffix;
            nlQuestion = nlQuestion.replace(toReplace, replacement);
        }
        return nlQuestion;
    }

    public Object performQuery(String question, String lang) throws ParseError {
        return grammarSearchClient.performQuery(question, lang);
    }

    private NameTypeCounts countMissingName(
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
            }
            else {
                total++;
                counts.put(typeName, 1);
            }
        }
        return new NameTypeCounts(counts, total);
    }

    private String fillTemplate(String template, NameResult nameInQuestion) {
        String type = nameInQuestion.getType();
        return defTempl.replaceFirst(template, type, nameInQuestion.getName());
    }

    private String fillTemplate(String template, List<NameResult> namesInQuestion) {
        for (NameResult nameInQuestion : namesInQuestion) {
            template = fillTemplate(template, nameInQuestion);
        }
        return template;
    }

    public List<String> getLanguages() {
        return grammarSearchClient.getLanguages();
    }
}
