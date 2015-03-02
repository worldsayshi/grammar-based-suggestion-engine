package com.findwise.crescent.model;

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
        return "StopLocation{" + "id=" + id + '}';
    }

}
