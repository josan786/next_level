package ru.konstantin_starikov.samsung.izhhelper.models.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

import ru.konstantin_starikov.samsung.izhhelper.models.Account;
import ru.konstantin_starikov.samsung.izhhelper.models.Address;

public class UsersDatabase {
    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "UsersAccounts";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FIRST_NAME = "firstName";
    private static final String COLUMN_lAST_NAME = "lastName";
    private static final String COLUMN_PHONE_NUMBER = "phoneNumber";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_ADDRESS_FLAT = "addressFlat";
    private static final String COLUMN_ADDRESS_HOME = "addressHome";
    private static final String COLUMN_ADDRESS_STREET = "addressStreet";
    private static final String COLUMN_ADDRESS_TOWN = "addressTown";
    private static final String COLUMN_AVATAR_PATH = "avatarPath";

    private static final int NUM_COLUMN_ID = 0;
    private static final int NUM_COLUMN_FIRST_NAME = 1;
    private static final int NUM_COLUMN_lAST_NAME = 2;
    private static final int NUM_COLUMN_PHONE_NUMBER = 3;
    private static final int NUM_COLUMN_EMAIL = 4;
    private static final int NUM_COLUMN_ADDRESS_FLAT = 5;
    private static final int NUM_COLUMN_ADDRESS_HOME= 6;
    private static final int NUM_COLUMN_ADDRESS_STREET = 7;
    private static final int NUM_COLUMN_ADDRESS_TOWN = 8;
    private static final int NUM_COLUMN_AVATAR_PATH = 9;

    private SQLiteDatabase database;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " STRING PRIMARY KEY," +
                    COLUMN_FIRST_NAME + " TEXT," +
                    COLUMN_lAST_NAME + " TEXT," +
                    COLUMN_PHONE_NUMBER + " TEXT," +
                    COLUMN_EMAIL + " TEXT," +
                    COLUMN_ADDRESS_FLAT + " INTEGER," +
                    COLUMN_ADDRESS_HOME + " TEXT," +
                    COLUMN_ADDRESS_STREET + " TEXT," +
                    COLUMN_ADDRESS_TOWN + " TEXT," +
                    COLUMN_AVATAR_PATH + " TEXT)";

    public UsersDatabase(Context context)
    {
        UsersDatabase.OpenHelper openHelper = new UsersDatabase.OpenHelper(context);
        database = openHelper.getWritableDatabase();
    }

