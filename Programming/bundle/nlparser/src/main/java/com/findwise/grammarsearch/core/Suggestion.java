package com.findwise.grammarsearch.core;

/**
 *  Class representing potential suggestion together with some numbers describing its relation to the original query
 */
public class Suggestion {
    
    //suggestion text
    private String text;
    
    //true - if name placeholders has been replaced with valid names already, false otherwise
    private boolean isNamesFilled;
    
    //count of names added in relation to the original query
    private int additionalNamesCount;
    
    //count of grammar words added in relation to the original query
    private int addtionalGrammarWords;
    
    //count of grammar words altered in relation to the original query
    private int alteredGrammarWordsCount;
    
    //words count in the suggestion text
    private int wordsCount;
    
    public Suggestion(String text, boolean isNamesFilled, int addNames, int addGrammar, int alterGrammar){
        this.text = text;
        this.isNamesFilled = isNamesFilled;
        this.additionalNamesCount = addNames;
        this.addtionalGrammarWords = addGrammar;
        this.alteredGrammarWordsCount = alterGrammar;
        
        this.wordsCount = text.split("\\s+").length;
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
    public int getAlteredGrammarWordsCount() {
        return alteredGrammarWordsCount;
    }

    /**
     * @param alteredGrammarWordsCount the alteredGammarWordsCount to set
     */
    public void setAlteredGrammarWordsCount(int alteredGrammarWordsCount) {
        this.alteredGrammarWordsCount = alteredGrammarWordsCount;
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

    /**
     * @return the wordsCount
     */
    public int getWordsCount() {
        return wordsCount;
    }
}
