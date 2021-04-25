package ru.konstantin_starikov.samsung.izhhelper.models;

import android.graphics.drawable.Drawable;

public class PhotoDescription {
    private String description;
    private Drawable viewpointDrawable;

    public PhotoDescription(String description, Drawable viewpointDrawable) {
        this.description = description;
        this.viewpointDrawable = viewpointDrawable;
    }

    public String getDescription() {
        return description;
    }

    public Drawable getViewpointDrawable() {
        return viewpointDrawable;
    }
}
