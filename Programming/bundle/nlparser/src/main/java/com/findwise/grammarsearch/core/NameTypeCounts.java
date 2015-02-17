/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.findwise.grammarsearch.core;

import java.util.Map;

/**
 * Class representing a summary of counts of different name types appearing in the query
 */
public class NameTypeCounts {

    //map from name type to how many times it appeared
    public final Map<String, Integer> counts;
    
    //total number of name appearances
    public final Integer total;

    public NameTypeCounts(Map<String, Integer> counts, Integer total) {
        this.counts = counts;
        this.total = total;
    }
}
