package com.findwise.grammarsearch.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.agfjord.server.result.NameResult;

/**
 *  Class representing one possible interpretation of the original query
 */
public class Interpretation {

    //list of names and their types appearing in the query
    private List<NameResult> nameTypes;
    
    //name type appearances stats
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
