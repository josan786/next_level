package ru.konstantin_starikov.samsung.izhhelper.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.activities.MainMenuActivity;
import ru.konstantin_starikov.samsung.izhhelper.activities.PlaceChoiceActivity;
import ru.konstantin_starikov.samsung.izhhelper.models.Account;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationReport;
import ru.konstantin_starikov.samsung.izhhelper.models.adapters.ViolationPhotosSliderAdapter;
import ru.konstantin_starikov.samsung.izhhelper.models.adapters.ViolationReportsListAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsersViolationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersViolationsFragment extends Fragment {

    public final static String VIOLATION_REPORT = "violation_report";
    private static final String ARG_ACCOUNT = "account";

    private ListView violationReportsList;
    private Account account;
    private LinearLayout bottomViolationSheet;
    private BottomSheetBehavior bottomSheetBehavior;

    public UsersViolationsFragment() {
        // Required empty public constructor
    }

    public static UsersViolationsFragment newInstance(Account account) {
        UsersViolationsFragment fragment = new UsersViolationsFragment();
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

    private void fillViolationReports(ArrayList<ViolationReport> violationReports)
    {
        ViolationReportsListAdapter violationReportsListAdapter = new ViolationReportsListAdapter(getContext(), R.layout.violation_report_item, violationReports);
        violationReportsList.setAdapter(violationReportsListAdapter);
        violationReportsList.setOnItemClickListener(new ViolationItemClickListener());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_users_violations, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().invalidateOptionsMenu();
        violationReportsList = view.findViewById(R.id.violationReports);
        fillViolationReports(account.getViolationReports());
        bottomViolationSheet = getActivity().findViewById(R.id.bottomViolationSheetMainMenu);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomViolationSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    class ViolationItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ArrayList<ViolationReport> violationReports = account.getViolationReports();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
            TextView violationType = getActivity().findViewById(R.id.violationTypeSheet);
            violationType.setText(violationReports.get(position).violationType.toString(getContext()));
            TextView violationStatus = getActivity().findViewById(R.id.violationStatusSheet);
            violationStatus.setText(violationReports.get(position).getStatus().toString(getContext()));
            TextView violationAddress = getActivity().findViewById(R.id.violationAddressSheet);
            violationAddress.setText(violationReports.get(position).location.getPlace());
            TextView violationCarNumber = getActivity().findViewById(R.id.violationCarNumberSheet);
            violationCarNumber.setText(violationReports.get(position).carNumber.toString());

            SliderView sliderView = getActivity().findViewById(R.id.violationPhotosSlider);
            ViolationPhotosSliderAdapter sliderAdapter = new ViolationPhotosSliderAdapter(getContext());
            for(String photo : violationReports.get(position).photosNames) sliderAdapter.addItem(photo);
            sliderView.setSliderAdapter(sliderAdapter);
        }
    }

    public void createViolationReport(View v)
    {
        ViolationReport violationReport = new ViolationReport();
        violationReport.senderAccount = account;
        Intent choosePlaceIntent = new Intent(getActivity(), PlaceChoiceActivity.class);
        choosePlaceIntent.putExtra(VIOLATION_REPORT, violationReport);
        startActivity(choosePlaceIntent);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}