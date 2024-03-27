package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WeatherViewAdapter extends RecyclerView.Adapter<WeatherViewHolder> {

    // Member variables for the adapter
    Context context;
    ArrayList<WeatherForecast> forecasts;
    String currentCode;

    // Constructor for the adapter, initializing member variables
    public WeatherViewAdapter(Context context, ArrayList<WeatherForecast> forecasts, String currentCode) {
        this.context = context;
        this.forecasts = forecasts;
        this.currentCode = currentCode;
    }

    // Called when RecyclerView needs a new ViewHolder to represent an item
    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WeatherViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view, parent, false));
    }

    // Called to display the data at a specified position
    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        // Get the unit preference from SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String unit = preferences.getString("unit", "");
        String unitShort = null;
        // Map unit preference to a short form for temperature display
        if (unit.equals("metric")) {
            unitShort = "C";
        } else if (unit.equals("imperial")) {
            unitShort = "F";
        }
        // Set the data to the corresponding views in the ViewHolder
        holder.tempText.setText(forecasts.get(position).getTemp() + "°" + unitShort);
        holder.weatherDescText.setText(forecasts.get(position).getDescription());
        holder.dayTimeText.setText((forecasts.get(position).getDayTime()));
        //Retrieve the resourceID of the png which matches the iconCode
        Log.e("code", forecasts.get(position).getWeatherPicCode());
        @SuppressLint("DiscouragedApi") int resourceId = context.getResources().getIdentifier(forecasts.get(position).getWeatherPicCode(), "drawable", context.getPackageName());
        Log.e("resourceID", resourceId + "");
        holder.weatherImage.setImageResource(resourceId);

        // Change text color based on dark or light UI background
        if(currentCode.equals("img01n") || currentCode.equals("img02n")){
            holder.tempText.setTextColor(context.getResources().getColor(R.color.white));
            holder.weatherDescText.setTextColor(context.getResources().getColor(R.color.white));
            holder.dayTimeText.setTextColor(context.getResources().getColor(R.color.white));
        }
        else{
            holder.tempText.setTextColor(context.getResources().getColor(R.color.black));
            holder.weatherDescText.setTextColor(context.getResources().getColor(R.color.black));
            holder.dayTimeText.setTextColor(context.getResources().getColor(R.color.black));
    }}

    // Returns the total number of items in the data set
    @Override
    public int getItemCount() {
        return forecasts.size();
    }
}
