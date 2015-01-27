package com.findwise.grammarsearch.core;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.agfjord.server.result.NameResult;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

/**
 *
 * @author per.fredelius
 */
public class SolrNameSuggester {
    private SolrServer namesServer;
    public SolrNameSuggester (String solr_url) {
        namesServer = new HttpSolrServer(solr_url+"/names");
    
    }

    public List<NameResult> suggestNameResolution(String word) throws NameLookupFailed {
        SolrQuery namesQuery = new SolrQuery();
		namesQuery.addSort("score", SolrQuery.ORDER.desc);
        namesQuery.setQuery(word+"*~0.7");
        namesQuery.addSort("abs(sub(length," + word.length() + "))", SolrQuery.ORDER.asc);
        QueryResponse rsp;
        try {
            rsp = namesServer.query(namesQuery);
        } catch (SolrServerException ex) {
            throw new NameLookupFailed(ex);
        }
        return rsp.getBeans(NameResult.class);
    }

    public List<NameResult> suggestNames(
            String absGrammarName,
            String missingNameType, 
            List<NameResult> namesInQuestion, Integer nr_of_additional_suggestions) throws NameLookupFailed {
        SolrQuery namesQuery = new SolrQuery();
        namesQuery.addSort("score", SolrQuery.ORDER.desc);
        namesQuery.addSort("length", SolrQuery.ORDER.asc);
        String queryForNamesNotInQuestion = "abs_lang:"+absGrammarName
                +" type:" + missingNameType;
        for(NameResult nameInQuestion:namesInQuestion){
            queryForNamesNotInQuestion += " -name:" + nameInQuestion.getName();
        }
        namesQuery.setFilterQueries(queryForNamesNotInQuestion);
        namesQuery.setQuery("*:*");
        int numberOfWantedSuggestions = nr_of_additional_suggestions;
        namesQuery.setRows(numberOfWantedSuggestions);
        List<NameResult> namesNotInQuestion;
        try {
            QueryResponse namesRespT = namesServer.query(namesQuery);
            namesNotInQuestion = namesRespT.getBeans(NameResult.class);
        } catch (SolrServerException ex) {
            throw new NameLookupFailed(ex);
        }
        return namesNotInQuestion;
    }

    static public class NameLookupFailed extends Exception {

        public NameLookupFailed(SolrServerException ex) {
            super(ex);
        }
    }
}
