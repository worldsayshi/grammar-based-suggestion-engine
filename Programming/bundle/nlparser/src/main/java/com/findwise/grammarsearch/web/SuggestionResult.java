/*
 */
package com.findwise.grammarsearch.web;

/**
 *
 * @author marcin.goss
 */
public class SuggestionResult {
    private String text;
    private String searchApiQuery;
    
    public SuggestionResult(String text, String searchApiQuery){
        this.text = text;
        this.searchApiQuery = searchApiQuery;
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
     * @return the searchApiQuery
     */
    public String getSearchApiQuery() {
        return searchApiQuery;
    }

    /**
     * @param searchApiQuery the searchApiQuery to set
     */
    public void setSearchApiQuery(String searchApiQuery) {
        this.searchApiQuery = searchApiQuery;
    }
}
