package com.tatkalx.booking;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class BookingReviewActivity extends AppCompatActivity {

    int navy = Color.rgb(16, 39, 71);
    int blue = Color.rgb(23, 101, 192);
    int bg = Color.rgb(243, 246, 250);

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(navy);
        getWindow().setNavigationBarColor(bg);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(bg);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(24), dp(20), dp(30));
        scroll.addView(root);

        TextView appTitle = makeText("TatkalX", 28, navy, true);
        root.addView(appTitle);

        TextView heading = makeText("Review Your Booking", 24, navy, true);

        LinearLayout.LayoutParams headingParams =
                new LinearLayout.LayoutParams(-1, -2);
        headingParams.topMargin = dp(22);
        root.addView(heading, headingParams);

        root.addView(makeText(
                "Please verify your journey details",
                14, Color.GRAY, false));

        // GET BOOKING DATA

        String trainName = safe(
                getIntent().getStringExtra("trainName"), "Selected Train");

        String trainNumber = safe(
                getIntent().getStringExtra("trainNumber"), "Not available");

        String journeyDate = safe(
                getIntent().getStringExtra("journeyDate"), "");

        String fromStation = safe(
                getIntent().getStringExtra("fromStation"), "");

        String toStation = safe(
                getIntent().getStringExtra("toStation"), "");

        String passengerName = safe(
                getIntent().getStringExtra("passengerName"), "Not provided");

        String passengerAge = safe(
                getIntent().getStringExtra("passengerAge"), "Not provided");

        String mobile = safe(
                getIntent().getStringExtra("mobile"), "Not provided");

        String gender = safe(
                getIntent().getStringExtra("gender"), "Not selected");

        String berth = safe(
                getIntent().getStringExtra("berth"), "No preference");

        String quota = safe(
                getIntent().getStringExtra("quota"), "Tatkal");

        String travelClass = safe(
                getIntent().getStringExtra("travelClass"), "Sleeper");

        // DEMO FARE CALCULATION

        int ticketFare;
        int tatkalCharge;

        switch (travelClass) {
            case "3A":
                ticketFare = 900;
                tatkalCharge = 200;
                break;

            case "2A":
                ticketFare = 1300;
                tatkalCharge = 300;
                break;

            default:
                ticketFare = 350;
                tatkalCharge = 100;
                break;
        }

        if (quota.equalsIgnoreCase("General")) {
            tatkalCharge = 0;
        }

        int convenienceFee = 20;

        int totalAmount =
                ticketFare + tatkalCharge + convenienceFee;

        // FINAL VALUES FOR PAYMENT BUTTON

        final String finalTrainName = trainName;
        final String finalTrainNumber = trainNumber;
        final String finalJourneyDate = journeyDate;
        final String finalFromStation = fromStation;
        final String finalToStation = toStation;

        final String finalTravelClass = travelClass;
        final String finalQuota = quota;

        final String finalPassengerName = passengerName;
        final String finalPassengerAge = passengerAge;
        final String finalMobile = mobile;
        final String finalGender = gender;
        final String finalBerth = berth;

        final int finalTotalAmount = totalAmount;

        // TRAIN CARD

        LinearLayout trainCard = createCard(root);

        trainCard.addView(makeText(
                "🚆  " + trainName, 19, navy, true));

        trainCard.addView(makeText(
                "Train Number: " + trainNumber,
                14, Color.GRAY, false));

        if (!fromStation.isEmpty() && !toStation.isEmpty()) {
            trainCard.addView(makeText(
                    "Route: " + fromStation + " → " + toStation,
                    14, navy, false));
        }

        if (!journeyDate.isEmpty()) {
            trainCard.addView(makeText(
                    "Journey Date: " + journeyDate,
                    14, navy, false));
        }

        addDivider(trainCard);

        trainCard.addView(makeText(
                "YOUR JOURNEY", 12, blue, true));

        trainCard.addView(makeText(
                "Travel Class: " + travelClass,
                14, navy, false));

        trainCard.addView(makeText(
                "Booking Quota: " + quota,
                14, navy, false));

        // PASSENGER CARD

        LinearLayout passengerCard = createCard(root);

        passengerCard.addView(makeText(
                "Passenger Information", 19, navy, true));

        addDivider(passengerCard);

        addRow(passengerCard, "Passenger Name", passengerName);
        addRow(passengerCard, "Age", passengerAge);
        addRow(passengerCard, "Mobile Number", mobile);
        addRow(passengerCard, "Gender", gender);
        addRow(passengerCard, "Berth Preference", berth);

        // FARE CARD

        LinearLayout fareCard = createCard(root);

        fareCard.addView(makeText(
                "Fare Summary", 19, navy, true));

        addDivider(fareCard);

        addRow(fareCard, "Ticket Fare",
                "₹" + ticketFare);

        addRow(fareCard, "Tatkal Charge",
                "₹" + tatkalCharge);

        addRow(fareCard, "Convenience Fee",
                "₹" + convenienceFee);

        addDivider(fareCard);

        TextView total = makeText(
                "Total Amount: ₹" + totalAmount,
                20, blue, true);

        fareCard.addView(total);

        // PAYMENT BUTTON

        Button paymentButton = new Button(this);
        paymentButton.setText("PROCEED TO PAYMENT  →");
        paymentButton.setTextColor(Color.WHITE);
        paymentButton.setTextSize(16);
        paymentButton.setTypeface(null, Typeface.BOLD);
        paymentButton.setAllCaps(false);

        GradientDrawable buttonBg = new GradientDrawable();
        buttonBg.setColor(blue);
        buttonBg.setCornerRadius(dp(12));

        paymentButton.setBackground(buttonBg);
        paymentButton.setBackgroundTintList(null);
        paymentButton.setMinHeight(dp(58));

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(-1, dp(58));

        buttonParams.topMargin = dp(25);

        root.addView(paymentButton, buttonParams);

        // SEND ALL DATA TO PAYMENT SCREEN

        paymentButton.setOnClickListener(v -> {

            android.content.Intent intent =
                    new android.content.Intent(
                            BookingReviewActivity.this,
                            PaymentActivity.class
                    );

            intent.putExtra("totalAmount", finalTotalAmount);

            intent.putExtra("trainName", finalTrainName);
            intent.putExtra("trainNumber", finalTrainNumber);
            intent.putExtra("journeyDate", finalJourneyDate);
            intent.putExtra("fromStation", finalFromStation);
            intent.putExtra("toStation", finalToStation);

            intent.putExtra("travelClass", finalTravelClass);
            intent.putExtra("quota", finalQuota);

            intent.putExtra("passengerName", finalPassengerName);
            intent.putExtra("passengerAge", finalPassengerAge);
            intent.putExtra("mobile", finalMobile);
            intent.putExtra("gender", finalGender);
            intent.putExtra("berth", finalBerth);

            startActivity(intent);
        });

        // BACK BUTTON

        Button backButton = new Button(this);
        backButton.setText("BACK TO PASSENGER DETAILS");
        backButton.setTextColor(navy);
        backButton.setAllCaps(false);

        root.addView(backButton,
                new LinearLayout.LayoutParams(-1, dp(52)));

        backButton.setOnClickListener(v -> finish());

        setContentView(scroll);
    }

    private String safe(String value, String fallback) {
        return value == null || value.trim().isEmpty()
                ? fallback : value;
    }

    private TextView makeText(
            String value, int size, int color, boolean bold) {

        TextView text = new TextView(this);
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(color);
        text.setGravity(Gravity.CENTER_VERTICAL);

        if (bold) {
            text.setTypeface(null, Typeface.BOLD);
        }

        return text;
    }

    private LinearLayout createCard(LinearLayout root) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(17), dp(18), dp(17), dp(18));

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.WHITE);
        drawable.setCornerRadius(dp(14));

        card.setBackground(drawable);
        card.setElevation(dp(3));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.topMargin = dp(20);

        root.addView(card, params);

        return card;
    }

    private void addRow(
            LinearLayout card, String label, String value) {

        TextView row = makeText(
                label + "\n" + value,
                14, navy, false);

        row.setPadding(0, dp(9), 0, dp(9));
        card.addView(row);
    }

    private void addDivider(LinearLayout card) {

        View divider = new View(this);
        divider.setBackgroundColor(Color.rgb(225, 230, 238));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, dp(1));

        params.topMargin = dp(12);
        params.bottomMargin = dp(12);

        card.addView(divider, params);
    }
}
