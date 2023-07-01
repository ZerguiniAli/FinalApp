package com.example.finalapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLite extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 1;

    public SQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create your tables here
        String createTableQuery = "CREATE TABLE IF NOT EXISTS FIELD_DATA (cordinates TEXT, keyField TEXT,fieldName Text )";
        String createTableQuery2 = "CREATE TABLE IF NOT EXISTS PARITION_DATA (KeyPartition TEXT, cordinates TEXT,Color Text )";
        String createTableQuery3 = "CREATE TABLE IF NOT EXISTS FIELD_NAME_DATA (fieldName TEXT )";
        String createTableQuery4 = "CREATE TABLE IF NOT EXISTS WEARHOUSE_DATA (ProductId TEXT, ProdcutName TEXT,Quantity  Text )";

        db.execSQL(createTableQuery);
        db.execSQL(createTableQuery2);
        db.execSQL(createTableQuery3);
        db.execSQL(createTableQuery4);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades here if needed
    }
}
