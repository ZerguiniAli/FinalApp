package com.example.finalapp;

import static android.content.Intent.getIntent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SETTINGS#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SETTINGS extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button Logout ;
    ImageButton ImgBack ;

    public static TextView Name , Email ;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SETTINGS() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SETTINGS.
     */
    // TODO: Rename and change types and number of parameters
    public static SETTINGS newInstance(String param1, String param2) {
        SETTINGS fragment = new SETTINGS();
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
        View view = inflater.inflate(R.layout.fragment_s_e_t_t_i_n_g_s, container, false);

        Logout = view.findViewById(R.id.logout);
        ImgBack = view.findViewById(R.id.set);
        Name = view.findViewById(R.id.name);
        Email = view.findViewById(R.id.email);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String username = bundle.getString("username");
            Name.setText(username);
            String email = bundle.getString("email");
            Email.setText(email);
            // Use the retrieved username and email values as needed
        }
        // Make a request to the PHP file


        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LoignEmailOrUsername.PREFS_NAME,0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("hasLoggedIn" , false);
                editor.commit();
                startActivity(intent);
            }
        });

        ImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment,new HOME()).commit();
                MAIN.bottomNavigationView.setVisibility(View.VISIBLE);
                MAIN.imageButton.setVisibility(View.VISIBLE);
            }
        });
        return view;

    }
}