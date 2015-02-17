/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.findwise.grammarsearch.core;

/**
 *
 * @author marcin.goss
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
    
    private final boolean allMustMatch;
    private final boolean useSimiliarity;
    private final boolean takeParamSimiliarity;
    private final int similiarity;
    private final boolean skipSuggestions;
    private final boolean justOneResult;
    private final boolean acceptLostInfo;
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
