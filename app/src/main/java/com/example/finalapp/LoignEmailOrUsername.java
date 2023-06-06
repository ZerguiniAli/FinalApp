package com.example.finalapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoignEmailOrUsername#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoignEmailOrUsername extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button button ;

    RelativeLayout relativeLayout ;

    EditText us , ps ;
    TextView f1 , f2, c1, c2;

    ProgressBar progressBar;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoignEmailOrUsername() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoignEmailOrUsername.
     */
    // TODO: Rename and change types and number of parameters
    public static LoignEmailOrUsername newInstance(String param1, String param2) {
        LoignEmailOrUsername fragment = new LoignEmailOrUsername();
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
    public static String PREFS_NAME = "MyPrefsFile" ,USERNAME ="user" ;



    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_loign_email_or_username, container, false);
        button = view.findViewById(R.id.button);
        progressBar = view.findViewById(R.id.progressBar);
        relativeLayout = view.findViewById(R.id.wait);
        us = view.findViewById(R.id.username);
        ps = view.findViewById(R.id.password);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = us.getText().toString();
                String password = ps.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter username or password", Toast.LENGTH_SHORT).show();
                } else {
                    relativeLayout.setVisibility(view.VISIBLE);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, endpoint.login_url, response -> {
                        if (response.equals("login success")) {
                            relativeLayout.setVisibility(view.INVISIBLE);
                            Intent intent = new Intent(getActivity(), MAIN.class);
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LoignEmailOrUsername.PREFS_NAME,0);
                            SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences(LoignEmailOrUsername.USERNAME,0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                            editor.putBoolean("hasLoggedIn" , true);
                            editor1.putString("userName" , username);
                            editor.commit();
                            editor1.commit();
                            intent.putExtra("username" , username);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "username or password not available", Toast.LENGTH_SHORT).show();
                            relativeLayout.setVisibility(view.INVISIBLE);
                        }

                    }, error -> {
                        Toast.makeText(getActivity(), "please check your internet connection", Toast.LENGTH_SHORT).show();
                        relativeLayout.setVisibility(view.INVISIBLE);
                    }) {
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("username", username);
                            params.put("password", password);
                            return params;
                        }
                    };
                    VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
                }
            }
        });

        f1 = view.findViewById(R.id.forget);
        f1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.login ,new Login_help_email()).commit();
            }
        });
        f2 = view.findViewById(R.id.forget2);
        f2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.login ,new Login_help_email()).commit();
            }
        });
        c1 = view.findViewById(R.id.creat);
        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.login ,new Create_Account()).commit();
            }
        });

        c2 = view.findViewById(R.id.creat2);
        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.login ,new Create_Account()).commit();
            }
        });

        return view;
    }
}