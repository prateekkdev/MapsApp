package com.example.prateekkesarwani.mapsdemo;

import android.annotation.SuppressLint;
import android.location.Location;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.config.GoogleDirectionConfiguration;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.model.Direction;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

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

    public Observable<PolylineData> getPolylineObservable(LatLng start, LatLng end, List<LatLng> waypointList) {

        GoogleDirectionConfiguration.getInstance().setLogEnabled(true);

        return Observable.create(e ->
                GoogleDirection.withServerKey(MapsDemoApplication.getAppContext().getResources().getString(R.string.google_maps_key))
                        .from(start)
                        .to(end)
                        .avoid(AvoidType.FERRIES)
                        .waypoints(waypointList)
                        .viaPoints(false)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(Direction direction, String rawBody) {

                                if (direction.isOK()) {

                                    // direction.getRouteList().get(0).getOverviewPolyline().getPointList();

                                    List<LatLng> list = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();

                                    e.onNext(new PolylineData(list,
                                            Html.fromHtml(direction.getRouteList().get(0).getLegList().get(0).getStepList().get(0).getHtmlInstruction()),
                                            direction.getRouteList().get(0).getLegList().get(0).getStepList().get(0).getManeuver()));

                                    Log.e("Prateek, direction", "OK" + rawBody);
                                } else {
                                    Log.e("Prateek, direction", "NotOk:" + rawBody);
                                }
                            }

                            @Override
                            public void onDirectionFailure(Throwable t) {
                                e.onError(new Throwable("Direction not ok"));
                                Log.e("Prateek, direction", "Failure: " + t.getMessage());
                            }
                        }));
    }

    class PolylineData {
        private List<LatLng> polyline;
        private Spanned instruction;
        private String maneuver;

        public PolylineData(List<LatLng> polyline, Spanned instruction, String maneuver) {
            this.polyline = polyline;
            this.instruction = instruction;
            this.maneuver = maneuver;
        }

        public List<LatLng> getPolyline() {
            return polyline;
        }

        public Spanned getInstruction() {
            return instruction;
        }

        public String getManeuver() {
            return maneuver;
        }
    }
}