package org.agfjord.graph;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;

import org.grammaticalframework.pgf.PGF;
import org.grammaticalframework.pgf.PGFError;
import java.nio.charset.StandardCharsets;
import static org.apache.lucene.analysis.standard.UAX29URLEmailTokenizer.URL;


public class Grammar {

    private final static String placeholderPrefix = "{{";
    private final static String placeholderSuffix = "}}";
    
	//private PGF gr;
    final private String[] nameFuns = new String[]{ "Station", "GeoLocation" };
	final private String[] nameCats = new String[]{ "Station", "GeoLocation" };
	//final private String[] nameFuns = new String[]{ "MkSkill", "MkLocation", "MkOrganization", "MkModule" };
	//final private String[] nameCats = new String[]{ "Skill", "Location", "Organization", "Module" };    
	//final private Properties prop = new Properties();
    final private String grammar_dir;
    final private String abs_grammar_filename;

	public Grammar(String grammar_dir,String abs_grammar_name) throws MalformedURLException, IOException {
        
        this.grammar_dir = grammar_dir;
        this.abs_grammar_filename = abs_grammar_name+".gf";
        
        /*URL url = new File(grammar_dir,abs_grammar_filename)
                .toURI().toURL();
        InputStream pgf = url.openStream();*/
		/*try {
			gr = PGF.readPGF(pgf);
		} catch (PGFError e) {
			e.printStackTrace();
		}*/
		/*try {
			prop.load(new FileInputStream("config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}

	/*
	 * Create Instrucs from generated linearizations
	 */
	public List<Instruction> createInstrucs(Set<String> asts, List<Set<String>> linearizations, String lang){
		List<Instruction> Instrucs = new ArrayList<>();
		Iterator<String> it = asts.iterator();
		for(int i=0; i < linearizations.size(); i++){
			Instruction question = new Instruction();
			question.setLinearizations(linearizations.get(i).toArray(new String[linearizations.get(i).size()]));
			String linearization = linearizations.get(i).iterator().next();
            String ast = it.next();
			Map<String,Integer> nameCounts = countTemplateVariablesByType(linearization);
			/*for(String nameCat : nameCats){
				int count = 0;
				while(linearization.contains((nameCat + count))){
					count++;
				}
				nameCounts.put(nameCat, count);
			}*/
			question.setAst(ast);
			question.setNameCounts(nameCounts);
			question.setLang(lang);
			Instrucs.add(question);
		}
		return Instrucs;
	}

	/*
	 * Generates abstract syntax trees from the GF-shell
	 */
	public Set<String> generateAbstractSyntaxTreesFromShell() throws IOException{
		//Generate trees by using the GF-shell
		List<String> commands = new ArrayList<String>();
		commands.add("import " + abs_grammar_filename);
		commands.add("gt -depth=6");
		Set<String> asts = sendGfShellCommands(commands);
        Set<String> solrPreparedAsts = new HashSet<>();
        for(String ast:asts){
            String solrPreparedAst = processNameTypes(ast);
            solrPreparedAsts.add(solrPreparedAst);
        }
		/*Set<String> result = new LinkedHashSet<String>();
		Iterator<String> it = asts.iterator();
		while(it.hasNext()){
			String ast = it.next();
			if(!hasDuplicateRelations(ast)){
				result.add(processNameTypes(ast));
			}
		}*/
		return solrPreparedAsts;
	}
    
    /*
	 * Modifies an abstract syntax tree. All names are by default named "Foo" by the gf-shell.
	 * We change each name into its type + index.
	 * E.g. Question_I Person_N (Know_R (MkSkill (MkSymb "Foo"))) ==>
	 *      Question_I Person_N (Know_R (MkSkill (MkSymb "Skill0")))
	 */
    private String processNameTypes(String ast) {
        
        Templating templ = new Templating("(", " (MkSymb \"Foo\")", null, null);
        List<String> varnames = templ.listVariables(ast);
        for(String varname : varnames){
            System.out.println(varname);
            if(varname!=null){
                ast = templ.replaceAll(ast,varname,"("+varname+" (MkSymb \""+placeholderPrefix+varname+placeholderSuffix+"\")");
            }
        }
        if(ast.contains("Foo")){
            System.out.println("Warning: ast processing was likely incomplete!");
        }
        return ast;
    }
	
	/*
	 * Determines if an abstract syntax tree has the same relation more than once
	 * E.g. Question_I Person_N (And_I (Know_R (MkSkill (MkSymb "Skill0"))) (Know_R (MkSkill (MkSymb "Skill1"))))
	 * contains Know_R two times. 
	 */
	/*private boolean hasDuplicateRelations(String ast){
		Set<String> relations = new HashSet<String>();
        Pattern regex = Pattern.compile("\\w+\\_R ");
        Matcher m = regex.matcher(ast);
        while(m.find()){
        	if(relations.contains(m.group())){
        		return true;
        	}else {
        		relations.add(m.group());
        	}
        } return false;
	}*/
	
	/*
	 * Generate linearizations from abstract syntax trees by using the GF-shell.
	 * Each ast can yield more than one linearization, so we have a list of 
	 * linearizations for each ast.
	 * 
	 * We do the linearization multiple intervals, GF wont be able to process
	 * all data otherwise.
	 */
	public List<Set<String>> generateLinearizations(Set<String> asts, String gfConcreteGrammarFile, String concreteSyntax) throws IOException{
		List<String> commands = new ArrayList<String>();
		for(String ast : asts){
			commands.add("linearize -lang=" + concreteSyntax + " -list " + ast);
		}
		int increment = 100;
		int from = 0, to = increment < asts.size() ? increment : asts.size();
		List<Set<String>> result = new ArrayList<Set<String>>();
		while(from < asts.size()){
			List<String> subCommands = commands.subList(from, to);
			subCommands.add(0, "import " + gfConcreteGrammarFile);
			Set<String> lines = sendGfShellCommands(subCommands);
			for(String line : lines) {
				Set<String> linearizations = new LinkedHashSet<String>();
                try (Scanner sc = new Scanner(line)) {
                    sc.useDelimiter(", ");
                    while(sc.hasNext()){
                        String linearization = sc.next();
                        if(!linearization.isEmpty()){
                            linearizations.add(linearization);
                        }
                    }
                }
				result.add(linearizations);
			}
			System.out.println("Added " + from + " to " + to); 
			to = to + increment < asts.size() ? to + increment : asts.size();
			from += increment ;

		}
		return result;
	}

	/*
	 * Method to interact with the gf-shell
	 */
	private Set<String> sendGfShellCommands(List<String> commands) throws IOException{
		// Run 'gf --run' in the correct directory
		ProcessBuilder pb = new ProcessBuilder("gf", "-literal=Symb", "--run");
		
		pb.directory(new File(grammar_dir));
		Process p = pb.start();
        try (OutputStream outputStream = p.getOutputStream()) {
            IOUtils.writeLines(commands, "\n", outputStream, StandardCharsets.UTF_8);
        }
        LinkedHashSet result = new LinkedHashSet();
        try (InputStream inputStream = p.getInputStream()) {
            List<String> lines = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
            
            result = new LinkedHashSet();
            for(String line:lines){
                if(!line.isEmpty()){
                    // -- System.out.println(line);
                    result.add(line);
                }
            }
        }
        return result;
	}

    Templating defTempl = new Templating(placeholderPrefix, placeholderSuffix);
    
    private Map<String, Integer> countTemplateVariablesByType(String ast) {
        Map<String, Integer> counts = new HashMap<>();
        List<String> variables = defTempl.listVariables(ast);
        for (String variable : variables) {
            if(counts.containsKey(variable)){
                counts.put(variable,counts.get(variable)+1);
            } else {
                counts.put(variable,1);
            }
        }
        return counts;
    }
}
