package com.tatkalx.booking;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.widget.*;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class PassengerDetailsActivity extends AppCompatActivity {

    EditText name, age, mobile;
    Spinner gender, berth, quota, travelClass;

    int navy = Color.rgb(16, 39, 71);
    int blue = Color.rgb(23, 101, 192);
    int background = Color.rgb(243, 246, 250);

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(background);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(22), dp(20), dp(30));
        scroll.addView(root);

        TextView title = makeText("TatkalX", 29, navy, true);
        root.addView(title);

        TextView heading = makeText("Passenger Details", 24, navy, true);
        LinearLayout.LayoutParams hp =
                new LinearLayout.LayoutParams(-1, -2);
        hp.topMargin = dp(22);
        root.addView(heading, hp);

        TextView subtitle = makeText(
                "Enter passenger information to continue",
                14, Color.GRAY, false);
        root.addView(subtitle);

        String trainName = getIntent().getStringExtra("trainName");
        String trainNumber = getIntent().getStringExtra("trainNumber");
        String journeyDate = getIntent().getStringExtra("journeyDate");
        String fromStation = getIntent().getStringExtra("fromStation");
        String toStation = getIntent().getStringExtra("toStation");

        if (trainName == null) trainName = "Selected Train";
        if (trainNumber == null) trainNumber = "Not available";

        StringBuilder sb = new StringBuilder();
        sb.append("🚆  ").append(trainName).append("\nTrain No: ").append(trainNumber);
        if (fromStation != null && toStation != null && !fromStation.isEmpty() && !toStation.isEmpty()) {
            sb.append("\n").append(fromStation).append(" → ").append(toStation);
        }
        if (journeyDate != null && !journeyDate.isEmpty()) {
            sb.append("  •  ").append(journeyDate);
        }

        TextView trainInfo = makeText(sb.toString(), 15, navy, true);
        trainInfo.setPadding(dp(16), dp(15), dp(16), dp(15));
        trainInfo.setLineSpacing(dp(4), 1f);

        GradientDrawable trainBg = new GradientDrawable();
        trainBg.setColor(Color.WHITE);
        trainBg.setCornerRadius(dp(12));
        trainBg.setStroke(dp(1), Color.rgb(225, 232, 242));
        trainInfo.setBackground(trainBg);

        LinearLayout.LayoutParams trainParams =
                new LinearLayout.LayoutParams(-1, -2);
        trainParams.topMargin = dp(20);
        root.addView(trainInfo, trainParams);

        name = addField(root, "PASSENGER NAME",
                "Enter full name",
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        age = addField(root, "AGE",
                "Enter age", InputType.TYPE_CLASS_NUMBER);

        mobile = addField(root, "MOBILE NUMBER",
                "Enter mobile number", InputType.TYPE_CLASS_PHONE);

        gender = addSpinner(root, "GENDER",
                new String[]{"Select gender", "Male", "Female", "Other"});

        berth = addSpinner(root, "BERTH PREFERENCE",
                new String[]{"No preference", "Lower", "Middle",
                        "Upper", "Side Lower", "Side Upper"});

        travelClass = addSpinner(root, "TRAVEL CLASS",
                new String[]{"Sleeper", "3A", "2A"});

        quota = addSpinner(root, "BOOKING QUOTA",
                new String[]{"Tatkal", "General"});

        Button continueButton = new Button(this);
        continueButton.setText("CONTINUE TO REVIEW  →");
        continueButton.setTextColor(Color.WHITE);
        continueButton.setTextSize(16);
        continueButton.setTypeface(null, Typeface.BOLD);
        continueButton.setAllCaps(false);

        GradientDrawable buttonBg = new GradientDrawable();
        buttonBg.setColor(blue);
        buttonBg.setCornerRadius(dp(12));

        continueButton.setBackground(buttonBg);
        continueButton.setBackgroundTintList(null);

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(-1, dp(58));
        buttonParams.topMargin = dp(25);
        buttonParams.bottomMargin = dp(20);

        root.addView(continueButton, buttonParams);

        continueButton.setOnClickListener(v -> validateForm());

        setContentView(scroll);
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

    private EditText addField(
            LinearLayout root, String label,
            String hint, int inputType) {

        TextView heading = makeText(label, 13, navy, true);

        LinearLayout.LayoutParams labelParams =
                new LinearLayout.LayoutParams(-1, -2);
        labelParams.topMargin = dp(18);
        labelParams.bottomMargin = dp(7);
        root.addView(heading, labelParams);

        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setHint(hint);
        input.setInputType(inputType);
        input.setTextSize(16);
        input.setTextColor(navy);
        input.setPadding(dp(15), 0, dp(15), 0);
        input.setGravity(Gravity.CENTER_VERTICAL);

        GradientDrawable fieldBg = new GradientDrawable();
        fieldBg.setColor(Color.WHITE);
        fieldBg.setCornerRadius(dp(10));
        fieldBg.setStroke(dp(1), Color.rgb(220, 228, 240));
        input.setBackground(fieldBg);

        root.addView(input,
                new LinearLayout.LayoutParams(-1, dp(56)));

        return input;
    }

    private Spinner addSpinner(
            LinearLayout root, String label, String[] options) {

        TextView heading = makeText(label, 13, navy, true);

        LinearLayout.LayoutParams labelParams =
                new LinearLayout.LayoutParams(-1, -2);
        labelParams.topMargin = dp(19);
        labelParams.bottomMargin = dp(4);
        root.addView(heading, labelParams);

        Spinner spinner = new Spinner(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                options
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        root.addView(spinner,
                new LinearLayout.LayoutParams(-1, dp(50)));

        return spinner;
    }

    private void validateForm() {

        String passengerName = name.getText().toString().trim();
        String passengerAge = age.getText().toString().trim();
        String phone = mobile.getText().toString().trim();

        if (passengerName.isEmpty() ||
                passengerAge.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this,
                    "Please fill all passenger details",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int ageValue;

        try {
            ageValue = Integer.parseInt(passengerAge);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid age",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (ageValue < 1 || ageValue > 120) {
            Toast.makeText(this, "Enter a valid age",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone.matches("[0-9]{10}")) {
            Toast.makeText(this,
                    "Enter a valid 10-digit mobile number",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (gender.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select gender",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(
                PassengerDetailsActivity.this,
                BookingReviewActivity.class
        );

        intent.putExtra("trainName",
                getIntent().getStringExtra("trainName"));
        intent.putExtra("trainNumber",
                getIntent().getStringExtra("trainNumber"));
        intent.putExtra("journeyDate",
                getIntent().getStringExtra("journeyDate"));
        intent.putExtra("fromStation",
                getIntent().getStringExtra("fromStation"));
        intent.putExtra("toStation",
                getIntent().getStringExtra("toStation"));

        intent.putExtra("passengerName", passengerName);
        intent.putExtra("passengerAge", passengerAge);
        intent.putExtra("mobile", phone);
        intent.putExtra("gender", gender.getSelectedItem().toString());
        intent.putExtra("berth", berth.getSelectedItem().toString());
        intent.putExtra("quota", quota.getSelectedItem().toString());
        intent.putExtra("travelClass",
                travelClass.getSelectedItem().toString());

        startActivity(intent);
    }
}
