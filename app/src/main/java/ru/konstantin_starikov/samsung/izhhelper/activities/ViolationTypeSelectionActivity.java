package ru.konstantin_starikov.samsung.izhhelper.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationReport;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationType;
import ru.konstantin_starikov.samsung.izhhelper.models.enumerators.ViolationTypeEnum;
import ru.konstantin_starikov.samsung.izhhelper.models.adapters.ViolationTypesListAdapter;

public class ViolationTypeSelectionActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ViolationReport violationReport;
    public final static String VIOLATION_REPORT = "violation_report";
    private ArrayList<ViolationType> violationTypes;
    private ListView violationsTypesListView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //получаем переданный violationReport (с данными местоположения пользователя)
        violationReport = getTransmittedViolationReport();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation_type_selection);

        tuneActionBar();

        violationTypes = new ArrayList<>();
        violationTypes.add(new ViolationType(ViolationTypeEnum.Lawn));
        violationTypes.add(new ViolationType(ViolationTypeEnum.PedestrianCrossing));
        violationTypes.add(new ViolationType(ViolationTypeEnum.Pavement));
        violationTypes.add(new ViolationType(ViolationTypeEnum.ParkingProhibited));
        violationTypes.add(new ViolationType(ViolationTypeEnum.StoppingProhibited));

        violationsTypesListView = findViewById(R.id.violationsTypesListView);
        ViolationTypesListAdapter violationTypesListAdapter = new ViolationTypesListAdapter(this, R.layout.violation_type_item, violationTypes);
        violationsTypesListView.setAdapter(violationTypesListAdapter);
        violationsTypesListView.setOnItemClickListener(this);
    }

    private ViolationReport getTransmittedViolationReport()
    {
        return (ViolationReport) getIntent().getSerializableExtra(MainMenuActivity.VIOLATION_REPORT);
    }

    private void tuneActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Выберете тип нарушения");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ViolationType violationType = new ViolationType(((TextView) view.findViewById(R.id.violationTypeTitle)).getText().toString());
        violationReport.violationType = violationType;
        Intent carDetectionIntent = new Intent(ViolationTypeSelectionActivity.this, CarNumberDetectionActivity.class);
        carDetectionIntent.putExtra(VIOLATION_REPORT, violationReport);
        startActivity(carDetectionIntent);
    }
}