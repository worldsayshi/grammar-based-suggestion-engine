package com.findwise.grammarsearch;

import com.findwise.crescent.model.TripList;
import com.findwise.grammarsearch.core.*;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.solr.common.SolrDocumentList;
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
            throw new BeanCreationException("Could not initialize search domain: solr.base.url was not set!");
        }
        
        return new GrammarSearchDomain<>("Vasttrafik",
                new SolrNameSuggester(solr_url),
                new SolrGrammarSuggester(solr_url),
                new VasttrafikGrammarSearchClient(),
                Arrays.asList(new String[]{"VasttrafikEngConcat","VasttrafikSweConcat", "VasttrafikPL"}),
                "Ask for travel directions from one station to another. Choose preferred means of transport and departure / arrival time!");
    }
    
    @Bean
    public GrammarSearchDomain<SolrDocumentList> precisionSearch () {
        String solr_url = System.getProperty("solr.base.url");
        if (null == solr_url) {
            throw new BeanCreationException("Could not initialize search domain: solr.base.url was not set!");
        }
        
        return new GrammarSearchDomain<>("Instrucs",
                new SolrNameSuggester(solr_url),
                new SolrGrammarSuggester(solr_url),
                new SolrGrammarSearchClient(solr_url+"/relations"),
                Arrays.asList(new String[]{"InstrucsEngRGL","InstrucsSweRGL","InstrucsPL"}),
                "Ask for people with certain skills or those worked in specific locations / organizations!");
    }
}
