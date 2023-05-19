package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MAIN extends AppCompatActivity {

    BottomNavigationView bottomNavigationView ;

    ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        bottomNavigationView = findViewById(R.id.navigation);

        imageButton = findViewById(R.id.set);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,new HOME()).commit();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                int ItemId = item.getItemId();

                imageButton.setVisibility(View.VISIBLE);

                if(ItemId == R.id.home)
                {
                    fragment = new HOME();
                }
                else if (ItemId == R.id.field)
                {
                    fragment = new FIELD();
                }
                else if (ItemId == R.id.stock)
                {
                    fragment = new STOCK();
                }
                else if (ItemId == R.id.stat)
                {
                    fragment = new STATISTICS();
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();

                return true;
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment,new SETTINGS()).commit();
                imageButton.setVisibility(View.INVISIBLE);
            }
        });
    }
}