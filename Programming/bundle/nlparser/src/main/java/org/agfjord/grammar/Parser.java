package org.agfjord.grammar;

import com.findwise.crescent.model.StopLocation;
import com.findwise.crescent.model.TripList;
import com.findwise.crescent.rest.VasttrafikRestClient;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.agfjord.domain.AbstractSyntaxTree;
import org.agfjord.domain.Query;
import org.agfjord.server.result.NameResult;
import org.agfjord.server.result.TreeResult;
import org.apache.log4j.Logger;
import org.grammaticalframework.pgf.Concr;
import org.grammaticalframework.pgf.Expr;
import org.grammaticalframework.pgf.ExprProb;
import org.grammaticalframework.pgf.NercLiteralCallback;
import org.grammaticalframework.pgf.PGF;
import org.grammaticalframework.pgf.PGFError;
import org.grammaticalframework.pgf.ParseError;


/*
 Authors: Martin Agfjord, Per Fredelius
 */
public class Parser {

    

    // Inject into VasttrafikGrammarSearchClient
    private PGF pgf;
    
    
    

    
	//final private Properties prop = new Properties();

    

    static Logger log = Logger.getLogger(
            Parser.class.getName());

    // Move into GrammarSearchDomain
    /*public Parser() {
        solr_url = System.getProperty("solr.base.url");
        if (null == solr_url) {
            throw new IllegalStateException("Could not initialize parser: solr.base.url variable was not set!");
        }

        nameSuggester = new SolrNameSuggester(solr_url);
        grammarSuggester = new SolrGrammarSuggester(solr_url);

        try {
            URL url = this.getClass().getClassLoader().getResource("Vasttrafik.pgf");
            pgf = PGF.readPGF(url.openStream());
        } catch (PGFError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //gr.getLanguages().get("VasttrafikSolr").addLiteral("Symb", new NercLiteralCallback());
        pgf.getLanguages().get("VasttrafikEngConcat").addLiteral("Symb", new NercLiteralCallback());
        //gr.getLanguages().get("InstrucsEngRGL").addLiteral("Symb", new NercLiteralCallback());
        //gr.getLanguages().get("InstrucsEngConcat").addLiteral("Symb", new NercLiteralCallback());
        //gr.getLanguages().get("InstrucsSweRGL").addLiteral("Symb", new NercLiteralCallback());
    }*/

    /*
     * Parse a string @question in a language @parseLang. Retrieve all distinct abstract syntax trees
     * and their distinct linearizations. I think there exists a bug in GF which causes a NLP-sentence
     * to be parsed into multiple equal abstract syntax trees, the same with linearizations. drbean @ #gf @ freenode
     * told me this bug existed.
     * 
     */
    // Deprecated?
    public List<AbstractSyntaxTree> parse(String question, String parseLang) throws ParseError {
        Iterable<ExprProb> exprProbs;
        // Map is used to only have distinct asts, Set is used to only have distinct linearizations
        Map<String, Set<Query>> astQuery = new HashMap<String, Set<Query>>();
        exprProbs = pgf.getLanguages().get(parseLang).parse(pgf.getStartCat(), question);
        for (String key : pgf.getLanguages().keySet()) {
            Concr lang = pgf.getLanguages().get(key);
            for (ExprProb exprProb : exprProbs) {
                Set<Query> qs = astQuery.get(exprProb.getExpr().toString());
                if (qs == null) {
                    qs = new TreeSet<Query>(comparator);
                    astQuery.put(exprProb.getExpr().toString(), qs);
                }
                qs.add(new Query(lang.linearize(exprProb.getExpr()), lang.getName()));
            }
        }
        List<AbstractSyntaxTree> asts = new ArrayList<AbstractSyntaxTree>();
        for (String key : astQuery.keySet()) {
            asts.add(new AbstractSyntaxTree(key, astQuery.get(key)));
        }
        return asts;
    }
    

    Comparator<Query> comparator = new Comparator<Query>() {
        public int compare(Query a, Query b) {
            return a.getLanguage().compareTo(b.getLanguage());
        }
    };


    

    

    
    
    /*
    // TODO Use Templating class
    public String replaceNames(String nlQuestion, List<NameResult> names, String replaceWith) {
        for (NameResult nameResult : names) {
            String toReplace, replacement;
            if (replaceWith.equals("types")) { // We replace name with type
                toReplace = nameResult.getOriginalName();
                replacement = nameResult.getType();
            } else { // We replace type with name
                toReplace = nameResult.getType();
                replacement = nameResult.getName();
            }
            nlQuestion = nlQuestion.replace(toReplace, replacement);
        }
        return nlQuestion;
    }
    */

    

    
}
