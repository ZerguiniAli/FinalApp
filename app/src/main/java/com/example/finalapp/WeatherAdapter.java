package com.example.finalapp;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalapp.Weather;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private List<Weather> weatherList;
    private static Context context;

    double MAX, MIN, PRE, WIND;

    private Interpreter interpreter;

    public WeatherAdapter(List<Weather> weatherList, Context context, Interpreter interpreter) {
        this.weatherList = weatherList;
        this.context = context;
        this.interpreter = interpreter;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        Weather weather = weatherList.get(position);
        holder.tempMaxTextView.setText(String.valueOf((int) (weather.getTempMax()- 273.15)));
        holder.dateTextView.setText(formatDate(weather.getDate()));
        holder.timeTextView.setText(formatTime(weather.getTime()));
        MAX = weather.getTempMax();
        MIN = weather.getTempMin();
        PRE = weather.getPre();
        WIND = weather.getWind();

        try {
            Interpreter interpreter = new Interpreter(HOME.loadModelFile(context));
            float[][] inputData = {
                    {(float) PRE, (float) (MAX - 273.15), (float) (MIN - 273.15), (float) WIND}
            };
            float[][] outputData = new float[1][5];
            interpreter.run(inputData, outputData);

            String[] weatherCategories = {"Drizzle", "Fog", "Rain", "Snow", "Sun"};
            int predictedIndex = HOME.argmax(outputData[0]);
            String predictedWeather = weatherCategories[predictedIndex];

            switch (predictedWeather) {
                case "Drizzle":
                    holder.drizzle.setVisibility(View.VISIBLE);
                    break;
                case "Fog":
                    holder.fog.setVisibility(View.VISIBLE);
                    break;
                case "Rain":
                    holder.rain.setVisibility(View.VISIBLE);
                    break;
                case "Snow":
                    holder.snow.setVisibility(View.VISIBLE);
                    break;
                case "Sun":
                    holder.sun.setVisibility(View.VISIBLE);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    private String formatDate(String date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());

        try {
            Date parsedDate = inputFormat.parse(date);
            return outputFormat.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    private String formatTime(String time) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        try {
            Date parsedTime = inputFormat.parse(time);
            return outputFormat.format(parsedTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }

    public static class WeatherViewHolder extends RecyclerView.ViewHolder {
        TextView tempMaxTextView;
        TextView dateTextView;
        TextView timeTextView;

        RelativeLayout rain, snow, drizzle, fog, sun, moon;


        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            tempMaxTextView = itemView.findViewById(R.id.tempMaxTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            rain = itemView.findViewById(R.id.rain1);
            snow = itemView.findViewById(R.id.snow1);
            fog = itemView.findViewById(R.id.fog1);
            sun = itemView.findViewById(R.id.sun1);
            drizzle = itemView.findViewById(R.id.drizzle1);
            moon = itemView.findViewById(R.id.moon1);
        }
    }
}
