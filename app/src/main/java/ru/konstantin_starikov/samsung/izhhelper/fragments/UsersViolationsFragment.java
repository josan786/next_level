package ru.konstantin_starikov.samsung.izhhelper.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationReport;
import ru.konstantin_starikov.samsung.izhhelper.models.adapters.ViolationReportsListAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsersViolationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersViolationsFragment extends Fragment {

    private static final String ARG_VIOLATIONS_REPORTS = "violationsReports";

    private ListView violationReportsList;
    private  ArrayList<ViolationReport> violationReports;
    private LinearLayout bottomViolationSheet;
    private BottomSheetBehavior bottomSheetBehavior;

    public UsersViolationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UsersViolationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UsersViolationsFragment newInstance(ArrayList<ViolationReport> violationReports) {
        UsersViolationsFragment fragment = new UsersViolationsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_VIOLATIONS_REPORTS, violationReports);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            violationReports = (ArrayList<ViolationReport>) getArguments().getSerializable(ARG_VIOLATIONS_REPORTS);
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
        violationReportsList = view.findViewById(R.id.violationReports);
        fillViolationReports(violationReports);
        bottomViolationSheet = getActivity().findViewById(R.id.bottomViolationSheetMainMenu);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomViolationSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    class ViolationItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
            TextView violationType = getActivity().findViewById(R.id.violationTypeSheet);
            violationType.setText(violationReports.get(position).violationType.toString(getContext()));
            TextView violationStatus = getActivity().findViewById(R.id.violationStatusSheet);
            violationStatus.setText(violationReports.get(position).getStatus().toString(getContext()));
            TextView violationAddress = getActivity().findViewById(R.id.violationAddressSheet);
            violationAddress.setText(violationReports.get(position).location.getPlace());
            TextView violationCarNumber = getActivity().findViewById(R.id.violationCarNumberSheet);
            violationCarNumber.setText(violationReports.get(position).carNumber.toString());
        }
    }
}