package com.example.finalapp;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HOME extends Fragment implements WeatherRequest.WeatherListener {

    private Interpreter interpreter;

    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private List<String> dates;
    private List<String> times;
    RelativeLayout rain, snow, drizzle, fog, sun, moon;
    private TextView temperatureMaxTextView, weather;
    private TextView temperatureMinTextView;
    private TextView precipitationTextView;
    private TextView windSpeedTextView;
    private List<Double> maxTemps;
    private List<Double> minTemps;
    private List<Double> precipitations;
    private List<Double> windSpeeds;
    TextView date, time;
    double MAX, MIN, PRE, WIND;
    private List<Weather> weatherList;
    private WeatherAdapter adapter;

    public HOME() {
        // Required empty public constructor
    }

    public static HOME newInstance() {
        return new HOME();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_h_o_m_e, container, false);
        date = view.findViewById(R.id.date);
        time = view.findViewById(R.id.time);
        temperatureMaxTextView = view.findViewById(R.id.Degree);
        weather = view.findViewById(R.id.Weather);
        rain = view.findViewById(R.id.rain);
        snow = view.findViewById(R.id.snow);
        fog = view.findViewById(R.id.fog);
        sun = view.findViewById(R.id.sun);
        drizzle = view.findViewById(R.id.drizzle);
        moon = view.findViewById(R.id.moon);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        weatherList = new ArrayList<>();
        adapter = new WeatherAdapter(weatherList, getActivity(), interpreter);
        recyclerView.setAdapter(adapter);

        Date currentDate = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);


        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String formattedTime = timeFormat.format(currentDate);

        date.setText(formattedDate);
        time.setText(formattedTime);

        requestQueue = Volley.newRequestQueue(getActivity());
        double latitude = 33.8080;
        double longitude = 2.8629;

        WeatherRequest weatherRequest = new WeatherRequest(requestQueue, this);
        weatherRequest.getWeatherData(latitude, longitude);

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

        try {
            Date startTime = timeFormat.parse("22:00");
            Date endTime = timeFormat.parse("05:30");
            Date currentTime = timeFormat.parse(timeFormat.format(currentDate));

            if ((currentTime.after(startTime) || currentTime.before(endTime)) && predictedWeather.equals("Sun")) {
                sun.setVisibility(View.INVISIBLE);
                moon.setVisibility(View.VISIBLE);
                weather.setText("Moon");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onWeatherResponse(String jsonResponse) {
        try {
            JSONObject response = new JSONObject(jsonResponse);
            JSONArray list = response.getJSONArray("list");

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

        } catch (JSONException e) {
            Log.e("WeatherException", e.toString());
            Toast.makeText(getActivity(), "Failed to parse weather data", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWeatherError(String message) {
        Toast.makeText(getActivity(), "No weather data available2", Toast.LENGTH_SHORT).show();
    }

    public static MappedByteBuffer loadModelFile(Context context) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd("weather.tflite");
        FileInputStream fileInputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
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

        if (!weatherList.isEmpty()) {
            MAX = maxTemps.get(0);
            MIN = minTemps.get(0);
            PRE = precipitations.get(0);
            WIND = windSpeeds.get(0);

            int maxi = (int) (MAX - 273.15);
            temperatureMaxTextView.setText(String.valueOf(maxi));

            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getActivity(), "No weather data available", Toast.LENGTH_SHORT).show();
        }
    }
}