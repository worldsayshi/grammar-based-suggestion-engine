/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.findwise.grammarsearch.core;

/**
 * Enum representing possible behaviors to be applied when giving suggestions.
 * Each behavior is represented by the set of rules that it uses.
 */
public enum SuggestionBehaviors {
    CorrectComplete(false, false, false, 0, false, false,false, true),
    Continue(true, true, false, 100, false, false, false, true),
    Alter(false, true, true, 0, false, false, true, false),
    DoNotSuggest(false, false, false, 0, true, false, false, false),
    ReturnOriginal(true, true, false, 100, false, true, false, false);
        
    SuggestionBehaviors(boolean allMatch, boolean useSim, boolean takeParamSim, int sim, boolean skip, boolean justOne, boolean acceptLost, boolean acceptOnlyAdd){
        this.allMustMatch = allMatch;
        this.useSimiliarity = useSim;
        this.takeParamSimiliarity = takeParamSim;
        this.similiarity = sim;
        this.skipSuggestions = skip;
        this.justOneResult = justOne;
        this.acceptLostInfo = acceptLost;
        this.acceptOnlyAdditions = acceptOnlyAdd;
    }
    // do all words need to match when choosing from possible linearizations
    private final boolean allMustMatch;
    
    // whether or not to use minimum match parameters when doing rules lookup
    private final boolean useSimiliarity;
    
    //whether or not to take minimum match percentage parameter from rest call when doing rules lookup
    private final boolean takeParamSimiliarity;
    
    //minimum match similiarity percentage to be used if not taking users parameter
    private final int similiarity;
    
    //whether or not to skip giving suggestions at all
    private final boolean skipSuggestions;
    
    //whether or not to only give one suggestion as a result
    private final boolean justOneResult;
    
    //whether or not to accept suggestions that remove valid information on final filtering
    private final boolean acceptLostInfo;
    
    //whether or not to accepr suggestions that only add information on final filtering
    private final boolean acceptOnlyAdditions;

    /**
     * @return the allMustMatch
     */
    public boolean isAllMustMatch() {
        return allMustMatch;
    }

    /**
     * @return the useSimiliarity
     */
    public boolean isUseSimiliarity() {
        return useSimiliarity;
    }

    /**
     * @return the takeParamSimiliarity
     */
    public boolean isTakeParamSimiliarity() {
        return takeParamSimiliarity;
    }

    /**
     * @return the similiarity
     */
    public int getSimiliarity() {
        return similiarity;
    }

    /**
     * @return the skipSuggestions
     */
    public boolean isSkipSuggestions() {
        return skipSuggestions;
    }

    /**
     * @return the justOneResult
     */
    public boolean isJustOneResult() {
        return justOneResult;
    }

    /**
     * @return the acceptLostInfo
     */
    public boolean isAcceptLostInfo() {
        return acceptLostInfo;
    }

    /**
     * @return the acceptOnlyAdditions
     */
    public boolean isAcceptOnlyAdditions() {
        return acceptOnlyAdditions;
    }
}
