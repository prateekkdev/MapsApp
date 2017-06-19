package com.example.prateekkesarwani.mapsdemo;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker mCurrLocationMarker;
    private FusedLocationProviderClient mFusedLocationClient;

    final LatLng egl = new LatLng(12.951292, 77.639570);
    final LatLng smondo = new LatLng(12.821949, 77.657729);

    private TextView txtNavigationNotification;

    private ImageView imgCurrent;
    private ImageView imgRoute;

    LatLng currentLocation;
    LatLng dropLocation;

    TextToSpeech ttsEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        txtNavigationNotification = (TextView) findViewById(R.id.txt_navigation_notification);

        imgCurrent = (ImageView) findViewById(R.id.img_current);

        imgRoute = (ImageView) findViewById(R.id.img_route);

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

        mMap.setMyLocationEnabled(true);

        mMap.getUiSettings().setCompassEnabled(true);

        // mMap.setPadding(0, 0, (int) getResources().getDimension(R.dimen.map_padding_bottom), 0);

        mMap.addMarker(new MarkerOptions().position(egl).title("Marker in Current Pickup"));
        mMap.addMarker(new MarkerOptions().position(smondo).title("Marker in Current Drop"));
    }
}