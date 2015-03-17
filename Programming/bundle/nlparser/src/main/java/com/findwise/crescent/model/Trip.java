package com.findwise.crescent.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Trip {
    @JsonProperty("Leg")
    private List<Leg> legList;

    @JsonProperty("TripTime")
    private String tripTime;
    
    @JsonProperty("transfers")
    private String transfers;
    
    @Override
    public String toString() {
        return "\nTrip [legList=" + getLegList() + "]";
    }

    /**
     * @param tripTime the tripTime to set
     */
    public void setTripTime(String tripTime) {
        this.tripTime = tripTime;
    }

    /**
     * @param transfers the transfers to set
     */
    public void setTransfers(String transfers) {
        this.transfers = transfers;
    }

    /**
     * @return the legList
     */
    public List<Leg> getLegList() {
        return legList;
    }
}
