/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.findwise.grammarsearch.core;

import java.util.List;
import java.util.Map;

/**
 *
 * @author marcin.goss
 */
public class Interpretations {
      
    private List<Interpretation> interpretations;
    private Map<String,WordType> wordTypes;
    
    public Interpretations(List<Interpretation> interpretations, Map<String,WordType> wordTypes ){
        this.interpretations = interpretations;
        this.wordTypes = wordTypes;
    }

    /**
     * @return the foundNames
     */
    public List<Interpretation> getInterpretations() {
        return interpretations;
    }

    /**
     * @return the wordTypes
     */
    public Map<String,WordType> getWordTypes() {
        return wordTypes;
    }
    
    
}
