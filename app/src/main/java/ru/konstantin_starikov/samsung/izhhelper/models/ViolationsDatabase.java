package ru.konstantin_starikov.samsung.izhhelper.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class ViolationsDatabase {
    private static final String DATABASE_NAME = "violations.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "SavedViolations";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SENDER_ACCOUNT_ID = "SenderAccountID";
    private static final String COLUMN_SENDER_ACCOUNT_FIRST_NAME = "SenderAccountFirstName";
    private static final String COLUMN_SENDER_ACCOUNT_lAST_NAME = "SenderAccountLastName";
    private static final String COLUMN_SENDER_ACCOUNT_PHONE_NUMBER = "SenderAccountPhoneNumber";
    private static final String COLUMN_SENDER_ACCOUNT_ADDRESS_FLAT = "SenderAccountAddressFlat";
    private static final String COLUMN_SENDER_ACCOUNT_ADDRESS_HOME = "SenderAccountAddressHome";
    private static final String COLUMN_SENDER_ACCOUNT_ADDRESS_STREET = "SenderAccountAddressStreet";
    private static final String COLUMN_SENDER_ACCOUNT_ADDRESS_TOWN = "SenderAccountAddressTown";
    private static final String COLUMN_CAR_NUMBER = "CarNumber";
    private static final String COLUMN_TYPE = "Type";
    private static final String COLUMN_LOCATION_LATITUDE = "locationLatitude";
    private static final String COLUMN_LOCATION_LONGITUDE = "locationLongitude";
    private static final String COLUMN_LOCATION_PLACE = "locationPlace";

    private static final int NUM_COLUMN_ID = 0;
    private static final int NUM_SENDER_ACCOUNT_ID = 1;
    private static final int NUM_COLUMN_SENDER_ACCOUNT_FIRST_NAME = 2;
    private static final int NUM_COLUMN_SENDER_ACCOUNT_lAST_NAME = 3;
    private static final int NUM_COLUMN_SENDER_ACCOUNT_PHONE_NUMBER = 4;
    private static final int NUM_COLUMN_SENDER_ACCOUNT_ADDRESS_FLAT= 5;
    private static final int NUM_COLUMN_SENDER_ACCOUNT_ADDRESS_HOME = 6;
    private static final int NUM_COLUMN_SENDER_ACCOUNT_ADDRESS_STREET = 7;
    private static final int NUM_COLUMN_SENDER_ACCOUNT_ADDRESS_TOWN = 8;
    private static final int NUM_COLUMN_CAR_NUMBER = 9;
    private static final int NUM_COLUMN_TYPE = 10;
    private static final int NUM_COLUMN_LOCATION_LATITUDE = 11;
    private static final int NUM_COLUMN_LOCATION_LONGITUDE  = 12;
    private static final int NUM_COLUMN_LOCATION_PLACE = 13;

    private SQLiteDatabase database;

    public ViolationsDatabase(Context context)
    {
        OpenHelper openHelper = new OpenHelper(context);
        database = openHelper.getWritableDatabase();
    }

    public long insert(int senderAccountID,
                       String senderAccountFirstName,
                       String senderAccountLastName,
                       String senderAccountPhoneNumber,
                       int senderAccountAddressFlat,
                       String senderAccountAddressHome,
                       String senderAccountAddressStreet,
                       String senderAccountAddressTown,
                       String carNumber,
                       String type,
                       long locationLatitude,
                       long locationLongitude,
                       String locationPlace) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SENDER_ACCOUNT_ID, senderAccountID);
        contentValues.put(COLUMN_SENDER_ACCOUNT_FIRST_NAME, senderAccountFirstName);
        contentValues.put(COLUMN_SENDER_ACCOUNT_lAST_NAME, senderAccountLastName);
        contentValues.put(COLUMN_SENDER_ACCOUNT_PHONE_NUMBER, senderAccountPhoneNumber);
        contentValues.put(COLUMN_SENDER_ACCOUNT_ADDRESS_FLAT, senderAccountAddressFlat);
        contentValues.put(COLUMN_SENDER_ACCOUNT_ADDRESS_HOME, senderAccountAddressHome);
        contentValues.put(COLUMN_SENDER_ACCOUNT_ADDRESS_STREET, senderAccountAddressStreet);
        contentValues.put(COLUMN_SENDER_ACCOUNT_ADDRESS_TOWN, senderAccountAddressTown);
        contentValues.put(COLUMN_CAR_NUMBER, carNumber);
        contentValues.put(COLUMN_TYPE, type);
        contentValues.put(COLUMN_LOCATION_LATITUDE, locationLatitude);
        contentValues.put(COLUMN_LOCATION_LONGITUDE, locationLongitude);
        contentValues.put(COLUMN_LOCATION_PLACE, locationPlace);


        return database.insert(TABLE_NAME, null, contentValues);
    }

    public int update(ViolationReport violationReport) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SENDER_ACCOUNT_ID, violationReport.senderAccount.ID);
        contentValues.put(COLUMN_SENDER_ACCOUNT_FIRST_NAME, violationReport.senderAccount.firstName);
        contentValues.put(COLUMN_SENDER_ACCOUNT_lAST_NAME, violationReport.senderAccount.lastName);
        contentValues.put(COLUMN_SENDER_ACCOUNT_PHONE_NUMBER, violationReport.senderAccount.phoneNumber);
        contentValues.put(COLUMN_SENDER_ACCOUNT_ADDRESS_FLAT, violationReport.senderAccount.address.flat);
        contentValues.put(COLUMN_SENDER_ACCOUNT_ADDRESS_HOME, violationReport.senderAccount.address.home);
        contentValues.put(COLUMN_SENDER_ACCOUNT_ADDRESS_STREET, violationReport.senderAccount.address.street);
        contentValues.put(COLUMN_SENDER_ACCOUNT_ADDRESS_TOWN, violationReport.senderAccount.address.town);
        contentValues.put(COLUMN_CAR_NUMBER, violationReport.carNumber.toString());
        contentValues.put(COLUMN_TYPE, violationReport.violationType.toString());
        contentValues.put(COLUMN_LOCATION_LATITUDE, violationReport.location.getLatitude());
        contentValues.put(COLUMN_LOCATION_LONGITUDE, violationReport.location.getLongitude());
        contentValues.put(COLUMN_LOCATION_PLACE, violationReport.location.getPlace());

        return database.update(TABLE_NAME, contentValues, COLUMN_ID + " = ?",new String[] { String.valueOf(violationReport.getID())});
    }

    public void deleteAll() {
        database.delete(TABLE_NAME, null, null);
    }

    public void delete(String id) {
        database.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
    }

    //Todo: select метод

    /* public Matches select(long id) {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        mCursor.moveToFirst();
        String TeamHome = mCursor.getString(NUM_COLUMN_TEAMHOME);
        String TeamGuest = mCursor.getString(NUM_COLUMN_TEAMGUAST);
        int GoalsHome = mCursor.getInt(NUM_COLUMN_GOALSHOME);
        int GoalsGuest=mCursor.getInt(NUM_COLUMN_GOALSGUEST);
        return new Matches(id, TeamHome, TeamGuest, GoalsHome,GoalsGuest);
    }
     */

    public ArrayList<ViolationReport> selectAll() {
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);

        ArrayList<ViolationReport> arrayList = new ArrayList<ViolationReport>();
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            do {
                String id = cursor.getString(NUM_COLUMN_ID);
                int senderAccountID = cursor.getInt(NUM_SENDER_ACCOUNT_ID);
                String senderAccountFirstName = cursor.getString(NUM_COLUMN_SENDER_ACCOUNT_FIRST_NAME);
                String senderAccountLastName = cursor.getString(NUM_COLUMN_SENDER_ACCOUNT_lAST_NAME);
                String senderAccountPhoneNumber = cursor.getString(NUM_COLUMN_SENDER_ACCOUNT_PHONE_NUMBER);
                int senderAccountAddressFlat = cursor.getInt(NUM_COLUMN_SENDER_ACCOUNT_ADDRESS_FLAT);
                String senderAccountAddressHome = cursor.getString(NUM_COLUMN_SENDER_ACCOUNT_ADDRESS_HOME);
                String senderAccountAddressStreet = cursor.getString(NUM_COLUMN_SENDER_ACCOUNT_ADDRESS_STREET);
                String senderAccountAddressTown = cursor.getString(NUM_COLUMN_SENDER_ACCOUNT_ADDRESS_TOWN);
                String carNumber = cursor.getString(NUM_COLUMN_CAR_NUMBER);
                String type = cursor.getString(NUM_COLUMN_TYPE);
                long locationLatitude = cursor.getLong(NUM_COLUMN_LOCATION_LATITUDE);
                long locationLongitude = cursor.getLong(NUM_COLUMN_LOCATION_LONGITUDE);
                String locationPlace = cursor.getString(NUM_COLUMN_LOCATION_PLACE);
                Address senderAccountAddress = new Address(senderAccountAddressHome, senderAccountAddressStreet, senderAccountAddressFlat, senderAccountAddressTown);
                Account senderAccount = new Account(senderAccountID, senderAccountFirstName, senderAccountLastName, senderAccountPhoneNumber, senderAccountAddress);
                ViolationReport violationReport = new ViolationReport(
                        new Location(locationPlace, locationLatitude, locationLongitude), null, senderAccount);
                violationReport.carNumber = new CarNumber(carNumber);
                violationReport.ID = id;
                //Todo: создание ViolationType по названию
                //violationReport.violationType = new ViolationType();
                arrayList.add(violationReport);
            } while (cursor.moveToNext());
        }
        return arrayList;
    }

    private class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        //Todo: onCreate метод

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        /*@Override
        public void onCreate(SQLiteDatabase db) {
            String query = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TEAMHOME+ " TEXT, " +
                    COLUMN_TEAMGUAST + " TEXT, " +
                    COLUMN_GOALSHOME + " INT,"+
                    COLUMN_GOALSGUAST+" INT);";
            db.execSQL(query);
        }
         */

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
