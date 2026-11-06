package com.tatkalx.booking;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.pdf.PdfDocument;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookingConfirmationActivity extends AppCompatActivity {

    int navy = Color.rgb(16, 39, 71);
    int blue = Color.rgb(23, 101, 192);
    int green = Color.rgb(30, 150, 90);
    int bg = Color.rgb(243, 246, 250);

    String trainName, trainNumber, passengerName, passengerAge;
    String mobile, gender, berth, travelClass, quota;
    String fromStation, toStation, journeyDate;
    String demoPnr, bookingId;
    int amount;

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(navy);
        getWindow().setNavigationBarColor(bg);

        trainName = safe(getIntent().getStringExtra("trainName"), "Selected Train");
        trainNumber = safe(getIntent().getStringExtra("trainNumber"), "Not available");
        passengerName = safe(getIntent().getStringExtra("passengerName"), "Not provided");
        passengerAge = safe(getIntent().getStringExtra("passengerAge"), "Not provided");
        mobile = safe(getIntent().getStringExtra("mobile"), "Not provided");
        gender = safe(getIntent().getStringExtra("gender"), "Not provided");
        berth = safe(getIntent().getStringExtra("berth"), "No preference");
        travelClass = safe(getIntent().getStringExtra("travelClass"), "Sleeper");
        quota = safe(getIntent().getStringExtra("quota"), "Tatkal");
        fromStation = safe(getIntent().getStringExtra("fromStation"), "");
        toStation = safe(getIntent().getStringExtra("toStation"), "");
        journeyDate = safe(getIntent().getStringExtra("journeyDate"), "");

        amount = getIntent().getIntExtra("totalAmount", 470);

        if (savedInstanceState != null) {
            demoPnr = savedInstanceState.getString("demoPnr", "TX" + System.currentTimeMillis());
            bookingId = savedInstanceState.getString("bookingId", "TXB" + System.currentTimeMillis());
        } else {
            demoPnr = "TX" + System.currentTimeMillis();
            bookingId = "TXB" + System.currentTimeMillis();
        }

        // Save booking locally using SharedPreferences and prevent duplicates
        saveBookingToHistory();

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(bg);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(24), dp(20), dp(30));
        scroll.addView(root);

        root.addView(makeText("TatkalX", 29, navy, true));

        TextView heading = makeText(
                "Booking Confirmation", 24, navy, true);

        LinearLayout.LayoutParams hp =
                new LinearLayout.LayoutParams(-1, -2);
        hp.topMargin = dp(20);
        root.addView(heading, hp);

        // SUCCESS CARD
        LinearLayout successCard = createCard(root);

        TextView tick = makeText("✓", 42, green, true);
        tick.setGravity(Gravity.CENTER);
        successCard.addView(tick);

        TextView success = makeText(
                "DEMO BOOKING CONFIRMED", 19, green, true);
        success.setGravity(Gravity.CENTER);
        successCard.addView(success);

        TextView message = makeText(
                "Your simulated booking is successful!",
                14, navy, false);
        message.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams mp =
                new LinearLayout.LayoutParams(-1, -2);
        mp.topMargin = dp(8);
        successCard.addView(message, mp);

        // PNR CARD
        LinearLayout pnrCard = createCard(root);

        pnrCard.addView(makeText(
                "DEMO PNR NUMBER", 13, Color.GRAY, true));

        TextView pnr = makeText(demoPnr, 23, blue, true);

        LinearLayout.LayoutParams pp =
                new LinearLayout.LayoutParams(-1, -2);
        pp.topMargin = dp(8);
        pnrCard.addView(pnr, pp);

        pnrCard.addView(makeText(
                "Booking ID: " + bookingId,
                13, Color.GRAY, false));

        // TRAIN DETAILS
        LinearLayout trainCard = createCard(root);
        trainCard.addView(makeText("Train Details", 19, navy, true));
        addDivider(trainCard);

        addRow(trainCard, "Train Name", trainName);
        addRow(trainCard, "Train Number", trainNumber);
        if (!fromStation.isEmpty() && !toStation.isEmpty()) {
            addRow(trainCard, "Route", fromStation + " → " + toStation);
        }
        if (!journeyDate.isEmpty()) {
            addRow(trainCard, "Journey Date", journeyDate);
        }
        addRow(trainCard, "Travel Class", travelClass);
        addRow(trainCard, "Booking Quota", quota);

        // PASSENGER DETAILS
        LinearLayout passengerCard = createCard(root);
        passengerCard.addView(makeText("Passenger Details", 19, navy, true));
        addDivider(passengerCard);

        addRow(passengerCard, "Passenger Name", passengerName);
        addRow(passengerCard, "Age", passengerAge);
        addRow(passengerCard, "Mobile Number", mobile);
        addRow(passengerCard, "Gender", gender);
        addRow(passengerCard, "Berth Preference", berth);

        // PAYMENT DETAILS
        LinearLayout fareCard = createCard(root);
        fareCard.addView(makeText("Payment Details", 19, navy, true));
        addDivider(fareCard);

        addRow(fareCard, "Payment Status", "Demo Successful");
        addRow(fareCard, "Total Amount", "₹" + amount);

        TextView disclaimer = makeText(
                "This is a demonstration ticket generated by TatkalX. " +
                        "It is not a valid railway ticket or actual PNR.",
                12, Color.GRAY, false);

        LinearLayout.LayoutParams dpParams =
                new LinearLayout.LayoutParams(-1, -2);
        dpParams.topMargin = dp(15);
        root.addView(disclaimer, dpParams);

        // DOWNLOAD PDF BUTTON
        Button downloadButton = createButton("DOWNLOAD TICKET (PDF)", blue);
        addButton(root, downloadButton);

        downloadButton.setOnClickListener(v -> downloadTicket());

        // SHARE BUTTON
        Button shareButton = createButton("SHARE TICKET", green);
        addButton(root, shareButton);

        shareButton.setOnClickListener(v -> shareTicket());

        // VIEW BOOKING HISTORY BUTTON
        Button historyButton = createButton("VIEW BOOKING HISTORY", navy);
        addButton(root, historyButton);

        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    BookingConfirmationActivity.this,
                    BookingHistoryActivity.class);
            startActivity(intent);
        });

        // HOME BUTTON
        Button homeButton = createButton("BACK TO HOME", Color.rgb(70, 80, 95));
        addButton(root, homeButton);

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    BookingConfirmationActivity.this,
                    MainActivity.class);

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivity(intent);
            finish();
        });

        setContentView(scroll);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("demoPnr", demoPnr);
        outState.putString("bookingId", bookingId);
    }

    private void saveBookingToHistory() {
        try {
            JSONObject booking = new JSONObject();
            booking.put("trainName", trainName);
            booking.put("trainNumber", trainNumber);
            booking.put("passengerName", passengerName);
            booking.put("passengerAge", passengerAge);
            booking.put("mobile", mobile);
            booking.put("gender", gender);
            booking.put("berth", berth);
            booking.put("travelClass", travelClass);
            booking.put("quota", quota);
            booking.put("amount", String.valueOf(amount));
            booking.put("pnr", demoPnr);
            booking.put("bookingId", bookingId);

            String bookingDate = new SimpleDateFormat(
                    "dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date());
            booking.put("date", bookingDate);

            if (!journeyDate.isEmpty()) {
                booking.put("journeyDate", journeyDate);
            }
            if (!fromStation.isEmpty()) {
                booking.put("fromStation", fromStation);
            }
            if (!toStation.isEmpty()) {
                booking.put("toStation", toStation);
            }

            BookingHistoryActivity.saveBooking(this, booking);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        card.setPadding(dp(18), dp(18), dp(18), dp(18));

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.WHITE);
        drawable.setCornerRadius(dp(14));

        card.setBackground(drawable);
        card.setElevation(dp(3));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dp(18);

        root.addView(card, params);

        return card;
    }

    private void addRow(
            LinearLayout card, String label, String value) {

        TextView row = makeText(
                label + "\n" + value, 14, navy, false);

        row.setPadding(0, dp(8), 0, dp(8));
        card.addView(row);
    }

    private void addDivider(LinearLayout card) {

        android.view.View divider =
                new android.view.View(this);

        divider.setBackgroundColor(
                Color.rgb(225, 230, 238));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, dp(1));

        params.topMargin = dp(12);
        params.bottomMargin = dp(12);

        card.addView(divider, params);
    }

    private Button createButton(String title, int color) {

        Button button = new Button(this);
        button.setText(title);
        button.setTextColor(Color.WHITE);
        button.setTextSize(15);
        button.setTypeface(null, Typeface.BOLD);
        button.setAllCaps(false);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(12));

        button.setBackground(drawable);
        button.setBackgroundTintList(null);

        return button;
    }

    private void addButton(LinearLayout root, Button button) {

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, dp(56));

        params.topMargin = dp(14);
        root.addView(button, params);
    }

    // GENERATE AND DOWNLOAD PDF
    private void downloadTicket() {

        PdfDocument pdf = new PdfDocument();

        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(595, 842, 1).create();

        PdfDocument.Page page = pdf.startPage(pageInfo);

        android.graphics.Canvas canvas = page.getCanvas();
        android.graphics.Paint paint = new android.graphics.Paint();

        paint.setAntiAlias(true);

        int y = 65;

        paint.setColor(navy);
        paint.setTextSize(28);
        paint.setTypeface(Typeface.create(
                Typeface.DEFAULT, Typeface.BOLD));

        canvas.drawText("TatkalX", 45, y, paint);

        y += 38;
        paint.setTextSize(20);
        canvas.drawText("BOOKING CONFIRMATION", 45, y, paint);

        y += 30;
        paint.setColor(green);
        paint.setTextSize(16);
        canvas.drawText("DEMO BOOKING SUCCESSFUL", 45, y, paint);

        y += 40;
        paint.setColor(navy);
        paint.setTextSize(14);

        java.util.List<String> lines = new java.util.ArrayList<>();
        lines.add("DEMO PNR: " + demoPnr);
        lines.add("Booking ID: " + bookingId);
        lines.add("");
        lines.add("TRAIN DETAILS");
        lines.add("Train Name: " + trainName);
        lines.add("Train Number: " + trainNumber);
        if (!fromStation.isEmpty() && !toStation.isEmpty()) {
            lines.add("Route: " + fromStation + " -> " + toStation);
        }
        if (!journeyDate.isEmpty()) {
            lines.add("Journey Date: " + journeyDate);
        }
        lines.add("Travel Class: " + travelClass);
        lines.add("Booking Quota: " + quota);
        lines.add("");
        lines.add("PASSENGER DETAILS");
        lines.add("Passenger Name: " + passengerName);
        lines.add("Age: " + passengerAge);
        lines.add("Mobile Number: " + mobile);
        lines.add("Gender: " + gender);
        lines.add("Berth Preference: " + berth);
        lines.add("");
        lines.add("PAYMENT DETAILS");
        lines.add("Payment Status: Demo Successful");
        lines.add("Total Amount: Rs. " + amount);
        lines.add("");
        lines.add("DISCLAIMER:");
        lines.add("This is a demonstration ticket generated by TatkalX.");
        lines.add("It is NOT a valid railway ticket or actual railway PNR.");

        for (String line : lines) {

            if (line.equals("TRAIN DETAILS") ||
                    line.equals("PASSENGER DETAILS") ||
                    line.equals("PAYMENT DETAILS") ||
                    line.equals("DISCLAIMER:")) {

                paint.setTypeface(Typeface.create(
                        Typeface.DEFAULT, Typeface.BOLD));
            } else {
                paint.setTypeface(Typeface.DEFAULT);
            }

            canvas.drawText(line, 45, y, paint);
            y += 27;
        }

        pdf.finishPage(page);

        try {

            String fileName = "TatkalX_Ticket_" +
                    System.currentTimeMillis() + ".pdf";

            OutputStream outputStream;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                values.put(MediaStore.Downloads.RELATIVE_PATH,
                        Environment.DIRECTORY_DOWNLOADS);

                Uri uri = getContentResolver().insert(
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                        values);

                if (uri == null) {
                    throw new Exception("Unable to create PDF file");
                }

                outputStream = getContentResolver().openOutputStream(uri);

            } else {

                File directory = getExternalFilesDir(
                        Environment.DIRECTORY_DOWNLOADS);

                if (directory == null) {
                    throw new Exception("Download folder unavailable");
                }

                File file = new File(directory, fileName);
                outputStream = new FileOutputStream(file);
            }

            if (outputStream == null) {
                throw new Exception("Unable to open PDF output");
            }

            pdf.writeTo(outputStream);
            outputStream.close();
            pdf.close();

            Toast.makeText(this,
                    "Ticket PDF downloaded successfully!",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {

            pdf.close();

            Toast.makeText(this,
                    "PDF download failed: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    // SHARE TICKET
    private void shareTicket() {

        StringBuilder sb = new StringBuilder();
        sb.append("TATKALX - DEMO BOOKING CONFIRMATION\n\n");
        sb.append("Demo PNR: ").append(demoPnr).append("\n");
        sb.append("Booking ID: ").append(bookingId).append("\n\n");
        sb.append("TRAIN DETAILS\n");
        sb.append("Train: ").append(trainName).append("\n");
        sb.append("Train Number: ").append(trainNumber).append("\n");
        if (!fromStation.isEmpty() && !toStation.isEmpty()) {
            sb.append("Route: ").append(fromStation).append(" → ").append(toStation).append("\n");
        }
        if (!journeyDate.isEmpty()) {
            sb.append("Journey Date: ").append(journeyDate).append("\n");
        }
        sb.append("Class: ").append(travelClass).append("\n");
        sb.append("Quota: ").append(quota).append("\n\n");
        sb.append("PASSENGER DETAILS\n");
        sb.append("Name: ").append(passengerName).append("\n");
        sb.append("Age: ").append(passengerAge).append("\n");
        sb.append("Mobile: ").append(mobile).append("\n");
        sb.append("Gender: ").append(gender).append("\n");
        sb.append("Berth: ").append(berth).append("\n\n");
        sb.append("PAYMENT DETAILS\n");
        sb.append("Payment Status: Demo Successful\n");
        sb.append("Total Amount: Rs. ").append(amount).append("\n\n");
        sb.append("Note: This is a demonstration ticket only. ");
        sb.append("It is NOT a valid railway ticket or actual PNR.");

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,
                "TatkalX Demo Booking Ticket");
        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());

        startActivity(Intent.createChooser(
                intent, "Share TatkalX Ticket"));
    }
}
