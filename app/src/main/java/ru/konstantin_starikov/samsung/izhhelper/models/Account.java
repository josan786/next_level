package ru.konstantin_starikov.samsung.izhhelper.models;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class Account implements Serializable {
    public String ID;
    public String firstName;
    public String lastName;
    public String phoneNumber;
    public Address address;
    public String email;
    private String avatarPath;
    private ArrayList<ViolationReport> violationReports = new ArrayList<ViolationReport>();

    public Account()
    {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null) ID = currentUser.getUid();
        email = null;
        avatarPath = null;
        address = new Address();
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

    public void CreateViolationReport(long ID)
    {
        violationReports.add(new ViolationReport());
    }

    public boolean SendViolationReport(String ID, Context context)
    {
        FindViolationReportByID(ID).submitViolationToAuthorizedBody(context);
        return true;
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

    public void retrieveUserDataFromFirebase(Action dataReceivedAction, Action dataIsEmptyAction)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users accounts").child(ID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if(!snapshot.hasChild("FirstName") || !snapshot.hasChild("LastName")
                    || !snapshot.hasChild("Address"))
                    {
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
                }
                else dataIsEmptyAction.run();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dataIsEmptyAction.run();
            }
        });
    }

    public void addViolationReport(ViolationReport violationReport, Context context)
    {
        violationReports.add(violationReport);
        saveViolationReport(violationReport, context);
    }

    private void saveViolationReport(ViolationReport violationReport, Context context)
    {
        ViolationsDatabase violationsDatabase;
        violationsDatabase = new ViolationsDatabase(context);
        violationsDatabase.insert(violationReport);
    }

    public void loadViolations(Context context)
    {
        ViolationsDatabase violationsDatabase;
        violationsDatabase = new ViolationsDatabase(context);
        ArrayList<ViolationReport> allReports = violationsDatabase.selectAll();
        for (ViolationReport violationReport : allReports)
        {
            if(violationReport.senderAccount.ID.equals(this.ID)) violationReports.add(violationReport);
        }
    }

    private ViolationReport FindViolationReportByID(String ID)
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

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getAvatarPath() {
        return avatarPath;
    }
}
