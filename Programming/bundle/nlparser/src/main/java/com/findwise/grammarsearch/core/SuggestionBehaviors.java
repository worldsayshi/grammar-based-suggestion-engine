package com.findwise.grammarsearch.core;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * Enum representing possible behaviors to be applied when giving suggestions.
 * Each behavior is represented by the set of rules that it uses.
 */
public enum SuggestionBehaviors {
    CorrectComplete(EnumSet.of(BehaviorDetails.AcceptOnlyAdditions), 0),
    Continue(EnumSet.of(BehaviorDetails.AllMustMatch, BehaviorDetails.UseSimiliarity, BehaviorDetails.AcceptOnlyAdditions),100),
    Alter(EnumSet.of(BehaviorDetails.UseSimiliarity, BehaviorDetails.TakeParamSimiliarity, BehaviorDetails.AcceptLostInfo),0),
    DoNotSuggest(EnumSet.of(BehaviorDetails.SkipSuggestions),0),
    ReturnOriginal(EnumSet.of(BehaviorDetails.AllMustMatch, BehaviorDetails.UseSimiliarity, BehaviorDetails.JustOneResult),100);
        
    SuggestionBehaviors(EnumSet<BehaviorDetails> details, int similiarity){
        this.details = details;
        this.similiarity = similiarity;
    }
    
    //detailed behavior actions/choices
    private final EnumSet<BehaviorDetails> details;

    //minimum match similiarity percentage to be used if not taking users parameter
    private final int similiarity;

    /**
     * @return the details
     */
    public EnumSet<BehaviorDetails> getDetails() {
        return details;
    }

    /**
     * @return the similiarity
     */
    public int getSimiliarity() {
        return similiarity;
    }
    
    public boolean contains(BehaviorDetails detail){
        return this.details.contains(detail);
    }
}
