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

    public SolrNameSuggester(String solr_url) {
        namesServer = new HttpSolrServer(solr_url + "/names");

    }

    /*
     * Returns a list of names found in the index most similiar to the string
     * provided as a parameter
     */
    public List<NameResult> suggestNameResolution(String word) throws NameLookupFailed {
        SolrQuery namesQuery = new SolrQuery();
        namesQuery.addSort("score", SolrQuery.ORDER.desc);
        namesQuery.setQuery(word + "*~0.7");
        namesQuery.addSort("abs(sub(length," + word.length() + "))", SolrQuery.ORDER.asc);
        QueryResponse rsp;
        try {
            rsp = namesServer.query(namesQuery);
        } catch (SolrServerException ex) {
            throw new NameLookupFailed(ex);
        }
        List<NameResult> res = rsp.getBeans(NameResult.class);

        //if first name is a perfect match, then return only this one
        if (!res.isEmpty() && res.get(0).getName().equalsIgnoreCase(word)) {
            res = res.subList(0, 1);
        }

        return res;
    }

    /**
     * Gives a list of names in the index of a given type
     *
     * @param absGrammarName names grammar
     * @param missingNameType type of names to be returned
     * @param namesInQuestion names already used so therefore not to be included
     * in result
     * @param resultSize number of names to be returned
     * @return
     * @throws
     * com.findwise.grammarsearch.core.SolrNameSuggester.NameLookupFailed
     */
    public List<NameResult> suggestNames(
            String absGrammarName,
            String missingNameType,
            List<NameResult> namesInQuestion, Integer resultSize) throws NameLookupFailed {

        SolrQuery namesQuery = new SolrQuery();

        //sort by score and keep shortest first
        namesQuery.addSort("score", SolrQuery.ORDER.desc);
        namesQuery.addSort("length", SolrQuery.ORDER.asc);

        //avoid names of this type already in query
        for (NameResult nameInQuestion : namesInQuestion) {
            if (nameInQuestion.getType().equals(missingNameType)) {
                namesQuery.addFilterQuery("-name:" + nameInQuestion.getName());
            }
        }

        namesQuery.addFilterQuery("type:" + missingNameType);
        namesQuery.addFilterQuery("abs_lang:" + absGrammarName);
        namesQuery.setQuery("*:*");

        namesQuery.setRows(resultSize);
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
