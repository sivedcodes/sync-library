package com.sync.library.models;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CallLog callLog = (CallLog) o;
        return duration == callLog.duration && timestamp == callLog.timestamp
                && Objects.equals(name, callLog.name)
                && Objects.equals(phoneNumber, callLog.phoneNumber)
                && Objects.equals(callType, callLog.callType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phoneNumber, duration, timestamp, callType);
    }

    @Override
    public String toString() {
        return "CallLog{name='" + name + "', phoneNumber='" + phoneNumber
                + "', duration=" + duration + ", timestamp=" + timestamp
                + ", callType='" + callType + "'}";
    }
}
