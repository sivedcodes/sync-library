package com.sync.library.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SyncResult {
    private final HashMap<String, Integer> uploaded;
    private final HashMap<String, Integer> skipped;
    private final long syncTime;

    public SyncResult(long syncTime) {
        this.uploaded = new HashMap<>();
        this.skipped = new HashMap<>();
        this.syncTime = syncTime;
    }

    public void addUploaded(String collection, int count) {
        uploaded.put(collection, count);
    }

    public void addSkipped(String collection, int count) {
        skipped.put(collection, count);
    }

    public Map<String, Integer> getUploaded() { return uploaded; }
    public Map<String, Integer> getSkipped() { return skipped; }
    public long getSyncTime() { return syncTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SyncResult that = (SyncResult) o;
        return syncTime == that.syncTime && Objects.equals(uploaded, that.uploaded)
                && Objects.equals(skipped, that.skipped);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uploaded, skipped, syncTime);
    }

    @Override
    public String toString() {
        return "SyncResult{uploaded=" + uploaded + ", skipped=" + skipped + ", syncTime=" + syncTime + "}";
    }
}
