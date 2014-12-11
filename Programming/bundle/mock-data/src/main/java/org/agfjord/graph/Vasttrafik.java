package org.agfjord.graph;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;
import org.apache.solr.client.solrj.SolrServerException;
import org.grammaticalframework.pgf.ParseError;

/**
 *
 * @author per.fredelius
 */
public class Vasttrafik {
    
    public void run() throws SolrServerException, ParseError, MalformedURLException {
        String grammmar_dir = System.getProperty("grammar.dir");
        System.out.println(grammmar_dir);

        //URL url = new File(System.getProperty("grammar.dir"),"Vasttrafik.pgf").toURI().toURL();
        /*this.getClass()
                .getClassLoader()
                .getResource("Vasttrafik.pgf");*/
        // Clear the indexes
        
        
		try {
            DataImportSolr solrData = new DataImportSolr();
            solrData.deleteAllInstrucs();
            String abs_grammar_name = "Vasttrafik";
			Grammar grammar = new Grammar(grammmar_dir,abs_grammar_name);
            
            Set<String> asts = grammar.generateAbstractSyntaxTreesFromShell();
            
            System.out.println(asts);
            System.out.println();
            System.out.println("LIN");
            List<Set<String>> linearizations = 
                    grammar.generateLinearizations(asts, 
                            abs_grammar_name+"EngConcat.gf", 
                            abs_grammar_name+"EngConcat");
            System.out.println(linearizations);
            System.out.println();
            System.out.println("INS");
            List<Instruction> instrucs = grammar
                    .createInstrucs(asts, linearizations, abs_grammar_name+"EngConcat");
            System.out.println(instrucs);
            solrData.addInstrucsToSolr(instrucs);
            
            // Clear the indexes
            //solrData.deleteAllInstrucs();
            
            
            
            //solrData.addInstrucsToSolr(instrucs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void main(String[] args) 
            throws ParseError, IOException, SolrServerException {
        System.out.println();
        System.out.println("MAIN");
        new Vasttrafik().run();
        //System.out.println(System.getProperty("java.class.path"));
        System.out.println();
        System.out.println();
        
    }
}
