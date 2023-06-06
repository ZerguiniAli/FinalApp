package com.example.finalapp;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.Manifest;

import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MAP extends Fragment implements OnMapReadyCallback {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int PERMISSION_REQUEST_LOCATION = 1000;


    private String mParam1;
    private String mParam2;
    private GoogleMap gmap;
    private FrameLayout map;
    private Button done;

    private List<LatLng> latLngList = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();

    public MAP() {
        // Required empty public constructor
    }

    public static MAP newInstance(String param1, String param2) {
        MAP fragment = new MAP();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_m_a_p, container, false);

        map = view.findViewById(R.id.map);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //done = view.findViewById(R.id.done);
        //Button capturePositionButton = view.findViewById(R.id.done);

        Button myLocationButton = view.findViewById(R.id.done);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the location permission is granted
                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Request the location permission
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
                } else {
                    // Location permission is already granted, proceed with getting the current location
                    getCurrentLocation();
                }
            }
        });


        return view;
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.gmap = googleMap;

        LatLng algeria = new LatLng(28.0339, 1.6596);
        this.gmap.moveCamera(CameraUpdateFactory.newLatLng(algeria));
        this.gmap.getUiSettings();
        this.gmap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style));

        gmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                Marker marker = gmap.addMarker(markerOptions);

                latLngList.add(latLng);
                markerList.add(marker);
            }
        });
    }

    private String getMarkerCoordinatesString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (LatLng latLng : latLngList) {
            stringBuilder.append("Latitude: ")
                    .append(latLng.latitude)
                    .append(", Longitude: ")
                    .append(latLng.longitude)
                    .append("\n");
        }
        return stringBuilder.toString();
    }

    private void capturePosition() {
        // Check for location permission
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
            return;
        }

        // Get the last known location
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    LatLng latLng = new LatLng(latitude, longitude);
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                    Marker marker = gmap.addMarker(markerOptions);

                    latLngList.add(latLng);
                    markerList.add(marker);
                } else {
                    Toast.makeText(getActivity(), "Location not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getCurrentLocation() {
        // Check if the location permission is granted
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();

                                LatLng currentLatLng = new LatLng(latitude, longitude);

                                // Add a marker at the current location
                                MarkerOptions markerOptions = new MarkerOptions().position(currentLatLng).title("My Location");
                                Marker marker = gmap.addMarker(markerOptions);

                                // Move the camera to the current location
                                gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                            } else {
                                Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Location permission is not granted
            Toast.makeText(getActivity(), "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }


}
