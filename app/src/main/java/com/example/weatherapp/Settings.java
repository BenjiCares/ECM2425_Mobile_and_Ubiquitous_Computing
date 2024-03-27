package com.example.weatherapp;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class Settings extends AppCompatActivity {

    private RadioGroup unitRadioGroup;
    private Button saveButton;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        // Initialize UI elements
        unitRadioGroup = findViewById(R.id.unitRadioGroup);

        // Load current settings
        loadSettings();
    }

    // Method called when the "Save" button is clicked
    public void saveSettings(android.view.View view) {
        // Get the ID of the selected radio button
        int selectedId = unitRadioGroup.getCheckedRadioButtonId();

        if (selectedId != -1) {
            // Find the selected radio button
            RadioButton selectedRadioButton = findViewById(selectedId);

            // Save selected unit to SharedPreferences
            SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            String unit = null;
            // Save as metric or imperial as they are used in the api request
            switch(selectedRadioButton.getText().toString()){
                case "Celsius":
                    unit = "metric";
                    break;
                case "Fahrenheit":
                    unit = "imperial";
                    break;
            }
            editor.putString("unit", unit);
            editor.apply();
            // Display a toast indicating that settings have been saved
            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();

            // Finish the activity and go back to the main activity
            Log.e("Sending", "Broadcasting");
            sendBroadcast(new Intent("SETTINGS_UPDATED"));
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("SETTINGS_UPDATED"));
            finish();
        } else {
            // Display a toast if no unit is selected
            Toast.makeText(this, "Please select a unit", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to load and set the current settings
    private void loadSettings() {
        //Get current settings from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String unit = preferences.getString("unit", "");

        if (unit.equals("metric")) {
            // Check the Celsius radio button if the unit is metric
            RadioButton celsiusRadioButton = findViewById(R.id.celsiusRadioButton);
            celsiusRadioButton.setChecked(true);
        } else if (unit.equals("imperial")) {
            // Check the Fahrenheit radio button if the unit is imperial
            RadioButton fahrenheitRadioButton = findViewById(R.id.fahrenheitRadioButton);
            fahrenheitRadioButton.setChecked(true);
        }
    }
}