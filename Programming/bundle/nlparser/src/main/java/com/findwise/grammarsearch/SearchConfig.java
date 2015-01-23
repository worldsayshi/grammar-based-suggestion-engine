package com.findwise.grammarsearch;

import com.findwise.crescent.model.TripList;
import com.findwise.grammarsearch.core.GrammarSearchDomain;
import com.findwise.grammarsearch.core.VasttrafikGrammarSearchClient;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.agfjord.grammar.SolrGrammarSuggester;
import org.agfjord.grammar.SolrNameSuggester;
import org.grammaticalframework.pgf.PGF;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author per.fredelius
 */
@Configuration
public class SearchConfig {
    
    @Bean
    public GrammarSearchDomain<TripList> vasttrafik(){
        String solr_url = System.getProperty("solr.base.url");
        if (null == solr_url) {
            throw new BeanCreationException("Could not initialize parser: solr.base.url variable was not set!");
        }
        URL url = this.getClass().getClassLoader().getResource("Vasttrafik.pgf");
        PGF pgf;
        try {
            pgf = PGF.readPGF(url.openStream());
        } catch (IOException ex) {
            Logger.getLogger(SearchConfig.class.getName()).log(Level.SEVERE, null, ex);
            throw new BeanCreationException("Could not import Vasttrafik pgf");
        }
         
        return new GrammarSearchDomain<>(new SolrNameSuggester(solr_url),
                new SolrGrammarSuggester(solr_url),
                new VasttrafikGrammarSearchClient(pgf));
    }
}
