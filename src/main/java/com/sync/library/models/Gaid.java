package com.sync.library.models;

import java.util.Objects;

public class Gaid {
    private String gaid;

    public Gaid(String gaid) {
        this.gaid = gaid;
    }

    public String getGaid() { return gaid; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gaid gaid1 = (Gaid) o;
        return Objects.equals(gaid, gaid1.gaid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gaid);
    }

    @Override
    public String toString() {
        return "Gaid{gaid='" + gaid + "'}";
    }
}
