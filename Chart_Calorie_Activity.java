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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Chart_Calorie_Activity extends AppCompatActivity {

    private Toolbar animtoolbar;
    private Context context;
    private BarChart mBarCharViewMonthly;
    private BarChart mBarCharViewWeek;
    private LineChart mChartView;
    private TextView txtTitle;
    private Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(Integer.MIN_VALUE);
            window.clearFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            window.setStatusBarColor(getResources().getColor(R.color.color1));
        }

        setContentView(R.layout.chart_calorie_activity);

        /*// Initialize Ads
        AdAdmob adAdmob = new AdAdmob(this);
        adAdmob.BannerAd(findViewById(R.id.banner), this);
        adAdmob.FullscreenAd_Counter(this);

        context = this;

         */

        // Toolbar setup
        animtoolbar = findViewById(R.id.toolbar);
        setSupportActionBar(animtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        Typeface customFont = Typeface.createFromAsset(getAssets(), "Titillium-Semibold.otf");
        txtTitle = findViewById(R.id.txt_title);
        txtTitle.setTypeface(customFont);

        // Charts
        mBarCharViewWeek = findViewById(R.id.chart1_week);
        mBarCharViewMonthly = findViewById(R.id.chart1_month);
        mChartView = findViewById(R.id.chart1_daily);

        // Populate charts
        dailyChart();
        weekChart();
        monthlyChart();
    }

    private void weekChart() {
        DataSource dataSource = new DataSource(this);
        dataSource.open();
        Calendar calendar = Calendar.getInstance();
        float[] weekData = new float[7];

        List<SingleRow> records = dataSource.getAllRecordsForWeek(
                calendar.get(Calendar.WEEK_OF_YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.YEAR)
        );

        for (SingleRow row : records) {
            if (row != null) {
                weekData[row.getDaysOfWeek() - 1] = row.getCaloriesBurn();
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
        for (int i = 0; i < 7; i++) entries.add(new BarEntry(i, weekData[i]));

        BarDataSet barDataSet = new BarDataSet(entries, getString(R.string.weekly));
        barDataSet.setColor(ContextCompat.getColor(this, R.color.chart));
        barDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.chart));
        barDataSet.setValueTextSize(0f);

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet);

        mBarCharViewWeek.setData(new BarData(dataSets));
        mBarCharViewWeek.setFitBars(true);
        mBarCharViewWeek.invalidate();
    }

    private void monthlyChart() {
        DataSource dataSource = new DataSource(this);
        dataSource.open();
        Calendar calendar = Calendar.getInstance();
        float[] monthData = new float[31];

        List<SingleRow> records = dataSource.getAllRecordsForMonth(
                calendar.get(Calendar.WEEK_OF_YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR)
        );

        for (SingleRow row : records) {
            if (row != null) {
                monthData[row.getDaysOfMonth() - 1] = row.getCaloriesBurn();
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
        for (int i = 0; i < 31; i++) entries.add(new BarEntry(i, monthData[i]));

        BarDataSet barDataSet = new BarDataSet(
                entries,
                getString(R.string.monthly) + " " +
                        Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
        );
        barDataSet.setColor(ContextCompat.getColor(this, R.color.chart));
        barDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.chart));
        barDataSet.setValueTextSize(0f);

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet);

        mBarCharViewMonthly.setData(new BarData(dataSets));
        mBarCharViewMonthly.setFitBars(true);
        mBarCharViewMonthly.invalidate();
    }

    private void dailyChart() {
        float[] dailyData = new float[24];
        DataSource dataSource = new DataSource(this);
        dataSource.open();
        Calendar calendar = Calendar.getInstance();

        List<SingleRow> records = dataSource.getAllRecordsForDay(
                calendar.get(Calendar.WEEK_OF_YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        for (SingleRow row : records) {
            if (row != null) dailyData[row.getHours()] = row.getCaloriesBurn();
        }

        Description description = new Description();
        description.setText("");
        mChartView.setDescription(description);
        mChartView.setDrawGridBackground(false);
        mChartView.getAxisLeft().setEnabled(true);
        mChartView.getAxisRight().setEnabled(true);

        XAxis xAxis = mChartView.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new DailyAxisFormatter());

        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 24; i++) entries.add(new Entry(i, dailyData[i]));

        LineDataSet lineDataSet = new LineDataSet(entries, getString(R.string.daily));
        lineDataSet.setColor(ContextCompat.getColor(this, R.color.chart));
        lineDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.chart));
        lineDataSet.setValueTextSize(0f);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        mChartView.setData(new LineData(dataSets));
        mChartView.invalidate();
    }

    // --- Axis formatters ---
    private static class WeekAxisFormatter implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Calendar cal = Calendar.getInstance();
            int dayIndex = (int) value;
            if (dayIndex < 0 || dayIndex > 6) dayIndex = 0;
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

    private static class DailyAxisFormatter implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            int hour = (int) value;
            if (hour == 0) return "12 AM";
            else if (hour < 12) return hour + " AM";
            else if (hour == 12) return "12 PM";
            else return (hour - 12) + " PM";
        }
    }

    // --- Menu options ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
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
                        Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName()))
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            } else showToast("No Internet Connection..");
            return true;
        } else if (id == R.id.share) {
            if (isOnline()) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,
                        "Hi! I'm using a great Step Counter Pedometer Free Calorie Counter application. Check it out: http://play.google.com/store/apps/details?id=" + context.getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(Intent.createChooser(intent, "Share with Friends"));
            } else showToast("No Internet Connection..");
            return true;
        } else return super.onOptionsItemSelected(menuItem);
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(17, 0, 0);
        toast.show();
    }

    public boolean isOnline() {
        NetworkInfo activeNetworkInfo =
                ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
