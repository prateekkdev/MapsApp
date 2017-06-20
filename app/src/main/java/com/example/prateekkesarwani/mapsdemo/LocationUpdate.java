package com.example.prateekkesarwani.mapsdemo;

import android.annotation.SuppressLint;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by prateek.kesarwani on 14/06/17.
 */

public class LocationUpdate {

    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;

    public LocationUpdate() {
        init();
    }

    private void init() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MapsDemoApplication.getAppContext());
    }

    public Observable<List<Location>> getLocationChangeObservable() {
        return Observable.create(new ObservableOnSubscribe<List<Location>>() {

            @SuppressLint("MissingPermission")
            @Override
            public void subscribe(final ObservableEmitter<List<Location>> emitter) throws Exception {

                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                        new LocationCallback() {

                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                if (locationResult != null && emitter != null) {
                                    emitter.onNext(locationResult.getLocations());
                                }
                            }
                        },
                        null);
            }
        });
    }

    public Observable<Location> getLastKnownLocationObservable() {

        return Observable.create(e -> {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        // Got last known location. In some rare situations this can be null.
                        // Example location would be null when fine location permission is not there,
                        // TODO Ideally for no permission this statement shouldn't be executed, but we should get exception(?)
                        if (location != null) {
                            e.onNext(location);

                            // If this is called immediately, we don't need to dispose(?)
                            e.onComplete();
                        }
                    });

        });
    }
}