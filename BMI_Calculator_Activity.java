package com.demo.example.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

//import com.demo.example.AdAdmob;
import com.demo.example.R;

import java.util.Locale;

public class BMI_Calculator_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set soft input mode
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // Set status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        window.setStatusBarColor(getResources().getColor(R.color.color1));

        // Set content view
        setContentView(R.layout.bmi_calculator_activity);

        /*
        // Initialize Ads
        //AdAdmob adAdmob = new AdAdmob(this);
        RelativeLayout bannerLayout = findViewById(R.id.banner);
        if (bannerLayout != null) {
            adAdmob.BannerAd(bannerLayout, this);
        }
        AdAdmob.FullscreenAd_Counter(this); // static method

        final Context context = this;

         */

        // Toolbar setup
        Toolbar animtoolbar = findViewById(R.id.toolbar);
        setSupportActionBar(animtoolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        animtoolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        // EditTexts
        final EditText edtHeight = findViewById(R.id.edtheight_bmi);
        final EditText edtWeight = findViewById(R.id.edtweight_bmi);

        // TextViews
        final TextView txtBmiResult = findViewById(R.id.txt_bmi_result);
        final TextView txtBmiCategory = findViewById(R.id.txt_bmi_category);
        final TextView txtTitle = findViewById(R.id.txt_title);
        final TextView txtResult = findViewById(R.id.txtresult);
        final TextView txtBmiCal = findViewById(R.id.txt_bmi_cal);

        // Apply custom font
        Typeface customFont = Typeface.createFromAsset(getAssets(), "Titillium-Semibold.otf");
        txtTitle.setTypeface(customFont);
        txtResult.setTypeface(customFont);
        txtBmiCal.setTypeface(customFont);
        txtBmiResult.setTypeface(customFont);
        txtBmiCategory.setTypeface(customFont);

        // BMI Calculation button
        LinearLayout linBmiCal = findViewById(R.id.lin_bmi_cal);
        linBmiCal.setOnClickListener(view -> {
            String heightStr = edtHeight.getText().toString();
            String weightStr = edtWeight.getText().toString();

            if (heightStr.isEmpty() || weightStr.isEmpty()) {
                showToast(BMI_Calculator_Activity.this, "Please enter height and weight", Toast.LENGTH_SHORT);
                txtBmiResult.setText("");
                txtBmiCategory.setText("");
                return;
            }

            try {
                double heightM = Double.parseDouble(heightStr) / 100.0;
                double weightKg = Double.parseDouble(weightStr);
                double bmi = weightKg / (heightM * heightM);

                txtBmiResult.setText(String.format(Locale.getDefault(), "Your BMI: %.1f", bmi));

                String category;
                if (bmi < 18.5) {
                    category = "Underweight";
                } else if (bmi < 25) {
                    category = "Normal weight";
                } else if (bmi < 30) {
                    category = "Overweight";
                } else {
                    category = "Obese";
                }

                txtBmiCategory.setText(category);

            } catch (NumberFormatException e) {
                showToast(BMI_Calculator_Activity.this, "Invalid number format", Toast.LENGTH_SHORT);
            }
        });
    }

    private void showToast(Context context, String message, int duration) {
        Toast toast = Toast.makeText(context, message, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.privacy) {
            startActivity(new Intent(this, Privacy_Policy_activity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return true;
        } else if (id == R.id.rate) {
            if (isOnline()) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName()))
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            } else {
                showToast(this, "No Internet Connection..", Toast.LENGTH_LONG);
            }
            return true;
        } else if (id == R.id.share) {
            if (isOnline()) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,
                        "Hi! I'm using a great Step Counter Pedometer Free Calorie Counter application. Check it out: http://play.google.com/store/apps/details?id="
                                + getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(Intent.createChooser(intent, "Share with Friends"));
            } else {
                showToast(this, "No Internet Connection..", Toast.LENGTH_SHORT);
            }
            return true;
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
    }

    public boolean isOnline() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
