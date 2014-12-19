package com.findwise.crescent.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({ "routeIdx", "$" })
public class LegItem {
    private String name;
    private String type;
    private String id;

    private String time;
    private String date;
    private String track;
    private String rtTime;
    private String rtDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getRtTime() {
        return rtTime;
    }

    public void setRtTime(String rtTime) {
        this.rtTime = rtTime;
    }

    public String getRtDate() {
        return rtDate;
    }

    public void setRtDate(String rtDate) {
        this.rtDate = rtDate;
    }

    @Override
    public String toString() {
        return "LegItem [name=" + name + ", type=" + type + ", id=" + id
                + ", time=" + time + ", date=" + date + ", track=" + track
                + ", rtTime=" + rtTime + ", rtDate=" + rtDate + "]";
    }

}
