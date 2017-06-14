package com.example.prateekkesarwani.mapsdemo;

import android.location.Location;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

/**
 * Created by prateek.kesarwani on 14/06/17.
 */

public class LocationUpdateCallbackListener extends LocationCallback {
    @Override
    public void onLocationResult(LocationResult locationResult) {
        for (Location location : locationResult.getLocations()) {
            // Update UI with location data
            // ...
        }
    }
}