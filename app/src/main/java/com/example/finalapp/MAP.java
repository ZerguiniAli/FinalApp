package com.example.finalapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.Manifest;

import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class MAP extends Fragment implements OnMapReadyCallback {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    static final int PERMISSION_REQUEST_LOCATION = 1000;

    LinearLayout linearLayout;

    EditText namefield ;

    private String mParam1;
    private String mParam2;
    private GoogleMap gmap;

    private String cordiantes ;
    private FrameLayout map;
    private Button done , addMarker , remove_position, save ;

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
        linearLayout = view.findViewById(R.id.linear);
        save = view.findViewById(R.id.save);
        map = view.findViewById(R.id.map);
        namefield = view.findViewById(R.id.fieldname);

        getCurrentLocation();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        addMarker = view.findViewById(R.id.addpostion);
        addMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the location permission is granted
                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Request the location permission
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
                } else {
                    // Location permission is already granted, proceed with getting the current location
                    capturePosition();
                }
            }
        });

        remove_position = view.findViewById(R.id.removepostion);
        remove_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeLastMarker();
            }
        });

        done =view.findViewById(R.id.done);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.setVisibility(View.VISIBLE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fieldName = namefield.getText().toString();
                String cordinates  = getMarkerCoordinatesString();
                SharedPreferences Email = getActivity().getSharedPreferences(LoignEmailOrUsername.A_EMAIL ,0);
                String email = Email.getString("email" ,"");
                StringRequest stringRequest = new StringRequest(Request.Method.POST,endpoint.add_field, response -> {
                    if (response.equals("success"))
                    {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment,new FIELD()).commit();
                        linearLayout.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "not done", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
                    Toast.makeText(getActivity(), "please check your internet connection", Toast.LENGTH_SHORT).show();
                }){
                    protected Map<String , String> getParams(){
                        Map<String , String> params= new HashMap<>();
                        params.put("cordinates",cordinates);
                        params.put("email" , email);
                        params.put("fieldName", fieldName);
                        return params;
                    }
                };
                VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
            }
        });

        return view;
    }




    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.gmap = googleMap;

        LatLng algeria = new LatLng(33.8033,  2.8802);
        this.gmap.moveCamera(CameraUpdateFactory.newLatLng(algeria));
        this.gmap.getUiSettings();

        // Draw the polygon if there are already captured positions
        if (latLngList.size() >= 3) {
            drawPolygon();
        }
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

                    // Create a custom marker icon (green circle)
                    int radius = 10; // Adjust the radius as needed
                    BitmapDescriptor markerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    Bitmap iconBitmap = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(iconBitmap);
                    Paint paint = new Paint();
                    paint.setColor(Color.WHITE);
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(radius, radius, radius, paint);
                    markerIcon = BitmapDescriptorFactory.fromBitmap(iconBitmap);

                    // Add a marker at the captured location with the custom marker icon
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .icon(markerIcon);
                    Marker marker = gmap.addMarker(markerOptions);
                    markerList.add(marker);
                    latLngList.add(latLng);

                    // Convert latLng to string
                    String coordinates = "Latitude: " + latitude + ", Longitude: " + longitude;

                    // Move the camera to the new marker position
                    gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

                    // Draw the polygon if there are at least 3 positions
                    if (latLngList.size() >= 3) {
                        drawPolygon();
                    }
                } else {
                    Toast.makeText(getActivity(), "Location not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void drawPolygon() {
        PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngList).strokeWidth(2).strokeColor(Color.GREEN);
        gmap.addPolygon(polygonOptions);
    }

    private void getCurrentLocation() {
        // Check for location permission
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, proceed with getting the current location
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        LatLng currentLatLng = new LatLng(latitude, longitude);

                    } else {
                        Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
        }
    }

    private void removeLastMarker() {
        int lastMarkerIndex = markerList.size() - 1;
        if (lastMarkerIndex >= 0) {
            Marker lastMarker = markerList.get(lastMarkerIndex);
            lastMarker.remove();
            markerList.remove(lastMarker);
            latLngList.remove(lastMarkerIndex);
        }
    }

}
