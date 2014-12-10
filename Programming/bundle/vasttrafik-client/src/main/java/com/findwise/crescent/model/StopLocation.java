package com.findwise.crescent.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties("idx")
public class StopLocation extends Location {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "StopLocation [name=" + getName() + ", longitude="
                + getLongitude() + ", latitude=" + getLatitude() + ", id=" + id
                + "]";
    }
}
