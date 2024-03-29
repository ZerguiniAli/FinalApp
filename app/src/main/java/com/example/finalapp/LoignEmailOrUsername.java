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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    EditText em , ps ;
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
    public static String PREFS_NAME = "MyPrefsFile" , U_name = "USER_NAME" , A_EMAIL = "EMAIL";



    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_loign_email_or_username, container, false);
        button = view.findViewById(R.id.button);
        progressBar = view.findViewById(R.id.progressBar);
        relativeLayout = view.findViewById(R.id.wait);
        em = view.findViewById(R.id.username);
        ps = view.findViewById(R.id.password);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = em.getText().toString();
                String password = ps.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter username or password", Toast.LENGTH_SHORT).show();
                } else {
                    relativeLayout.setVisibility(view.VISIBLE);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, endpoint.login_url, response -> {
                        if (response.equals("login success")) {
                            relativeLayout.setVisibility(view.INVISIBLE);
                            Intent intent = new Intent(getActivity(), MAIN.class);
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LoignEmailOrUsername.PREFS_NAME,0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("hasLoggedIn" , true);
                            editor.commit();
                            startActivity(intent);
                            getActivity().finishAffinity();
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
                            params.put("email", email);
                            params.put("password", password);
                            return params;
                        }
                    };
                    VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
                    StringRequest stringRequest2 = new StringRequest(Request.Method.POST, endpoint.settings, response -> {
                        try {
                            JSONArray jsonArray = new JSONArray(response); // Convert the response string to a JSONArray
                            if (jsonArray.length() > 0) {
                                JSONObject jsonObject = jsonArray.getJSONObject(0); // Get the first object from the array
                                String USERNAME = jsonObject.getString("username"); // Extract the username value
                                String EMAIL = jsonObject.getString("email");
                                String PHONE = jsonObject.getString("phone_number");// Extract the email valueBundle bundle = new Bundle();
                                SharedPreferences Uname = getActivity().getSharedPreferences(U_name,0);
                                SharedPreferences Email = getActivity().getSharedPreferences(A_EMAIL , 0);
                                SharedPreferences.Editor Euname = Uname.edit();
                                SharedPreferences.Editor Eemail = Email.edit();
                                Euname.putString("user" , USERNAME);
                                Eemail.putString("email" , EMAIL);
                                Eemail.putString("phone" , PHONE);
                                Euname.commit();
                                Eemail.commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                        Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }) {
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("email", email);
                            return params;
                        }
                    };
                    VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest2);

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