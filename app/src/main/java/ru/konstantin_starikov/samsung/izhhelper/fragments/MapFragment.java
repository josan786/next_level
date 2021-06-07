package ru.konstantin_starikov.samsung.izhhelper.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.Helper;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationReport;
import ru.konstantin_starikov.samsung.izhhelper.models.interfaces.Action;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    private static final String TAG = "MapFragment";

    private static final Point IZHEVSK_CENTER_LOCATION = new Point(56.857118, 53.232999);
    private static final float IZHEVSK_CENTER_ZOOM = 11.0f;
    private static final float IZHEVSK_CENTER_AZIMUTH = 0.0f;
    private static final float IZHEVSK_CENTER_TILT = 0.0f;

    private static final String USERS_ACCOUNTS_LABEL = "Users accounts";
    private static final String VIOLATIONS_REPORTS_LABEL = "Violations reports";
    private static final String LOCATION_LABEL = "Location";
    private static final String LOCATION_LATITUDE_LABEL = "Latitude";
    private static final String LOCATION_LONGITUDE_LABEL = "Longitude";

    private MapKit mapKit;
    private MapView mapView;
    ArrayList<Point> violationsPoints;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setupMapKitFactory();
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = rootView.findViewById(R.id.mapFragmentView);
        Map map = mapView.getMap();
        tuneMap(map);
        addViolationsOnMap(map);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().invalidateOptionsMenu();
    }

    private void setupMapKitFactory() {
        try {
            MapKitFactory.setApiKey(Helper.getConfigValue(getContext(), "MAPKIT_API_KEY"));
            MapKitFactory.setLocale("ru_RU");
        }
        catch (AssertionError assertionError)
        {
            Log.w(TAG , "setApiKey() should be called before initialize()!");
        }
        MapKitFactory.initialize(getContext());
    }

    private void tuneMap(Map map)
    {
        map.setRotateGesturesEnabled(false);
        map.move(new CameraPosition(IZHEVSK_CENTER_LOCATION, IZHEVSK_CENTER_ZOOM, IZHEVSK_CENTER_AZIMUTH, IZHEVSK_CENTER_TILT));
        map.setZoomGesturesEnabled(true);
    }

    private void addViolationsOnMap(Map map)
    {
        violationsPoints = new ArrayList<Point>();
        ImageProvider imageProvider = ImageProvider.fromBitmap(
                Helper.getBitmapFromVectorDrawable(getContext(), R.drawable.ic_violation_point));
        fillViolationsPointsListAndDoAction(() ->
        {
            for(Point violationPoint : violationsPoints)
            {
                map.getMapObjects()
                        .addPlacemark(violationPoint, imageProvider)
                        .addTapListener(new ViolationPointTapListener());
            }
        });
    }

    class ViolationPointTapListener implements MapObjectTapListener
    {

        @Override
        public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
            //Todo: add tap listener
            return false;
        }
    }

    private ArrayList<Point> fillViolationsPointsListAndDoAction(Action action)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(USERS_ACCOUNTS_LABEL);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    fillViolationPointsFromDataSnapshot(snapshot);
                    action.run();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return violationsPoints;
    }

    private void fillViolationPointsFromDataSnapshot(DataSnapshot dataSnapshot)
    {
        for(DataSnapshot user : dataSnapshot.getChildren())
        {
            if(user.hasChild(VIOLATIONS_REPORTS_LABEL))
            {
                for(DataSnapshot violationReport : user.child(VIOLATIONS_REPORTS_LABEL).getChildren())
                {
                    if(violationReport.hasChild(LOCATION_LABEL))
                    {
                        if(violationReport.child(LOCATION_LABEL).hasChild(LOCATION_LATITUDE_LABEL)
                                && violationReport.child(LOCATION_LABEL).hasChild(LOCATION_LONGITUDE_LABEL)) {
                            double latitude = violationReport.child(LOCATION_LABEL).child(LOCATION_LATITUDE_LABEL).getValue(Double.class);
                            double longitude = violationReport.child(LOCATION_LABEL).child(LOCATION_LONGITUDE_LABEL).getValue(Double.class);
                            violationsPoints.add(new Point(latitude, longitude));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}