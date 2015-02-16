/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.findwise.grammarsearch.core;

/**
 *
 * @author marcin.goss
 */
public class Suggestion {
    private String text;
    private boolean isNamesFilled;
    private int additionalNamesCount;
    private int addtionalGrammarWords;
    private int alteredGammarWordsCount;
    
    public Suggestion(String text, boolean isNamesFilled, int addNames, int addGrammar, int alterGrammar){
        this.text = text;
        this.isNamesFilled = isNamesFilled;
        this.additionalNamesCount = addNames;
        this.addtionalGrammarWords = addGrammar;
        this.alteredGammarWordsCount = alterGrammar;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the additionalNamesCount
     */
    public int getAdditionalNamesCount() {
        return additionalNamesCount;
    }

    /**
     * @param additionalNamesCount the additionalNamesCount to set
     */
    public void setAdditionalNamesCount(int additionalNamesCount) {
        this.additionalNamesCount = additionalNamesCount;
    }

    /**
     * @return the addtionalGrammarWords
     */
    public int getAddtionalGrammarWords() {
        return addtionalGrammarWords;
    }

    /**
     * @param addtionalGrammarWords the addtionalGrammarWords to set
     */
    public void setAddtionalGrammarWords(int addtionalGrammarWords) {
        this.addtionalGrammarWords = addtionalGrammarWords;
    }

    /**
     * @return the alteredGammarWordsCount
     */
    public int getAlteredGammarWordsCount() {
        return alteredGammarWordsCount;
    }

    /**
     * @param alteredGammarWordsCount the alteredGammarWordsCount to set
     */
    public void setAlteredGammarWordsCount(int alteredGammarWordsCount) {
        this.alteredGammarWordsCount = alteredGammarWordsCount;
    }

    /**
     * @return the isNamesFilled
     */
    public boolean isIsNamesFilled() {
        return isNamesFilled;
    }

    /**
     * @param isNamesFilled the isNamesFilled to set
     */
    public void setIsNamesFilled(boolean isNamesFilled) {
        this.isNamesFilled = isNamesFilled;
    }
}
