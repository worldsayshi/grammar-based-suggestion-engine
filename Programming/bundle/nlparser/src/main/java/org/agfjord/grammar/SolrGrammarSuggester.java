package org.agfjord.grammar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private Integer max_nr_of_trees = 10;
    public SolrGrammarSuggester (String solr_url) {
        treesServer = new HttpSolrServer(solr_url+"/trees");
    }

    // Check if word is found in the grammar
    public boolean checkIfGrammarWord(String word) throws GrammarLookupFailure {
        SolrQuery treesQuery = new SolrQuery();
        treesQuery.setRows(max_nr_of_trees);
        treesQuery.setQuery(word);
        QueryResponse rsp;
        try {
            rsp = treesServer.query(treesQuery);
        } catch (SolrServerException ex) {
            throw new GrammarLookupFailure(ex);
        }
        return 0 != rsp.getResults().getNumFound();
    }

    public List<TreeResult> suggestRules(String nlQuestion, String parseLang, List<NameResult> namesInQuestion) throws GrammarLookupFailure {
        SolrQuery treesQuery = new SolrQuery();
        // 
		treesQuery.setRows(max_nr_of_trees);
        treesQuery.setQuery("linearizations:" + ClientUtils.escapeQueryChars(nlQuestion)
                +" "+boostByTypeQuery(namesInQuestion));
		treesQuery.addFilterQuery("lang:" + parseLang);
        
        //treesQuery.setParam(parseLang, )
        
        // My guess: Weighting suggestions based on what already appear in the query 
        /*String sorting = getSort(namesInQuestion);
        if(sorting != null){
			treesQuery.addSort(SolrQuery.SortClause.asc(sorting));			
		}*/
        
        // Sorting based on suggestion length and score
        treesQuery.addSort(SolrQuery.SortClause.desc("score"));
		treesQuery.addSort(SolrQuery.SortClause.asc("length"));
        
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

    //people who know Skill0 and who work in Location0 ==> add(sub(Skill_i,1),sub(Location_i,1))

	//people who know Skill0 and who work in Location0 and who work with Solr ==> add(add(sub(Skill_i,1),sub(Location_i,1)),sub(Object_i,1))

	private String getSort(List<NameResult> names) {
        // TODO getNamesByType feels a bit redundant... refactor this.
        Map<String,List<NameResult>> namesByType = getNamesByType(names);
		StringBuilder sb = new StringBuilder();
		int sum = 0;
		for(String key : namesByType.keySet()){
			sum += namesByType.get(key).size();
		}
		if(sum == 0){
			return null;
		}
		if(namesByType.keySet().size() > 1) {
			sb.append("add(");
		}
		for(String nameType : namesByType.keySet()){
			sb.append("abs(");
			sb.append("sub(" + nameType + "_i" + "," + (namesByType.get(nameType).size()) + ")");
			sb.append(")");
			sb.append(",");
			// "abs(add(add(sub(Location_i,1),sub(Skill_i,1)),%20sub(Object_i,0)))%20asc,%20score%20desc";	
		}// abs(add(add(add(sub(Skill_i,0),sub(Object_i,0),sub(Location_i,1),)
		sb.deleteCharAt(sb.length()-1);
		if(namesByType.keySet().size() > 1){
			sb.append(")");
		}
		return sb.toString();
	}

    private Map<String, List<NameResult>> getNamesByType(List<NameResult> names) {
        Map<String, List<NameResult>> namesByType = new HashMap<>();
        for (NameResult name : names) {
            if(namesByType.containsKey(name.getType())){
                List<NameResult> nameList = namesByType.get(name.getType());
                nameList.add(name);
            } else {
                namesByType.put(name.getType(), 
                        new ArrayList<>(Arrays.asList(name)));
            }
        }
        return namesByType;
    }

    private String boostByTypeQuery(List<NameResult> namesInQuestion) {
        Map<String,List<NameResult>> namesByType = getNamesByType(namesInQuestion);
        String res = "";
        for (String typeName : namesByType.keySet()) {
            int count = namesByType.get(typeName).size();
            res += typeName+"_i:"+count;
        }
        return res;
    }
    
    
    static public class GrammarLookupFailure extends Exception {

        public GrammarLookupFailure(SolrServerException ex) {
            super(ex);
        }
    }
    
    
}
