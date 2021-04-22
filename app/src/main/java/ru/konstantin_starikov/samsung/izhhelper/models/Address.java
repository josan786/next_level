package ru.konstantin_starikov.samsung.izhhelper.models;

import java.io.Serializable;

public class Address implements Serializable {
    public String home;
    public String street;
    public int flat;
    public String town;

    public Address() {
        flat = 0;
    }

    public Address(String home, String street, int flat, String town) {
        this.home = home;
        this.street = street;
        this.flat = flat;
        this.town = town;
    }
}
