package com.findwise.grammarsearch.web;

import java.util.Collections;
import java.util.Set;

/**
 * Class representing parameters used by suggestion algorithm
 */
public class SuggestionParams {

    private static final int DEFAULT_MAX_SUGGESTIONS = 15;
    private static final Set<String> DEFAULT_NO_REPETITION_TYPES = Collections.EMPTY_SET;
    private static final int DEFAULT_MAX_ADDITIONAL_NAMES = 0;
    private static final int DEFAULT_MAX_INTERPRETATIONS = 10;
    private static final boolean DEFAULT_CONTINUE = true;
    private static final boolean DEFAULT_ALTER = false;
    private static final int DEFAULT_ALTER_SIMILIARITY = 80;
    
    //name types that should not have duplicates returned in suggestions
    private Set<String> noRepetitionTypes = DEFAULT_NO_REPETITION_TYPES;
    
    //maximum number of suggestions returned
    private int maxSuggestions = DEFAULT_MAX_SUGGESTIONS;
    
    //maximum number of additional name combinations given for each suggestion rule
    private int maxAdditionalSuggestedNames = DEFAULT_MAX_ADDITIONAL_NAMES;
    
    //maximum number of name interpretations resulting in initial query analysis
    private int maxInterpretations = DEFAULT_MAX_INTERPRETATIONS;
      
    //whether or not to enable continue behavior
    private boolean enableContinue = DEFAULT_CONTINUE;
    
    //whether or not to enable alter behavior
    private boolean enableAlter = DEFAULT_ALTER;
    
    //similiarity of suggestions when using alter behavior 
    private int alterSimiliarity = DEFAULT_ALTER_SIMILIARITY;
    
    /**
     * @return the noRepetitionTypes
     */
    public Set<String> getNoRepetitionTypes() {
        return noRepetitionTypes;
    }

    /**
     * @param noRepetitionTypes the noRepetitionTypes to set
     */
    public void setNoRepetitionTypes(Set<String> noRepetitionTypes) {
        this.noRepetitionTypes = noRepetitionTypes;
    }

    /**
     * @return the maxSuggestions
     */
    public int getMaxSuggestions() {
        return maxSuggestions;
    }

    /**
     * @param maxSuggestions the maxSuggestions to set
     */
    public void setMaxSuggestions(int maxSuggestions) {
        this.maxSuggestions = maxSuggestions;
    }

    /**
     * @return the maxAdditionalSuggestedNames
     */
    public int getMaxAdditionalSuggestedNames() {
        return maxAdditionalSuggestedNames;
    }

    /**
     * @param maxAdditionalSuggestedNames the maxAdditionalSuggestedNames to set
     */
    public void setMaxAdditionalSuggestedNames(int maxAdditionalSuggestedNames) {
        this.maxAdditionalSuggestedNames = maxAdditionalSuggestedNames;
    }


    /**
     * @return the enableContinue
     */
    public boolean isEnableContinue() {
        return enableContinue;
    }

    /**
     * @param enableContinue the enableContinue to set
     */
    public void setEnableContinue(boolean enableContinue) {
        this.enableContinue = enableContinue;
    }

    /**
     * @return the enableAlter
     */
    public boolean isEnableAlter() {
        return enableAlter;
    }

    /**
     * @param enableAlter the enableAlter to set
     */
    public void setEnableAlter(boolean enableAlter) {
        this.enableAlter = enableAlter;
    }

    /**
     * @return the alterSimiliarity
     */
    public int getAlterSimiliarity() {
        return alterSimiliarity;
    }

    /**
     * @param alterSimiliarity the alterSimiliarity to set
     */
    public void setAlterSimiliarity(int alterSimiliarity) {
        this.alterSimiliarity = alterSimiliarity;
    }

    /**
     * @return the maxInterpretations
     */
    public int getMaxInterpretations() {
        return maxInterpretations;
    }

    /**
     * @param maxInterpretations the maxInterpretations to set
     */
    public void setMaxInterpretations(int maxInterpretations) {
        this.maxInterpretations = maxInterpretations;
    }
}
