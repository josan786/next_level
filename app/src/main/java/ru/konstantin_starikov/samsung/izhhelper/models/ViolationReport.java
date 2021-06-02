package ru.konstantin_starikov.samsung.izhhelper.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import ru.konstantin_starikov.samsung.izhhelper.models.databases.ViolationsDatabase;
import ru.konstantin_starikov.samsung.izhhelper.models.enumerators.ViolationStatusEnum;

public class ViolationReport implements Serializable{
    public String ID;

    private ViolationStatus status;

    public Location location;
    public Time departureTime;
    public Account senderAccount;
    public ViolationType violationType;
    public CarNumber carNumber;
    public String carNumberPhotoName;

    public ArrayList<String> photosNames;

    FirebaseDatabase firebaseDatabase;

    private CountDownLatch countDownLatch;

    public ViolationReport()
    {
        generateID();
        status = new ViolationStatus(ViolationStatusEnum.Created);
        photosNames = new ArrayList<String>();
    }

    public ViolationReport(String ID)
    {
        setID(ID);
        status = new ViolationStatus(ViolationStatusEnum.Created);
        photosNames = new ArrayList<String>();
    }

    public ViolationReport(DataSnapshot dataSnapshot)
    {
        this();
        ID = dataSnapshot.getKey();
        if(dataSnapshot.hasChild("Location"))
        {
            DataSnapshot dataLocation = dataSnapshot.child("Location");
            Location location = new Location();
            if(dataLocation.hasChild("Latitude")) location.setLatitude(dataLocation.child("Latitude").getValue(Double.class));
            if(dataLocation.hasChild("Longitude")) location.setLongitude(dataLocation.child("Longitude").getValue(Double.class));
            if(dataLocation.hasChild("Place")) location.setPlace(dataLocation.child("Place").getValue(String.class));
            this.location = location;
        }
        if(dataSnapshot.hasChild("Status")) setStatus(new ViolationStatus(dataSnapshot.child("Status").getValue(String.class)));
        if(dataSnapshot.hasChild("Type")) violationType = new ViolationType(dataSnapshot.child("Type").getValue(String.class));
        if(dataSnapshot.hasChild("CarNumber")) carNumber = new CarNumber(dataSnapshot.child("CarNumber").getValue(String.class));
    }

    public ViolationReport(Location location, Time departureTime, Account senderAccount, ViolationType violationType)
    {
        this();
        this.location = location;
        this.departureTime = departureTime;
        this.senderAccount = senderAccount;
        this.violationType = violationType;
    }

    public ViolationReport(Location location, Time departureTime, Account senderAccount)
    {
        this();
        this.location = location;
        this.departureTime = departureTime;
        this.senderAccount = senderAccount;
    }

    public ViolationReport(Location location, Time departureTime, Account senderAccount, CarNumber carNumber, ArrayList<String> photosNames)
    {
        this();
        this.location = location;
        this.departureTime = departureTime;
        this.senderAccount = senderAccount;
        this.carNumber = carNumber;
        this.photosNames = photosNames;
    }

    public ViolationReport(ViolationReport violationReport)
    {
        this.ID = violationReport.getID();
        this.location = violationReport.location;
        this.status = violationReport.getStatus();
        this.departureTime = violationReport.departureTime;
        this.senderAccount = violationReport.senderAccount;
        this.carNumber = violationReport.carNumber;
        this.photosNames = violationReport.photosNames;
    }

    public ViolationReport(Location location, Time departureTime, Account senderAccount, ViolationType violationType, CarNumber carNumber) {
        this();
        this.location = location;
        this.departureTime = departureTime;
        this.senderAccount = senderAccount;
        this.violationType = violationType;
        this.carNumber = carNumber;
    }

    public void addPhoto(String photoName)
    {
        photosNames.add(photoName);
    }

