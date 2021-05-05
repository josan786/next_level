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
import ru.konstantin_starikov.samsung.izhhelper.models.CarNumber;
import ru.konstantin_starikov.samsung.izhhelper.models.Location;
import ru.konstantin_starikov.samsung.izhhelper.models.PhotosNamesCompressor;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationReport;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationStatus;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationType;

public class ViolationsDatabase implements Serializable {

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
    private static final String COLUMN_STATUS = "Status";
    private static final String COLUMN_LOCATION_LATITUDE = "locationLatitude";
    private static final String COLUMN_LOCATION_LONGITUDE = "locationLongitude";
    private static final String COLUMN_LOCATION_PLACE = "locationPlace";
    private static final String COLUMN_CAR_NUMBER_PHOTO = "CarNumberPhoto";
    private static final String COLUMN_PHOTOS = "Photos";

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
    private static final int NUM_COLUMN_STATUS = 11;
    private static final int NUM_COLUMN_LOCATION_LATITUDE = 12;
    private static final int NUM_COLUMN_LOCATION_LONGITUDE  = 13;
    private static final int NUM_COLUMN_LOCATION_PLACE = 14;
    private static final int NUM_COLUMN_CAR_NUMBER_PHOTO  = 15;
    private static final int NUM_COLUMN_PHOTOS = 16;

    private SQLiteDatabase database;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " STRING PRIMARY KEY," +
                    COLUMN_SENDER_ACCOUNT_ID + " TEXT," +
                    COLUMN_SENDER_ACCOUNT_FIRST_NAME + " TEXT," +
                    COLUMN_SENDER_ACCOUNT_lAST_NAME + " TEXT," +
                    COLUMN_SENDER_ACCOUNT_PHONE_NUMBER + " TEXT," +
                    COLUMN_SENDER_ACCOUNT_ADDRESS_FLAT + " INTEGER," +
                    COLUMN_SENDER_ACCOUNT_ADDRESS_HOME + " TEXT," +
                    COLUMN_SENDER_ACCOUNT_ADDRESS_STREET + " TEXT," +
                    COLUMN_SENDER_ACCOUNT_ADDRESS_TOWN + " TEXT," +
                    COLUMN_CAR_NUMBER + " TEXT," +
                    COLUMN_TYPE + " TEXT," +
                    COLUMN_STATUS + " TEXT," +
                    COLUMN_LOCATION_LATITUDE + " REAL," +
                    COLUMN_LOCATION_LONGITUDE + " REAL," +
                    COLUMN_LOCATION_PLACE + " TEXT," +
                    COLUMN_CAR_NUMBER_PHOTO + " TEXT," +
                    COLUMN_PHOTOS + " TEXT)";

    public ViolationsDatabase(Context context)
    {
        OpenHelper openHelper = new OpenHelper(context);
        database = openHelper.getWritableDatabase();
    }

    public long insert(String senderAccountID,
                       String senderAccountFirstName,
                       String senderAccountLastName,
                       String senderAccountPhoneNumber,
                       int senderAccountAddressFlat,
                       String senderAccountAddressHome,
                       String senderAccountAddressStreet,
                       String senderAccountAddressTown,
                       String carNumber,
                       String type,
                       String status,
                       long locationLatitude,
                       long locationLongitude,
                       String locationPlace,
                       String carNumberPhoto,
                       ArrayList<String> photosNames) {
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
        contentValues.put(COLUMN_STATUS, status);
        contentValues.put(COLUMN_LOCATION_LATITUDE, locationLatitude);
        contentValues.put(COLUMN_LOCATION_LONGITUDE, locationLongitude);
        contentValues.put(COLUMN_LOCATION_PLACE, locationPlace);
        contentValues.put(COLUMN_CAR_NUMBER_PHOTO, carNumberPhoto);
        contentValues.put(COLUMN_PHOTOS, PhotosNamesCompressor.compress(photosNames));

        return database.insert(TABLE_NAME, null, contentValues);
    }

