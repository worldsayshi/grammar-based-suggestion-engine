package com.findwise.grammarsearch;

import com.findwise.grammarsearch.core.GrammarSearchClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.SolrParams;
import org.grammaticalframework.pgf.Concr;
import org.grammaticalframework.pgf.Expr;
import org.grammaticalframework.pgf.ExprProb;
import org.grammaticalframework.pgf.PGF;
import org.grammaticalframework.pgf.ParseError;

/**
 *
 * @author per.fredelius
 */
class SolrGrammarSearchClient implements GrammarSearchClient<SolrDocumentList>{

    PGF pgf;
    SolrServer server;
    
    public SolrGrammarSearchClient(PGF pgf, String solr_url) {
        this.pgf = pgf;
        server = new HttpSolrServer(solr_url);
    }
    
    @Override
    public SolrDocumentList performQuery (String question, String lang) throws ParseError {
        List<Expr> exprs = parseToExpression(question,lang);
        if(exprs.isEmpty()){
            throw new Error("TODO");
        }
        
        Expr expr = exprs.get(0);
        Concr apiLang = pgf.getLanguages().get("InstrucsSolr");
        String apiLinearization = apiLang.linearize(expr);
        try {
            List<NameValuePair> params = URLEncodedUtils.parse(new URI(apiLinearization), "UTF-8");
            try {
                QueryResponse query = server.query(buildSolrQuery(params));
                return query.getResults();
            } catch (SolrServerException ex) {
                Logger.getLogger(SolrGrammarSearchClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (URISyntaxException ex) {
            Logger.getLogger(SolrGrammarSearchClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return null;
    }

    
    List<Expr> parseToExpression(String question, String parseLang) throws ParseError {
        Iterable<ExprProb> exprProbs;
        List<Expr> expressions = new ArrayList<>();
        exprProbs = pgf.getLanguages()
                .get(parseLang)
                .parse(
                        pgf.getStartCat(), question);
        for (ExprProb exprProb : exprProbs) {
            Expr expr = exprProb.getExpr();
            expressions.add(expr);
        }
        return expressions;
    }

    private SolrParams buildSolrQuery(List<NameValuePair> params) {
        SolrQuery q = new SolrQuery();
        for(NameValuePair p:params){
            q.set(p.getName(), p.getValue());
        }
        return q;
    }
}
