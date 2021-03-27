package ru.konstantin_starikov.samsung.izhhelper.models;

import java.io.Serializable;

public class Location implements Serializable {
    private String place;
    private double latitude;
    private double longitude;

    public Location(String place) {
        this.place = place;
    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(String place, double latitude, double longitude) {
        this.place = place;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPlace() {
        return place;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
