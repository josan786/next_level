package ru.konstantin_starikov.samsung.izhhelper.models.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;

import com.google.firestore.v1.GetDocumentRequest;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.Random;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.CropSquareTransformation;
import ru.konstantin_starikov.samsung.izhhelper.models.Helper;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationReport;

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
        ImageView violationReportStatusIndicator = view.findViewById(R.id.indicator);
        ImageView violationReportPreview = view.findViewById(R.id.violationTypeItemIcon);

        ViolationReport violationReport = violationReportsList.get(position);

        violationReportTitle.setText(violationReport.violationType.toString(context));
        violationReportPlace.setText(Helper.cropText(violationReport.location.getPlace(), 27) + "...");
        Resources res = context.getResources();
        violationReportStatusIndicator.setImageDrawable(ResourcesCompat.getDrawable(res, violationReport.getStatus().getStatusBlob(), null));

        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(violationReportStatusIndicator.getLayoutParams());
        marginParams.setMargins(0, 0, 0, new Random().nextInt(35));
        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(marginParams);
        violationReportStatusIndicator.setLayoutParams(layoutParams);

        if(!violationReport.photosNames.isEmpty()) {
            File previewFile = new File(Helper.getFullPathFromDataDirectory(violationReport.photosNames.get(0), context));
            Picasso.get().load(previewFile).transform(new CropSquareTransformation()).into(violationReportPreview);
        }
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
