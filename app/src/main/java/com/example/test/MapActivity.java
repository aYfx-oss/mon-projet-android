package com.example.test;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.test.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Arrays;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private static final List<LatLng> EMSI_LOCATIONS = Arrays.asList(
            new LatLng(33.589886, -7.603869), // EMSI Maârif
            new LatLng(33.588674, -7.622126), // EMSI Aïn Sebaâ
            new LatLng(33.567485, -7.664688), // EMSI Hay Hassani
            new LatLng(33.588908, -7.620066)  // EMSI Oasis
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_map); // Assure-toi que ce fichier existe

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Erreur de chargement de la carte", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        mMap.setMyLocationEnabled(true);
        showEmsiMarkers();
        showUserLocationAndDrawRoutes();
    }

    private void showEmsiMarkers() {
        for (LatLng emsi : EMSI_LOCATIONS) {
            mMap.addMarker(new MarkerOptions()
                    .position(emsi)
                    .title("EMSI")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
    }

    private void showUserLocationAndDrawRoutes() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) {
                Log.e("DEBUG_LOCATION", "Location is null. Using fallback.");
                Toast.makeText(this, "Impossible de localiser l'utilisateur. Position par défaut utilisée.", Toast.LENGTH_LONG).show();

                // Position de secours (EMSI Maârif)
                location = new Location("");
                location.setLatitude(33.589886);
                location.setLongitude(-7.603869);
            } else {
                Log.d("DEBUG_LOCATION", "Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude());
            }

            LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 13f));

            mMap.addMarker(new MarkerOptions()
                    .position(userLatLng)
                    .title("Vous êtes ici")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            for (LatLng emsi : EMSI_LOCATIONS) {
                mMap.addPolyline(new PolylineOptions()
                        .add(userLatLng, emsi)
                        .color(0xFFFF0000)
                        .width(4f));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                onMapReady(mMap);
            }
        } else {
            Toast.makeText(this, "Localisation refusée", Toast.LENGTH_SHORT).show();
        }
    }
}
