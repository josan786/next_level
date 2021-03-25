package ru.konstantin_starikov.samsung.izhhelper.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.storage.StorageManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.UUID;

public class ViolationReport implements Serializable {
    protected String ID;
    private ViolationStatus status;

    public String place;
    public Time departureTime;
    public Account senderAccount;
    public ViolationType violationType;
    public CarNumber carNumber;
    public String carNumberPhotoName;

    public ArrayList<String> photosNames;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    public ViolationReport()
    {
        generateID();
        status = ViolationStatus.Created;
        photosNames = new ArrayList<String>();
    }

    public ViolationReport(String place, Time departureTime, Account senderAccount, ViolationType violationType)
    {
        this();
        this.place = place;
        this.departureTime = departureTime;
        this.senderAccount = senderAccount;
        this.violationType = violationType;
    }

    public ViolationReport(String place, Time departureTime, Account senderAccount)
    {
        this();
        this.place = place;
        this.departureTime = departureTime;
        this.senderAccount = senderAccount;
    }

    public ViolationReport(String place, Time departureTime, Account senderAccount, CarNumber carNumber, ArrayList<String> photosNames)
    {
        this();
        this.place = place;
        this.departureTime = departureTime;
        this.senderAccount = senderAccount;
        this.carNumber = carNumber;
        this.photosNames = photosNames;
    }

    public ViolationReport(ViolationReport violationReport)
    {
        this.ID = violationReport.GetID();
        this.place = violationReport.place;
        this.status = violationReport.GetStatus();
        this.departureTime = violationReport.departureTime;
        this.senderAccount = violationReport.senderAccount;
        this.carNumber = violationReport.carNumber;
        this.photosNames = violationReport.photosNames;
    }

    public ViolationReport(String place, Time departureTime, Account senderAccount, ViolationType violationType, CarNumber carNumber) {
        this();
        this.place = place;
        this.departureTime = departureTime;
        this.senderAccount = senderAccount;
        this.violationType = violationType;
        this.carNumber = carNumber;
    }

    public void AddPhoto(String photoName)
    {
        photosNames.add(photoName);
    }

    public boolean SubmitViolationToAuthorizedBody(Context context)
    {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(ID);
        databaseReference = firebaseDatabase.getReference(ID).child("Place");
        databaseReference.setValue(place);
        databaseReference = firebaseDatabase.getReference(ID).child("Type");
        databaseReference.setValue(violationType.toString());
        databaseReference = firebaseDatabase.getReference(ID).child("CarNumber");
        databaseReference.setValue(carNumber.toString());

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReferenceFromUrl("gs://izh-helper.appspot.com/" + ID + "/CarNumberPhoto");
        File file = new File(context.getApplicationInfo().dataDir + File.separator + carNumberPhotoName);
        UploadTask uploadTask = storageReference.putFile(Uri.fromFile(file));
        storageReference = firebaseStorage.getReferenceFromUrl("gs://izh-helper.appspot.com/" + ID + "/Photos");
        return true;
    }

    private void generateID()
    {
        UUID uuid = UUID.randomUUID();
        ID = uuid.toString();
    }

    public String GetID()
    {
        return ID;
    }

    public ViolationStatus GetStatus()
    {
        return status;
    }

    public void SetStatus(ViolationStatus status)
    {
        this.status = status;
    }
}