    public long insert(ViolationReport violationReport) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, violationReport.ID);
        contentValues.put(COLUMN_SENDER_ACCOUNT_ID, violationReport.senderAccount.ID);
        contentValues.put(COLUMN_SENDER_ACCOUNT_FIRST_NAME, violationReport.senderAccount.firstName);
        contentValues.put(COLUMN_SENDER_ACCOUNT_lAST_NAME, violationReport.senderAccount.lastName);
        contentValues.put(COLUMN_SENDER_ACCOUNT_PHONE_NUMBER, violationReport.senderAccount.phoneNumber);
        contentValues.put(COLUMN_SENDER_ACCOUNT_ADDRESS_FLAT, violationReport.senderAccount.address.flat);
        contentValues.put(COLUMN_SENDER_ACCOUNT_ADDRESS_HOME, violationReport.senderAccount.address.home);
        contentValues.put(COLUMN_SENDER_ACCOUNT_ADDRESS_STREET, violationReport.senderAccount.address.street);
        contentValues.put(COLUMN_SENDER_ACCOUNT_ADDRESS_TOWN, violationReport.senderAccount.address.town);
        if(violationReport.carNumber != null) contentValues.put(COLUMN_CAR_NUMBER, violationReport.carNumber.toString());
        contentValues.put(COLUMN_TYPE, violationReport.violationType.toString());
        contentValues.put(COLUMN_STATUS, violationReport.getStatus().toString());
        contentValues.put(COLUMN_LOCATION_LATITUDE, violationReport.location.getLatitude());
        contentValues.put(COLUMN_LOCATION_LONGITUDE, violationReport.location.getLongitude());
        contentValues.put(COLUMN_LOCATION_PLACE, violationReport.location.getPlace());
        if(violationReport.carNumberPhotoName != null) contentValues.put(COLUMN_CAR_NUMBER_PHOTO, violationReport.carNumberPhotoName);
        if(violationReport.photosNames != null && !violationReport.photosNames.isEmpty()) contentValues.put(COLUMN_PHOTOS, PhotosNamesCompressor.compress(violationReport.photosNames));

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
        contentValues.put(COLUMN_STATUS, violationReport.getStatus().toString());
        contentValues.put(COLUMN_LOCATION_LATITUDE, violationReport.location.getLatitude());
        contentValues.put(COLUMN_LOCATION_LONGITUDE, violationReport.location.getLongitude());
        contentValues.put(COLUMN_LOCATION_PLACE, violationReport.location.getPlace());
        contentValues.put(COLUMN_CAR_NUMBER_PHOTO, violationReport.carNumberPhotoName);
        contentValues.put(COLUMN_PHOTOS, PhotosNamesCompressor.compress(violationReport.photosNames));

        return database.update(TABLE_NAME, contentValues, COLUMN_ID + " = ?",new String[] { String.valueOf(violationReport.getID())});
    }

    public void deleteAll() {
        database.delete(TABLE_NAME, null, null);
    }

    public void delete(String id) {
        database.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
    }

    //Todo: select метод

    public ViolationReport select(String id) {
        Cursor cursor = database.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        cursor.moveToFirst();
        String senderAccountID = cursor.getString(NUM_SENDER_ACCOUNT_ID);
        String senderAccountFirstName = cursor.getString(NUM_COLUMN_SENDER_ACCOUNT_FIRST_NAME);
        String senderAccountLastName = cursor.getString(NUM_COLUMN_SENDER_ACCOUNT_lAST_NAME);
        String senderAccountPhoneNumber = cursor.getString(NUM_COLUMN_SENDER_ACCOUNT_PHONE_NUMBER);
        int senderAccountAddressFlat = cursor.getInt(NUM_COLUMN_SENDER_ACCOUNT_ADDRESS_FLAT);
        String senderAccountAddressHome = cursor.getString(NUM_COLUMN_SENDER_ACCOUNT_ADDRESS_HOME);
        String senderAccountAddressStreet = cursor.getString(NUM_COLUMN_SENDER_ACCOUNT_ADDRESS_STREET);
        String senderAccountAddressTown = cursor.getString(NUM_COLUMN_SENDER_ACCOUNT_ADDRESS_TOWN);
        String carNumber = cursor.getString(NUM_COLUMN_CAR_NUMBER);
        String type = cursor.getString(NUM_COLUMN_TYPE);
        double locationLatitude = cursor.getDouble(NUM_COLUMN_LOCATION_LATITUDE);
        double locationLongitude = cursor.getDouble(NUM_COLUMN_LOCATION_LONGITUDE);
        String locationPlace = cursor.getString(NUM_COLUMN_LOCATION_PLACE);
        String carNumberPhoto = cursor.getString(NUM_COLUMN_CAR_NUMBER_PHOTO);
        String photosNames = cursor.getString(NUM_COLUMN_PHOTOS);
        Address senderAccountAddress = new Address(senderAccountAddressHome, senderAccountAddressStreet, senderAccountAddressFlat, senderAccountAddressTown);
        Account senderAccount = new Account(senderAccountID, senderAccountFirstName, senderAccountLastName, senderAccountPhoneNumber, senderAccountAddress);
        ViolationReport violationReport = new ViolationReport(
                new Location(locationPlace, locationLatitude, locationLongitude), null, senderAccount);
        violationReport.carNumber = new CarNumber(carNumber);
        violationReport.ID = id;
        violationReport.violationType = new ViolationType(cursor.getString(NUM_COLUMN_TYPE));
        violationReport.setStatus(new ViolationStatus(cursor.getString(NUM_COLUMN_STATUS)));
        violationReport.carNumberPhotoName = carNumberPhoto;
        if(photosNames != null && !photosNames.isEmpty()) violationReport.setPhotosNames(PhotosNamesCompressor.deCompress(photosNames));
        return violationReport;
    }

    public ArrayList<ViolationReport> selectAll() {
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);

        ArrayList<ViolationReport> arrayList = new ArrayList<ViolationReport>();
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            do {
                String id = cursor.getString(NUM_COLUMN_ID);
                String senderAccountID = cursor.getString(NUM_SENDER_ACCOUNT_ID);
                String senderAccountFirstName = cursor.getString(NUM_COLUMN_SENDER_ACCOUNT_FIRST_NAME);
                String senderAccountLastName = cursor.getString(NUM_COLUMN_SENDER_ACCOUNT_lAST_NAME);
                String senderAccountPhoneNumber = cursor.getString(NUM_COLUMN_SENDER_ACCOUNT_PHONE_NUMBER);
                int senderAccountAddressFlat = cursor.getInt(NUM_COLUMN_SENDER_ACCOUNT_ADDRESS_FLAT);
                String senderAccountAddressHome = cursor.getString(NUM_COLUMN_SENDER_ACCOUNT_ADDRESS_HOME);
                String senderAccountAddressStreet = cursor.getString(NUM_COLUMN_SENDER_ACCOUNT_ADDRESS_STREET);
                String senderAccountAddressTown = cursor.getString(NUM_COLUMN_SENDER_ACCOUNT_ADDRESS_TOWN);
                String carNumber = cursor.getString(NUM_COLUMN_CAR_NUMBER);
                String type = cursor.getString(NUM_COLUMN_TYPE);
                double locationLatitude = cursor.getDouble(NUM_COLUMN_LOCATION_LATITUDE);
                double locationLongitude = cursor.getDouble(NUM_COLUMN_LOCATION_LONGITUDE);
                String locationPlace = cursor.getString(NUM_COLUMN_LOCATION_PLACE);
                String carNumberPhoto = cursor.getString(NUM_COLUMN_CAR_NUMBER_PHOTO);
                String photosNames = cursor.getString(NUM_COLUMN_PHOTOS);
                Address senderAccountAddress = new Address(senderAccountAddressHome, senderAccountAddressStreet, senderAccountAddressFlat, senderAccountAddressTown);
                Account senderAccount = new Account(senderAccountID, senderAccountFirstName, senderAccountLastName, senderAccountPhoneNumber, senderAccountAddress);
                ViolationReport violationReport = new ViolationReport(
                        new Location(locationPlace, locationLatitude, locationLongitude), null, senderAccount);
                violationReport.carNumber = new CarNumber(carNumber);
                violationReport.ID = id;
                violationReport.violationType = new ViolationType(cursor.getString(NUM_COLUMN_TYPE));
                violationReport.setStatus(new ViolationStatus(cursor.getString(NUM_COLUMN_STATUS)));
                violationReport.carNumberPhotoName = carNumberPhoto;
                if(photosNames != null && !photosNames.isEmpty()) violationReport.setPhotosNames(PhotosNamesCompressor.deCompress(photosNames));
                arrayList.add(violationReport);
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
