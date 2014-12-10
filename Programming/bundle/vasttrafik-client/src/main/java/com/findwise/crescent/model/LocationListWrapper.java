package com.findwise.crescent.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationListWrapper {
    @JsonProperty("LocationList")
    private LocationList locationList;

    public LocationList getLocationList() {
        return locationList;
    }

    public void setLocationList(LocationList locationList) {
        this.locationList = locationList;
    }

}
