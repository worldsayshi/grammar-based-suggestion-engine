package org.agfjord.domain;

public class Query {

	String query;
	String language;
	
	public Query(String query, String language) {
		this.query = query;
		this.language = language;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
