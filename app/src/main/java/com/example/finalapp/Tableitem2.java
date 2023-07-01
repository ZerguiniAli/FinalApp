package com.example.finalapp;

public class Tableitem2 {
    private String keyPartition;
    private String coordinates;
    private String Color;

    public Tableitem2(String keyPartition, String coordinates, String Color ) {
        this.coordinates = coordinates;
        this.keyPartition = keyPartition;
        this.Color = Color;
    }
    public String getCoordinates() {
        return coordinates;
    }
    public String getKeyPartition() {return keyPartition;}
    public String getColor(){return Color;}
}
