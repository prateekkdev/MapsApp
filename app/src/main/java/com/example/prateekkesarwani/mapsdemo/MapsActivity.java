package com.example.prateekkesarwani.mapsdemo;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
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

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int ZOOM_LEVEL = 17;

    private GoogleMap mMap;
    private Marker mCurrLocationMarker;
    private FusedLocationProviderClient mFusedLocationClient;

    final LatLng egl = new LatLng(12.951292, 77.639570);
    final LatLng smondo = new LatLng(12.821949, 77.657729);

    private TextView txtNavigationNotification;

    private ImageView imgCurrent;
    private ImageView imgRoute;

    Location savedLocation;
    Location currentLocation;
    float currentBearing;
    private Polyline polyline;

    TextToSpeech ttsEngine;

    LocationUpdate mLocationUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        txtNavigationNotification = (TextView) findViewById(R.id.txt_navigation_notification);

        imgCurrent = (ImageView) findViewById(R.id.img_current);

        imgRoute = (ImageView) findViewById(R.id.img_route);

        mLocationUpdate = new LocationUpdate();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mini)));
                    updateCamera(location, location.getBearing());
                });
    }

    private void updatePolyline() {
        mLocationUpdate
                .getPolylineObservable(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), egl, null)
                .subscribe(polylineData -> {
                    txtNavigationNotification.setText(polylineData.getInstruction());
                    drawPolyline(polylineData.getPolyline());
                });
    }

    private void drawPolyline(List<LatLng> latLngList) {
        if (polyline == null) {

            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.addAll(latLngList);

            polyline = mMap.addPolyline(polylineOptions.width(9)
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
                            mCurrLocationMarker.setPosition(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                            updateBearing();
                            updateCamera(currentLocation, currentBearing);

                            updatePolyline();
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
     * Updates the map orientation and center of focus based on the current
     * location and calculated bearing(Cab orientation).
     *
     * @param bearing
     */
    public void updateCamera(Location location, float bearing) {
        if (location != null) {
            CameraPosition currentPlace = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(),
                            location.getLongitude())).bearing(bearing)
                    .zoom(ZOOM_LEVEL).build();
            mMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(currentPlace), 1000,
                    null);
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
}