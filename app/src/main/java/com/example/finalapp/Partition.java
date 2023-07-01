package com.example.finalapp;

import static com.example.finalapp.MAP.PERMISSION_REQUEST_LOCATION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.finalapp.WeatherRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.Key;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Partition extends Fragment implements OnMapReadyCallback, WeatherRequest.WeatherListener {

    private static final int PERMISSION_REQUEST_LOCATION = 1000;

    private GoogleMap gmap;
    private String location;

    private ImageButton addparts ;
    private List<Marker> markerList = new ArrayList<>();
    private Button addMarker,removeMarker, DoneButton;
    private RadioGroup radiogrp ;
    private RecyclerView recyclerView, recyclerView2;

    private EditText dateEditText;

    private Marker marker ;
    private WeatherAdapter adapter;
    private List<Double> maxTemps;
    private List<Double> minTemps;
    private List<Weather> weatherList;
    double MAX, MIN, PRE, WIND;
    private List<Double> precipitations;
    private List<Double> windSpeeds;
    private RelativeLayout rain, snow, drizzle, fog, sun, moon, Map, Data, partitions , MapAddPartitons;
    private List<String> dates;
    private List<String> times;
    private TextView fieldName, fieldLocaiton, fieldTemp, Workernumber,weather ;
    private RequestQueue requestQueue;
    private List<LatLng> latLngList = new ArrayList<>();
    private List<LatLng> latLngList2 = new ArrayList<>();
    private Interpreter interpreter;

    public Partition() {
        // Required empty public constructor
    }

    public static Partition newInstance() {
        return new Partition();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_partition, container, false);

        fieldName = view.findViewById(R.id.FieldNameTextView);
        fieldLocaiton = view.findViewById(R.id.FieldLocationTextView);
        Workernumber = view.findViewById(R.id.WorkerCountTextView);
        fieldTemp = view.findViewById(R.id.TempTextView);

        weather = view.findViewById(R.id.weather);
        rain = view.findViewById(R.id.rain);
        snow = view.findViewById(R.id.snow);
        fog = view.findViewById(R.id.fog);
        sun = view.findViewById(R.id.sun);
        drizzle = view.findViewById(R.id.drizzle);
        moon = view.findViewById(R.id.moon);
        MapAddPartitons = view.findViewById(R.id.mapaddpartitions);
        DoneButton = view.findViewById(R.id.doneButtton);

        partitions = view.findViewById(R.id.partitionsRelativeLayout);

        addparts = view.findViewById(R.id.addbtn);

        addMarker = view.findViewById(R.id.addMarker);
        removeMarker = view.findViewById(R.id.removeMarker);

        radiogrp = view.findViewById(R.id.buttonGroup);
        MaterialRadioButton summaryRadioButton = view.findViewById(R.id.summary);
        radiogrp.check(summaryRadioButton.getId());

        addMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Request the location permission
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
                } else {
                    // Location permission is already granted, proceed with getting the current location
                    capturePosition();
                }
            }
        });


        removeMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeLastMarker();
            }
        });


        addparts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                partitions.setVisibility(View.INVISIBLE);
                MapAddPartitons.setVisibility(View.VISIBLE);
                Map.setVisibility(View.VISIBLE);
            }
        });


        Map = view.findViewById(R.id.maprelative);
        Data = view.findViewById(R.id.fieldData);

        Bundle arguments = getArguments();
        if (arguments != null) {
            String coordinatesString = arguments.getString("cordinates");
            latLngList.addAll(getLatLngListFromString(coordinatesString));
        }
        String nameField  = arguments.getString("fieldName");
        fieldName.setText(nameField);
        String KeyField = arguments.getString("keyField");


        requestQueue = Volley.newRequestQueue(getActivity());


        WeatherRequest weatherRequest = new WeatherRequest(requestQueue, this);
        weatherRequest.getWeatherData(latLngList.get(0).latitude , latLngList.get(0).longitude);

        recyclerView = view.findViewById(R.id.partsrecylerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        weatherList = new ArrayList<>();
        adapter = new WeatherAdapter(weatherList, getActivity(), interpreter);
        recyclerView.setAdapter(adapter);



        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        try {
            interpreter = new Interpreter(loadModelFile(getContext()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        float[][] inputData = {
                {(float) PRE, (float) MAX, (float) MIN, (float) WIND}
        };
        float[][] outputData = new float[1][5];
        interpreter.run(inputData, outputData);

        String[] weatherCategories = {"Drizzle", "Fog", "Rain", "Snow", "Sun"};
        int predictedIndex = argmax(outputData[0]);
        String predictedWeather = weatherCategories[predictedIndex];
        weather.setText(predictedWeather);

        switch (predictedWeather) {
            case "Drizzle":
                drizzle.setVisibility(View.VISIBLE);
                break;
            case "Fog":
                fog.setVisibility(View.VISIBLE);
                break;
            case "Rain":
                rain.setVisibility(View.VISIBLE);
                break;
            case "Snow":
                snow.setVisibility(View.VISIBLE);
                break;
            case "Sun":
                sun.setVisibility(View.VISIBLE);
                break;
        }

        radiogrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (i == R.id.summary) {
                    Map.setVisibility(View.VISIBLE);
                    Data.setVisibility(View.INVISIBLE);
                }else if (i == R.id.fielddata){
                    Data.setVisibility(View.VISIBLE);
                    Map.setVisibility(View.INVISIBLE);
                }
            }
        });

        DoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String coordinates = getMarkerCoordinatesString2();
                String Color = "GREEN";
                String key = "90199";
                StringRequest stringRequest = new StringRequest(Request.Method.POST,endpoint.add_new_partition, response -> {
                    if (response.equals("success"))
                    {
//                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment,new FIELD()).commit();
//                        linearLayout.setVisibility(View.INVISIBLE);
                        Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "not done", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
                    Toast.makeText(getActivity(), "please check your internet connection", Toast.LENGTH_SHORT).show();
                }){
                    protected java.util.Map<String , String> getParams(){
                        Map<String , String> params= new HashMap<>();
                        params.put("keyPartition" , key);
                        params.put("keyField", KeyField);
                        params.put("cordinates", coordinates );
                        params.put("Color" , Color);
                        return params;
                    }
                };
                VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
            }
        });

        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, endpoint.fetch_partition_data, response -> {
            // Create a SQLiteOpenHelper instance to handle database operations
            SQLite dbHelper = new SQLite(getActivity());

            try {
                JSONArray jsonArray = new JSONArray(response); // Convert the response string to a JSONArray
                if (jsonArray.length() > 0) {
                    // Open the SQLite database for writing
                    SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();

                    // Delete all existing data from the table
                    sqliteDatabase.delete("PARITION_DATA", null, null);

                    // Iterate over each object in the JSONArray
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject row = jsonArray.getJSONObject(i);

                        // Retrieve values from the row
                        String keyPartition = row.getString("keyPartition");
                        //Toast.makeText(this, coordinates, Toast.LENGTH_SHORT).show();
                        String cordinates = row.getString("cordinates");
                        //Toast.makeText(this, keyField, Toast.LENGTH_SHORT).show();
                        String Color = row.getString("Color");

                        // Create a ContentValues object to hold the row data
                        ContentValues values = new ContentValues();
                        values.put("keyPartition", keyPartition);
                        values.put("cordinates", cordinates);
                        values.put("Color", Color);

                        // Insert the row into the SQLite database
                        // Insert the row into the SQLite database
                        long newRowId = sqliteDatabase.insert("FIELD_DATA", null, values);
                        // Check if the insertion was successful
                        if (newRowId == -1) {
                            //Toast.makeText(this, "Failed to insert data", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_SHORT).show();
                        }

                    }

                    // Close the SQLite database
                    sqliteDatabase.close();
                } else {
                    Toast.makeText(getActivity(), "No data", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("keyField", KeyField);
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest2);

        SQLite dbHelper = new SQLite(getActivity());
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();

        List<Tableitem2> tableitems2 = new ArrayList<>();

        Cursor cursor = sqliteDatabase.rawQuery("SELECT * FROM PARITION_DATA", null);
        if (cursor != null) {
            int keyPartitionIndex = cursor.getColumnIndex("KeyPartition");
            int CordinatesIndex= cursor.getColumnIndex("cordinates");
            int ColorIndex = cursor.getColumnIndex("Color");
            while (cursor.moveToNext()) {
                String keyPartition = (keyPartitionIndex != -1) ? cursor.getString(keyPartitionIndex) : "Default Coordinates";
                String Cordinates = (CordinatesIndex != -1) ? cursor.getString(CordinatesIndex) : "Default Key Field";
                String Color = (ColorIndex != -1) ? cursor.getString(ColorIndex) : "Default field name";
                Tableitem2 item = new Tableitem2(keyPartition,Cordinates,Color);
                tableitems2.add(item);
            }
            cursor.close();
        }
        sqliteDatabase.close();

        Tableadapter2 tableadapter2 = new Tableadapter2(tableitems2);

        recyclerView2 = view.findViewById(R.id.recylerViewForParition);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView2.setAdapter(tableadapter2);

        dateEditText = view.findViewById(R.id.dateEditText);
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });


        return view;
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.gmap = googleMap;

        LatLng algeria = new LatLng(33.8033, 2.8802);
        this.gmap.moveCamera(CameraUpdateFactory.newLatLng(algeria));

        try {
            boolean success = this.gmap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(requireActivity(), R.raw.map_style)
            );

            if (!success) {
                // Failed to set the map style
            } else {
                // Delay the execution of drawPolygon() until the layout is complete
                gmap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        drawPolygon();
                    }
                });
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void drawPolygon() {
        if (latLngList.size() >= 3) {
            PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngList).strokeWidth(6).strokeColor(Color.RED);
            gmap.addPolygon(polygonOptions);

            // Get the bounds of the polygon
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng latLng : latLngList) {
                builder.include(latLng);
            }
            LatLngBounds bounds = builder.build();

            // Set the camera position and zoom level to fit the polygon
            int padding = 100; // Adjust padding as needed
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            gmap.animateCamera(cameraUpdate);
        } else {
            // Handle case where there are not enough points to create a polygon
        }
    }
    private void drawPolygone2(){
        PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngList2).strokeWidth(2).strokeColor(Color.GREEN);
        gmap.addPolygon(polygonOptions);
    }

    private List<LatLng> getLatLngListFromString(String coordinatesString) {
        List<LatLng> latLngList = new ArrayList<>();
        String[] lines = coordinatesString.split("\n");
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length == 2) {
                String latitudeString = parts[0].trim().replace("Latitude: ", "");
                String longitudeString = parts[1].trim().replace("Longitude: ", "");
                try {
                    double latitude = Double.parseDouble(latitudeString);
                    double longitude = Double.parseDouble(longitudeString);
                    LatLng latLng = new LatLng(latitude, longitude);
                    latLngList.add(latLng);
                } catch (NumberFormatException e) {
                    // Handle parsing errors if needed
                }
            }
        }
        return latLngList;
    }

    @Override
    public void onWeatherResponse(String jsonResponse) throws JSONException {

        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray list = jsonObject.getJSONArray("list");

        // Extract city name
        String cityName = jsonObject.getJSONObject("city").getString("name");

        location = cityName;
        // Use the city name as needed
        fieldLocaiton.setText(location);
        maxTemps = new ArrayList<>();
        minTemps = new ArrayList<>();
        precipitations = new ArrayList<>();
        windSpeeds = new ArrayList<>();
        dates = new ArrayList<>();
        times = new ArrayList<>();

        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            JSONObject main = item.getJSONObject("main");
            JSONArray weather = item.getJSONArray("weather");
            JSONObject weatherItem = weather.getJSONObject(0);
            JSONObject wind = item.getJSONObject("wind");

            double tempMax = main.getDouble("temp_max");
            double tempMin = main.getDouble("temp_min");
            double precipitation = item.getDouble("pop");
            double windSpeed = wind.getDouble("speed");
            String dateTime = item.getString("dt_txt");
            String[] parts = dateTime.split(" ");
            String date = parts[0];
            String time = parts[1];

            maxTemps.add(tempMax);
            minTemps.add(tempMin);
            precipitations.add(precipitation);
            windSpeeds.add(windSpeed);
            dates.add(date);
            times.add(time);
        }

        updateWeatherData();
    }

    @Override
    public void onWeatherError(String message) {
        Toast.makeText(requireActivity(), "No Weather available", Toast.LENGTH_SHORT).show();
    }
    private void updateWeatherData() {
        weatherList.clear();

        for (int i = 0; i < maxTemps.size(); i++) {
            double tempMax = maxTemps.get(i);
            double tempMin = minTemps.get(i);
            double pre = precipitations.get(i);
            double wind = windSpeeds.get(i);
            String date = dates.get(i);
            String time = times.get(i);
            Weather weather = new Weather(tempMax,tempMin,pre,wind, date, time);
            weatherList.add(weather);
        }
            MAX = maxTemps.get(0);
            MIN = minTemps.get(0);
            PRE = precipitations.get(0);
            WIND = windSpeeds.get(0);
            int maxi = (int) (MAX - 273.15);
            fieldTemp.setText(String.valueOf(maxi + "°C"));
            adapter.notifyDataSetChanged();
            //Toast.makeText(getActivity(), "No weather data available", Toast.LENGTH_SHORT).show();
    }
    public static int argmax(float[] array) {
        int maxIndex = 0;
        float maxValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxIndex = i;
                maxValue = array[i];
            }
        }
        return maxIndex;
    }

    public static MappedByteBuffer loadModelFile(Context context) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd("weather.tflite");
        FileInputStream fileInputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
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

                    // Check if the marker is inside the polygon
                    boolean isInsidePolygon = PolyUtil.containsLocation(latLng, latLngList, true);
                    if (!isInsidePolygon) {
                        // The marker is inside the polygon
                        Toast.makeText(getActivity(), "Marker is inside the polygon", Toast.LENGTH_SHORT).show();
                        int radius = 10; // Adjust the radius as needed
                        BitmapDescriptor markerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                        Bitmap iconBitmap = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.RGB_565);
                        Canvas canvas = new Canvas(iconBitmap);
                        Paint paint = new Paint();
                        paint.setColor(Color.WHITE);
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawCircle(radius, radius, radius, paint);
                        markerIcon = BitmapDescriptorFactory.fromBitmap(iconBitmap);

                        // Add a marker at the captured location with the marker icon
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(latLng)
                                .icon(markerIcon);
                        marker = gmap.addMarker(markerOptions);
                        markerList.add(marker);
                        latLngList2.add(latLng);

                        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

                        // Draw the polygon if there are at least 3 positions
                        if (latLngList2.size() >= 3) {
                            drawPolygone2();
                        }
                    } else {
                        // The marker is outside the polygon
                        Toast.makeText(getActivity(), "Marker is outside the polygon", Toast.LENGTH_SHORT).show();
                    }

                    // Create a custom marker icon (green circle)

                }
            }
        });
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

    private List<LatLng> getLatLngList() {
        // Return the list of LatLng points of the polygon
        return latLngList;
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
    private String getMarkerCoordinatesString2() {
        StringBuilder stringBuilder = new StringBuilder();
        for (LatLng latLng : latLngList2) {
            stringBuilder.append("Latitude: ")
                    .append(latLng.latitude)
                    .append(", Longitude: ")
                    .append(latLng.longitude)
                    .append("\n");
        }
        return stringBuilder.toString();
    }
    private void showDatePickerDialog() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog and set the listener
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Update the EditText with the selected date
                String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                dateEditText.setText(selectedDate);
            }
        }, year, month, day);

        // Show the dialog
        datePickerDialog.show();
    }

}
