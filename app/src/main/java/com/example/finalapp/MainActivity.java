package com.example.finalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences(LoignEmailOrUsername.PREFS_NAME,0);
        boolean hasLoggedIn = sharedPreferences.getBoolean("hasLoggedIn",false);
        if (hasLoggedIn)
        {
            Intent intent = new Intent(MainActivity.this,MAIN.class);
            startActivity(intent);
            finish();
        }
        else
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.body ,new Firstpage()).commit();
        }
    }

}