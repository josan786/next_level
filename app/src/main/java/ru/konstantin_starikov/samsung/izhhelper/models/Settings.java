package ru.konstantin_starikov.samsung.izhhelper.models;

import android.content.Context;
import android.os.Build;

import java.util.Locale;

import ru.konstantin_starikov.samsung.izhhelper.models.databases.SettingsDatabase;

public class Settings {
    private static Settings settings;
    private static String DEFAULT_LANGUAGE = "ru";

    private Locale locale;

    private Settings(Locale locale) {
        this.locale = locale;
    }

    public static Settings getInstance()
    {
        if (settings == null) settings = new Settings(new Locale(DEFAULT_LANGUAGE));
        return settings;
    }

    public void save(Context context)
    {
        SettingsDatabase settingsDatabase;
        settingsDatabase = new SettingsDatabase(context);
        if(settingsDatabase.isEmpty()) settingsDatabase.insert(this);
        else settingsDatabase.update(this);
    }

    public void loadFromPhone(Context context)
    {
        SettingsDatabase settingsDatabase;
        settingsDatabase = new SettingsDatabase(context);
        if(!settingsDatabase.isEmpty()) locale = settingsDatabase.select(1).getLocale();
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
