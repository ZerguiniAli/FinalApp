package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MAIN extends AppCompatActivity {

    public static BottomNavigationView bottomNavigationView ;

    public static ImageButton imageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        bottomNavigationView = findViewById(R.id.navigation);

        imageButton = findViewById(R.id.set);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new HOME()).commit();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);



        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("userName", "");


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                int ItemId = item.getItemId();

                imageButton.setVisibility(View.VISIBLE);

                if (ItemId == R.id.home) {
                    fragment = new HOME();
                } else if (ItemId == R.id.field) {
                    fragment = new FIELD();
                } else if (ItemId == R.id.stock) {
                    fragment = new STOCK();
                } else if (ItemId == R.id.stat) {
                    fragment = new STATISTICS();
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();

                return true;
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, endpoint.settings, response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response); // Convert the response string to a JSONArray
                        if (jsonArray.length() > 0) {
                            JSONObject jsonObject = jsonArray.getJSONObject(0); // Get the first object from the array
                            String USERNAME = jsonObject.getString("username"); // Extract the username value
                            String EMAIL = jsonObject.getString("email"); // Extract the email valueBundle bundle = new Bundle();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", USERNAME);
                            bundle.putString("email", EMAIL);

                            SETTINGS settingsFragment = new SETTINGS();
                            settingsFragment.setArguments(bundle);

                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment, settingsFragment)
                                    .commit();
                        } else {
                            Toast.makeText(getApplicationContext(), "No data available", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
                    Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }) {
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", username);
                        return params;
                    }
                };
                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);



                imageButton.setVisibility(View.INVISIBLE);
                bottomNavigationView.setVisibility(View.INVISIBLE);
            }
        });
    }
}