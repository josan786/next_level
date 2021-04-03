package ru.konstantin_starikov.samsung.izhhelper.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ViolationReport implements Serializable {
    protected String ID;
    private ViolationStatus status;

    public Location location;
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
        if (Helper.isOnline(context)) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference(ID);
            databaseReference = firebaseDatabase.getReference(ID).child("Location").child("Place");
            databaseReference.setValue(location.getPlace());
            databaseReference = firebaseDatabase.getReference(ID).child("Location").child("Latitude");
            databaseReference.setValue(location.getLatitude());
            databaseReference = firebaseDatabase.getReference(ID).child("Location").child("Longitude");
            databaseReference.setValue(location.getLongitude());
            databaseReference = firebaseDatabase.getReference(ID).child("Type");
            databaseReference.setValue(violationType.toString());
            databaseReference = firebaseDatabase.getReference(ID).child("CarNumber");
            databaseReference.setValue(carNumber.toString());
            databaseReference = firebaseDatabase.getReference(ID).child("SenderAccount").child("ID");
            databaseReference.setValue(senderAccount.ID);
            databaseReference = firebaseDatabase.getReference(ID).child("SenderAccount").child("FirstName");
            databaseReference.setValue(senderAccount.firstName);
            databaseReference = firebaseDatabase.getReference(ID).child("SenderAccount").child("LastName");
            databaseReference.setValue(senderAccount.lastName);
            databaseReference = firebaseDatabase.getReference(ID).child("SenderAccount").child("PhoneNumber");
            databaseReference.setValue(senderAccount.phoneNumber);
            databaseReference = firebaseDatabase.getReference(ID).child("SenderAccount").child("Address").child("Flat");
            databaseReference.setValue(senderAccount.address.flat);
            databaseReference = firebaseDatabase.getReference(ID).child("SenderAccount").child("Address").child("Home");
            databaseReference.setValue(senderAccount.address.home);
            databaseReference = firebaseDatabase.getReference(ID).child("SenderAccount").child("Address").child("Street");
            databaseReference.setValue(senderAccount.address.street);
            databaseReference = firebaseDatabase.getReference(ID).child("SenderAccount").child("Address").child("Town");
            databaseReference.setValue(senderAccount.address.town);

            firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReferenceFromUrl("gs://izh-helper.appspot.com/" + ID + "/CarNumberPhoto");
            File file = new File(context.getApplicationInfo().dataDir + File.separator + carNumberPhotoName);
            UploadTask uploadTask = storageReference.putFile(Uri.fromFile(file));
            for(int i = 0 ; i < photosNames.size(); i++)
            {
                File photoFile = new File(context.getApplicationInfo().dataDir + File.separator + photosNames.get(i));
                storageReference = firebaseStorage.getReferenceFromUrl("gs://izh-helper.appspot.com/" + ID + "/Photos/" + photosNames.get(i));
                UploadTask photoUploadTask = storageReference.putFile(Uri.fromFile(photoFile));
            }
            status = ViolationStatus.Sent;
        } else {
            status = ViolationStatus.Saved;
        }
        senderAccount.addViolationReport(this, context);
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

    public void setPhotosNames(List<String> photosNames) {
        this.photosNames = new ArrayList<String>(photosNames);
    }
}
