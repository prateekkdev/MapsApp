package com.example.prateekkesarwani.mapsdemo;

import android.location.Location;
import android.util.Log;

import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Step;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by prateek.kesarwani on 22/06/17.
 */

public class LocationTracker {

    List<LatLng> locationList;

    Leg legInfo;

    // TODO Use custom data
    int currentPointer = 0;

    // TODO These should be a single structure.
    Step currentStep;
    int currentStepIndex;
    LatLng currentStepLocation;


    public Step getCurrentStep() {
        return currentStep;
    }

    public LocationTracker(List<LatLng> locationList) {
        this.locationList = locationList;
    }

    // TODO When changes are made, should we cache and stuff(Although caching is mosltly needed for images unlike backend which might need caching to avoid network calls.
    public LocationTracker(Leg leg) {

        // Need to figure out these.

        this.legInfo = leg;
        this.currentStep = leg.getStepList().get(0);
        this.currentStepLocation = currentStep.getPolyline().getPointList().get(0);
    }

    // First start with brute force, then we will optimize
    public void updateCurrentPointer(LatLng currentLocation) {

        float currentPointerDistance = distanceFind(currentLocation, currentStepLocation);

        for (int index = currentStepIndex; index < legInfo.getStepList().size(); index++) {

            Step step = legInfo.getStepList().get(index);

            for (LatLng location : step.getPolyline().getPointList()) {

                float indexDistance = distanceFind(currentLocation, location);

                if (indexDistance <= currentPointerDistance) {
                    currentStepLocation = location;
                    currentStep = step;
                    currentStepIndex = index;
                }
            }
        }

        float distance = distanceFind(currentLocation, currentStepLocation);
        Log.e("Prateek, ", "currentStepIndex: " + currentStepIndex + ", currentLocationAndPointerDiff: " + distance);
    }

    public float distanceFind(LatLng location1, LatLng location2) {

        // function also returns bearings
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

    // Should we find closest point, and then iterate the stuff out.
    // Actually below code is doing the same. Maybe we are iterating all currently, moving forward, might use binary search based thing, as points are in order of distance.
    // So, O(n) would reduce to O(logn)
    public void updateCurrentPointerOlder(LatLng currentLocation) {

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
}