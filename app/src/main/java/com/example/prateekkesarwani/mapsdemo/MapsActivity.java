package com.example.prateekkesarwani.mapsdemo;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker mCurrentMarker;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
                    mCurrentMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
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

        // Initially, move to current location
        moveToCurrentLocation();

        /*
        final LatLng egl = new LatLng(12.951292, 77.639570);
        final LatLng smondo = new LatLng(12.821949, 77.657729);
        LatLng bangalore = smondo;
        LatLng delhi = new LatLng(28.645340, 77.213562);

        mMap.addMarker(new MarkerOptions().position(delhi).title("Marker in Delhi"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(delhi));


        Observable.just("")
                .delay(5000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(s -> {
                    mMap.addMarker(new MarkerOptions().position(bangalore).title("Marker in Bangalore"));
                    // mMap.moveCamera(CameraUpdateFactory.newLatLng(delhi));
                    // mMap.animateCamera(CameraUpdateFactory.newLatLng(delhi));

                    // Zoom level ranges from 2.0 to 21.0 (But not all locations support max zoom)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bangalore, 12.0f));
                })
                .delay(10000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> moveToCurrentLocation());
                */
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
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mCurrentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Current Location"));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));

                        Log.e("Prateek, ", "Altitude:" + location + "");
                    }
                });
    }
}