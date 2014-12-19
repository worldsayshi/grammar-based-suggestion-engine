package com.findwise.crescent.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TripListWrapper {
    @JsonProperty("TripList")
    private TripList tripList;

    public TripList getTripList() {
        return tripList;
    }

    public void setTripList(TripList tripList) {
        this.tripList = tripList;
    }

}
