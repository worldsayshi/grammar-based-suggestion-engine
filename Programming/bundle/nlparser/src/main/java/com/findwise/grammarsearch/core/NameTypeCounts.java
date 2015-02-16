/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.findwise.grammarsearch.core;

import java.util.Map;

/**
 *
 * @author marcin.goss
 */
public class NameTypeCounts {

    public final Map<String, Integer> counts;
    public final Integer total;

    public NameTypeCounts(Map<String, Integer> counts, Integer total) {
        this.counts = counts;
        this.total = total;
    }
}
