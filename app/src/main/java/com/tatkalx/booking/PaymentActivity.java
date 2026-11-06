package com.tatkalx.booking;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

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

        int amount = getIntent().getIntExtra("totalAmount", 470);

        String trainName = getIntent().getStringExtra("trainName");
        String travelClass = getIntent().getStringExtra("travelClass");
        String quota = getIntent().getStringExtra("quota");

        if (trainName == null) trainName = "Selected Train";
        if (travelClass == null) travelClass = "Sleeper";
        if (quota == null) quota = "Tatkal";

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(bg);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(25), dp(20), dp(30));

        scroll.addView(root);

        TextView title = makeText("TatkalX", 29, navy, true);
        root.addView(title);

        TextView heading = makeText(
                "Complete Your Payment", 23, navy, true);

        LinearLayout.LayoutParams hp =
                new LinearLayout.LayoutParams(-1, -2);
        hp.topMargin = dp(25);
        root.addView(heading, hp);

        root.addView(makeText(
                "Choose your preferred payment method",
                14, Color.GRAY, false));

        // AMOUNT CARD

        LinearLayout amountCard = createCard(root);

        amountCard.addView(makeText(
                "BOOKING AMOUNT", 13, Color.GRAY, true));

        TextView amountText = makeText(
                "₹" + amount, 30, blue, true);

        LinearLayout.LayoutParams ap =
                new LinearLayout.LayoutParams(-1, -2);
        ap.topMargin = dp(8);
        amountCard.addView(amountText, ap);

        amountCard.addView(makeText(
                trainName + "\n" + travelClass + " • " + quota,
                14, navy, false));

        // PAYMENT OPTIONS

        LinearLayout paymentCard = createCard(root);

        paymentCard.addView(makeText(
                "Choose Payment Method", 19, navy, true));

        RadioGroup methods = new RadioGroup(this);
        methods.setOrientation(RadioGroup.VERTICAL);

        RadioButton upi = createRadio("UPI Payment");
        RadioButton card = createRadio("Debit / Credit Card");
        RadioButton netBanking = createRadio("Net Banking");

        methods.addView(upi);
        methods.addView(card);
        methods.addView(netBanking);

        upi.setChecked(true);

        LinearLayout.LayoutParams rp =
                new LinearLayout.LayoutParams(-1, -2);
        rp.topMargin = dp(15);
        paymentCard.addView(methods, rp);

        // PAY BUTTON

        Button payButton = new Button(this);
        payButton.setText("PAY ₹" + amount + "  →");
        payButton.setTextColor(Color.WHITE);
        payButton.setTextSize(16);
        payButton.setTypeface(null, Typeface.BOLD);
        payButton.setAllCaps(false);

        GradientDrawable buttonBg = new GradientDrawable();
        buttonBg.setColor(blue);
        buttonBg.setCornerRadius(dp(12));

        payButton.setBackground(buttonBg);
        payButton.setBackgroundTintList(null);

        LinearLayout.LayoutParams bp =
                new LinearLayout.LayoutParams(-1, dp(58));
        bp.topMargin = dp(25);

        root.addView(payButton, bp);

        TextView note = makeText(
                "This is a simulated payment for the TatkalX project.",
                12, Color.GRAY, false);
        note.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams np =
                new LinearLayout.LayoutParams(-1, -2);
        np.topMargin = dp(15);
        root.addView(note, np);

        // BACK BUTTON

        Button backButton = new Button(this);
        backButton.setText("BACK TO BOOKING REVIEW");
        backButton.setAllCaps(false);
        backButton.setTextColor(navy);

        root.addView(backButton,
                new LinearLayout.LayoutParams(-1, dp(52)));

        backButton.setOnClickListener(v -> finish());

        // PAYMENT ACTION

        payButton.setOnClickListener(v -> {

            int selectedId = methods.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(this,
                        "Please select a payment method",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selected = findViewById(selectedId);

            showPaymentSuccess(
                    amount,
                    selected.getText().toString()
            );
        });

        setContentView(scroll);
    }

    private RadioButton createRadio(String value) {

        RadioButton radio = new RadioButton(this);
        radio.setText(value);
        radio.setTextSize(16);
        radio.setTextColor(navy);
        radio.setPadding(dp(5), dp(10), dp(5), dp(10));

        return radio;
    }

    // PAYMENT SUCCESS AND OPEN CONFIRMATION SCREEN

    private void showPaymentSuccess(int amount, String method) {

        new android.app.AlertDialog.Builder(this)
                .setTitle("Demo Payment Successful!")
                .setMessage(
                        "Your simulated payment of ₹" + amount +
                                " using " + method + " was successful.\n\n" +
                                "This is only a project demonstration. " +
                                "No real money was transferred.")
                .setCancelable(false)
                .setPositiveButton("VIEW TICKET", (dialog, which) -> {

                    android.content.Intent intent =
                            new android.content.Intent(
                                    PaymentActivity.this,
                                    BookingConfirmationActivity.class
                            );

                    // PASS ALL BOOKING DETAILS

                    android.os.Bundle bookingData =
                            getIntent().getExtras();

                    if (bookingData != null) {
                        intent.putExtras(bookingData);
                    }

                    startActivity(intent);
                    finish();
                })
                .show();
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
        params.topMargin = dp(20);

        root.addView(card, params);

        return card;
    }
}
