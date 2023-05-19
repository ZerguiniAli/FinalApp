package com.example.finalapp;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MAP#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MAP extends Fragment implements OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    GoogleMap gmap ;
    FrameLayout map;

    Button done;

    String polygonCoordinatesString;

    List<LatLng> polygonCoordinates = new ArrayList<>();


    Polygon polygon = null;
    List<LatLng> latLngList = new ArrayList<>();
    List<Marker> markerList = new ArrayList<>();

    public MAP() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MAP.
     */
    // TODO: Rename and change types and number of parameters
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

        done = view.findViewById(R.id.done);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), polygonCoordinatesString, Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        this.gmap = googleMap;

        LatLng algeria = new LatLng(28.0339,1.6596);

        this.gmap.moveCamera(CameraUpdateFactory.newLatLng(algeria));
        this.gmap.getUiSettings();
        this.gmap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(),R.raw.map_style));

        gmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                Marker marker = gmap.addMarker(markerOptions);

                latLngList.add(latLng);
                markerList.add(marker);

                // Update the polygon's points
                if (polygon != null) {
                    polygon.setPoints(latLngList);
                } else {
                    // Create and update the polygon
                    PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngList);
                    polygon = gmap.addPolygon(polygonOptions);
                    polygon.setStrokeColor(Color.GREEN);
                    polygon.setFillColor(Color.GREEN);

                    polygonCoordinates = latLngList ;

                    polygonCoordinatesString = getPolygonCoordinatesString(latLngList);


                }
            }
        });



    }

    private String getPolygonCoordinatesString(List<LatLng> latLngList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (LatLng latLng : latLngList) {
            stringBuilder.append("V = ")
                    .append(latLng.latitude)
                    .append(" H = ")
                    .append(latLng.longitude)
                    .append("; ");
        }
        // Remove the trailing semicolon and space if needed
        if (stringBuilder.length() > 0) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }
        return stringBuilder.toString();
    }
}