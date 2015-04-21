package com.findwise.grammarsearch.core;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
import org.grammaticalframework.pgf.*;

/**
 *
 * @author per.fredelius
 */
public class SolrGrammarSearchClient implements GrammarSearchClient<SolrDocumentList> {

    SolrServer server;

    public SolrGrammarSearchClient(String solr_url) {
        server = new HttpSolrServer(solr_url);
    }

    @Override
    public SolrDocumentList performQuery(String textQuery, String apiQuery) {

            if(apiQuery != null){
                return performApiQuery(apiQuery);
            }
            else{
                return performFreeTextSearch(textQuery);
            }
    }

    private SolrDocumentList performApiQuery(String apiQuery) {
        try {
            List<NameValuePair> params = URLEncodedUtils.parse(parseUrl("http://www.foo.bar/" + apiQuery), "UTF-8");
            QueryResponse query = server.query(buildSolrQuery(params));
            return query.getResults();
        } catch (SolrServerException ex) {
            Logger.getLogger(SolrGrammarSearchClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(SolrGrammarSearchClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(SolrGrammarSearchClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private SolrDocumentList performFreeTextSearch(String input) {
        try {
            SolrQuery sq = new SolrQuery();
            sq.setQuery(input);
            QueryResponse response = server.query(sq);
            return response.getResults();
        } catch (SolrServerException ex) {
            Logger.getLogger(SolrGrammarSearchClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private SolrParams buildSolrQuery(List<NameValuePair> params) {
        SolrQuery q = new SolrQuery();
        for (NameValuePair p : params) {
            q.set(p.getName(), p.getValue());
        }
        return q;
    }

    public static URI parseUrl(String s) throws URISyntaxException, MalformedURLException {
        URL u = new URL(s);
        return new URI(
                u.getProtocol(),
                u.getAuthority(),
                u.getPath(),
                u.getQuery(),
                u.getRef());
    }
}
