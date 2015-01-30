package com.findwise.crescent.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Leg {
    private String name;
    private String sname;
    private String type;
    private String id;
    private String direction;
    private String accessibility;
    private boolean reachable;

    @JsonProperty("Origin")
    private LegItem origin;

    @JsonProperty("Destination")
    private LegItem destination;

    @JsonProperty("JourneyDetailRef")
    private JourneyDetailRef journeyDetailRef;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(String accessibility) {
        this.accessibility = accessibility;
    }

    public boolean isReachable() {
        return reachable;
    }

    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }

    public LegItem getOrigin() {
        return origin;
    }

    public void setOrigin(LegItem origin) {
        this.origin = origin;
    }

    public LegItem getDestination() {
        return destination;
    }

    public void setDestination(LegItem destination) {
        this.destination = destination;
    }

    public JourneyDetailRef getJourneyDetailRef() {
        return journeyDetailRef;
    }

    public void setJourneyDetailRef(JourneyDetailRef journeyDetailRef) {
        this.journeyDetailRef = journeyDetailRef;
    }

    @Override
    public String toString() {
        return "Leg [name=" + name + ", sname=" + sname + ", type=" + type
                + ", id=" + id + ", direction=" + direction
                + ", accessibility=" + accessibility + ", reachable="
                + reachable + ", origin=" + origin + ", destination="
                + destination + ", journeyDetailRef=" + journeyDetailRef + "]";
    }

}
