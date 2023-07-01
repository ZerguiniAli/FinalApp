package com.example.finalapp;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class WeatherRequest {
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/forecast";
    private static final String API_KEY = "71b5db9ac9616ff268e1d117dc018d55";

    private RequestQueue requestQueue;
    private WeatherListener listener;

    public interface WeatherListener {
        void onWeatherResponse(String jsonResponse) throws JSONException;

        void onWeatherError(String message);
    }

    public WeatherRequest(RequestQueue requestQueue, WeatherListener listener) {
        this.requestQueue = requestQueue;
        this.listener = listener;
    }

    public void getWeatherData(double latitude, double longitude) {
        String url = BASE_URL + "?lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Inside the onResponse method
                        Log.d("WeatherResponse", response);
                        try {
                            listener.onWeatherResponse(response);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onWeatherError(error.getMessage());
                        Log.e("VolleyError", error.toString());
                    }
                });

        requestQueue.add(stringRequest);
    }
}