package com.example.prateekkesarwani.mapsdemo;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.model.Direction;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker mCurrentMarker;
    private FusedLocationProviderClient mFusedLocationClient;

    final LatLng egl = new LatLng(12.951292, 77.639570);
    final LatLng smondo = new LatLng(12.821949, 77.657729);

    TextView txtNavigationNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        initTTS();

        txtNavigationNotification = (TextView) findViewById(R.id.txt_navigation_notification);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Observable.just("")
                .delay(10000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(location -> {
                    Toast.makeText(this, "Startint location updates, ", Toast.LENGTH_SHORT).show();
                    startLocationUpdatesSmooth();
                });
    }

    TextToSpeech ttsEngine;

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

    private void startLocationUpdates() {
        LocationUpdate locationUpdate = new LocationUpdate();
        locationUpdate.getLocationObservable()
                .filter(locationList -> {
                    if (locationList != null) {
                        return true;
                    }
                    return false;
                })
                .subscribe(location -> {
                    mCurrentMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                    // mCurrentMarker.
                    Log.d("Prateek, ", "LocationChange:" + location.toString());
                });

    }

    private void startLocationUpdatesSmooth() {
        LocationUpdate locationUpdate = new LocationUpdate();

        // TODO We can directly put observable(if not used elsewhere), here only.
        // TODO So, we won't have to look else where, and would be a proper functional approach(Just relying on inputs)

        locationUpdate.getLocationObservableSmooth()
                .filter(locationList -> {
                    if (locationList != null) {
                        return true;
                    }
                    return false;
                })
                .flatMap(locationList -> Observable.fromIterable(locationList))
                .filter(location -> {
                    // TODO Here we can add accuracy stuff as well.
                    if (location != null) {
                        return true;
                    }
                    return false;
                })
                .subscribe(location -> {

                    // TODO
                    // Can't we use some operator to evently distribute if multiple location objects come in.

                    LatLng currentLatLong = new LatLng(location.getLatitude(), location.getLongitude());
                    mCurrentMarker.setPosition(currentLatLong);

                    // updatePolyline(currentLatLong, egl);
                    // mCurrentMarker.
                    Log.d("Prateek, ", "LocationChange:" + location.toString());
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

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        // mMap.getUiSettings().set


        mMap.setPadding(0, 0, (int) getResources().getDimension(R.dimen.map_padding_bottom), 0);

        mMap.addMarker(new MarkerOptions().position(egl).title("Marker in Current Pickup"));
        mMap.addMarker(new MarkerOptions().position(smondo).title("Marker in Current Drop"));

        // Initially, move to current location

        Observable.just("")
                .delay(5000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> moveToCurrentLocation());
    }

    private void drawPolyline(List<LatLng> latLngList) {

        Polyline polyline;
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(latLngList);

        polyline = mMap.addPolyline(polylineOptions.width(9)
                .color(Color.parseColor("#aa006aff")).geodesic(true));
    }

    private void moveToCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    // Example location would be null when fine location permission is not there,
                    // TODO Ideally for no permission this statement shouldn't be executed, but we should get exception(?)
                    if (location != null) {
                        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        // Changing marker icon
                        mCurrentMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Marker in Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.mini)));

                        CameraPosition currentPlace = new CameraPosition.Builder()
                                .target(new LatLng(location.getLatitude(),
                                        location.getLongitude())).bearing(location.getBearing())
                                .zoom(16.0f).build();
                        mMap.animateCamera(
                                CameraUpdateFactory.newCameraPosition(currentPlace), 1000,
                                null);

                        // t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                        ttsEngine.speak("Hello There", TextToSpeech.QUEUE_FLUSH, null);


                        List<LatLng> latLngs = new ArrayList<>();
                        latLngs.add(new LatLng(12.92452, 77.67371));
                        latLngs.add(new LatLng(12.95820, 77.66860));
                        updatePolyline(smondo, egl, latLngs);

                        Log.e("Prateek, ", "Altitude:" + location + "");
                    }
                });
    }

    LatLng currentLocation;
    LatLng dropLocation;

    public void updatePolyline(LatLng start, LatLng end, List<LatLng> waypointList) {

        GoogleDirection.withServerKey(getResources().getString(R.string.google_maps_key))
                .from(new LatLng(start.latitude, start.longitude))
                .to(new LatLng(end.latitude, end.longitude))
                .avoid(AvoidType.FERRIES)
                .waypoints(waypointList)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {

                        if (direction.isOK()) {

                            List<LatLng> list = direction.getRouteList().get(0).getOverviewPolyline().getPointList();

                            txtNavigationNotification.setText(Html.fromHtml(direction.getRouteList().get(0).getLegList().get(0).getStepList().get(0).getHtmlInstruction()));

                            drawPolyline(list);

                            Log.e("Prateek, direction", "OK" + rawBody);
                        } else {
                            Log.e("Prateek, direction", "NotOk:" + rawBody);
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Log.e("Prateek, direction", "Failure: " + t.getMessage());
                    }
                });
    }
}