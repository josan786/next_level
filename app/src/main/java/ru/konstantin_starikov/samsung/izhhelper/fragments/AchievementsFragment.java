package ru.konstantin_starikov.samsung.izhhelper.fragments;

import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.Account;
import ru.konstantin_starikov.samsung.izhhelper.models.Helper;
import ru.konstantin_starikov.samsung.izhhelper.models.adapters.AccountAchievementsAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AchievementsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AchievementsFragment extends Fragment {

    private static final String ARG_ACCOUNT = "account";

    private Account account;
    private RecyclerView achievementsRecycleView;

    public AchievementsFragment() {
        // Required empty public constructor
    }


    public static AchievementsFragment newInstance(Account account) {
        AchievementsFragment fragment = new AchievementsFragment();
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
        return inflater.inflate(R.layout.fragment_achievements, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        achievementsRecycleView = view.findViewById(R.id.achievementsFragment);
        int spanCount;
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int recyclerViewWidth = Helper.convertPixelsInDp(size.x, getContext()) - 30;
        spanCount = (int) Math.floor((double) recyclerViewWidth / 100);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), spanCount);
        achievementsRecycleView.setLayoutManager(layoutManager);
        AccountAchievementsAdapter achievementsAdapter = new AccountAchievementsAdapter(getContext(), account);
        achievementsRecycleView.setAdapter(achievementsAdapter);
    }
}