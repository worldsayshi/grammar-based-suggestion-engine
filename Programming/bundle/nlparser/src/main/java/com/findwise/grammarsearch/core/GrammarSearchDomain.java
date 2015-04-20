package com.findwise.grammarsearch.core;

import com.findwise.grammarsearch.core.SolrGrammarSuggester.GrammarLookupFailure;
import com.findwise.grammarsearch.util.Templating;
import com.findwise.grammarsearch.web.SuggestionParams;
import com.findwise.grammarsearch.web.SuggestionResult;
import java.util.Map.Entry;
import java.util.*;
import org.agfjord.server.result.NameResult;
import org.agfjord.server.result.TreeResult;
import org.grammaticalframework.pgf.*;

/**
 * Replacement for Parser
 *
 * @author M. Agfjord & per.fredelius
 */
public class GrammarSearchDomain<T> {

    private final static String placeholderPrefix = "{{";
    private final static String placeholderSuffix = "}}";
    private final static String CONTINUE_HINT = " ";
    private String absGrammarName;
    private SolrGrammarSuggester grammarSuggester;
    private SolrNameSuggester nameSuggester;
    private GrammarSearchClient<T> grammarSearchClient;
    private SuggestionComparator suggestionComparator = new SuggestionComparator();
    private List<String> userLanguages;
    private String description;


    public GrammarSearchDomain(
            String absGrammarName,
            SolrNameSuggester nameSuggester,
            SolrGrammarSuggester grammarSuggester,
            GrammarSearchClient<T> grammarSearchClient,
            List<String> userLanguages,
            String description) {
        this.absGrammarName = absGrammarName;
        this.nameSuggester = nameSuggester;
        this.grammarSuggester = grammarSuggester;
        this.grammarSearchClient = grammarSearchClient;
        this.userLanguages = userLanguages;
        this.description = description;
    }

