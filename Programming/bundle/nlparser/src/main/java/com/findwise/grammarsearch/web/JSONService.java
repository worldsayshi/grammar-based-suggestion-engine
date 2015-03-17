package com.findwise.grammarsearch.web;

import com.findwise.grammarsearch.SearchConfig;
import com.findwise.grammarsearch.core.GrammarSearchDomain;
import com.findwise.grammarsearch.core.SolrGrammarSuggester;
import com.findwise.grammarsearch.core.SolrNameSuggester;
import com.google.gson.Gson;
import java.util.*;
import javax.ws.rs.core.MediaType;
import org.grammaticalframework.pgf.ParseError;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.*;
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

    public JSONService() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SearchConfig.class);
        searchDomains = ctx.getBeansOfType(GrammarSearchDomain.class);
    }

    @RequestMapping(method = RequestMethod.GET,
    produces = MediaType.TEXT_HTML)
    public ModelAndView index(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "domain", required = false) String currentDomain,
            @RequestParam(value = "lang", required = false) String lang) {
        Map<String, Map<String, ?>> model = new HashMap<>();

        Map<String, List<String>> domainsModel = new HashMap<>();
        Map<String,String> descriptions = new HashMap<>();
        for (String searchDomainName : searchDomains.keySet()) {
            GrammarSearchDomain domain = searchDomains.get(searchDomainName);
            List languages = domain.getUserLanguages();
            domainsModel.put(searchDomainName, languages);
            
            descriptions.put(searchDomainName, domain.getDescription());
        }
        model.put("searchDomains", domainsModel);
        model.put("descriptions", descriptions);
        
        Map<String, String> params = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            params.put("query", query);
        }
        if (currentDomain != null && !currentDomain.isEmpty()) {
            params.put("domain", currentDomain);
        }
        if (lang != null && !lang.isEmpty()) {
            params.put("lang", lang);
        }

        model.put("params", params);

        return new ModelAndView("index", model);
    }

    @RequestMapping(value = "/api/{grammarSearchDomain}/search",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public String search(
            @PathVariable("grammarSearchDomain") String grammarSearchDomain,
            @RequestParam(value = "q", required = true) String question,
            @RequestParam(value = "lang", required = true) String concreteLang) throws ParseError {
        String toJson = gson.toJson(
                                searchDomains.get(grammarSearchDomain).performQuery(question, concreteLang));
        
        return toJson;
    }

    @RequestMapping(value = "/api/{grammarSearchDomain}/suggestSentences",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public String suggestSentences(
            @PathVariable String grammarSearchDomain,
            @RequestParam(value = "q", required = true) String question,
            @RequestParam(value = "lang", required = true) String concreteLang,
            SuggestionParams params) throws SolrGrammarSuggester.GrammarLookupFailure, SolrNameSuggester.NameLookupFailed, Exception {

        return gson.toJson(
                searchDomains.get(grammarSearchDomain).suggestSentences(question, concreteLang, params));
    }

    @RequestMapping(value = "/api/listDomains",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public String listDomains(
            @RequestParam("callback") String callback) {
        return callback + "(" + gson.toJson(searchDomains.keySet()) + ")";
    }
}
