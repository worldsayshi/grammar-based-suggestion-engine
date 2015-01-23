package com.findwise.grammarsearch.web;

import com.findwise.grammarsearch.SearchConfig;
import com.findwise.grammarsearch.core.GrammarSearchDomain;
import com.google.gson.Gson;
import java.util.Map;
import javax.ws.rs.*;
import org.agfjord.grammar.SolrGrammarSuggester;
import org.agfjord.grammar.SolrNameSuggester;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author per.fredelius
 */
@Path("/")
public class JSONService {
    
    private Map<String, GrammarSearchDomain> searchDomains;
    private static Gson gson = new Gson();
    
    public JSONService () {
        ApplicationContext ctx
                = new AnnotationConfigApplicationContext(SearchConfig.class);
        searchDomains = ctx.getBeansOfType(GrammarSearchDomain.class);
    }
    
    @GET
    @Path("/{grammarSearchDomain}/search")
    public String search(
            @PathParam("grammarSearchDomain") String grammarSearchDomain,
            @QueryParam("q") String question,
            @QueryParam("callback") String callback) {
        return callback + "(" + gson.toJson( searchDomains.get(grammarSearchDomain).performQuery(question) ) + "}";
    }
    
    @GET
    @Path("/{grammarSearchDomain}/suggestSentences")
    public String suggestSentences(
            @PathParam("grammarSearchDomain") String grammarSearchDomain,
            @QueryParam("q") String question,
            @QueryParam("callback") String callback) throws SolrGrammarSuggester.GrammarLookupFailure, SolrNameSuggester.NameLookupFailed {
        return callback + "(" + gson.toJson( searchDomains.get(grammarSearchDomain).suggestSentences(question) ) + "}";
    }
    
}
