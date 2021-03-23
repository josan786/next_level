package ru.konstantin_starikov.samsung.izhhelper.models;

public class CarNumber {
    private String series;
    private int registrationNumber;
    private int region;
    private String country;

    public CarNumber(String series, int registrationNumber) {
        this.series = series;
        this.registrationNumber = registrationNumber;
        this.region = 18;
        this.country = "ru";
    }

    public CarNumber(String series, int registrationNumber, int region) {
        this.series = series;
        this.registrationNumber = registrationNumber;
        this.region = region;
        this.country = "ru";
    }

    public CarNumber(String series, int registrationNumber, int region, String country) {
        this.series = series;
        this.registrationNumber = registrationNumber;
        this.region = region;
        this.country = country;
    }

    public String getSeries() {
        return series;
    }

    public int getRegistrationNumber() {
        return registrationNumber;
    }

    public int getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }
}
