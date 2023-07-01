package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
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
    public static String FIELD_DATA ;
    MenuItem stat ;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        bottomNavigationView = findViewById(R.id.navigation);


        SharedPreferences sharedPreferences2 = this.getSharedPreferences(LoignEmailOrUsername.A_EMAIL, 0);
        String email = sharedPreferences2.getString("email", "");

        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, endpoint.fetch_field_data, response -> {
            // Create a SQLiteOpenHelper instance to handle database operations
            SQLite dbHelper = new SQLite(this);

            try {
                JSONArray jsonArray = new JSONArray(response); // Convert the response string to a JSONArray
                if (jsonArray.length() > 0) {
                    // Open the SQLite database for writing
                    SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();

                    // Delete all existing data from the table
                    sqliteDatabase.delete("FIELD_DATA", null, null);

                    // Iterate over each object in the JSONArray
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject row = jsonArray.getJSONObject(i);

                        // Retrieve values from the row
                        String coordinates = row.getString("cordinates");
                        //Toast.makeText(this, coordinates, Toast.LENGTH_SHORT).show();
                        String keyField = row.getString("keyField");
                        //Toast.makeText(this, keyField, Toast.LENGTH_SHORT).show();
                        String fieldName = row.getString("fieldName");

                        // Create a ContentValues object to hold the row data
                        ContentValues values = new ContentValues();
                        values.put("cordinates", coordinates);
                        values.put("keyField", keyField);
                        values.put("fieldName", fieldName);

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
                    Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest2);


        imageButton = findViewById(R.id.set);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new HOME()).commit();





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
                SETTINGS settingsFragment = new SETTINGS();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, settingsFragment)
                        .commit();

                imageButton.setVisibility(View.INVISIBLE);
                bottomNavigationView.setVisibility(View.INVISIBLE);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem statMenuItem = menu.findItem(R.id.stat);
        statMenuItem.setVisible(true);

        return true;
    }


}