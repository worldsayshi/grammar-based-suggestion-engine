package com.findwise.grammarsearch.core;

import org.grammaticalframework.pgf.ParseError;

/**
 *
 * @author per.fredelius
 */
public interface GrammarSearchClient<T> {

    public T performQuery (String question) throws ParseError;
}
