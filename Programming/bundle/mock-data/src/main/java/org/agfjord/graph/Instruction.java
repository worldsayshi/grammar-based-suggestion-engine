package org.agfjord.graph;

import org.apache.solr.client.solrj.beans.Field;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;

public class Instruction {

    @Field("id")
    int id;
    @Field("ast")
    String ast;
    @Field("linearizations")
    String[] linearizations;
    @Field("lang")
    String lang;
    Map<String, Integer> nameCounts;
    
    @Field("apiTemplate")
    private String apiTemplate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAst() {
        return ast;
    }

    public void setAst(String ast) {
        this.ast = ast;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String[] getLinearizations() {
        return linearizations;
    }

    public void setLinearizations(String[] linearizations) {
        this.linearizations = linearizations;
    }

    public Map<String, Integer> getNameCounts() {
        return nameCounts;
    }

    public void setNameCounts(Map<String, Integer> nameCounts) {
        this.nameCounts = nameCounts;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);

        ///"Instruction {" + "linearizations: " + "," "}";
    }

    /**
     * @return the apiTemplate
     */
    public String getApiTemplate() {
        return apiTemplate;
    }

    /**
     * @param apiTemplate the apiTemplate to set
     */
    public void setApiTemplate(String apiTemplate) {
        this.apiTemplate = apiTemplate;
    }
}
