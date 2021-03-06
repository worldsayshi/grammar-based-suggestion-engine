package com.findwise.crescent.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({ "noNamespaceSchemaLocation", "servertime", "serverdate" })
public class LocationList {
	@JsonProperty("StopLocation")
	private List<StopLocation> stopLocations;
	@JsonProperty("CoordLocation")
	private List<CoordLocation> coordLocations;

	public List<StopLocation> getStopLocations() {
		return stopLocations;
	}

	public void setStopLocations(List<StopLocation> stopLocations) {
		this.stopLocations = stopLocations;
	}

	public List<CoordLocation> getCoordLocations() {
		return coordLocations;
	}

	public void setCoordLocations(List<CoordLocation> coordLocations) {
		this.coordLocations = coordLocations;
	}

	public List<Location> getAllLocations() {
		List<Location> res = new ArrayList<>();
		if (null != stopLocations) {
			res.addAll(stopLocations);
		}
		if (null != coordLocations) {
			res.addAll(coordLocations);
		}
		return res;
	}

}
