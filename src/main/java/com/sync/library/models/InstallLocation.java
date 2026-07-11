package com.sync.library.models;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstallLocation that = (InstallLocation) o;
        return Double.compare(lat, that.lat) == 0
                && Double.compare(lng, that.lng) == 0
                && time == that.time;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lng, time);
    }

    @Override
    public String toString() {
        return "InstallLocation{lat=" + lat + ", lng=" + lng + ", time=" + time + "}";
    }
}
