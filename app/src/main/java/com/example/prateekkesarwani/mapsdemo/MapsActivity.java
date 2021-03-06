package com.example.prateekkesarwani.mapsdemo;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;

import com.akexorcist.googledirection.constant.Maneuver;
import com.example.prateekkesarwani.mapsdemo.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int ZOOM_LEVEL = 20;
    private static final int TILT_LEVEL = 90;
    private static final int ROUT_POLYLINE_WIDTH = 12;
    private static final int DEVIATED_POLYLINE_WIDTH = 10;


    private GoogleMap mMap;
    private Marker mCurrLocationMarker;
    private FusedLocationProviderClient mFusedLocationClient;

    public static final LatLng egl = new LatLng(12.951292, 77.639570);
    public static final LatLng smondo = new LatLng(12.821949, 77.657729);

    public static final LatLng smondoEglWP1 = new LatLng(12.924249, 77.652104);
    public static final LatLng smondoEglWP2 = new LatLng(12.947900, 77.659490);

    Location savedLocation;
    Location currentLocation;
    float currentBearing;
    private Polyline polyline;

    TextToSpeech ttsEngine;

    LocationUpdate mLocationUpdate;

    ActivityMapsBinding mapsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapsBinding = DataBindingUtil.setContentView(this, R.layout.activity_maps);

        mLocationUpdate = new LocationUpdate();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initTTS();

        mapsBinding.mapsClose.setOnClickListener(view -> finish());
        Intent intent = new Intent(this, LocationService.class);
        intent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(intent);
    }

    void initTTS() {
        ttsEngine = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    ttsEngine.setLanguage(Locale.US);
                }
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set night style for now.
        // setSelectedStyle(R.string.style_label_night);

        // Default mylocation along with direction is given by this.
        // mMap.setMyLocationEnabled(true);

        mMap.getUiSettings().setCompassEnabled(true);

        // mMap.setPadding(0, 0, (int) getResources().getDimension(R.dimen.map_padding_bottom), 0);

        mMap.addMarker(new MarkerOptions().position(egl).title("Marker in Current Pickup"));
        mMap.addMarker(new MarkerOptions().position(smondo).title("Marker in Current Drop"));

        initWithLastKnownLocation();

        updateCurrentLocationData();
    }

    private void initWithLastKnownLocation() {

        mLocationUpdate.getLastKnownLocationObservable()
                .subscribeOn(Schedulers.io())
                .doOnNext(location -> {
                    currentLocation = location;
                    updatePolyline();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(location -> {
                    mCurrLocationMarker = mMap
                            .addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .title("Marker in Current Location")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_direction1)));
                    updateCamera(location, location.getBearing());
                });
    }

    private void updatePolyline() {

        List<LatLng> waypointsList = new ArrayList<>();
        waypointsList.add(smondoEglWP1);
        waypointsList.add(smondoEglWP2);

        mLocationUpdate
                .getPolylineObservableNew(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), egl, null)
                .subscribe(leg -> {

                    // Even this is an overkill, if we are already iterating later.
                    drawPolyline(leg.getDirectionPoint());

                    if (locationTracker == null) {
                        locationTracker = new LocationTracker(leg);

                        mapsBinding.footerDesc.setText(leg.getEndAddress());
                    }
                });
    }

    LocationTracker locationTracker;

    private void drawPolyline(List<LatLng> latLngList) {

        int listSize = latLngList.size();

        Log.e("Prateek", "Size of List: " + listSize);

        if (polyline == null) {

            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.addAll(latLngList);

            polyline = mMap.addPolyline(polylineOptions.width(ROUT_POLYLINE_WIDTH)
                    .color(Color.parseColor("#aa006aff")).geodesic(true));
        } else {
            polyline.setPoints(latLngList);
        }
    }

    private void updateCurrentLocationData() {

        mLocationUpdate.getLocationChangeObservable()
                .observeOn(Schedulers.io())
                .filter(locationList -> {
                    if (locationList != null) {
                        return true;
                    }
                    return false;
                })
                // Basically multiple locations might come even in single callback during the interval.
                .flatMap(locationList -> Observable.fromIterable(locationList))
                .filter(location -> {
                    // TODO Here we can add accuracy stuff as well.
                    if (location != null) {
                        return true;
                    }
                    return false;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(location -> {
                            currentLocation = location;

                            // LocationTracker is getting created only when we receive the polyline item.
                            if (locationTracker != null) {

                                mapsBinding.mapsRemaining.setText((int) locationTracker.getRemainingStepDistance() + " m");

                                // Only apply if step is changed.
                                if (locationTracker.isStepUpdate(new LatLng(location.getLatitude(), location.getLongitude()))) {
                                    mapsBinding.headerDesc.setText(Html.fromHtml(locationTracker.getCurrentStep().getHtmlInstruction()));

                                    if (Maneuver.STRAIGHT.equalsIgnoreCase(locationTracker.getCurrentStep().getManeuver())) {
                                        mapsBinding.mapsTurn.setBackground(getResources().getDrawable(R.drawable.turn_straight));
                                    } else if (Maneuver.TURN_LEFT.equalsIgnoreCase(locationTracker.getCurrentStep().getManeuver())) {
                                        mapsBinding.mapsTurn.setBackground(getResources().getDrawable(R.drawable.turn_left));
                                    } else if (Maneuver.TURN_RIGHT.equalsIgnoreCase(locationTracker.getCurrentStep().getManeuver())) {
                                        mapsBinding.mapsTurn.setBackground(getResources().getDrawable(R.drawable.turn_right));
                                    }

                                    // This is done to get text in string format(step's format is html)
                                    String text = mapsBinding.headerDesc.getText() + "";
                                    // ttsEngine.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }

                            mCurrLocationMarker.setPosition(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                            updateBearing();
                            updateCamera(currentLocation, currentBearing);

                            // updatePolyline();
                        }
                )
                .subscribe();
    }

    private void updateBearing() {

        if (currentLocation != null) {

            if (savedLocation == null || savedLocation.getAccuracy() > 20) {
                savedLocation = currentLocation;
                currentBearing = currentLocation.getBearing();
            }

            final float bearing = (float) BearingCalculation.finalBearing(
                    savedLocation.getLatitude(),
                    savedLocation.getLongitude(),
                    currentLocation.getLatitude(), currentLocation.getLongitude());

            double bearing_change_distance = savedLocation
                    .distanceTo(currentLocation);

            savedLocation = currentLocation;
            if (bearing_change_distance >= 20
                    && savedLocation.getAccuracy() < 20
                    && currentLocation.getAccuracy() < 20) {
                currentBearing = bearing;
            }

        }
    }

    /**
     * Remove fab for maps app.
     */
    @Override
    protected void onStart() {
        super.onStart();
        stopService(new Intent(this, MapsFAB.class));
    }

    /**
     * Add fab for maps app.
     */
    @Override
    protected void onStop() {
        super.onStop();
        startService(new Intent(this, MapsFAB.class));
    }

    /**
     * Updates the map orientation and center of focus based on the current
     * location and calculated bearing(Cab orientation).
     *
     * @param bearing
     */

    // TODO Again and again new LatLng getting created, resolve this.
    public void updateCamera(Location location, float bearing) {
        Log.d("Prateek, ", "Bearing:" + bearing);
        if (location != null) {
            CameraPosition currentPlace = new CameraPosition.Builder()
                    // .tilt(TILT_LEVEL)
                    .target(new LatLng(location.getLatitude(),
                            location.getLongitude())).bearing(bearing)
                    .zoom(ZOOM_LEVEL).build();
            mMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(currentPlace), 1000,
                    null);

            // If we set tilt in above code, then would have to set consider the degrees in currentlocation marker rotation for this thing.
            Log.e("Prateek", "Bearing: " + bearing);
            mCurrLocationMarker.setRotation(bearing);
        }
    }

    public static class BearingCalculation {
        static public double initial(double lat1, double long1, double lat2, double long2) {
            return (_bearing(lat1, long1, lat2, long2) + 360.0) % 360;
        }

        static public double finalBearing(double lat1, double long1, double lat2, double long2) {
            return (_bearing(lat2, long2, lat1, long1) + 180.0) % 360;
        }

        static private double _bearing(double lat1, double long1, double lat2, double long2) {
            double degToRad = Math.PI / 180.0;
            double phi1 = lat1 * degToRad;
            double phi2 = lat2 * degToRad;
            double lam1 = long1 * degToRad;
            double lam2 = long2 * degToRad;

            return Math.atan2(Math.sin(lam2 - lam1) * Math.cos(phi2),
                    Math.cos(phi1) * Math.sin(phi2) - Math.sin(phi1) * Math.cos(phi2) * Math.cos(lam2 - lam1)
            ) * 180 / Math.PI;
        }
    }

    /**
     * Creates a {@link MapStyleOptions} object via loadRawResourceStyle() (or via the
     * constructor with a JSON String), then sets it on the {@link GoogleMap} instance,
     * via the setMapStyle() method.
     */
    private void setSelectedStyle(int mSelectedStyleId) {
        MapStyleOptions style;
        switch (mSelectedStyleId) {
            case R.string.style_label_night:
                // Sets the night style via raw resource JSON.
                style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night);
                break;

            case R.string.style_label_default:
                // Removes previously set style, by setting it to null.
                style = null;
                break;
            default:
                return;
        }
        mMap.setMapStyle(style);
    }
}