    public long insert(Account userAccount) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, userAccount.ID);
        contentValues.put(COLUMN_FIRST_NAME, userAccount.firstName);
        contentValues.put(COLUMN_lAST_NAME, userAccount.lastName);
        contentValues.put(COLUMN_PHONE_NUMBER, userAccount.phoneNumber);
        contentValues.put(COLUMN_EMAIL, userAccount.email);
        contentValues.put(COLUMN_ADDRESS_FLAT, userAccount.address.flat);
        contentValues.put(COLUMN_ADDRESS_HOME, userAccount.address.home);
        contentValues.put(COLUMN_ADDRESS_STREET, userAccount.address.street);
        contentValues.put(COLUMN_ADDRESS_TOWN, userAccount.address.town);
        contentValues.put(COLUMN_AVATAR_PATH, userAccount.getAvatarPath());

        return database.insert(TABLE_NAME, null, contentValues);
    }

    public int update(Account userAccount) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, userAccount.ID);
        if (userAccount.firstName != null && !userAccount.firstName.isEmpty()) contentValues.put(COLUMN_FIRST_NAME, userAccount.firstName);
        if (userAccount.lastName != null && !userAccount.lastName.isEmpty()) contentValues.put(COLUMN_lAST_NAME, userAccount.lastName);
        if (userAccount.phoneNumber != null && !userAccount.phoneNumber.isEmpty()) contentValues.put(COLUMN_PHONE_NUMBER, userAccount.phoneNumber);
        if (userAccount.email != null && !userAccount.email.isEmpty()) contentValues.put(COLUMN_EMAIL, userAccount.email);
        contentValues.put(COLUMN_ADDRESS_FLAT, userAccount.address.flat);
        if (userAccount.address.home != null && !userAccount.address.home.isEmpty()) contentValues.put(COLUMN_ADDRESS_HOME, userAccount.address.home);
        if (userAccount.address.street != null && !userAccount.address.street.isEmpty()) contentValues.put(COLUMN_ADDRESS_STREET, userAccount.address.street);
        if (userAccount.address.town != null && !userAccount.address.town.isEmpty()) contentValues.put(COLUMN_ADDRESS_TOWN, userAccount.address.town);
        if (userAccount.getAvatarPath() != null && !userAccount.getAvatarPath().isEmpty()) contentValues.put(COLUMN_AVATAR_PATH, userAccount.getAvatarPath());

        int result = database.update(TABLE_NAME, contentValues, COLUMN_ID + " = ?",new String[] { String.valueOf(userAccount.ID)});
        Log.i("SQL_UPDATE", Integer.toString(result));
        return result;
    }

    public void deleteAll() {
        database.delete(TABLE_NAME, null, null);
    }

    public void delete(String id) {
        database.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
    }

    public Account select(String id) {
        Cursor cursor = database.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        cursor.moveToFirst();
        if(cursor.getCount() == 0) return null;
        String ID = cursor.getString(NUM_COLUMN_ID);
        String firstName = cursor.getString(NUM_COLUMN_FIRST_NAME);
        String lastName = cursor.getString(NUM_COLUMN_lAST_NAME);
        String phoneNumber = cursor.getString(NUM_COLUMN_PHONE_NUMBER);
        String email = cursor.getString(NUM_COLUMN_EMAIL);
        int addressFlat = cursor.getInt(NUM_COLUMN_ADDRESS_FLAT);
        String addressHome = cursor.getString(NUM_COLUMN_ADDRESS_HOME);
        String addressStreet = cursor.getString(NUM_COLUMN_ADDRESS_STREET);
        String addressTown = cursor.getString(NUM_COLUMN_ADDRESS_TOWN);
        String avatarPath = cursor.getString(NUM_COLUMN_AVATAR_PATH);
        Address accountAddress = new Address(addressHome, addressStreet, addressFlat, addressTown);
        Account account = new Account(ID, firstName, lastName, phoneNumber, accountAddress);
        account.email = email;
        account.setAvatarPath(avatarPath);
        return account;
    }

    public ArrayList<Account> selectAll() {
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);

        ArrayList<Account> arrayList = new ArrayList<Account>();
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            do {
                String ID = cursor.getString(NUM_COLUMN_ID);
                String firstName = cursor.getString(NUM_COLUMN_FIRST_NAME);
                String lastName = cursor.getString(NUM_COLUMN_lAST_NAME);
                String phoneNumber = cursor.getString(NUM_COLUMN_PHONE_NUMBER);
                String email = cursor.getString(NUM_COLUMN_EMAIL);
                int addressFlat = cursor.getInt(NUM_COLUMN_ADDRESS_FLAT);
                String addressHome = cursor.getString(NUM_COLUMN_ADDRESS_HOME);
                String addressStreet = cursor.getString(NUM_COLUMN_ADDRESS_STREET);
                String addressTown = cursor.getString(NUM_COLUMN_ADDRESS_TOWN);
                String avatarPath = cursor.getString(NUM_COLUMN_AVATAR_PATH);
                Address accountAddress = new Address(addressHome, addressStreet, addressFlat, addressTown);
                Account account = new Account(ID, firstName, lastName, phoneNumber, accountAddress);
                account.email = email;
                account.setAvatarPath(avatarPath);
                arrayList.add(account);
            } while (cursor.moveToNext());
        }
        return arrayList;
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
