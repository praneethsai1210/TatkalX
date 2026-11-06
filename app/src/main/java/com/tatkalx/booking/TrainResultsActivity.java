package com.tatkalx.booking;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TrainResultsActivity extends AppCompatActivity {

    LinearLayout trainContainer;
    String from, to, date;

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_results);

        trainContainer = findViewById(R.id.trainContainer);

        from = getIntent().getStringExtra("FROM_STATION");
        to = getIntent().getStringExtra("TO_STATION");
        date = getIntent().getStringExtra("JOURNEY_DATE");

        if (from == null || from.trim().isEmpty()) from = "Hyderabad";
        if (to == null || to.trim().isEmpty()) to = "Bengaluru";
        if (date == null || date.trim().isEmpty()) date = "Select date";

        TextView routeText = findViewById(R.id.routeText);
        TextView resultDate = findViewById(R.id.resultDate);

        routeText.setText(from.toUpperCase() + "  →  " + to.toUpperCase());
        resultDate.setText("Journey Date: " + date);

        displayTrains();
    }

    private void displayTrains() {
        addTrain(
                "12727",
                "Godavari Express",
                "17:30",
                "02:00",
                "8h 30m",
                "Sleeper / 3A"
        );

        addTrain(
                "12760",
                "Charminar Express",
                "18:00",
                "06:00",
                "12h 00m",
                "Sleeper / 3A"
        );

        addTrain(
                "12604",
                "Chennai Express",
                "16:45",
                "05:30",
                "12h 45m",
                "Sleeper / 2A"
        );
    }

    private void addTrain(
            String number,
            String name,
            String departure,
            String arrival,
            String duration,
            String classes) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE);
        background.setCornerRadius(dp(16));
        background.setStroke(dp(1), Color.rgb(225, 232, 242));

        card.setBackground(background);
        card.setElevation(dp(3));

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(0, 0, 0, dp(16));
        card.setLayoutParams(cardParams);

        TextView trainName = makeText(
                name, 18, "#102747", true);
        card.addView(trainName);

        TextView trainNumber = makeText(
                "Train No: " + number, 12, "#8491A3", false);
        card.addView(trainNumber);

        LinearLayout timings = new LinearLayout(this);
        timings.setOrientation(LinearLayout.HORIZONTAL);
        timings.setGravity(Gravity.CENTER_VERTICAL);
        timings.setPadding(0, dp(14), 0, dp(14));

        TextView start = makeText(
                departure + "\nDeparture", 15, "#102747", true);

        TextView arrow = makeText(
                "   →   " + duration + "   →   ", 12, "#176BC5", true);

        TextView end = makeText(
                arrival + "\nArrival", 15, "#102747", true);

        timings.addView(start);
        timings.addView(arrow);
        timings.addView(end);

        card.addView(timings);

        TextView classInfo = makeText(
                classes + "  |  DEMO DATA",
                12,
                "#64748B",
                false
        );
        card.addView(classInfo);

        Button bookButton = new Button(this);
        bookButton.setText("BOOK NOW  →");
        bookButton.setTextColor(Color.WHITE);
        bookButton.setTextSize(14);
        bookButton.setTypeface(null, Typeface.BOLD);
        bookButton.setAllCaps(false);

        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setColor(Color.rgb(23, 101, 192));
        btnBg.setCornerRadius(dp(10));
        bookButton.setBackground(btnBg);
        bookButton.setBackgroundTintList(null);

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(48)
                );
        buttonParams.setMargins(0, dp(14), 0, 0);
        bookButton.setLayoutParams(buttonParams);

        // Click on book button opens passenger details
        bookButton.setOnClickListener(v -> proceedToPassengerDetails(name, number));

        card.addView(bookButton);

        // Click on card also opens passenger details
        card.setOnClickListener(v -> proceedToPassengerDetails(name, number));

        trainContainer.addView(card);
    }

    private void proceedToPassengerDetails(String name, String number) {
        Intent intent = new Intent(
                TrainResultsActivity.this,
                PassengerDetailsActivity.class
        );

        intent.putExtra("trainName", name);
        intent.putExtra("trainNumber", number);
        intent.putExtra("journeyDate", date);
        intent.putExtra("fromStation", from);
        intent.putExtra("toStation", to);

        startActivity(intent);
    }

    private TextView makeText(
            String value,
            int size,
            String color,
            boolean bold) {

        TextView text = new TextView(this);
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(Color.parseColor(color));

        if (bold) {
            text.setTypeface(null, Typeface.BOLD);
        }

        return text;
    }
}
