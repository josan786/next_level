package ru.konstantin_starikov.samsung.izhhelper.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.yandex.mapkit.GeoObjectCollection;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.logo.Alignment;
import com.yandex.mapkit.logo.HorizontalAlignment;
import com.yandex.mapkit.logo.VerticalAlignment;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.SearchType;
import com.yandex.mapkit.search.Session;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;

import javax.xml.transform.sax.TemplatesHandler;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.Helper;
import ru.konstantin_starikov.samsung.izhhelper.models.Location;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationReport;

public class PlaceChoiceActivity extends AppCompatActivity implements UserLocationObjectListener, Session.SearchListener, CameraListener, ActivityCompat.OnRequestPermissionsResultCallback {

    public final static String TAG = "PlaceChoiceActivity";

    public final static String MAPKIT_API_KEY_NAME = "MAPKIT_API_KEY";

    public final static String PIN_ICON_NAME = "pin";

    public final static String VIOLATION_REPORT = "violation_report";

    private static final Point ITCUBE_LOCATION = new Point(56.856417, 53.285774);
    private static final float ITCUBE_ZOOM = 18.0f;
    private static final float ITCUBE_AZIMUTH = 0.0f;
    private static final float ITCUBE_TILT = 0.0f;

    private static final int PERMISSION_REQUEST_LOCATION = 1;

    //счётчик, сколько раз камера перемещалась в местоположение пльзователя
    private int userLocationUsagesCount = 0;

    TextView placeDescription;

    private ViolationReport violationReport;
    private FrameLayout placeDescriptionLayout;

    private MapKit mapKit;
    private MapView mapView;
    private UserLocationLayer userLocationLayer;
    private SearchManager searchManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        violationReport = getTransmittedViolationReport();

        setupMapKitFactory();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_choice);

        tuneActionBar();

        mapKit = MapKitFactory.getInstance();

        findAndSetViews();
        if(!Helper.isOnline(this)) placeDescriptionLayout.setVisibility(View.INVISIBLE);

        Map map = mapView.getMap();

        tuneMap(map);

        //обратное геокодирование для поиска улицы, находящейся в местоположении камеры
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

    private void setupMapKitFactory() {
        try {
            MapKitFactory.setApiKey(Helper.getConfigValue(this, MAPKIT_API_KEY_NAME));
            MapKitFactory.setLocale("ru_RU");
        }
        catch (AssertionError assertionError)
        {
            Log.w(TAG , "setApiKey() should be called before initialize()!");
        }
        MapKitFactory.initialize(this);
    }

    private void tuneMap(Map map)
    {
        map.setRotateGesturesEnabled(false);
        map.move(new CameraPosition(ITCUBE_LOCATION, ITCUBE_ZOOM, ITCUBE_AZIMUTH, ITCUBE_TILT));
        map.setZoomGesturesEnabled(false);
        trackCameraChanges(map);
        trackUserLocation();
        setupLogoInLeftBottomPart(map);
    }

    private void trackCameraChanges(Map map)
    {
        map.addCameraListener(this);
    }

    private void trackUserLocation()
    {
        if (checkIfAlreadyHavePermission()) {
            userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
            userLocationLayer.setVisible(true);
            userLocationLayer.setHeadingEnabled(true);
            userLocationLayer.setObjectListener(this);
        }
    }

    private void setupLogoInLeftBottomPart(Map map)
    {
        map.getLogo().setAlignment(new Alignment(HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM));
    }

    private void findAndSetViews()
    {
        mapView = (MapView) findViewById(R.id.mapFragmentView);
        placeDescription = (TextView) findViewById(R.id.placeDescription);
        placeDescriptionLayout = (FrameLayout) findViewById(R.id.placeDescriptionLayout);
    }

    private ViolationReport getTransmittedViolationReport()
    {
        return (ViolationReport) getIntent().getSerializableExtra(MainMenuActivity.VIOLATION_REPORT);
    }

    private void tuneActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.PlaceChoiceActivityTitle));
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
    public void onObjectAdded(UserLocationView userLocationView) {
        userLocationView.getArrow().setIcon(ImageProvider.fromResource(
                this, R.drawable.navigation));

        CompositeIcon pinIcon = userLocationView.getPin().useCompositeIcon();

        pinIcon.setIcon(
                PIN_ICON_NAME,
                ImageProvider.fromResource(this, R.drawable.search_result),
                new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(1f)
                        .setScale(0.5f)
        );
    }

    @Override
    public void onObjectRemoved(UserLocationView view) {
    }

    @Override
    public void onObjectUpdated(UserLocationView view, ObjectEvent event) {
        if(userLocationUsagesCount == 0)
        {
            mapView.getMap().move(userLocationLayer.cameraPosition());
            userLocationUsagesCount++;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void moveToUserLocation(View v) {
        if (!checkIfAlreadyHavePermission()) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else if (userLocationLayer.cameraPosition() != null)
            mapView.getMap().move(userLocationLayer.cameraPosition());
    }

    private boolean checkIfAlreadyHavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Log.v("Grant_Results", String.valueOf(grantResults[0]));
                    Toast.makeText(this, "Разрешение на местоположение получено", Toast.LENGTH_LONG).show();
                    userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
                    userLocationLayer.setVisible(true);
                    userLocationLayer.setHeadingEnabled(true);
                    userLocationLayer.setObjectListener(this);
                    moveToUserLocation(null);
                } else {
                    Toast.makeText(this, "Нет разрешения на местоположение", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    public void openViolationTypeSelectionActivity(View v) {
        String violationPlace = violationReport.location.getPlace();
        if(violationPlace == null || violationPlace.isEmpty()) violationReport.location.setPlace(getString(R.string.NotSet));

        Intent openViolationTypeSelectionIntent = new Intent(PlaceChoiceActivity.this, ViolationTypeSelectionActivity.class);
        openViolationTypeSelectionIntent.putExtra(VIOLATION_REPORT, violationReport);
        startActivity(openViolationTypeSelectionIntent);
    }

    @Override
    public void onSearchResponse(@NonNull Response response) {
        String violationPlace = "";
        int addressDepth = response.getCollection().getChildren().size();
        for(int i = 0; i < addressDepth; i++)
        {
            GeoObjectCollection.Item geoObject = response.getCollection().getChildren().get(i);
            violationPlace += geoObject.getObj().getName();
            if(geoObject.getObj().getName().contains("Ижевск")) break;
            if(i < addressDepth - 1) violationPlace += ", ";
        }
        placeDescription.setText(violationPlace);
        violationReport.location.setPlace(violationPlace);
    }

    @Override
    public void onSearchError(@NonNull Error error) {

    }

    @Override
    public void onCameraPositionChanged(@NonNull Map map, @NonNull CameraPosition cameraPosition, @NonNull CameraUpdateReason cameraUpdateReason, boolean b) {
        SearchOptions searchOptions = new SearchOptions();
        searchOptions.setSearchTypes(SearchType.GEO.value);
        Point cameraTarget = mapView.getMap().getCameraPosition().getTarget();
        violationReport.location = new Location(cameraTarget.getLatitude(), cameraTarget.getLongitude());
        searchManager.submit(cameraTarget, 20, searchOptions, this);
    }
}