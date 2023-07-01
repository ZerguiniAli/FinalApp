package com.example.finalapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link STOCK#newInstance} factory method to
 * create an instance of this fragment.
 */
public class STOCK extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText prdName , prdQT ;
    private  String selectedValue;
    private LinearLayout addrela;

    private Button btnadd;
    private ImageButton imgbtn;
    private RecyclerView recyclerView;

    public STOCK() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment STOCK.
     */
    // TODO: Rename and change types and number of parameters
    public static STOCK newInstance(String param1, String param2) {
        STOCK fragment = new STOCK();
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

    Spinner spinner;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_s_t_o_c_k, container, false);

        spinner = view.findViewById(R.id.spinner);
        btnadd = view.findViewById(R.id.add);
        prdName = view.findViewById(R.id.prdName);
        prdQT = view.findViewById(R.id.prdQT);
        addrela = view.findViewById(R.id.addproductlay);
        imgbtn = view.findViewById(R.id.imgbtn);
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addrela.setVisibility(View.VISIBLE);
            }
        });
        recyclerView = view.findViewById(R.id.recylerViewForStock);

        SharedPreferences sharedPreferences2 = getActivity().getSharedPreferences(LoignEmailOrUsername.A_EMAIL, 0);
        String email = sharedPreferences2.getString("email", "");

        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, endpoint.fetch_field_name, response -> {
            // Create a SQLiteOpenHelper instance to handle database operations
            SQLite dbHelper = new SQLite(getActivity());

            try {
                JSONArray jsonArray = new JSONArray(response); // Convert the response string to a JSONArray
                if (jsonArray.length() > 0) {
                    // Open the SQLite database for writing
                    SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();

                    // Delete all existing data from the table
                    sqliteDatabase.delete("FIELD_NAME_DATA", null, null);

                    // Iterate over each object in the JSONArray
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject row = jsonArray.getJSONObject(i);

                        // Retrieve values from the row
                        String fieldName = row.getString("fieldName");
                        //Toast.makeText(this, coordinates, Toast.LENGTH_SHORT).show();
                        // Create a ContentValues object to hold the row data
                        ContentValues values = new ContentValues();
                        values.put("fieldName", fieldName);
                        // Insert the row into the SQLite database
                        long newRowId = sqliteDatabase.insert("FIELD_NAME_DATA", null, values);

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
                params.put("email", email);
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest2);

        SQLite dbHelper = new SQLite(getActivity());
        // Retrieve field names from the SQLite database
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.rawQuery("SELECT * FROM FIELD_NAME_DATA", null);

        ArrayList<String> fieldNames = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            int fieldNameIndex = cursor.getColumnIndex("fieldName");
            do {
                String fieldName = (fieldNameIndex != -1) ? cursor.getString(fieldNameIndex) : "Default field name";
                fieldNames.add(fieldName);
            } while (cursor.moveToNext());

            cursor.close();
        }

        sqliteDatabase.close();

        // Create an ArrayAdapter using the fieldNames ArrayList
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, fieldNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Set the adapter for the Spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedValue = parent.getItemAtPosition(position).toString();
                // Perform actions with the selected value
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when nothing is selected
            }
        });

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ProdcutName = prdName.getText().toString();
                String Quantity = prdQT.getText().toString();

                StringRequest stringRequest2 = new StringRequest(Request.Method.POST, endpoint.get_field_name, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response1) {
                        // Create a SQLiteOpenHelper instance to handle database operations
                        SQLite dbHelper = new SQLite(getActivity());

                        try {
                            JSONArray jsonArray = new JSONArray(response1); // Convert the response string to a JSONArray
                            if (jsonArray.length() > 0) {
                                // Iterate over each object in the JSONArray
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject row = jsonArray.getJSONObject(i);
                                    String keyField = row.getString("keyField");
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, endpoint.add_new_product, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response2) {
                                            if (response2.equals("success")) {
                                                addrela.setVisibility(View.INVISIBLE);
                                            } else {
                                                Toast.makeText(getActivity(), "not done", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(getActivity(), "please check your internet connection", Toast.LENGTH_SHORT).show();
                                        }
                                    }) {
                                        protected Map<String, String> getParams() {
                                            Map<String, String> params = new HashMap<>();
                                            params.put("ProdcutName", ProdcutName);
                                            params.put("Quantity", Quantity);
                                            params.put("keyField", keyField);
                                            return params;
                                        }
                                    };
                                    VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

                                }

                                // Close the SQLite database
                            } else {
                                Toast.makeText(getActivity(), "No data", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("fieldName", selectedValue);
                        return params;
                    }
                };
                VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest2);
            }
        });

        recyclerView = view.findViewById(R.id.recylerViewForStock);
        List<Product> productList = new ArrayList<>(); // Create an empty list to hold the products
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, endpoint.get_field_keys, new Response.Listener<String>() {
            @Override
            public void onResponse(String response1) {
                SQLite dbHelper = new SQLite(getActivity());
                SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();

                try {
                    JSONArray jsonArray = new JSONArray(response1); // Convert the response string to a JSONArray
                    if (jsonArray.length() > 0) {
                        // Delete all existing data from the table
                        sqliteDatabase.delete("WEARHOUSE_DATA", null, null);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject row = jsonArray.getJSONObject(i);
                            String keyField = row.getString("keyField");

                            StringRequest stringRequest = new StringRequest(Request.Method.POST, endpoint.fetch_wearHoouse_data, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response2) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(response2);
                                        if (jsonArray.length() > 0) {
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject row = jsonArray.getJSONObject(i);
                                                String productId = row.getString("ProductId");
                                                String productName = row.getString("ProdcutName");
                                                String quantity = row.getString("Quantity");
                                                Toast.makeText(getActivity(), quantity, Toast.LENGTH_SHORT).show();
                                                ContentValues values = new ContentValues();
                                                values.put("ProductId", productId);
                                                values.put("ProdcutName", productName);
                                                values.put("Quantity", quantity);

                                                long newRowId = sqliteDatabase.insert("WEARHOUSE_DATA", null, values);

                                                if (newRowId == -1) {
                                                    // Handle insertion failure, if necessary
                                                }
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                                }
                            }) {
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("keyField", keyField);
                                    return params;
                                }
                            };
                            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
                        }
                    } else {
                        Toast.makeText(getActivity(), "No data", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

        sqliteDatabase = dbHelper.getReadableDatabase();

        List<Product> products = new ArrayList<>();

        ProductAdapter adapter2 = new ProductAdapter(products);
        recyclerView.setAdapter(adapter2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        Cursor cursor2 = sqliteDatabase.rawQuery("SELECT * FROM WEARHOUSE_DATA", null);
        if (cursor2 != null) {
            int ProductIdIndex = cursor2.getColumnIndex("ProductId");
            int ProdcutNameIndex = cursor2.getColumnIndex("ProdcutName");
            int QuantityIndex = cursor2.getColumnIndex("Quantity");
            while (cursor2.moveToNext()) {
                String prdID = (ProductIdIndex != -1) ? cursor2.getString(ProductIdIndex) : "Default Coordinates";
                String prdName = (ProdcutNameIndex != -1) ? cursor2.getString(ProdcutNameIndex) : "Default Key Field";
                String QT = (QuantityIndex != -1) ? cursor2.getString(QuantityIndex) : "Default field name";
                Product product = new Product(prdID,prdName,QT);
                products.add(product);
            }

            cursor2.close();
        }
        sqliteDatabase.close();
        adapter2.notifyDataSetChanged();
        return view;
    }
}