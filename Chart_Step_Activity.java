package com.demo.example.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

//import com.demo.example.AdAdmob;
import com.demo.example.DB.DataSource;
import com.demo.example.DB.SingleRow;
import com.demo.example.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Chart_Step_Activity extends AppCompatActivity {

    private Toolbar animtoolbar;
    private final Calendar cal = Calendar.getInstance();
    private Context context;
    private BarChart mBarCharViewMonthly;
    private BarChart mBarCharViewWeek;
    private BarChart mBarCharViewDaily;
    private TextView txt_title;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(Integer.MIN_VALUE);
            window.clearFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            window.setStatusBarColor(getResources().getColor(R.color.color1));
        }

        setContentView(R.layout.chart_step_activity);

        /*// Ads
        AdAdmob adAdmob = new AdAdmob(this);
        adAdmob.FullscreenAd_Counter(this);

        context = this;
        */


        // Toolbar
        animtoolbar = findViewById(R.id.toolbar);
        setSupportActionBar(animtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        animtoolbar.setTitleTextColor(-1);

        Typeface font = Typeface.createFromAsset(getAssets(), "Titillium-Semibold.otf");
        txt_title = findViewById(R.id.txt_title);
        txt_title.setTypeface(font);

        // Charts
        mBarCharViewWeek = findViewById(R.id.chart1_week);
        mBarCharViewMonthly = findViewById(R.id.chart1_month);
        mBarCharViewDaily = findViewById(R.id.chart_daily);

        dailyChart();
        weekChart();
        monthlyChart();
    }

    private void dailyChart() {
        int[] steps = new int[24];
        DataSource dataSource = new DataSource(this);
        dataSource.open();

        Calendar calendar = Calendar.getInstance();
        List<SingleRow> records = dataSource.getAllRecordsForDay(
                calendar.get(Calendar.WEEK_OF_YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        for (SingleRow record : records) {
            if (record != null) {
                steps[record.getHours()] = record.getSteps();
            }
        }

        Description description = new Description();
        description.setText("");
        mBarCharViewDaily.setDescription(description);
        mBarCharViewDaily.setDrawGridBackground(false);
        mBarCharViewDaily.setDrawBarShadow(false);
        mBarCharViewDaily.getAxisLeft().setEnabled(false);
        mBarCharViewDaily.getAxisRight().setEnabled(false);

        XAxis xAxis = mBarCharViewDaily.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new DailyAxisFormatter());

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            entries.add(new BarEntry(i, steps[i]));
        }

        BarDataSet barDataSet = new BarDataSet(entries, getString(R.string.daily));
        barDataSet.setColor(ContextCompat.getColor(this, R.color.chart));
        barDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.chart));
        barDataSet.setValueTextSize(0f);

        mBarCharViewDaily.setData(new BarData(barDataSet));
        mBarCharViewDaily.setFitBars(false);
        mBarCharViewDaily.invalidate();
    }

    private void weekChart() {
        DataSource dataSource = new DataSource(this);
        dataSource.open();
        Calendar calendar = Calendar.getInstance();

        int[] steps = new int[7];
        List<SingleRow> records = dataSource.getAllRecordsForWeek(
                calendar.get(Calendar.WEEK_OF_YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.YEAR)
        );

        for (SingleRow record : records) {
            if (record != null) {
                steps[record.getDaysOfWeek() - 1] = record.getStepsForWeekView();
            }
        }

        Description description = new Description();
        description.setText("");
        mBarCharViewWeek.setDescription(description);
        mBarCharViewWeek.setDrawGridBackground(false);
        mBarCharViewWeek.setDrawBarShadow(false);
        mBarCharViewWeek.getAxisLeft().setEnabled(true);
        mBarCharViewWeek.getAxisRight().setEnabled(true);

        XAxis xAxis = mBarCharViewWeek.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new WeekAxisFormatter());

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            entries.add(new BarEntry(i, steps[i]));
        }

        BarDataSet barDataSet = new BarDataSet(entries, getString(R.string.weekly));
        barDataSet.setColor(ContextCompat.getColor(this, R.color.chart));
        barDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.chart));
        barDataSet.setValueTextSize(0f);

        mBarCharViewWeek.setData(new BarData(barDataSet));
        mBarCharViewWeek.setFitBars(true);
        mBarCharViewWeek.invalidate();
    }

    private void monthlyChart() {
        DataSource dataSource = new DataSource(this);
        dataSource.open();
        Calendar calendar = Calendar.getInstance();

        int[] steps = new int[31];
        List<SingleRow> records = dataSource.getAllRecordsForMonth(
                calendar.get(Calendar.WEEK_OF_YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR)
        );

        for (SingleRow record : records) {
            if (record != null) {
                steps[record.getDaysOfMonth() - 1] = record.getStepsForWeekView();
            }
        }

        Description description = new Description();
        description.setText("");
        mBarCharViewMonthly.setDescription(description);
        mBarCharViewMonthly.setDrawGridBackground(false);
        mBarCharViewMonthly.setDrawBarShadow(false);
        mBarCharViewMonthly.getAxisLeft().setEnabled(true);
        mBarCharViewMonthly.getAxisRight().setEnabled(true);

        XAxis xAxis = mBarCharViewMonthly.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new MonthAxisFormatter());

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 31; i++) {
            entries.add(new BarEntry(i, steps[i]));
        }

        BarDataSet barDataSet = new BarDataSet(entries,
                getString(R.string.monthly) + " " +
                        calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));
        barDataSet.setColor(ContextCompat.getColor(this, R.color.chart));
        barDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.chart));
        barDataSet.setValueTextSize(0f);

        mBarCharViewMonthly.setData(new BarData(barDataSet));
        mBarCharViewMonthly.setFitBars(true);
        mBarCharViewMonthly.invalidate();
    }

    // --- Axis formatters ---
    private static class DailyAxisFormatter implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            int hour = (int) value;
            if (hour == 0) return "12 AM";
            if (hour < 12) return hour + " AM";
            if (hour == 12) return "12 PM";
            return (hour - 12) + " PM";
        }
    }

    private class WeekAxisFormatter implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            int dayIndex = (int) value;
            cal.set(Calendar.DAY_OF_WEEK, dayIndex + 1);
            return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
        }
    }

    private static class MonthAxisFormatter implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return String.valueOf((int) value + 1);
        }
    }

    // --- Menu handling (modern if/else) ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.privacy) {
            startActivity(new Intent(this, Privacy_Policy_activity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return true;
        } else if (id == R.id.rate) {
            if (isOnline()) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else showToast("No Internet Connection..");
            return true;
        } else if (id == R.id.share) {
            if (isOnline()) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hi! I'm using a great Step Counter Pedometer Free Calorie Counter application. Check it out: http://play.google.com/store/apps/details?id=" + context.getPackageName());
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(Intent.createChooser(shareIntent, "Share with Friends"));
            } else showToast("No Internet Connection..");
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(17, 0, 0);
        toast.show();
    }

    private boolean isOnline() {
        NetworkInfo netInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
