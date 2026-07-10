package com.sync.library.models;

public class PublicIP {
    private String ipAddress;

    public PublicIP(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() { return ipAddress; }
}
