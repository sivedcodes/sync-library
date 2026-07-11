package com.sync.library.models;

import java.util.Objects;

public class MobileNumber {
    private String simCard;
    private String phoneNumber;

    public MobileNumber(String simCard, String phoneNumber) {
        this.simCard = simCard;
        this.phoneNumber = phoneNumber;
    }

    public String getSimCard() { return simCard; }
    public String getPhoneNumber() { return phoneNumber; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MobileNumber that = (MobileNumber) o;
        return Objects.equals(simCard, that.simCard)
                && Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(simCard, phoneNumber);
    }

    @Override
    public String toString() {
        return "MobileNumber{simCard='" + simCard + "', phoneNumber='" + phoneNumber + "'}";
    }
}
