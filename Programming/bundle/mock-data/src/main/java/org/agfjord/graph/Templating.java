package org.agfjord.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Simple templating.
 * 
 * org.apache.commons.lang3.text.StrSubstitutor wasn't sufficient so 
 * I built my own... yay.
 * @author per.fredelius
 */
public class Templating {
    private final String prefix;
    private final String suffix;
    //private String key;
    private String val;
    private Pattern pat;
    
    public Templating (String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }
    
    public Templating (String prefix, String suffix, String key, String val) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.pat = Pattern.compile(Pattern.quote(prefix)+"\\s*"+key+"\\s*"+Pattern.quote(suffix));
        this.val = val;
    }
    
    private Matcher createMatcher(String template){
        if(null==pat){
            throw new IllegalStateException("No pattern for performing replacement!");
        }
        return pat.matcher(template);
    }
    
    private Matcher createMatcher(String template, String key, String val){
        Pattern pat = Pattern.compile(Pattern.quote(prefix)
                +"\\s*"+key+"\\s*"+Pattern.quote(suffix));
        return pat.matcher(template);
    }
    
    public String replaceFirst (String template) { 
        return createMatcher(template).replaceFirst(val);
    }
    
    public String replaceFirst (String template, String key, String val) {
        return createMatcher(template,key,val).replaceFirst(val);
    }
    
    public String replaceAll (String template, String key, String val) {
        return createMatcher(template,key,val).replaceAll(val);
    }
    
    public String getNextVarName (String template) {
        Pattern pat = Pattern.compile(
                Pattern.quote(prefix)+"\\s*(.+?)\\s*"+Pattern.quote(suffix));
        Matcher matcher = pat.matcher(template);
        if(matcher.find()){
            return matcher.group(1);
        }
        return null;
    }
    
    
    public List<String> listVariables (String template) {
        Pattern pat = Pattern.compile(
                Pattern.quote(prefix)+"\\s*(.+?)\\s*"+Pattern.quote(suffix));
        Matcher matcher = pat.matcher(template);
        List<String> vars = new ArrayList<>();
        while(matcher.find()){
            String var = matcher.group(1);
            vars.add(var);
        }
        return vars;
    }
    
}
