package com.findwise.grammarsearch.core;

import java.util.ArrayList;
import java.util.List;
import org.grammaticalframework.pgf.Expr;
import org.grammaticalframework.pgf.ExprProb;
import org.grammaticalframework.pgf.ParseError;

/**
 *
 * @author per.fredelius
 */
public interface GrammarSearchClient<T> {

    public T performQuery (String question, String lang) throws ParseError;
    
    public List<String> getLanguages ();
}
