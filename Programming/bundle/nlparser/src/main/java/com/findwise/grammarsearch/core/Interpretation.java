/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.findwise.grammarsearch.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.agfjord.server.result.NameResult;

/**
 *
 * @author marcin.goss
 */
public class Interpretation {

    private List<NameResult> nameTypes;
    private NameTypeCounts nameTypeCounts;

    public Interpretation(List<NameResult> nameTypes) {
        this.nameTypes = nameTypes;
        calculateNameTypeCounts(nameTypes);
    }

    private void calculateNameTypeCounts(List<NameResult> nameTypes) {

        Map<String, Integer> countsMap = new HashMap<>();

        for (NameResult name : nameTypes) {
            if (countsMap.containsKey(name.getType())) {
                countsMap.put(name.getType(), countsMap.get(name.getType()).intValue() + 1);
            }
            else {
                countsMap.put(name.getType(), 1);
            }
        }

        nameTypeCounts = new NameTypeCounts(countsMap, nameTypes.size());
    }

    /**
     * @return the nameTypes
     */
    public List<NameResult> getNameTypes() {
        return nameTypes;
    }

    /**
     * @return the nameTypeCounts
     */
    public NameTypeCounts getNameTypeCounts() {
        return nameTypeCounts;
    }
}
