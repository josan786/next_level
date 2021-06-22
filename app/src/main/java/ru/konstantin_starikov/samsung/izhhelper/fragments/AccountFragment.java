package ru.konstantin_starikov.samsung.izhhelper.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.activities.SplashScreenActivity;
import ru.konstantin_starikov.samsung.izhhelper.models.Account;
import ru.konstantin_starikov.samsung.izhhelper.models.Helper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    public final static String USER_ACCOUNT = "user_account";
    private static final String ARG_ACCOUNT = "account";

    private Account account;

    private ImageView avatarImageView;
    private ListView capabilitiesListView;
    private Menu actionBarMenu;

    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance(Account account) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ACCOUNT, account);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            account = (Account)  getArguments().getSerializable(ARG_ACCOUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().invalidateOptionsMenu();
        tuneActionBar();
        avatarImageView = view.findViewById(R.id.avatar);
        if(account.getAvatarFilename() != null)
            setUserAvatar();
        ((TextView) view.findViewById(R.id.userName)).setText(account.firstName);
        ((TextView) view.findViewById(R.id.userID)).setText(account.ID);
    }

    private void setUserAvatar()
    {
        String avatarPath = Helper.getFullPathFromDataDirectory(account.getAvatarFilename(), getContext());
        avatarImageView.setImageDrawable(Drawable.createFromPath(avatarPath));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        actionBarMenu = menu;
        getActivity().getMenuInflater().inflate(R.menu.top_app_bar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.signOut:
            {
                signOut();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void tuneActionBar()
    {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    private void signOut()
    {
        FirebaseAuth firebaseAuth;
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null)
                    reloadApplication();
            }
        };
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);
        firebaseAuth.signOut();
    }

    private void reloadApplication()
    {
        Intent intent = new Intent(getActivity(), SplashScreenActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}