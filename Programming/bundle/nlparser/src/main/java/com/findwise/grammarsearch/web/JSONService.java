package com.findwise.grammarsearch.web;

import com.findwise.grammarsearch.SearchConfig;
import com.findwise.grammarsearch.core.GrammarSearchDomain;
import com.google.gson.Gson;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import com.findwise.grammarsearch.core.SolrGrammarSuggester;
import com.findwise.grammarsearch.core.SolrNameSuggester;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.grammaticalframework.pgf.ParseError;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author per.fredelius
 */
@RestController
@RequestMapping(value = "/")
public class JSONService {
    
    
    
    // @Autowire? Add @Component on the class first..
    private Map<String, GrammarSearchDomain> searchDomains;
    private static Gson gson = new Gson();
    
    public JSONService () {
        ApplicationContext ctx
                = new AnnotationConfigApplicationContext(SearchConfig.class);
        searchDomains = ctx.getBeansOfType(GrammarSearchDomain.class);
    }
    
    //@GET
    //@Produces("text/html")
    
    @RequestMapping(
            method=RequestMethod.GET, 
            produces = MediaType.TEXT_HTML)
    public ModelAndView index() {
        Map<String, Map<String,List<String>>> model = new HashMap<>();
        
        Map<String,List<String>> domainsModel = new HashMap<>();
        for(String searchDomainName:searchDomains.keySet()){
            GrammarSearchDomain dom = searchDomains.get(searchDomainName);
            List languages = dom.getLanguages();
            domainsModel.put(searchDomainName, languages);
        }
        model.put("searchDomains", domainsModel);
        
        return new ModelAndView("index",model);
    }
    
    //@GET
    //@Path("/api/{grammarSearchDomain}/search")
    //@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @RequestMapping(
            value = "/api/{grammarSearchDomain}/search",
            method=RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON+";charset=UTF-8"
            )
    public String search(
            @PathVariable("grammarSearchDomain") String grammarSearchDomain,
            @RequestParam(value = "q",required = true) String question,
            @RequestParam(value = "lang",required = true) String concreteLang) throws ParseError {
        return gson.toJson( 
                searchDomains
                        .get(grammarSearchDomain)
                        .performQuery(question,concreteLang) );
    }
    
    //@GET
    //@Path("/api/{grammarSearchDomain}/suggestSentences")
    //@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    
    @RequestMapping(
            value = "/api/{grammarSearchDomain}/suggestSentences",
            method=RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON+";charset=UTF-8"
            )
    public String suggestSentences(
            @PathVariable String grammarSearchDomain,
            @RequestParam(value = "q",required = true) String question,
            @RequestParam(value = "lang",required = true) String concreteLang) throws SolrGrammarSuggester.GrammarLookupFailure, SolrNameSuggester.NameLookupFailed {
        return gson.toJson( 
                searchDomains
                        .get(grammarSearchDomain)
                        .suggestSentences(question,concreteLang) );
    }
    
    //@GET
    //@Path("/api/listDomains")
    //@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    
    @RequestMapping(value = "/api/listDomains",
            method=RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON+";charset=UTF-8")
    public String listDomains (
        @RequestParam("callback") String callback){
        return callback + "(" + gson.toJson( searchDomains.keySet() ) + ")";
    }
}
