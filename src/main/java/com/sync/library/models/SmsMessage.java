package com.sync.library.models;

import java.util.Objects;

public class SmsMessage {
    private String address;
    private String body;
    private long date;
    private String type;
    private boolean read;

    public SmsMessage(String address, String body, long date, String type, boolean read) {
        this.address = address;
        this.body = body;
        this.date = date;
        this.type = type;
        this.read = read;
    }

    public String getAddress() { return address; }
    public String getBody() { return body; }
    public long getDate() { return date; }
    public String getType() { return type; }
    public boolean isRead() { return read; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SmsMessage that = (SmsMessage) o;
        return date == that.date && read == that.read && Objects.equals(address, that.address)
                && Objects.equals(body, that.body) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, body, date, type, read);
    }

    @Override
    public String toString() {
        return "SmsMessage{address='" + address + "', body='" + body + "', date=" + date
                + ", type='" + type + "', read=" + read + "}";
    }
}
