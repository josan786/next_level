package ru.konstantin_starikov.samsung.izhhelper.models;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import ru.konstantin_starikov.samsung.izhhelper.R;

public class ViolationReportsListAdapter extends ArrayAdapter<ViolationReport> {
    List<ViolationReport> violationReportsList;

    Context context;

    int resource;

    public ViolationReportsListAdapter(Context context, int resource, List<ViolationReport> violationReportsList) {
        super(context, resource, violationReportsList);
        this.context = context;
        this.resource = resource;
        this.violationReportsList = violationReportsList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(resource, null, false);

        TextView violationReportTitle = view.findViewById(R.id.violationTypeTitle);
        TextView violationReportPlace = view.findViewById(R.id.violationReportPlace);
        TextView violationReportStatus = view.findViewById(R.id.violationReportStatus);
        Button violationReportStatusIndicator = view.findViewById(R.id.violationReportStatusIndicator);
        ImageView violationReportPreview = view.findViewById(R.id.violationTypeItemIcon);

        ViolationReport violationReport = violationReportsList.get(position);

        violationReportTitle.setText(violationReport.violationType.toString());
        violationReportPlace.setText(Helper.cropText(violationReport.location.getPlace(), 27) + "...");
        violationReportStatus.setText(violationReport.getStatus().toString());
        violationReportStatusIndicator.setBackgroundColor(violationReport.getStatus().getStatusColor());
        violationReportPreview.setImageDrawable(Drawable.createFromPath((Helper.getFullPathFromDataDirectory(violationReport.photosNames.get(0), context))));

        return view;
    }

    private void removeHero(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure you want to delete this?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                violationReportsList.remove(position);

                notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
