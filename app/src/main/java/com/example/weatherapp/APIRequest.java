package com.example.weatherapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class APIRequest extends AsyncTask<String, Void, String> {
    // OpenWeatherMap API key for making requests
    private static final String API_KEY = "147e93e240e81ea392ddd8bc7e833012";
    private Context context;

    // Constructor to receive the application context
    public APIRequest(Context context) {
        this.context = context;
    }

    // Background task to make API request in the background
    @Override
    protected String doInBackground(String... params) {
        // Retrieve API URL and preferred unit
        String apiUrl = params[0];
        SharedPreferences preferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String unit = preferences.getString("unit", "");

        try {
            // Construct the complete API URL with key and units
            URL url = new URL(apiUrl + "&appid=" + API_KEY + "&units=" + unit);
            Log.e("URL", "URL is " + url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try {
                // Get the input stream and read the API response
                InputStream in = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                return result.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            // Handle API call failure
            Log.e("OpenWeatherMapApiTask", "Error making API call: " + e.getMessage());
            return null;
        }
    }

    // Method called after API request is completed
    @Override
    protected void onPostExecute(String result) {
        // Handle the API response here
        if (result != null) {
            try {
                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(result);
                JSONArray listArray = jsonResponse.getJSONArray("list");
                JSONObject locationDetails = jsonResponse.getJSONObject("city");

                if (listArray.length() > 0) {
                    // Process the response data and create a list
                    ArrayList<String> list = new ArrayList<>();
                    //Initially add the location
                    String location = locationDetails.getString("name");
                    String country = locationDetails.getString("country");
                    list.add(location + ", " + country);
                    //Then add temp, weather type, icon code and date time for every future forecast
                    for(int x = 0; x < listArray.length(); x++){
                        JSONObject Entry = listArray.getJSONObject(x);
                        JSONArray weatherArray = Entry.getJSONArray("weather");
                        JSONObject weather = weatherArray.getJSONObject(0);
                        JSONObject mainObject = Entry.getJSONObject("main");
                        double temperature = mainObject.getDouble("temp");
                        String temp = String.valueOf(temperature);
                        String weatherType = weather.getString("description");
                        String iconCode = weather.getString("icon");
                        String dateTime = Entry.getString("dt_txt");
                        list.add(temp);
                        list.add(weatherType);
                        list.add(iconCode);
                        list.add(dateTime);
                    }
                    // Broadcast the API result to the application components
                    Intent intent = new Intent("API_RESULT");
                    intent.putExtra("list", list);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                } else {
                    // Log error if the list array is empty
                    Log.e("OpenWeatherMapApiTask", "Empty list array in the response");
                }
            } catch (JSONException e) {
                // Log error if JSON parsing fails
                Log.e("OpenWeatherMapApiTask", "Error parsing JSON: " + e.getMessage());
            }
        } else {
            // Log error if the API call failed
            Log.e("OpenWeatherMapApiTask", "API call failed");
        }
    }

}
