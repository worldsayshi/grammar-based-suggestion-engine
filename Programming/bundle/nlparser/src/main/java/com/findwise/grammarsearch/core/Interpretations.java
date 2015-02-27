package com.findwise.grammarsearch.core;

import java.util.List;
import java.util.Map;
import org.agfjord.server.result.NameResult;

/**
 * Class representing a result of initial query analysis
 */
public class Interpretations {

    //list of query interpretations
    private List<Interpretation> interpretations;
    //map from words appearing in the query to their deduced wordtypes
    private Map<String, WordType> wordTypes;
    //true if all names in the query are fully valid names
    private boolean allNamesComplete;

    public Interpretations(List<Interpretation> interpretations, Map<String, WordType> wordTypes) {
        this.interpretations = interpretations;
        this.wordTypes = wordTypes;
        this.allNamesComplete = checkAllNamesComplete();
    }

    private boolean checkAllNamesComplete() {
        if(interpretations.size()>1){
            return false;
        }
        
        for(NameResult nameResult : interpretations.get(0).getNameResults()){
            if(!nameResult.getName().equalsIgnoreCase(nameResult.getOriginalName())){
                return false;
            }
        }
        
        return true;
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
    public Map<String, WordType> getWordTypes() {
        return wordTypes;
    }

    /**
     * @return the allNamesComplete
     */
    public boolean isAllNamesComplete() {
        return allNamesComplete;
    }
}