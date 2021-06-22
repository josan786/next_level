package ru.konstantin_starikov.samsung.izhhelper.models;

import android.graphics.drawable.Drawable;

public class Achievement {
    public String name;
    public Drawable colorIcon;
    public Drawable wbIcon; // white and black icon
    public boolean userHas;
    public int scoreValue;
    public String description;

    public Achievement(String name, Drawable colorIcon, Drawable wbIcon, boolean userHas) {
        this.name = name;
        this.colorIcon = colorIcon;
        this.wbIcon = wbIcon;
        this.userHas = userHas;
    }

    public Achievement(String name, Drawable colorIcon, Drawable wbIcon, boolean userHas, int scoreValue, String description) {
        this.name = name;
        this.colorIcon = colorIcon;
        this.wbIcon = wbIcon;
        this.userHas = userHas;
        this.scoreValue = scoreValue;
        this.description = description;
    }
}
