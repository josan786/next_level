package ru.konstantin_starikov.samsung.izhhelper.models;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Comparator;

import ru.konstantin_starikov.samsung.izhhelper.models.adapters.LeaderboardListAdapter;

public class Leaderboard {

    private static int SCORE_INCREMENT = 1;

    public ArrayList<Account> leaderboardAccounts;

    public Leaderboard() {
        this.leaderboardAccounts = new ArrayList<Account>();
    }

    public void fillLeaderboard(Context context, LeaderboardListAdapter leaderboardListAdapter)
    {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users accounts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot accountData : snapshot.getChildren())
                {
                    Account userAccount = new Account();

                    if(accountData.child("FirstName").exists())
                    {
                        userAccount.firstName = accountData.child("FirstName").getValue(String.class);
                    }
                    if(accountData.child("LastName").exists())
                    {
                        userAccount.lastName = accountData.child("LastName").getValue(String.class);
                    }

                    if(accountData.child("Violations reports").exists())
                    {
                        for(DataSnapshot violationReportData : accountData.child("Violations reports").getChildren())
                        {
                            if(violationReportData.child("Status").getValue(String.class).equals("Подтверждено"))
                            {
                                userAccount.setScore(userAccount.getScore() + SCORE_INCREMENT);
                            }
                        }
                    }
                    String violationReportStorageURL = "gs://izh-helper.appspot.com/";
                    userAccount.ID = accountData.getKey();
                    StorageReference storageReference = firebaseStorage.getReferenceFromUrl(violationReportStorageURL + userAccount.ID + "/avatar");
                    storageReference.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    userAccount.setAvatarURL(uri.toString());
                                    leaderboardListAdapter.notifyDataSetChanged();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    userAccount.setAvatarURL(null);
                                }
                            });
                    leaderboardAccounts.add(userAccount);
                    Log.i(userAccount.ID, userAccount.firstName + " " + userAccount.lastName + " "  + userAccount.getScore());
                }
                leaderboardListAdapter.notifyDataSetChanged();
                leaderboardListAdapter.sort(new Comparator<Account>() {
                    @Override
                    public int compare(Account account1, Account account2) {
                        return Integer.compare(account2.getScore(), account1.getScore());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
