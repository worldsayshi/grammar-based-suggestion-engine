package com.findwise.grammarsearch.core;

import java.util.List;
import org.grammaticalframework.pgf.ParseError;

/**
 *
 * @author per.fredelius
 */
public interface GrammarSearchClient<T> {

    public T performQuery (String question, String lang, String apiQuery) throws ParseError;
}
