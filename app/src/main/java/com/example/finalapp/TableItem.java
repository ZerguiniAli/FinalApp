package com.example.finalapp;

public class TableItem {
    private String coordinates;
    private String keyField;

    private String FieldName;

    public TableItem(String FieldName, String coordinates, String keyField ) {
        this.coordinates = coordinates;
        this.keyField = keyField;
        this.FieldName =  FieldName;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public String getFieldname() {return FieldName;}
    public String getKeyField(){return keyField;}

}
