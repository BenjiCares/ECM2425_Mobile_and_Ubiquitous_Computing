package com.example.weatherapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WeatherViewHolder extends RecyclerView.ViewHolder {
    // Declare member variables for views in the item layout
    ImageView weatherImage;
    TextView tempText, weatherDescText, dayTimeText;

    // Constructor for the ViewHolder, initializes views based on the item layout
    public WeatherViewHolder(@NonNull View itemView) {
        super(itemView);
        // Associate member variables with corresponding views in the item layout
        weatherImage=itemView.findViewById(R.id.weatherImageView);
        tempText=itemView.findViewById(R.id.temperatureText);
        weatherDescText=itemView.findViewById(R.id.weatherText);
        dayTimeText=itemView.findViewById(R.id.dayTimeText);
    }
}
