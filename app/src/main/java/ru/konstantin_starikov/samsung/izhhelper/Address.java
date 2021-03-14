package ru.konstantin_starikov.samsung.izhhelper;

import java.io.Serializable;

public class Address implements Serializable {
    public String home;
    public String street;
    public int flat;
    public String town;

    public Address(String home, String street, int flat, String town) {
        this.home = home;
        this.street = street;
        this.flat = flat;
        this.town = town;
    }
}
