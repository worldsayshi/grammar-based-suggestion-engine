package org.agfjord.graph;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;
import org.grammaticalframework.pgf.PGF;
import org.grammaticalframework.pgf.ParseError;

public class Main {

//	static File file = new File("/src/main/resources/Instrucs.pgf");
//	static InputStream is = ClassLoader.getSystemResourceAsStream("Instrucs.pgf")
    static File file = new File("src/main/resources/Instrucs.pgf");
//	static InputStream is;

    public Main() {
        try {
            String grammmar_dir = System.getProperty("grammar.dir");
            URL url = this.getClass().getClassLoader().getResource("Instrucs.pgf");
            grammar = new Grammar(grammmar_dir, "Instrucs", PGF.readPGF(url.openStream()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    Grammar grammar;
    DataImportSolr solr = new DataImportSolr();
    DataImportNeo4j dataImport = new DataImportNeo4j();

    public static void main(String[] args) throws ParseError, IOException, SolrServerException {
        
        Main main = new Main();
        
        // Generate random objects and then create persons who uses these values
        main.importDocumentsNeo4j();
//		// Add all names that have a relation to solr
        main.importNamesSolr();
//		// Generate instructions (suggestions) by using
//		// the GF-shell and add them to solr
        main.importInstrucsSolr();

        main.dataImport.shutdown();
        // Add all persons with its relations to solr
        main.solr.importRelationsFromNeo4j();

        new Vasttrafik().run();
    }

    public void importDocumentsNeo4j() throws IOException {
        dataImport.deleteDatabase();
        dataImport.importFromFile(new File("First_names.txt"), "Firstname", "name");
        dataImport.importFromFile(new File("Last_names.txt"), "Lastname", "name");
        dataImport.importFromFile(new File("Programming_languages.txt"), "Expertise", "name");
        dataImport.importFromFile(new File("Modules.txt"), "Module", "name");
        dataImport.importFromFile(new File("Locations.txt"), "Location", "name");
        dataImport.importFromFile(new File("Charity_organizations.txt"), "Organization", "name");

        dataImport.createPersons(100);
        dataImport.createPersonRelations();
        dataImport.createOrganizationRelations();
    }

    public void importNamesSolr() throws SolrServerException, IOException {
        String absLangName = "Instrucs";
        solr.deleteNamesOfAbsLang(absLangName);

        solr.importNames("Skill", dataImport.fetchAllLabelWithRelation("Expertise"), absLangName);
        solr.importNames("Location", dataImport.fetchAllLabelWithRelation("Location"), absLangName);
        solr.importNames("Organization", dataImport.fetchAllLabelWithRelation("Organization"), absLangName);
        solr.importNames("Module", dataImport.fetchAllLabelWithRelation("Module"), absLangName);
    }

    public void importInstrucsSolr() throws IOException, SolrServerException, ParseError {
        Set<String> asts = grammar.generateAbstractSyntaxTreesFromShell();
        System.out.format("Successfully generated %d abstract syntax trees\n", asts.size());
        {
            List<Set<String>> linearizations = grammar.generateLinearizations(asts, "InstrucsEngRGL.gf", "InstrucsEngRGL");
            System.out.format("Successfully generated %d English linearizations\n", linearizations.size());
            List<Instruction> Instrucs = grammar.createInstrucs(asts, linearizations, "InstrucsEngRGL", "InstrucsSolr");
            System.out.format("Successfully instantiated %d English linearizations\n", Instrucs.size());
            solr.deleteLinearizationsOfLang("InstrucsEngRGL");
            solr.addInstrucsToSolr(Instrucs);
            System.out.format("Successfully added English linearizations to solr\n");

        }
        {
            List<Set<String>> linearizations = grammar.generateLinearizations(asts, "InstrucsSweRGL.gf", "InstrucsSweRGL");
            System.out.format("Successfully generated %d Swedish linearizations\n", linearizations.size());
            List<Instruction> Instrucs = grammar.createInstrucs(asts, linearizations, "InstrucsSweRGL", "InstrucsSolr");
            System.out.format("Successfully instantiated %d Swedish linearizations\n", Instrucs.size());
            solr.deleteLinearizationsOfLang("InstrucsSweRGL");
            solr.addInstrucsToSolr(Instrucs);
            System.out.format("Successfully added Swedish linearizations to solr\n");
        }
        {
            List<Set<String>> linearizations = grammar.generateLinearizations(asts, "InstrucsEngConcat.gf", "InstrucsEngConcat");
            System.out.format("Successfully generated %d Concat English linearizations\n", linearizations.size());
            List<Instruction> Instrucs = grammar.createInstrucs(asts, linearizations, "InstrucsEngConcat", "InstrucsSolr");
            System.out.format("Successfully instantiated %d Concat English linearizations\n", Instrucs.size());
            solr.deleteLinearizationsOfLang("InstrucsEngConcat");
            solr.addInstrucsToSolr(Instrucs);
            System.out.format("Successfully added Concat English linearizations to solr\n");
        }
        {
            List<Set<String>> linearizations = grammar.generateLinearizations(asts, "InstrucsPL.gf", "InstrucsPL");
            System.out.format("Successfully generated %d PL linearizations\n", linearizations.size());
            List<Instruction> Instrucs = grammar.createInstrucs(asts, linearizations, "InstrucsPL", "InstrucsSolr");
            System.out.format("Successfully instantiated %d PL linearizations\n", Instrucs.size());
            solr.deleteLinearizationsOfLang("InstrucsPL");
            solr.addInstrucsToSolr(Instrucs);
            System.out.format("Successfully added PL linearizations to solr\n");
        }
    }
}