    /**
     *
     * @param nlQuestion - Natural language question
     * @param concreteLang - Natural language used
     * @param params - Suggestion parameters
     * @return List of suggestion strings
     * @throws Exception
     * @throws
     * com.findwise.grammarsearch.core.SolrGrammarSuggester.GrammarLookupFailure
     * @throws
     * com.findwise.grammarsearch.core.SolrNameSuggester.NameLookupFailed
     */
    public List<SuggestionResult> suggestSentences(String nlQuestion, String concreteLang, String searchApiLang, SuggestionParams params)
            throws Exception, SolrGrammarSuggester.GrammarLookupFailure, SolrNameSuggester.NameLookupFailed {
        List<Suggestion> questions = new ArrayList<>();

        nlQuestion = nlQuestion.toLowerCase();
        Interpretations interpretations = interpretNamesOfNLQuestion(
                nlQuestion, params.getMaxInterpretations(), params);

        //find out whether the query is complete / partial / invalid and choose right suggestion behavior
        SuggestionBehaviors behavior = chooseBehavior(
                nlQuestion, interpretations, params, concreteLang);

        if (behavior.contains(BehaviorDetails.SkipSuggestions)) {
            return Collections.EMPTY_LIST;
        }

        for (Interpretation current : interpretations.getInterpretations()) {
            List<NameResult> namesInQuestion = current.getNameResults();

            String template = templateCandidate(nlQuestion, namesInQuestion);

            List<TreeResult> templateLinearizationDocs = grammarSuggester.suggestRules(
                    template, concreteLang, namesInQuestion,
                    behavior.contains(BehaviorDetails.UseSimiliarity),
                    behavior.contains(BehaviorDetails.TakeParamSimiliarity)
                    ? params.getAlterSimiliarity()
                    : behavior.getSimiliarity());

            for (TreeResult templateLinearizationDoc : templateLinearizationDocs) {

                NameTypeCounts missingCounts = countMissingName(
                        namesInQuestion, templateLinearizationDoc);
                
                Suggestion linearization = getBestLinearization(
                        templateLinearizationDoc.getLinearizations(),
                        current, interpretations.getWordTypes(),
                        templateLinearizationDoc.getApiTemplate(),
                        behavior.contains(BehaviorDetails.AllMustMatch));

                if (linearization == null) {
                    continue;
                }

                if (missingCounts.total < 1) {
                    List<Suggestion> suggestions = createSuggestionsForLinearization(
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
                        List<NameResult> forbiddenNames = new ArrayList<>(namesInQuestion);

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

                    List<Suggestion> suggestions = createSuggestionsForLinearization(
                            namesInQuestion,
                            linearization,
                            additionalNames);

                    questions.addAll(suggestions);
                }

            }
        }

        filterSuggestions(questions, behavior);
        Collections.sort(questions, suggestionComparator);

        return suggestionsToResultSuggestionsList(questions.subList(0, behavior.contains(BehaviorDetails.JustOneResult) ? 1 : Math.min(questions.size(), params.getMaxSuggestions())));
    }

    /*
     * Filtering out suggestions that are not allowed by the behavior
     */
    private void filterSuggestions(List<Suggestion> suggestions, SuggestionBehaviors behavior) {
        Iterator<Suggestion> iterator = suggestions.iterator();

        while (iterator.hasNext()) {
            Suggestion suggestion = iterator.next();

            //remove those losing valid information if not allowed
            if (!behavior.contains(BehaviorDetails.AcceptLostInfo) && suggestion.getAlteredGrammarWordsCount() > 0) {
                iterator.remove();
            }

            //remove those that only add information if not allowed
            if (!behavior.contains(BehaviorDetails.AcceptOnlyAdditions)
                    && suggestion.getAlteredGrammarWordsCount() == 0
                    && suggestion.getAdditionalNamesCount() + suggestion.getAdditionalGrammarWords() > 0) {
                iterator.remove();
            }
        }
    }

    /*
     * Converts suggestions list to their string representations
     */
    private List<SuggestionResult> suggestionsToResultSuggestionsList(List<Suggestion> suggestions) {
        List<SuggestionResult> result = new ArrayList<>();

        for (Suggestion suggestion : suggestions) {
            result.add(new SuggestionResult(suggestion.getText(), suggestion.getSearchApiLinearization()));
        }

        return result;
    }
    


    /*
     * Analysing the query and parameters and choosing the right behavior
     */
    private SuggestionBehaviors chooseBehavior(String nlQuestion,
            Interpretations interpretations,
            SuggestionParams params,
            String concreteLang) throws GrammarLookupFailure, ParseError {

        boolean continuePossible = params.isEnableContinue() && nlQuestion.endsWith(CONTINUE_HINT);
        boolean alterPossible = params.isEnableAlter() && !nlQuestion.endsWith(CONTINUE_HINT);

        SuggestionBehaviors result = SuggestionBehaviors.DoNotSuggest;

        for (Interpretation interpretation : interpretations.getInterpretations()) {
            List<NameResult> nameTypes = interpretation.getNameResults();
            String template = templateCandidate(nlQuestion, nameTypes);
            List<TreeResult> suggestRules = grammarSuggester.suggestRules(
                    template, concreteLang, nameTypes, 1, false, 0);

            if (suggestRules.isEmpty()) {
                continue;
            }

            Suggestion bestSuggestion = getBestLinearization(suggestRules.get(0).getLinearizations(),
                    interpretation, interpretations.getWordTypes(), suggestRules.get(0).getApiTemplate(), false);

            // the query is complete: give alter or continue suggestions if enabled
            if (template.trim().equalsIgnoreCase(bestSuggestion.getText().trim())
                    && interpretations.isAllNamesComplete()) {
                if (continuePossible) {
                    result = SuggestionBehaviors.Continue;
                }
                else if (alterPossible) {
                    result = SuggestionBehaviors.Alter;
                }
                else {
                    if (result != SuggestionBehaviors.Continue && result != SuggestionBehaviors.Alter) {
                        result = SuggestionBehaviors.ReturnOriginal;
                    }
                }
            }
            //the query is invalid or partial: give correct/complete suggestions
            else if (result == SuggestionBehaviors.DoNotSuggest) {
                result = SuggestionBehaviors.CorrectComplete;
            }
        }

        return result;
    }

    /**
     * @return the userLanguages
     */
    public List<String> getUserLanguages() {
        return userLanguages;
    }

    /**
     * @param userLanguages the userLanguages to set
     */
    public void setUserLanguages(List<String> userLanguages) {
        this.userLanguages = userLanguages;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /*
     * Comparator used when applying final sorting to suggestion list
     */
    private class SuggestionComparator implements Comparator<Suggestion> {

        @Override
        public int compare(Suggestion sugg1, Suggestion sugg2) {

            //less altered word first
            int alteredGrammarWordsDifference = sugg1.getAlteredGrammarWordsCount()
                    - sugg2.getAlteredGrammarWordsCount();

            if (alteredGrammarWordsDifference != 0) {
                return alteredGrammarWordsDifference;
            }

            //if tied: less added info first
            int additionalNamesDifference = sugg1.getAdditionalNamesCount() + sugg1.getAdditionalGrammarWords()
                    - (sugg2.getAdditionalNamesCount() + sugg2.getAdditionalGrammarWords());

            if (additionalNamesDifference != 0) {
                return additionalNamesDifference;
            }

            //if tied: less words first
            int wordCountDifference = sugg1.getWordsCount() - sugg2.getWordsCount();

            if (wordCountDifference == 0) {
                return wordCountDifference;
            }

            //if tied: shortest text first
            return sugg1.getText().length() - sugg2.getText().length();
        }
    }

    /*
     * Chooses the best matching linearization from the ones available for a
     * single rule. Returns null if none of the linearization is valid.
     */
    private Suggestion getBestLinearization(List<String> linearizations,
            Interpretation interpretation,
            Map<String, WordType> originalWords, String searchApiLinearization, boolean matchAllWords) throws ParseError {

        Suggestion best = null;

        for (String current : linearizations) {
            Suggestion suggestion = buildSuggestion(
                    current, interpretation,
                    originalWords, searchApiLinearization, matchAllWords);

            if (suggestion != null) {
                if (best == null) {
                    best = suggestion;
                }
                else {
                    if (suggestionComparator.compare(best, suggestion) > 0) {
                        best = suggestion;
                    }
                }
            }
        }

        return best;
    }

    /*
     * Builds a Suggestion object from linearization string collecting info
     * about words added / altered etc
     */
    private Suggestion buildSuggestion(String linearization,
            Interpretation interpretation,
            Map<String, WordType> originalWords, String searchApiLinearization, boolean matchAllWords) throws ParseError {
        String[] words = linearization.split("\\s+");

        Map<String, Integer> namesMissing = new HashMap(
                interpretation.getNameTypeCounts().counts);

        int additionalNamesCount = 0;

        Set<String> wordsNotMatched = new HashSet<>(originalWords.keySet());
        for (Entry<String, WordType> entry : originalWords.entrySet()) {
            if (entry.getValue() == WordType.Name) {
                wordsNotMatched.remove(entry.getKey());
            }
        }
        int additionalGrammarWordsCount = 0;


        for (String word : words) {

            //name word
            if (defTempl.isVariable(word)) {
                String nameType = word.substring(2, word.length() - 2);
                if (namesMissing.containsKey(nameType)) {
                    Integer missingCount = namesMissing.get(nameType);
                    if (missingCount > 1) {
                        namesMissing.put(nameType, missingCount - 1);
                    }
                    else {
                        namesMissing.remove(nameType);
                    }
                }
                else {
                    additionalNamesCount++;
                }
            }
            //grammar word
            else {
                String lowerCaseWord = word.toLowerCase();

                if (wordsNotMatched.contains(lowerCaseWord)) {
                    wordsNotMatched.remove(lowerCaseWord);
                }
                else {
                    additionalGrammarWordsCount++;
                }
            }
        }

        //if removing words from query is not allowed
        if (matchAllWords && !wordsNotMatched.isEmpty()) {
            return null;
        }

        int grammarWordsAltered = wordsNotMatched.size();
        int grammarWordsAdded = additionalGrammarWordsCount - grammarWordsAltered;

        return new Suggestion(linearization, false,
                additionalNamesCount, grammarWordsAdded, grammarWordsAltered, searchApiLinearization);
    }

    /*
     * Produces final suggestion from linearization by replacing name
     * placeholders with names provided in namesInQuestion and additionalNames
     * parameters
     */
    private List<Suggestion> createSuggestionsForLinearization(
            List<NameResult> namesInQuestion,
            Suggestion input,
            List<List<NameResult>> additionalNames) {

        List<Suggestion> suggestions = new ArrayList<>();

        String linearization = fillTemplate(input.getText(), namesInQuestion);
        String searchApiLinearization = fillTemplate(input.getSearchApiLinearization(), namesInQuestion);

        int nrUnknowns = defTempl.listVariables(linearization).size();
        if (nrUnknowns > additionalNames.size()) {
            throw new InternalError("No suggestions for unknown template variables.");
        }

        if (nrUnknowns == 0) {
            return Arrays.asList(new Suggestion(
                    linearization,
                    true, input.getAdditionalNamesCount(),
                    input.getAdditionalGrammarWords(),
                    input.getAlteredGrammarWordsCount(), searchApiLinearization));
        }

        while (true) {
            String currentSuggestion = linearization;
            String currentSearchApiLinearization = searchApiLinearization;
            boolean allNamesFilled = true;

            for (List<NameResult> currentNames : additionalNames) {

                if (currentNames.size() <= suggestions.size()) {
                    allNamesFilled = false;
                    break;
                }

                currentSuggestion = fillTemplate(currentSuggestion,
                        currentNames.get(suggestions.size()));
                currentSearchApiLinearization = fillTemplate(currentSearchApiLinearization, currentNames.get(suggestions.size()));
            }

            if (allNamesFilled) {
                suggestions.add(new Suggestion(
                        currentSuggestion, true,
                        input.getAdditionalNamesCount(),
                        input.getAdditionalGrammarWords(),
                        input.getAlteredGrammarWordsCount(), currentSearchApiLinearization));
            }
            else {
                break;
            }
        }

        return suggestions;
    }
    private static final Templating defTempl = new Templating(
            placeholderPrefix, placeholderSuffix);

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

    /*
     * Returning a list of interpretations
     * Each template has a list of potential names found in the natural language question
     */
    private Interpretations interpretNamesOfNLQuestion(
            String nlQuestion, int maxNumOfInterpretations, SuggestionParams params)
            throws SolrGrammarSuggester.GrammarLookupFailure, SolrNameSuggester.NameLookupFailed {

        Map<String, WordType> wordTypes = new HashMap<>();
        List<Interpretation> namesInterpretations = new ArrayList<>();

        String[] words = nlQuestion.split("\\s+");

        boolean anyKnownName = false;

        // Find all names with their corresponding types in the question.
        // Add type and names to the hashmap names.
        for (String word : words) {

            //grammar word
            boolean isGrammarWord = grammarSuggester.checkIfGrammarWord(word);
            if (isGrammarWord) {
                wordTypes.put(word, WordType.Grammar);
                continue;
            }

            //check if the word can be interpreted as name
            List<NameResult> nameResults = nameSuggester.suggestNameResolution(word);

            //unknown word
            if (nameResults.isEmpty()) {
                wordTypes.put(word, WordType.Unknown);
                continue;
            }
            else { //name
                wordTypes.put(word, WordType.Name);
                anyKnownName = true;

                namesInterpretations = updateInterpretations(namesInterpretations,
                        nameResults, word, maxNumOfInterpretations);
            }
        }

        avoidNameDuplicates(namesInterpretations, params);

        //there was no known names -> return one "empty" template
        if (!anyKnownName) {
            namesInterpretations.add(new Interpretation(new ArrayList<NameResult>()));
        }

        return new Interpretations(namesInterpretations, wordTypes);
    }

    /*
     * Updates list of interpretations by adding new information contained in
     * nameResults
     */
    private List<Interpretation> updateInterpretations(List<Interpretation> namesInterpretations, 
            List<NameResult> nameResults, String word, int maxNumOfInterpretations) {

        //nothing added before - just create one interpretation for each nameResult
        if (namesInterpretations.isEmpty()) {
            List<Interpretation> newInterpretations = new ArrayList<>();
            for (NameResult nameResult : nameResults) {
                nameResult.setOriginalName(word);
                newInterpretations.add(new Interpretation(
                        new ArrayList<>(Arrays.asList(nameResult))));
            }
            namesInterpretations = newInterpretations;
        }
        //merge current name results with previous interpretations
        else {
            List<Interpretation> newInterpretations = new ArrayList<>();
            for (Interpretation interpretation : namesInterpretations) {
                if (newInterpretations.size() >= maxNumOfInterpretations) {
                    break;
                }
                for (int i = 0; i < nameResults.size(); i++) {
                    NameResult altName = nameResults.get(i);
                    altName.setOriginalName(word);
                    ArrayList<NameResult> newInterpretation = new ArrayList<>(
                            interpretation.getNameResults());
                    newInterpretation.add(altName);
                    newInterpretations.add(new Interpretation(newInterpretation));
                }
            }
            namesInterpretations = newInterpretations;
        }

        return namesInterpretations;
    }

    /*
     * Checks if generated name interpretations produced any unneed duplications
     * and removes them if possible
     */
    private void avoidNameDuplicates(List<Interpretation> namesInterpretations, SuggestionParams params) {

        //if there is no nametypes that should avoid duplicating or there is at most one interpretation skip this
        if (params.getNoRepetitionTypes().isEmpty() || namesInterpretations.size() <= 1) {
            return;
        }

        List<Interpretation> toBeRemoved = new ArrayList<>();

        for (Interpretation current : namesInterpretations) {

            //prepare occurences sets
            Map<String, Set<String>> nameTypeOccurences = new HashMap<>();
            for (String nameType : current.getNameTypeCounts().counts.keySet()) {
                if (params.getNoRepetitionTypes().contains(nameType.toLowerCase())) {
                    nameTypeOccurences.put(nameType.toLowerCase(), new HashSet<String>());
                }
            }

            //collect occurences watching for duplicates
            for (NameResult name : current.getNameResults()) {
                if (params.getNoRepetitionTypes().contains(name.getType().toLowerCase())) {

                    if (!nameTypeOccurences.get(name.getType().toLowerCase()).add(name.getName())) {
                        //duplicate detected, removing this interpretation
                        toBeRemoved.add(current);
                    }
                }
            }
        }

        //all interpetations identified as containing duplicates
        //impossible to avoid duplicates
        //fallback to not removing anything
        if (toBeRemoved.size() == namesInterpretations.size()) {
            return;
        }

        for (Interpretation current : toBeRemoved) {
            namesInterpretations.remove(current);
        }
    }

    public List<String> getLanguages() {
        return grammarSearchClient.getLanguages();
    }
}
