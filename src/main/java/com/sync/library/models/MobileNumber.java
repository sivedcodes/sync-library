package com.sync.library.models;

public class MobileNumber {
    private String simCard;
    private String phoneNumber;

    public MobileNumber(String simCard, String phoneNumber) {
        this.simCard = simCard;
        this.phoneNumber = phoneNumber;
    }

    public String getSimCard() { return simCard; }
    public String getPhoneNumber() { return phoneNumber; }
}
