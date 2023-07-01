package com.example.finalapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
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
 * Use the {@link FIELD#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FIELD extends Fragment implements TableAdapter.OnItemClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RelativeLayout re ;

    private ImageButton imageButton;

    private RecyclerView recyclerView;


    public FIELD() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FIELD.
     */
    // TODO: Rename and change types and number of parameters
    public static FIELD newInstance(String param1, String param2) {
        FIELD fragment = new FIELD();
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
        View view = inflater.inflate(R.layout.fragment_f_i_e_l_d, container, false);

        SharedPreferences sharedPreferences2 = getActivity().getSharedPreferences(LoignEmailOrUsername.A_EMAIL, 0);
        String email = sharedPreferences2.getString("email", "");

        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, endpoint.fetch_field_data, response -> {
            // Create a SQLiteOpenHelper instance to handle database operations
            SQLite dbHelper = new SQLite(getActivity());

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
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();

        List<TableItem> tableItems = new ArrayList<>();



        Cursor cursor = sqliteDatabase.rawQuery("SELECT * FROM FIELD_DATA", null);
        if (cursor != null) {
            int coordinatesIndex = cursor.getColumnIndex("cordinates");
            int keyFieldIndex = cursor.getColumnIndex("keyField");
            int fieldNameIndex = cursor.getColumnIndex("fieldName");
            while (cursor.moveToNext()) {
                String coordinates = (coordinatesIndex != -1) ? cursor.getString(coordinatesIndex) : "Default Coordinates";
                String keyField = (keyFieldIndex != -1) ? cursor.getString(keyFieldIndex) : "Default Key Field";
                String fieldName = (keyFieldIndex != -1) ? cursor.getString(fieldNameIndex) : "Default field name";
                TableItem item = new TableItem(fieldName,coordinates,keyField);
                tableItems.add(item);
            }

            cursor.close();
        }
        sqliteDatabase.close();


        TableAdapter adapter = new TableAdapter(tableItems, new TableAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TableItem item) {
                String fieldName = item.getFieldname();
                String cord = item.getCoordinates();
                String keyField = item.getKeyField();
                Bundle bundle = new Bundle();
                bundle.putString("fieldName", fieldName);
                bundle.putString("cordinates", cord);
                bundle.putString("keyField",keyField);
                Fragment PRT = new Partition();

                PRT.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.field, PRT)
                        .commit();
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);


        imageButton = view.findViewById(R.id.imgbtn);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.field,new MAP()).commit();
            }
        });


        return view;
    }

    @Override
    public void onItemClick(TableItem item) {

    }
}