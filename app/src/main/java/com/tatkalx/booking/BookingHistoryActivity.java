package com.tatkalx.booking;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class BookingHistoryActivity extends AppCompatActivity {

    public static final String PREF_NAME = "TatkalXBookings";
    public static final String KEY_BOOKING_LIST = "bookingList";

    private final int navy = Color.rgb(16, 39, 71);
    private final int blue = Color.rgb(23, 101, 192);
    private final int green = Color.rgb(30, 150, 90);
    private final int bg = Color.rgb(243, 246, 250);
    private final int gray = Color.rgb(115, 129, 152);
    private final int border = Color.rgb(225, 232, 242);

    private LinearLayout listContainer;
    private TextView headerSubtitle;
    private Button clearButton;

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * Saves a booking to SharedPreferences.
     * Prevents duplicate bookings by checking demo PNR and Booking ID.
     *
     * @return true if successfully saved, false if duplicate or error.
     */
    public static synchronized boolean saveBooking(Context context, JSONObject newBooking) {
        if (context == null || newBooking == null) return false;

        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            String existingData = prefs.getString(KEY_BOOKING_LIST, "[]");
            JSONArray array = new JSONArray(existingData);

            String newPnr = newBooking.optString("pnr", "").trim();
            String newBookingId = newBooking.optString("bookingId", "").trim();

            // Prevent duplicate bookings
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                String pnr = item.optString("pnr", "").trim();
                String bookingId = item.optString("bookingId", "").trim();

                if (!newPnr.isEmpty() && newPnr.equalsIgnoreCase(pnr)) {
                    return false; // Already saved
                }
                if (!newBookingId.isEmpty() && newBookingId.equalsIgnoreCase(bookingId)) {
                    return false; // Already saved
                }
            }

            array.put(newBooking);
            prefs.edit().putString(KEY_BOOKING_LIST, array.toString()).apply();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all bookings from SharedPreferences.
     */
    public static JSONArray getBookings(Context context) {
        if (context == null) return new JSONArray();
        try {
            String data = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                    .getString(KEY_BOOKING_LIST, "[]");
            return new JSONArray(data);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    /**
     * Clears all saved demo bookings.
     */
    public static void clearAllBookings(Context context) {
        if (context == null) return;
        context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                .edit()
                .remove(KEY_BOOKING_LIST)
                .apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(navy);
        getWindow().setNavigationBarColor(bg);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(20), dp(18), dp(20));
        root.setBackgroundColor(bg);

        // TOP APP BAR / HEADER
        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout titleCol = new LinearLayout(this);
        titleCol.setOrientation(LinearLayout.VERTICAL);

        TextView brand = new TextView(this);
        brand.setText("TATKALX");
        brand.setTextSize(12);
        brand.setTextColor(blue);
        brand.setTypeface(null, Typeface.BOLD);
        brand.setLetterSpacing(0.12f);
        titleCol.addView(brand);

        TextView title = new TextView(this);
        title.setText("Booking History");
        title.setTextSize(24);
        title.setTextColor(navy);
        title.setTypeface(null, Typeface.BOLD);
        titleCol.addView(title);

        headerSubtitle = new TextView(this);
        headerSubtitle.setText("Saved Demo Tickets");
        headerSubtitle.setTextSize(13);
        headerSubtitle.setTextColor(gray);
        titleCol.addView(headerSubtitle);

        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(0, -2, 1);
        topBar.addView(titleCol, titleLp);

        // CLEAR HISTORY BUTTON
        clearButton = new Button(this);
        clearButton.setText("Clear All");
        clearButton.setTextSize(12);
        clearButton.setTextColor(Color.rgb(180, 40, 40));
        clearButton.setBackgroundTintList(null);

        GradientDrawable clearBg = new GradientDrawable();
        clearBg.setColor(Color.WHITE);
        clearBg.setCornerRadius(dp(8));
        clearBg.setStroke(dp(1), Color.rgb(235, 200, 200));
        clearButton.setBackground(clearBg);
        clearButton.setPadding(dp(12), dp(6), dp(12), dp(6));

        clearButton.setOnClickListener(v -> confirmClearHistory());
        topBar.addView(clearButton, new LinearLayout.LayoutParams(-2, dp(38)));

        root.addView(topBar);

        // SCROLL CONTAINER
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        listContainer = new LinearLayout(this);
        listContainer.setOrientation(LinearLayout.VERTICAL);
        listContainer.setPadding(0, dp(12), 0, dp(16));
        scroll.addView(listContainer);

        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(-1, 0, 1);
        scrollLp.topMargin = dp(10);
        root.addView(scroll, scrollLp);

        // BACK BUTTON AT BOTTOM
        Button backButton = new Button(this);
        backButton.setText("← BACK TO HOME");
        backButton.setTextColor(Color.WHITE);
        backButton.setTextSize(15);
        backButton.setTypeface(null, Typeface.BOLD);
        backButton.setAllCaps(false);

        GradientDrawable backBg = new GradientDrawable();
        backBg.setColor(navy);
        backBg.setCornerRadius(dp(12));
        backButton.setBackground(backBg);
        backButton.setBackgroundTintList(null);

        LinearLayout.LayoutParams backLp = new LinearLayout.LayoutParams(-1, dp(52));
        backLp.topMargin = dp(12);
        root.addView(backButton, backLp);

        backButton.setOnClickListener(v -> finish());

        setContentView(root);

        loadBookingsUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookingsUI();
    }

    private void loadBookingsUI() {
        if (listContainer == null) return;
        listContainer.removeAllViews();

        JSONArray bookings = getBookings(this);
        int total = bookings.length();

        if (total == 0) {
            headerSubtitle.setText("0 saved tickets");
            clearButton.setVisibility(View.GONE);
            showEmptyState();
            return;
        }

        headerSubtitle.setText(total + (total == 1 ? " saved ticket" : " saved tickets"));
        clearButton.setVisibility(View.VISIBLE);

        // Show newest bookings first
        for (int i = total - 1; i >= 0; i--) {
            try {
                JSONObject item = bookings.getJSONObject(i);
                addBookingCard(item, i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showEmptyState() {
        LinearLayout emptyCard = new LinearLayout(this);
        emptyCard.setOrientation(LinearLayout.VERTICAL);
        emptyCard.setGravity(Gravity.CENTER);
        emptyCard.setPadding(dp(24), dp(40), dp(24), dp(40));

        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(Color.WHITE);
        cardBg.setCornerRadius(dp(16));
        cardBg.setStroke(dp(1), border);
        emptyCard.setBackground(cardBg);
        emptyCard.setElevation(dp(2));

        LinearLayout.LayoutParams emptyLp = new LinearLayout.LayoutParams(-1, -2);
        emptyLp.topMargin = dp(24);
        listContainer.addView(emptyCard, emptyLp);

        TextView icon = new TextView(this);
        icon.setText("🚆");
        icon.setTextSize(46);
        icon.setGravity(Gravity.CENTER);
        emptyCard.addView(icon);

        TextView emptyTitle = new TextView(this);
        emptyTitle.setText("No Bookings Found");
        emptyTitle.setTextSize(20);
        emptyTitle.setTextColor(navy);
        emptyTitle.setTypeface(null, Typeface.BOLD);
        emptyTitle.setGravity(Gravity.CENTER);
        emptyTitle.setPadding(0, dp(12), 0, dp(6));
        emptyCard.addView(emptyTitle);

        TextView emptyDesc = new TextView(this);
        emptyDesc.setText("You haven't made any demo bookings yet.\nSearch for trains and complete a demo booking to see your tickets stored here.");
        emptyDesc.setTextSize(14);
        emptyDesc.setTextColor(gray);
        emptyDesc.setGravity(Gravity.CENTER);
        emptyDesc.setLineSpacing(dp(3), 1f);
        emptyCard.addView(emptyDesc);

        Button bookNow = new Button(this);
        bookNow.setText("BOOK A TRAIN NOW  →");
        bookNow.setTextColor(Color.WHITE);
        bookNow.setTextSize(14);
        bookNow.setTypeface(null, Typeface.BOLD);
        bookNow.setAllCaps(false);

        GradientDrawable bookBg = new GradientDrawable();
        bookBg.setColor(blue);
        bookBg.setCornerRadius(dp(10));
        bookNow.setBackground(bookBg);
        bookNow.setBackgroundTintList(null);

        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(-1, dp(48));
        btnLp.topMargin = dp(22);
        emptyCard.addView(bookNow, btnLp);

        bookNow.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void addBookingCard(JSONObject item, int index) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));

        GradientDrawable shape = new GradientDrawable();
        shape.setColor(Color.WHITE);
        shape.setCornerRadius(dp(14));
        shape.setStroke(dp(1), border);
        card.setBackground(shape);
        card.setElevation(dp(3));

        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2);
        cp.topMargin = dp(14);
        listContainer.addView(card, cp);

        // TOP ROW: Train Name and Demo Confirmed Badge
        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView trainName = new TextView(this);
        trainName.setText("🚆 " + item.optString("trainName", "Train"));
        trainName.setTextSize(18);
        trainName.setTextColor(navy);
        trainName.setTypeface(null, Typeface.BOLD);
        topRow.addView(trainName, new LinearLayout.LayoutParams(0, -2, 1));

        TextView badge = new TextView(this);
        badge.setText("DEMO CONFIRMED");
        badge.setTextSize(10);
        badge.setTextColor(green);
        badge.setTypeface(null, Typeface.BOLD);
        badge.setPadding(dp(8), dp(4), dp(8), dp(4));

        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setColor(Color.rgb(232, 245, 233));
        badgeBg.setCornerRadius(dp(6));
        badge.setBackground(badgeBg);
        topRow.addView(badge);

        card.addView(topRow);

        // TRAIN NUMBER & ROUTE
        String trainNo = item.optString("trainNumber", "—");
        String fromStation = item.optString("fromStation", "");
        String toStation = item.optString("toStation", "");
        String routeInfo = "Train No: " + trainNo;
        if (!fromStation.isEmpty() && !toStation.isEmpty()) {
            routeInfo += " • " + fromStation + " → " + toStation;
        }

        TextView subInfo = new TextView(this);
        subInfo.setText(routeInfo);
        subInfo.setTextSize(13);
        subInfo.setTextColor(gray);
        subInfo.setPadding(0, dp(4), 0, dp(6));
        card.addView(subInfo);

        addDivider(card);

        // PASSENGER & JOURNEY DETAILS
        String passenger = item.optString("passengerName", "—");
        String age = item.optString("passengerAge", "");
        String gender = item.optString("gender", "");
        String passengerDesc = passenger;
        if (!age.isEmpty() && !gender.isEmpty()) {
            passengerDesc += " (" + age + " yrs, " + gender + ")";
        }

        addDetailRow(card, "Passenger", passengerDesc);

        String travelClass = item.optString("travelClass", "Sleeper");
        String quota = item.optString("quota", "Tatkal");
        addDetailRow(card, "Class & Quota", travelClass + "  •  " + quota + " Quota");

        String berth = item.optString("berth", "");
        if (!berth.isEmpty() && !berth.equalsIgnoreCase("No preference")) {
            addDetailRow(card, "Berth Preference", berth);
        }

        String journeyDate = item.optString("journeyDate", "");
        if (!journeyDate.isEmpty()) {
            addDetailRow(card, "Journey Date", journeyDate);
        }

        addDivider(card);

        // DEMO PNR & AMOUNT ROW
        LinearLayout pnrRow = new LinearLayout(this);
        pnrRow.setOrientation(LinearLayout.HORIZONTAL);
        pnrRow.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout pnrCol = new LinearLayout(this);
        pnrCol.setOrientation(LinearLayout.VERTICAL);

        TextView pnrLabel = new TextView(this);
        pnrLabel.setText("DEMO PNR");
        pnrLabel.setTextSize(11);
        pnrLabel.setTextColor(gray);
        pnrLabel.setTypeface(null, Typeface.BOLD);
        pnrCol.addView(pnrLabel);

        TextView pnrVal = new TextView(this);
        pnrVal.setText(item.optString("pnr", "—"));
        pnrVal.setTextSize(16);
        pnrVal.setTextColor(blue);
        pnrVal.setTypeface(null, Typeface.BOLD);
        pnrCol.addView(pnrVal);

        pnrRow.addView(pnrCol, new LinearLayout.LayoutParams(0, -2, 1));

        LinearLayout fareCol = new LinearLayout(this);
        fareCol.setOrientation(LinearLayout.VERTICAL);
        fareCol.setGravity(Gravity.END);

        TextView fareLabel = new TextView(this);
        fareLabel.setText("AMOUNT PAID");
        fareLabel.setTextSize(11);
        fareLabel.setTextColor(gray);
        fareLabel.setTypeface(null, Typeface.BOLD);
        fareCol.addView(fareLabel);

        TextView fareVal = new TextView(this);
        fareVal.setText("₹" + item.optString("amount", "0"));
        fareVal.setTextSize(18);
        fareVal.setTextColor(navy);
        fareVal.setTypeface(null, Typeface.BOLD);
        fareCol.addView(fareVal);

        pnrRow.addView(fareCol);

        card.addView(pnrRow);

        // FOOTER ROW: Booking Date and Academic Disclaimer
        String bookingDate = item.optString("date", "—");
        TextView dateText = new TextView(this);
        dateText.setText("Booked on: " + bookingDate);
        dateText.setTextSize(11);
        dateText.setTextColor(gray);
        dateText.setPadding(0, dp(10), 0, 0);
        card.addView(dateText);

        TextView disclaimer = new TextView(this);
        disclaimer.setText("Demo record • Not an official railway ticket");
        disclaimer.setTextSize(10);
        disclaimer.setTextColor(Color.rgb(150, 160, 175));
        disclaimer.setPadding(0, dp(2), 0, 0);
        card.addView(disclaimer);
    }

    private void addDetailRow(LinearLayout card, String label, String value) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, dp(3), 0, dp(3));

        TextView lbl = new TextView(this);
        lbl.setText(label + ": ");
        lbl.setTextSize(13);
        lbl.setTextColor(gray);
        lbl.setTypeface(null, Typeface.BOLD);
        row.addView(lbl);

        TextView val = new TextView(this);
        val.setText(value);
        val.setTextSize(13);
        val.setTextColor(navy);
        row.addView(val);

        card.addView(row);
    }

    private void addDivider(LinearLayout card) {
        View div = new View(this);
        div.setBackgroundColor(border);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(1));
        lp.topMargin = dp(10);
        lp.bottomMargin = dp(10);
        card.addView(div, lp);
    }

    private void confirmClearHistory() {
        new AlertDialog.Builder(this)
                .setTitle("Clear Booking History")
                .setMessage("Are you sure you want to remove all saved demo booking records? This action cannot be undone.")
                .setPositiveButton("Clear All", (dialog, which) -> {
                    clearAllBookings(BookingHistoryActivity.this);
                    loadBookingsUI();
                    Toast.makeText(BookingHistoryActivity.this, "Booking history cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
