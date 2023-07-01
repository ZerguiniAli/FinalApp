package com.example.finalapp;

class Weather {
    private double tempMax , tempMin , precipation , wind;

    private int TempMAx ;
    private String date;
    private String time;

    public Weather(double tempMax,double tempMin,double precipation , double wind, String date, String time) {
        this.tempMax = tempMax;
        this.date = date;
        this.time = time;
        this.tempMin = tempMin;
        this.precipation = precipation;
        this.wind = wind ;
    }

    public double getTempMax() {
        return tempMax;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
    public double getTempMin(){
        return tempMin;
    }
    public double getPre(){
        return precipation;
    }
    public double getWind(){
        return wind;
    }
}

