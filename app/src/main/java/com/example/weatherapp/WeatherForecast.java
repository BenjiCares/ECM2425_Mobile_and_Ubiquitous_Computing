package com.example.weatherapp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class WeatherForecast {
    private int Temp;
    private String Description;
    private String WeatherPicCode;
    private String DayTime;

    // Constructor to initialize the WeatherForecast object
    public WeatherForecast(String temp, String description, String weatherPicCode, String dateTime) {
        // Convert temperature to integer and round it
        double temp1 = Double.parseDouble(temp);
        Temp = (int) Math.round(temp1);
        // Capitalize the first letter of each word in the description
        Description = capitalizeEachWord(description);
        // Prepend "img" to the weather icon code
        WeatherPicCode = "img" + weatherPicCode;
        // Format the date and time
        DayTime = formatDateTime(dateTime);

    }
    // Capitalize the first letter of each word in a string
    public static String capitalizeEachWord(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        String[] words = input.split("\\s");

        for (String word : words) {
            if (!word.isEmpty()) {
                char firstChar = Character.toUpperCase(word.charAt(0));
                result.append(firstChar).append(word.substring(1)).append(" ");
            }
        }

        return result.toString().trim();
    }

    // Format the date and time string
    public static String formatDateTime(String input) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(input, formatter);

        // Format the date and time as "DayOfWeek Hour:Minute"
        return dateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) +
                " " + dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    // Getter methods to retrieve object properties
    public int getTemp() {
        return Temp;
    }

    public String getDescription() {
        return Description;
    }

    public String getWeatherPicCode() {
        return WeatherPicCode;
    }

    public String getDayTime() {
        return DayTime;
    }
}
