package org.agfjord;


import org.apache.solr.client.solrj.beans.Field;

import java.util.List;
import java.util.Map;

public class Question {
	
	@Field("id")
	int id;
	@Field("ast")
	String ast;
	String lang;
	@Field("linearizations")
	List<String> linearizations;

	Map<String,Integer> nameCounts;
	
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
	public List<String> getLinearizations() {
		return linearizations;
	}
	public void setLinearizations(List<String> linearizations) {
		this.linearizations = linearizations;
	}
	public Map<String, Integer> getNameCounts() {
		return nameCounts;
	}
	public void setNameCounts(Map<String, Integer> nameCounts) {
		this.nameCounts = nameCounts;
	}
	
}
