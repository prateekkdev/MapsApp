package com.example.prateekkesarwani.mapsdemo;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by prateek.kesarwani on 22/06/17.
 */

public class LocationTracker {

    List<LatLng> locationList;

    // TODO Use custom data
    int currentPointer = 0;

    public LocationTracker(List<LatLng> locationList) {
        this.locationList = locationList;
    }

    public void distanceFind(Location location) {

        // location.distanceTo()

    }

    public void updateCurrentPointer(LatLng currentLocation) {

        float currentPointerDistance = distanceFind(currentLocation, locationList.get(currentPointer));
        for (int index = currentPointer + 1; index < locationList.size(); index++) {

            float indexDistance = distanceFind(currentLocation, locationList.get(index));

            if (indexDistance <= currentPointerDistance) {
                currentPointerDistance = indexDistance;
                currentPointer = index;
            }

            // TODO IMP
            // TODO Is it really needed to traverse all the points? Can't just skip after some points, as indexDistance won't be less after later. Although need to see for a few points, for just one point if considering it won't work.
            // Although, even for such a large set of 450 datapoints its working just fine.
            // Maybe just pushing off to computation thread and then considering all the points might suffice.
        }

        float distance = distanceFind(currentLocation, locationList.get(currentPointer));
        Log.e("Prateek, ", "currentPointer: " + currentPointer + ", currentLocationAndPointerDiff: " + distance);
    }

    public void distanceFind(LatLng location) {

        // function also returns bearings
        float[] result = new float[3];
        Location.distanceBetween(location.latitude, location.longitude, locationList.get(0).latitude, locationList.get(0).longitude, result);

    }

    public float distanceFind(LatLng location1, LatLng location2) {
        float[] result = new float[3];
        Location.distanceBetween(location1.latitude, location1.longitude, location2.latitude, location2.longitude, result);

        return result[0];
    }

    public void printDistancesBetweenPoints() {

        float totalDistance = 0.0f;
        for (int index = 1; index < locationList.size(); index++) {
            float distance = distanceFind(locationList.get(index - 1), locationList.get(index));
            totalDistance += distance;
            Log.e("Prateek, distance", "" + distance);
        }

        Log.e("Prateek, totalDistance", "" + totalDistance);
    }
}