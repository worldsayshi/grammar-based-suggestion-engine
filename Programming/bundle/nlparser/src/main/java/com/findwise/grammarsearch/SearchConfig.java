package com.findwise.grammarsearch;

import com.findwise.crescent.model.TripList;
import com.findwise.grammarsearch.core.GrammarSearchDomain;
import com.findwise.grammarsearch.core.VasttrafikGrammarSearchClient;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.findwise.grammarsearch.core.SolrGrammarSuggester;
import com.findwise.grammarsearch.core.SolrNameSuggester;
import org.apache.solr.common.SolrDocumentList;
import org.grammaticalframework.pgf.NercLiteralCallback;
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
        URL url = this.getClass().getClassLoader().getResource("Vasttrafik.pgf");
        PGF pgf = importPGF(url);
        
        return new GrammarSearchDomain<>("Vasttrafik",
                new SolrNameSuggester(solr_url),
                new SolrGrammarSuggester(solr_url),
                new VasttrafikGrammarSearchClient(pgf));
    }
    
    @Bean
    public GrammarSearchDomain<SolrDocumentList> precisionSearch () {
        String solr_url = System.getProperty("solr.base.url");
        if (null == solr_url) {
            throw new BeanCreationException("Could not initialize search domain: solr.base.url was not set!");
        }
        URL url = this.getClass().getClassLoader().getResource("Instrucs.pgf");
        PGF pgf = importPGF(url);
        
        return new GrammarSearchDomain<>("Instrucs",
                new SolrNameSuggester(solr_url),
                new SolrGrammarSuggester(solr_url),
                new SolrGrammarSearchClient(pgf,solr_url+"/relations"));
    }
    
    private PGF importPGF (URL url) {
        PGF pgf;
        try {
            pgf = PGF.readPGF(url.openStream());
        } catch (IOException ex) {
            Logger.getLogger(SearchConfig.class.getName()).log(Level.SEVERE, null, ex);
            throw new BeanCreationException("Could not import Vasttrafik pgf");
        }
        return pgf;
    }
}
