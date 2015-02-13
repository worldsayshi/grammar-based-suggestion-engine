package com.findwise.grammarsearch.web;

import com.findwise.crescent.rest.SuggestionParams;
import com.findwise.grammarsearch.SearchConfig;
import com.findwise.grammarsearch.core.GrammarSearchDomain;
import com.google.gson.Gson;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import com.findwise.grammarsearch.core.SolrGrammarSuggester;
import com.findwise.grammarsearch.core.SolrNameSuggester;
import java.util.*;
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
    
    /**
     * 
     * @param grammarSearchDomain
     * @param question
     * @param concreteLang
     * @return
     * @throws com.findwise.grammarsearch.core.SolrGrammarSuggester.GrammarLookupFailure
     * @throws com.findwise.grammarsearch.core.SolrNameSuggester.NameLookupFailed
     */
    @RequestMapping(
            value = "/api/{grammarSearchDomain}/suggestSentences",
            method=RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON+";charset=UTF-8"
            )
    public String suggestSentences(
            @PathVariable String grammarSearchDomain,
            @RequestParam(value = "q",required = true) String question,
            @RequestParam(value = "lang",required = true) String concreteLang,
            @RequestParam(value = "norep", required = false) String noRepetitionTypes,
            @RequestParam(value = "n", required = false) String maxSuggestions,
            @RequestParam(value = "add", required = false) String addCombinations,
            @RequestParam(value = "hint", required = false) String continueHint,
            @RequestParam(value = "continue", required = false) String enableContinue,
            @RequestParam(value = "alter", required = false) String enableAlter,
            @RequestParam(value = "sim", required = false) String alterSimilarity) throws SolrGrammarSuggester.GrammarLookupFailure, SolrNameSuggester.NameLookupFailed, Exception {
        
        SuggestionParams params = buildSuggestionParams(noRepetitionTypes, maxSuggestions, addCombinations, continueHint, enableContinue, enableAlter, alterSimilarity);
        
        return gson.toJson( 
                searchDomains
                        .get(grammarSearchDomain)
                        .suggestSentences(question,concreteLang,params) );
    }

    private SuggestionParams buildSuggestionParams(String noRepetitionTypes, String maxSuggestions, String addCombinations, String continueHint, String enableContinue, String enableAlter, String alterSimilarity) throws NumberFormatException {
        SuggestionParams params = new SuggestionParams();
        Set<String> noRepetitionTypesSet = new HashSet<>();
        if(noRepetitionTypes!=null && !noRepetitionTypes.isEmpty()){
            noRepetitionTypesSet.addAll(Arrays.asList(noRepetitionTypes.toLowerCase().split(",")));
        }
        params.setNoRepetitionTypes(noRepetitionTypesSet);
        if(maxSuggestions != null && !maxSuggestions.isEmpty()){
        params.setMaxSuggestions(Integer.parseInt(maxSuggestions));
        }
        if(addCombinations != null && !addCombinations.isEmpty()){
        params.setMaxAdditionalSuggestedNames(Integer.parseInt(addCombinations));
        }
        if(continueHint != null && !continueHint.isEmpty()){
        params.setContinueHint(continueHint);
        }
        if(enableContinue != null && !enableContinue.isEmpty()){
        params.setEnableContinue(Boolean.parseBoolean(enableContinue));
        }
        if(enableAlter != null && !enableAlter.isEmpty()){
        params.setEnableAlter(Boolean.parseBoolean(enableAlter));
        }
        if(alterSimilarity != null && !alterSimilarity.isEmpty()){
        params.setAlterSimiliarity(Integer.parseInt(alterSimilarity));
        }
        return params;
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
