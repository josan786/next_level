package ru.konstantin_starikov.samsung.izhhelper.models;

import java.io.Serializable;

public class CarNumber implements Serializable {
    private String series;
    private String registrationNumber;
    private int region;
    private String country;

    public CarNumber(String series, String registrationNumber) {
        this.series = series;
        this.registrationNumber = registrationNumber;
        this.region = 18;
        this.country = "ru";
    }

    public CarNumber(String series, String registrationNumber, int region) {
        this.series = series;
        this.registrationNumber = registrationNumber;
        this.region = region;
        this.country = "ru";
    }

    public CarNumber(String series, String registrationNumber, int region, String country) {
        this.series = series;
        this.registrationNumber = registrationNumber;
        this.region = region;
        this.country = country;
    }

    @Override
    public String toString() {
        String result = "";
        result += series.charAt(0);
        result += registrationNumber;
        result += series.substring(1,3);
        return result;
    }

    public String getSeries() {
        return series;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public int getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }
}
