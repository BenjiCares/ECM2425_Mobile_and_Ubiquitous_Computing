package com.example.weatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R.color;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;

    private TextView temperatureTextView;
    private TextView weatherTypeTextView;
    private ImageView iconCodeIMG;
    private RelativeLayout layout;
    private Button locationButton;
    private RecyclerView recyclerView;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize UI elements
        temperatureTextView = findViewById(R.id.CurrentTemperature);
        weatherTypeTextView = findViewById(R.id.CurrentWeather);
        locationButton = findViewById(R.id.location);
        iconCodeIMG = findViewById(R.id.WeatherIMG);
        layout = (RelativeLayout) findViewById(R.id.rl);
        recyclerView = findViewById(R.id.recyclerViewTemperature);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Register the BroadcastReceiver for settings updates
        LocalBroadcastManager.getInstance(this).registerReceiver(settingsUpdateReceiver, new IntentFilter("SETTINGS_UPDATED"));
        // Initialize default unit settings if not present
        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        if (!preferences.contains("unit")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("unit", "metric");
            editor.apply();
        }
        // Start the process of getting the weather forecast
        weatherUpdate();
    }

    // BroadcastReceiver to receive API result
    private final BroadcastReceiver apiResultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract API result data
            if ("API_RESULT".equals(intent.getAction())) {
                ArrayList<String> list = intent.getStringArrayListExtra("list");

                if (list != null && list.size() == 161) {
                    String location = list.get(0);
                    // Update UI with weather data
                    if (!location.equals(", ")) {
                        locationButton.setText("⚲ " + location);
                    }
                    // Get current shared preferences for unit
                    SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
                    String unit = preferences.getString("unit", "");
                    String unitShort = null;
                    if (unit.equals("metric")) {
                        unitShort = "C";
                    } else if (unit.equals("imperial")) {
                        unitShort = "F";
                    }
                    // Set the text to the current weather
                    WeatherForecast current = new WeatherForecast((list.get(1)), (list.get(2)), list.get(3), list.get(4));
                    temperatureTextView.setText(current.getTemp() + "°" + unitShort);
                    weatherTypeTextView.setText(current.getDescription());
                    // Retrieve the resourceID of the png which matches the imageName
                    String imageName = current.getWeatherPicCode();
                    @SuppressLint("DiscouragedApi") int resourceId = getResources().getIdentifier(imageName, "drawable", getPackageName());
                    iconCodeIMG.setImageResource(resourceId);
                    // Set text color based on dark on light UI
                    temperatureTextView.setTextColor(getResources().getColor(color.black));
                    weatherTypeTextView.setTextColor(getResources().getColor(color.black));
                    locationButton.setTextColor(getResources().getColor(color.black));
                    //Set UI background based on current weather
                    switch (imageName) {
                        case "img01d":
                        case "img02d":
                            layout.setBackgroundResource(R.drawable.sunny);
                            break;
                        case "img01n":
                        case "img02n":
                            layout.setBackgroundResource(R.drawable.night);
                            temperatureTextView.setTextColor(getResources().getColor(color.white));
                            weatherTypeTextView.setTextColor(getResources().getColor(color.white));
                            locationButton.setTextColor(getResources().getColor(color.white));
                            break;
                        case "img03d":
                        case "img03n":
                        case "img04d":
                        case "img04n":
                            layout.setBackgroundResource(R.drawable.cloudy);
                            break;
                        case "img09d":
                        case "img09n":
                        case "img10d":
                        case "img10n":
                        case "img50d":
                        case "img50n":
                            layout.setBackgroundResource(R.drawable.rain);
                            break;
                        case "img11n":
                        case "img11d":
                            layout.setBackgroundResource(R.drawable.thunder);
                            break;
                        case "img13n":
                        case "img13d":
                            layout.setBackgroundResource(R.drawable.snow);
                            break;
                    }
                    //Create list of future weather-forecasts to be sent to recycler view
                    ArrayList<WeatherForecast> futureForecasts = new ArrayList<>();
                    for (int x = 5; x < 161; x += 4) {
                        futureForecasts.add(new WeatherForecast(list.get(x), list.get(x + 1), list.get(x + 2), list.get(x + 3)));
                    }
                    recyclerView.setAdapter(new WeatherViewAdapter(getApplicationContext(), futureForecasts, imageName));
                }
            }
        }
    };

    // BroadcastReceiver to receive settings update
    private BroadcastReceiver settingsUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("SETTINGS_UPDATED".equals(intent.getAction())) {
                // Update weather data when settings are updated
                weatherUpdate();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // Update weather data when the activity is resumed
        weatherUpdate();
    }


    // Method to initiate weather data update
    public void weatherUpdate() {
        Log.e("WeatherUpdate", "Updating weather!");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
    }

    // Get the current location
    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            // Set the latitude and longitude
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            callAPI(latitude, longitude);
                        } else {
                            Log.e("Location", "Location is null");
                        }
                    })
                    .addOnFailureListener(this, e -> Log.e("Location", "Error getting location", e));
        } else {
            // Handle the case where permissions are not granted
            Log.e("Location", "No permissions");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    // Handle the result of location permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start getting location
                weatherUpdate();
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Make API call to get weather data
    private void callAPI(double latitude, double longitude) {
        LocalBroadcastManager.getInstance(this).registerReceiver(apiResultReceiver, new IntentFilter("API_RESULT"));
        //Input latitude and longitude into the api url
        String apiUrl = ("https://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude);
        APIRequest apiTask = new APIRequest(this);
        apiTask.execute(apiUrl);
    }

    // Create an Intent to start SettingsActivity when the settings button is clicked
    public void openSettingsActivity(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);

    }

    //Implicit intent triggered when location is clicked on to open location in google maps
    public void openGoogleMaps(View view) {
        Log.e("GM", "Opening google maps");
        // Create a Uri with the website URL including the latitude and longitude
        Uri mapsWebsiteUri = Uri.parse("https://www.google.com/maps?q=" + latitude + "," + longitude);
        // Create the Intent and start it
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, mapsWebsiteUri);
        try {
            startActivity(websiteIntent);
        }catch(Exception e){
            // In case of any errors such as no browser installed
            Log.e("GM", "Failed to open google maps");
        }

    }
}
