package com.findwise.crescent.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({ "noNamespaceSchemaLocation", "servertime", "serverdate" })
public class TripList {
    @JsonProperty("Trip")
    private List<Trip> trip;

    public List<Trip> getTrip() {
        return trip;
    }

    public void setTrip(List<Trip> trip) {
        this.trip = trip;
    }

    @Override
    public String toString() {
        return "TripList [trip=" + trip.toString() + "]";
    }
}
