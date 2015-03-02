/*
 */
package com.findwise.grammarsearch.core;

/**
 * Enum representing detailed actions that can be taken by suggesting behavior
 */
public enum BehaviorDetails {
    /*
     * do all words need to match when choosing from possible linearizations
     */
    AllMustMatch,
    
    /*
     * whether or not to use minimum match parameters when doing rules lookup
     */
    UseSimiliarity,
    
    /*
     * whether or not to take minimum match percentage parameter from rest call when doing rules lookup
     */
    TakeParamSimiliarity,
    
    /*
     * whether or not to skip giving suggestions at all
     */
    SkipSuggestions,
    
    /*
     * whether or not to only give one suggestion as a result
     */
    JustOneResult,
    
    /*
     * whether or not to accept suggestions that remove valid information on final filtering
     */
    AcceptLostInfo,
    
    /*
     * whether or not to accepr suggestions that only add information on final filtering
     */
    AcceptOnlyAdditions
}
