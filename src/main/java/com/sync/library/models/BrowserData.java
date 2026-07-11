package com.sync.library.models;

import java.util.Objects;

public class BrowserData {
    private String title;
    private String url;
    private long visits;
    private boolean isBookmark;
    private long created;

    public BrowserData(String title, String url, long visits, boolean isBookmark, long created) {
        this.title = title;
        this.url = url;
        this.visits = visits;
        this.isBookmark = isBookmark;
        this.created = created;
    }

    public String getTitle() { return title; }
    public String getUrl() { return url; }
    public long getVisits() { return visits; }
    public boolean isBookmark() { return isBookmark; }
    public long getCreated() { return created; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrowserData that = (BrowserData) o;
        return visits == that.visits && isBookmark == that.isBookmark && created == that.created
                && Objects.equals(title, that.title) && Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, url, visits, isBookmark, created);
    }

    @Override
    public String toString() {
        return "BrowserData{title='" + title + "', url='" + url + "', visits=" + visits
                + ", isBookmark=" + isBookmark + ", created=" + created + "}";
    }
}
