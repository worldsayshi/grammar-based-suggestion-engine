package com.findwise.grammarsearch.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.agfjord.server.result.NameResult;
import org.agfjord.server.result.TreeResult;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;

/**
 *
 * @author per.fredelius
 */
public class SolrGrammarSuggester {
    
    private SolrServer treesServer;
    private Integer max_nr_of_trees = 40;
    
    public SolrGrammarSuggester(String solr_url) {
        treesServer = new HttpSolrServer(solr_url + "/trees");
    }

    // Check if word is found in the grammar
    public boolean checkIfGrammarWord(String word) throws GrammarLookupFailure {
        SolrQuery treesQuery = new SolrQuery();
        treesQuery.setRows(1);
        treesQuery.setQuery(word);
        QueryResponse rsp;
        try {
            rsp = treesServer.query(treesQuery);
        } catch (SolrServerException ex) {
            throw new GrammarLookupFailure(ex);
        }
        return 0 != rsp.getResults().getNumFound();
    }
    
    public List<TreeResult> suggestRules(String nlQuestion, String concreteLang, List<NameResult> namesInQuestion) throws GrammarLookupFailure {
        return suggestRules(nlQuestion, concreteLang, namesInQuestion, max_nr_of_trees, false, 0);
    } 
    
    public List<TreeResult> suggestRules(String nlQuestion, String concreteLang, List<NameResult> namesInQuestion, boolean useSimiliarity, int similiarity) throws GrammarLookupFailure {
        return suggestRules(nlQuestion, concreteLang, namesInQuestion, max_nr_of_trees, useSimiliarity, similiarity);
    } 
    
    /**
     * Returns a list of grammar rules that match give query
     * @param template natural language query that has names replaced by name placeholders
     * @param concreteLang natural language used
     * @param namesInQuestion names in query
     * @param maxRules maximum number of rules to be returned
     * @param useSimiliarity whether or not to include minimum match parameter to query
     * @param similiarity percent of terms that should match in order to be returned
     * @return list of rules matching given constraints
     * @throws com.findwise.grammarsearch.core.SolrGrammarSuggester.GrammarLookupFailure 
     */
    public List<TreeResult> suggestRules(String template, String concreteLang, List<NameResult> namesInQuestion, int maxRules, boolean useSimiliarity, int similiarity) throws GrammarLookupFailure {
        SolrQuery treesQuery = new SolrQuery();
        // 
        treesQuery.setRows(maxRules);
        treesQuery.setQuery("linearizations:" + ClientUtils.escapeQueryChars(template)
                + " " + boostByTypeQuery(namesInQuestion));
        treesQuery.addFilterQuery("lang:" + concreteLang);

        // Sorting based on suggestion length and score
        treesQuery.addSort(SolrQuery.SortClause.desc("score"));
        treesQuery.addSort(SolrQuery.SortClause.asc("length"));
        
        if (useSimiliarity) {
            treesQuery.add("mm", similiarity + "%");
        }

        // Run query for getting suggestion templates
        System.out.println(treesQuery.toString());
        
        QueryResponse treesResp;
        try {
            treesResp = treesServer.query(treesQuery);
        } catch (SolrServerException ex) {
            throw new GrammarLookupFailure(ex);
        }
        return treesResp.getBeans(TreeResult.class);
    }
   
    private Map<String, List<NameResult>> getNamesByType(List<NameResult> names) {
        Map<String, List<NameResult>> namesByType = new HashMap<>();
        for (NameResult name : names) {
            if (namesByType.containsKey(name.getType())) {
                List<NameResult> nameList = namesByType.get(name.getType());
                nameList.add(name);
            }
            else {
                namesByType.put(name.getType(),
                        new ArrayList<>(Arrays.asList(name)));
            }
        }
        return namesByType;
    }
    
    private String boostByTypeQuery(List<NameResult> namesInQuestion) {
        Map<String, List<NameResult>> namesByType = getNamesByType(namesInQuestion);
        String res = "";
        for (String typeName : namesByType.keySet()) {
            int count = namesByType.get(typeName).size();
            res += typeName + "_i:" + count;
        }
        return res;
    }
    
    static public class GrammarLookupFailure extends Exception {
        
        public GrammarLookupFailure(SolrServerException ex) {
            super(ex);
        }
    }
}
