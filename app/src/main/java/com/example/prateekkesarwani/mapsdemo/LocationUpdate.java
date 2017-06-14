package com.example.prateekkesarwani.mapsdemo;

import android.annotation.SuppressLint;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

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

    private void init() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MapsDemoApplication.getAppContext());
    }

        /*
    @Override
    protected void onResume() {
        super.onResume();
        boolean mRequestingLocationUpdates = true;
        // Notice that the above code snippet refers to a boolean flag, mRequestingLocationUpdates, used to track whether the user has turned location updates on or off
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
    */


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        /*
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);


        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MapsActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
        */


    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void startLocationUpdates() {

    }

    public Observable<Location> getLocationObservable() {
        return Observable.create(new ObservableOnSubscribe<Location>() {

            @SuppressLint("MissingPermission")
            @Override
            public void subscribe(final ObservableEmitter<Location> emitter) throws Exception {


                /*
                if (ActivityCompat.checkSelfPermission(MapsDemoApplication.getAppContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsDemoApplication.getAppContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                */

                init();

                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                        new LocationCallback() {

                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                emitter.onNext(locationResult.getLastLocation());
                            }
                        },
                        null);
            }
        });
    }
}