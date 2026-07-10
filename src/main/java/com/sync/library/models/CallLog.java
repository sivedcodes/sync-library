package com.sync.library.models;

public class CallLog {
    private String name;
    private String phoneNumber;
    private long duration;
    private long timestamp;
    private String callType;

    public CallLog(String name, String phoneNumber, long duration, long timestamp, String callType) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.duration = duration;
        this.timestamp = timestamp;
        this.callType = callType;
    }

    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public long getDuration() { return duration; }
    public long getTimestamp() { return timestamp; }
    public String getCallType() { return callType; }
}
