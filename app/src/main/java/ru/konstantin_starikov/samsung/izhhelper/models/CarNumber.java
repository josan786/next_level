package ru.konstantin_starikov.samsung.izhhelper.models;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.Serializable;

public class CarNumber implements Serializable {
    private String series;
    private String registrationNumber;
    private int region;
    private String country;

    @SuppressLint("LongLogTag")
    public CarNumber(String plate) {
        if(plate.length() < 6) return;
        //Серия номера машины - это первый, пятый и шестой символы (это  буквы)
        this.series = "";
        this.series += plate.charAt(0);
        this.series += plate.charAt(4);
        this.series += plate.charAt(5);
        normalizeSeries();
        //Регистрационный номер машины - второй, третий и четвёртый символы (это цифры)
        this.registrationNumber = "";
        this.registrationNumber += plate.charAt(1);
        this.registrationNumber += plate.charAt(2);
        this.registrationNumber += plate.charAt(3);
        if(plate.length() == 8)
        {
            try {
                region = Integer.parseInt(Character.toString(plate.charAt(6))) * 10 + Integer.parseInt(Character.toString(plate.charAt(7)));
            }
            catch (NumberFormatException exception)
            {
                Log.e("Car number region parsing exception", exception.getMessage());
            }
        }
        if(plate.length() == 9)
        {
            try {
                region = Integer.parseInt(Character.toString(plate.charAt(6))) * 100 + Integer.parseInt(Character.toString(plate.charAt(7))) * 10 + Integer.parseInt(Character.toString(plate.charAt(6)));
            }
            catch (NumberFormatException exception)
            {
                Log.e("Car number region parsing exception", exception.getMessage());
            }
        }
    }

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

    private void normalizeSeries()
    {
        if(getSeries().length() < 3) return;
        String result = "";
        for(int i = 0; i < getSeries().length(); i++)
        {
            result += normaliseRecognizedSymbol(getSeries().charAt(i));
        }
        series = result;
    }

    private char normaliseRecognizedSymbol(char symbol)
    {
        char result = symbol;
        switch (symbol)
        {
            case '0':
                result = 'O';
                break;
            case 'e':
            case 'E':
                result = 'Е';
                break;
            case 'm':
            case 'M':
                result = 'М';
                break;
            case 't':
            case 'T':
                result = 'Т';
                break;
            case 'y':
            case 'Y':
                result = 'У';
                break;
            case 'o':
            case 'O':
                result = 'О';
                break;
            case 'p':
            case 'P':
                result = 'Р';
                break;
            case 'a':
            case 'A':
                result = 'А';
                break;
            case 'h':
            case 'H':
                result = 'Н';
                break;
            case 'k':
            case 'K':
                result = 'К';
                break;
            case 'x':
            case 'X':
                result = 'Х';
                break;
            case 'c':
            case 'C':
                result = 'С';
                break;
            case 'b':
            case 'B':
                result = 'В';
                break;
        }
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
