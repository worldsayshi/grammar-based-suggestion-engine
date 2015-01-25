package com.findwise.grammarsearch.web;

import com.findwise.grammarsearch.SearchConfig;
import com.findwise.grammarsearch.core.GrammarSearchDomain;
import com.google.gson.Gson;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import com.findwise.grammarsearch.core.SolrGrammarSuggester;
import com.findwise.grammarsearch.core.SolrNameSuggester;
import org.grammaticalframework.pgf.ParseError;

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
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    public String search(
            @PathParam("grammarSearchDomain") String grammarSearchDomain,
            @QueryParam("q") String question,
            @QueryParam("lang") String concreteLang,
            @QueryParam("callback") String callback) throws ParseError {
        return callback + "(" + gson.toJson( 
                searchDomains
                        .get(grammarSearchDomain)
                        .performQuery(question,concreteLang) ) + "}";
    }
    
    @GET
    @Path("/{grammarSearchDomain}/suggestSentences")
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    public String suggestSentences(
            @PathParam("grammarSearchDomain") String grammarSearchDomain,
            @QueryParam("q") String question,
            @QueryParam("lang") String concreteLang,
            @QueryParam("callback") String callback) throws SolrGrammarSuggester.GrammarLookupFailure, SolrNameSuggester.NameLookupFailed {
        return callback + "(" + gson.toJson( 
                searchDomains
                        .get(grammarSearchDomain)
                        .suggestSentences(question,concreteLang) ) + "}";
    }
}