    @SuppressLint("LongLogTag")
    public void submitViolationToAuthorizedBody(Context context)
    {
        DatabaseReference databaseReference;
        FirebaseStorage firebaseStorage;
        StorageReference storageReference;
        if (Helper.isOnline(context)) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("Users accounts").child(senderAccount.ID).child("Violations reports").child(ID).child("Location").child("Place");
            databaseReference.setValue(location.getPlace());
            databaseReference = firebaseDatabase.getReference("Users accounts").child(senderAccount.ID).child("Violations reports").child(ID).child("Location").child("Latitude");
            databaseReference.setValue(location.getLatitude());
            databaseReference = firebaseDatabase.getReference("Users accounts").child(senderAccount.ID).child("Violations reports").child(ID).child("Location").child("Longitude");
            databaseReference.setValue(location.getLongitude());
            databaseReference = firebaseDatabase.getReference("Users accounts").child(senderAccount.ID).child("Violations reports").child(ID).child("Type");
            databaseReference.setValue(violationType.toString());
            databaseReference = firebaseDatabase.getReference("Users accounts").child(senderAccount.ID).child("Violations reports").child(ID).child("CarNumber");
            databaseReference.setValue(carNumber.toString());

            firebaseStorage = FirebaseStorage.getInstance();
            String violationReportStorageURL = "gs://izh-helper.appspot.com/" + senderAccount.ID + "/Violations reports/" + ID + "/";
            storageReference = firebaseStorage.getReferenceFromUrl(violationReportStorageURL + "CarNumberPhoto");
            File file = new File(context.getApplicationInfo().dataDir + File.separator + carNumberPhotoName);
            UploadTask uploadTask = storageReference.putFile(Uri.fromFile(file));
            for(int i = 0 ; i < photosNames.size(); i++)
            {
                File photoFile = new File(context.getApplicationInfo().dataDir + File.separator + photosNames.get(i));
                storageReference = firebaseStorage.getReferenceFromUrl(violationReportStorageURL + "/Photos/" + photosNames.get(i));
                UploadTask photoUploadTask = storageReference.putFile(Uri.fromFile(photoFile));
            }
            status = new ViolationStatus(ViolationStatusEnum.Sent);
            databaseReference = firebaseDatabase.getReference("Users accounts").child(senderAccount.ID).child("Violations reports").child(ID).child("Status");
            databaseReference.setValue(status.toString());
        } else {
            status = new ViolationStatus(ViolationStatusEnum.Saved);
        }
        if(senderAccount.isViolationReportInSQLDatabase(ID, context))
        {
            senderAccount.updateViolationReport(this, context);
        }
        else {
            senderAccount.addViolationReport(this, context);
        }
        if(countDownLatch != null) countDownLatch.countDown();
    }

    public void loadPhotosFromFirebase(Context context)
    {
        FirebaseStorage firebaseStorage;
        StorageReference storageReference;
        firebaseStorage = FirebaseStorage.getInstance();
        String violationReportPhotosStorageURL = "gs://izh-helper.appspot.com/" + senderAccount.ID + "/Violations reports/" + ID + "/Photos/";
        storageReference = firebaseStorage.getReferenceFromUrl(violationReportPhotosStorageURL);
        storageReference.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            String destinationPath = String.format(context.getApplicationInfo().dataDir + File.separator + item.getName());
                            File file = new File(destinationPath);
                            FileDownloadTask downloadTask = item.getFile(file);
                            downloadTask.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    photosNames.add(item.getName());
                                    ViolationsDatabase violationsDatabase;
                                    violationsDatabase = new ViolationsDatabase(context);
                                    violationsDatabase.update(ViolationReport.this);
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void setID(String ID)
    {
        this.ID = ID;
    }

    private void generateID()
    {
        UUID uuid = UUID.randomUUID();
        ID = uuid.toString();
    }

    public String getID()
    {
        return ID;
    }

    public ViolationStatus getStatus()
    {
        return status;
    }

    public void setStatus(ViolationStatus status) {
        this.status = status;
    }

    public void setPhotosNames(List<String> photosNames) {
        this.photosNames = new ArrayList<String>(photosNames);
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }
}
