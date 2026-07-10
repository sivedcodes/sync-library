package com.sync.library.models;

public class InstallLocation {
    private double lat;
    private double lng;
    private long time;

    public InstallLocation(double lat, double lng, long time) {
        this.lat = lat;
        this.lng = lng;
        this.time = time;
    }

    public double getLat() { return lat; }
    public double getLng() { return lng; }
    public long getTime() { return time; }
}
