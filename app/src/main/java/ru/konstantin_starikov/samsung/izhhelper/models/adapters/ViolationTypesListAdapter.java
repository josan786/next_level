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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationType;

public class ViolationTypesListAdapter extends ArrayAdapter<ViolationType> {
    List<ViolationType> violationTypesList;

    Context context;

    int resource;

    public ViolationTypesListAdapter(Context context, int resource, List<ViolationType> violationTypesList) {
        super(context, resource, violationTypesList);
        this.context = context;
        this.resource = resource;
        this.violationTypesList = violationTypesList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(resource, null, false);

        TextView violationTypeTitle = view.findViewById(R.id.violationTypeTitle);
        ImageView violationTypeItemIcon = view.findViewById(R.id.violationTypeItemIcon);

        ViolationType violationType = violationTypesList.get(position);

        violationTypeTitle.setText(violationType.toString());

        Resources resources = getContext().getResources();
        Drawable icon = null;
        switch (violationType.getViolationType())
        {
            case Lawn:
            {
                icon = resources.getDrawable(R.drawable.parking_on_lawn);
                break;
            }
            case PedestrianCrossing:
            {
                icon = resources.getDrawable(R.drawable.parking_pedestrian_crossing);
                break;
            }
            case Pavement:
            {
                icon = resources.getDrawable(R.drawable.sidewalk_parking);
                break;
            }
            case ParkingProhibited:
            {
                icon = resources.getDrawable(R.drawable.parking_no_parking_sign);
                break;
            }
            case StoppingProhibited:
            {
                icon = resources.getDrawable(R.drawable.parking_stop_prohibited);
                break;
            }
        }
        violationTypeItemIcon.setImageDrawable(icon);
        return view;
    }

    private void removeHero(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure you want to delete this?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                violationTypesList.remove(position);

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
