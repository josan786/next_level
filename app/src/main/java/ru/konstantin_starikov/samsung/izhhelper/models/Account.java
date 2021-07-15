package ru.konstantin_starikov.samsung.izhhelper.models;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import ru.konstantin_starikov.samsung.izhhelper.models.databases.UsersDatabase;
import ru.konstantin_starikov.samsung.izhhelper.models.databases.ViolationsDatabase;
import ru.konstantin_starikov.samsung.izhhelper.models.enumerators.ViolationStatusEnum;
import ru.konstantin_starikov.samsung.izhhelper.models.interfaces.Action;

public class Account implements Serializable {
    public String ID;
    public String firstName;
    public String lastName;
    public String phoneNumber;
    public Address address;
    public String email;
    private String avatarFilename;
    private String avatarURL;
    private ArrayList<ViolationReport> violationReports = new ArrayList<ViolationReport>();
    private ArrayList<Achievement> achievements;
    private int score;

    public Account()
    {
        violationReports = new ArrayList<ViolationReport>();
        achievements = new ArrayList<Achievement>();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null) ID = currentUser.getUid();
        email = null;
        avatarFilename = null;
        address = new Address();
        score = 0;
    }

    public Account(FirebaseUser firebaseUser)
    {
        this();
        this.phoneNumber = firebaseUser.getPhoneNumber();
        this.email = firebaseUser.getEmail();
    }

    public Account(String ID, String firstName, String lastName, String phoneNumber, Address address)
    {
        this();
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public void createViolationReport(long ID)
    {
        violationReports.add(new ViolationReport());
    }

    public boolean sendViolationReport(String ID, Context context)
    {
        findViolationReportByID(ID).submitViolationToAuthorizedBodyAndDoAction(context, new Action() {
            @Override
            public void run() {

            }
        });
        return true;
    }

    public void sendNotSentViolations(Context context)
    {
        for(ViolationReport violationReport : violationReports)
        {
            if(violationReport.getStatus().getViolationStatusEnum() == ViolationStatusEnum.Saved)
            {
                violationReport.submitViolationToAuthorizedBodyAndDoAction(context, new Action() {
                    @Override
                    public void run() {

                    }
                });
            }
        }
    }

    public void updateUserData(Context context)
    {
        UsersDatabase usersDatabase;
        usersDatabase = new UsersDatabase(context);
        usersDatabase.update(this);
    }

    public void saveAccount(Context context)
    {
        UsersDatabase usersDatabase;
        usersDatabase = new UsersDatabase(context);
        usersDatabase.insert(this);
    }

    public boolean isHasData()
    {
        if(firstName == null || lastName == null)
            return false;
        return true;
    }

    public boolean isUserHasDataInDatabase(Context context)
    {
        UsersDatabase usersDatabase;
        usersDatabase = new UsersDatabase(context);
        Account accountDataInDatabase = usersDatabase.select(ID);
        if(accountDataInDatabase == null) return false;
        return true;
    }

    public void updateUserDataOnFirebase()
    {
        FirebaseDatabase firebaseDatabase;
        DatabaseReference databaseReference;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users accounts").child(ID).child("FirstName");
        databaseReference.setValue(firstName);
        databaseReference = firebaseDatabase.getReference("Users accounts").child(ID).child("LastName");
        databaseReference.setValue(lastName);
        databaseReference = firebaseDatabase.getReference("Users accounts").child(ID).child("Address").child("Flat");
        databaseReference.setValue(address.flat);
        databaseReference = firebaseDatabase.getReference("Users accounts").child(ID).child("Address").child("Home");
        databaseReference.setValue(address.home);
        databaseReference = firebaseDatabase.getReference("Users accounts").child(ID).child("Address").child("Street");
        databaseReference.setValue(address.street);
        databaseReference = firebaseDatabase.getReference("Users accounts").child(ID).child("Address").child("Town");
        databaseReference.setValue(address.town);
    }

    public void uploadUserAvatarOnFirebase(Context context)
    {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference;
        String avatarStorageURL = "gs://izh-helper.appspot.com/" + ID + "/avatar";
        if (Helper.isOnline(context)) {
            storageReference = firebaseStorage.getReferenceFromUrl(avatarStorageURL);
            File file = new File(Helper.getFullPathFromDataDirectory(avatarFilename, context));
            UploadTask uploadTask = storageReference.putFile(Uri.fromFile(file));
        }
    }

    public void doIfUserPhoneAndPasswordRight(String phoneNumber, String password, Action rightAction, Action falseAction)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users accounts").child(ID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String realPhoneNumber = snapshot.child("Phone number").getValue(String.class);
                    String realPassword = snapshot.child("Password").getValue(String.class);
                    if(realPhoneNumber.equals(phoneNumber) && realPassword.equals(password))
                        rightAction.run();
                    else falseAction.run();
                }
                else falseAction.run();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                falseAction.run();
            }
        });
    }

    public void setUserPhone(String phone)
    {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference;
        databaseReference = firebaseDatabase.getReference("Users accounts").child(ID).child("Phone number");
        databaseReference.setValue(phone);
    }

    public void setUserPassword(String password)
    {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference;
        databaseReference = firebaseDatabase.getReference("Users accounts").child(ID).child("Password");
        databaseReference.setValue(password);
    }

    public void retrieveUserDataFromFirebase(Action dataReceivedAction, Action dataIsEmptyAction, Activity activity)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users accounts").child(ID);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!activity.isFinishing()) {
                    if (snapshot.exists()) {
                        if (!snapshot.hasChild("FirstName") || !snapshot.hasChild("LastName")
                                || !snapshot.hasChild("Address")) {
                            dataIsEmptyAction.run();
                            return;
                        }
                        firstName = snapshot.child("FirstName").getValue(String.class);
                        lastName = snapshot.child("LastName").getValue(String.class);
                        Log.i("DataRetrieved", firstName);
                        Log.i("DataRetrieved", lastName);
                        address.flat = snapshot.child("Address").child("Flat").getValue(Integer.class);
                        address.home = snapshot.child("Address").child("Home").getValue(String.class);
                        address.street = snapshot.child("Address").child("Street").getValue(String.class);
                        address.town = snapshot.child("Address").child("Town").getValue(String.class);
                        dataReceivedAction.run();
                    } else dataIsEmptyAction.run();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.addValueEventListener(eventListener);
    }

    public void addViolationReport(ViolationReport violationReport, Context context)
    {
        violationReports.add(violationReport);
        saveViolationReport(violationReport, context);
    }

    public void updateViolationReport(ViolationReport violationReport, Context context)
    {
        ViolationsDatabase violationsDatabase;
        violationsDatabase = new ViolationsDatabase(context);
        violationsDatabase.update(violationReport);
    }

    private void saveViolationReport(ViolationReport violationReport, Context context)
    {
        ViolationsDatabase violationsDatabase;
        violationsDatabase = new ViolationsDatabase(context);
        violationsDatabase.insert(violationReport);
    }

    public boolean isViolationReportInSQLDatabase(String ID, Context context)
    {
        boolean result = false;
        ViolationsDatabase violationsDatabase;
        violationsDatabase = new ViolationsDatabase(context);
        result = violationsDatabase.hasViolation(ID);
        return result;
    }

    public void loadViolations(Context context)
    {
        ViolationsDatabase violationsDatabase;
        violationsDatabase = new ViolationsDatabase(context);
        //violationsDatabase.deleteAll();
        ArrayList<ViolationReport> allReports = violationsDatabase.selectAll();
        for (ViolationReport violationReport : allReports)
        {
            if(violationReport.senderAccount.ID.equals(this.ID)) violationReports.add(violationReport);
        }
    }

    public void loadViolationsFromFirebase(Context context)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users accounts").child(ID).child("Violations reports");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for(DataSnapshot dataViolationReport : snapshot.getChildren())
                    {
                        ViolationReport violationReport = new ViolationReport(dataViolationReport);
                        ViolationReport existViolationReport = findViolationReportByID(violationReport.ID);
                        if(existViolationReport != null)
                        {
                            existViolationReport.setStatus(violationReport.getStatus());
                            if (existViolationReport.photosNames == null || existViolationReport.photosNames.isEmpty()) {
                                existViolationReport.loadPhotosFromFirebase(context);
                            }
                        }
                        else
                        {
                            Log.i("Loading_Violation", violationReport.getID() + " " + violationReport.location.getPlace());
                            violationReport.senderAccount = Account.this;
                            violationReport.loadPhotosFromFirebase(context);
                            violationReports.add(violationReport);
                            saveViolationReport(violationReport, context);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //получает достижения пользователя с Firebase
    public void loadAchievementsFromFirebase(Context context)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users accounts").child(ID).child("Achievements");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    achievements.clear();
                    for(DataSnapshot dataViolationReport : snapshot.getChildren())
                    {
                        String achievement = dataViolationReport.getKey();
                        //Todo: Получение иконок, описания и количества баллов по названию достижения
                        achievements.add(new Achievement(achievement, true, context));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static void loadAvailableAchievementsFromFirebase()
    {

    }

    private ViolationReport findViolationReportByID(String ID)
    {
        for (ViolationReport violationReport : violationReports)
        {
            if(violationReport.getID().equals(ID)) return violationReport;
        }
        return null;
    }

    public ArrayList<ViolationReport> getViolationReports()
    {
        return violationReports;
    }

    public void setAvatarFilename(String avatarFilename) {
        this.avatarFilename = avatarFilename;
    }

    public String getAvatarFilename() {
        return avatarFilename;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public ArrayList<Achievement> getAchievements() {
        return achievements;
    }
}
