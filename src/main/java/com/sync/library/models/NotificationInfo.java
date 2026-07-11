package com.sync.library.models;

import java.util.Objects;

public class NotificationInfo {
    private String packageName;
    private String title;
    private String text;
    private long postedTime;
    private String channelId;

    public NotificationInfo(String packageName, String title, String text, long postedTime, String channelId) {
        this.packageName = packageName;
        this.title = title;
        this.text = text;
        this.postedTime = postedTime;
        this.channelId = channelId;
    }

    public String getPackageName() { return packageName; }
    public String getTitle() { return title; }
    public String getText() { return text; }
    public long getPostedTime() { return postedTime; }
    public String getChannelId() { return channelId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationInfo that = (NotificationInfo) o;
        return postedTime == that.postedTime && Objects.equals(packageName, that.packageName)
                && Objects.equals(title, that.title) && Objects.equals(text, that.text)
                && Objects.equals(channelId, that.channelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageName, title, text, postedTime, channelId);
    }

    @Override
    public String toString() {
        return "NotificationInfo{packageName='" + packageName + "', title='" + title
                + "', text='" + text + "', postedTime=" + postedTime + ", channelId='" + channelId + "'}";
    }
}
