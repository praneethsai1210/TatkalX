package com.tatkalx.booking;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    EditText fromStation, toStation, journeyDate;
    Button searchTrains, btnBookingHistory;
    View btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fromStation = findViewById(R.id.fromStation);
        toStation = findViewById(R.id.toStation);
        journeyDate = findViewById(R.id.journeyDate);
        searchTrains = findViewById(R.id.searchTrains);
        btnBookingHistory = findViewById(R.id.btnBookingHistory);
        btnProfile = findViewById(R.id.btnProfile);

        // Date Picker
        journeyDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePicker = new DatePickerDialog(
                    MainActivity.this,
                    (view, year, month, dayOfMonth) -> {

                        String date = dayOfMonth + "/" +
                                (month + 1) + "/" + year;

                        journeyDate.setText(date);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePicker.getDatePicker().setMinDate(
                    System.currentTimeMillis() - 1000
            );

            datePicker.show();
        });

        // Search Trains
        searchTrains.setOnClickListener(v -> {

            String from = fromStation.getText().toString().trim();
            String to = toStation.getText().toString().trim();
            String date = journeyDate.getText().toString().trim();

            if (from.isEmpty()) {
                fromStation.setError("Enter departure station");
                fromStation.requestFocus();
                return;
            }

            if (to.isEmpty()) {
                toStation.setError("Enter destination station");
                toStation.requestFocus();
                return;
            }

            if (from.equalsIgnoreCase(to)) {
                toStation.setError("Choose a different destination");
                return;
            }

            if (date.isEmpty()) {
                Toast.makeText(this,
                        "Please select journey date",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(
                    MainActivity.this,
                    TrainResultsActivity.class
            );

            intent.putExtra("FROM_STATION", from);
            intent.putExtra("TO_STATION", to);
            intent.putExtra("JOURNEY_DATE", date);

            startActivity(intent);
        });

        // Booking History Button
        if (btnBookingHistory != null) {
            btnBookingHistory.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, BookingHistoryActivity.class);
                startActivity(intent);
            });
        }

        // Profile Badge click opens Booking History
        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, BookingHistoryActivity.class);
                startActivity(intent);
            });
        }
    }
}
