package com.findwise.crescent.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Trip {
    @JsonProperty("Leg")
    private List<Leg> legList;

    @Override
    public String toString() {
        return "\nTrip [legList=" + legList + "]";
    }
}
