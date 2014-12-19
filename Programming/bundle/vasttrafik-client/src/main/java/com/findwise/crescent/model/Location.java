package com.findwise.crescent.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Location {
	private String name;
	@JsonProperty("lon")
	private double longitude;
	@JsonProperty("lat")
	private double latitude;
	@JsonProperty("idx")
	private int idx;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

}
