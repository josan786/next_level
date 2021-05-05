package ru.konstantin_starikov.samsung.izhhelper.models.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

import ru.konstantin_starikov.samsung.izhhelper.models.Account;
import ru.konstantin_starikov.samsung.izhhelper.models.Address;
import ru.konstantin_starikov.samsung.izhhelper.models.Settings;

public class SettingsDatabase {
    private static final String DATABASE_NAME = "settings.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Settings";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LANGUAGE = "language";

    private static final int NUM_COLUMN_ID = 0;
    private static final int NUM_COLUMN_LANGUAGE = 1;

    private SQLiteDatabase database;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_LANGUAGE + " TEXT)";

    public SettingsDatabase(Context context)
    {
        SettingsDatabase.OpenHelper openHelper = new SettingsDatabase.OpenHelper(context);
        database = openHelper.getWritableDatabase();
    }

    public long insert(Settings settings) {
        ContentValues contentValues = new ContentValues();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            contentValues.put(COLUMN_LANGUAGE, settings.getLocale().toLanguageTag());
        }

        return database.insert(TABLE_NAME, null, contentValues);
    }

    public int update(Settings settings) {
        ContentValues contentValues = new ContentValues();
        if (settings.getLocale() != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            contentValues.put(COLUMN_LANGUAGE, settings.getLocale().toLanguageTag());
            Log.i("COLUMN_LANGUAGE", settings.getLocale().toLanguageTag());
        }
        int result = database.update(TABLE_NAME, contentValues, COLUMN_ID + " = ?",new String[] { String.valueOf(1)});
        Log.i("SQL_UPDATE", Integer.toString(result));
        return result;
    }

    public int update(Locale locale) {
        ContentValues contentValues = new ContentValues();
        if (locale != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            contentValues.put(COLUMN_LANGUAGE, locale.toLanguageTag());
            Log.i("COLUMN_LANGUAGE", locale.toLanguageTag());
        }
        int result = database.update(TABLE_NAME, contentValues, COLUMN_ID + " = ?",new String[] { String.valueOf(1)});
        Log.i("SQL_UPDATE", Integer.toString(result));
        return result;
    }

    public void deleteAll() {
        database.delete(TABLE_NAME, null, null);
    }

    public void delete(Integer id) {
        database.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
    }

    public Settings select(Integer id) {
        Cursor cursor = database.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        cursor.moveToFirst();
        if(cursor.getCount() == 0) return null;
        Settings settings = Settings.getInstance();
        settings.setLocale(new Locale(cursor.getString(NUM_COLUMN_LANGUAGE)));
        return settings;
    }

    public ArrayList<Settings> selectAll() {
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);

        ArrayList<Settings> arrayList = new ArrayList<Settings>();
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            do {
                Settings settings = Settings.getInstance();
                settings.setLocale(new Locale(cursor.getString(NUM_COLUMN_LANGUAGE)));
                arrayList.add(settings);
            } while (cursor.moveToNext());
        }
        return arrayList;
    }

    public boolean isEmpty()
    {
        boolean result = false;

        Cursor cursor = database.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{String.valueOf(1)}, null, null, null);
        if(!cursor.moveToFirst()) result = true;
        return result;
    }

    private class OpenHelper extends SQLiteOpenHelper implements Serializable {

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
