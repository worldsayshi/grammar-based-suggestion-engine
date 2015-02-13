/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.findwise.grammarsearch.web;

import java.util.Collections;
import java.util.Set;

/**
 *
 * @author marcin.goss
 */
public class SuggestionParams {

    private static final int DEFAULT_MAX_SUGGESTIONS = 15;
    private static final Set<String> DEFAULT_NO_REPETITION_TYPES = Collections.EMPTY_SET;
    private static final int DEFAULT_MAX_ADDITIONAL_NAMES = 0;
    private static final int DEFAULT_MAX_INTERPRETATIONS = 10;
    private static final String DEFAULT_HINT = " ";
    private static final boolean DEFAULT_CONTINUE = true;
    private static final boolean DEFAULT_ALTER = false;
    private static final int DEFAULT_ALTER_SIMILIARITY = 80;
    
    private Set<String> noRepetitionTypes = DEFAULT_NO_REPETITION_TYPES;
    private int maxSuggestions = DEFAULT_MAX_SUGGESTIONS;
    private int maxAdditionalSuggestedNames = DEFAULT_MAX_ADDITIONAL_NAMES;
    private int maxInterpretations = DEFAULT_MAX_INTERPRETATIONS;
    private String continueHint = DEFAULT_HINT;
    private boolean enableContinue = DEFAULT_CONTINUE;
    private boolean enableAlter = DEFAULT_ALTER;
    private int alterSimiliarity = DEFAULT_ALTER_SIMILIARITY;
    
    public SuggestionParams(){
        
    }

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
     * @return the continueHint
     */
    public String getContinueHint() {
        return continueHint;
    }

    /**
     * @param continueHint the continueHint to set
     */
    public void setContinueHint(String continueHint) {
        this.continueHint = continueHint;
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
