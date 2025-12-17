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
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

//import com.demo.example.AdAdmob;
import com.demo.example.R;

import java.util.List;

import com.demo.example.DB.DataSource;
import com.demo.example.DB.SingleRow;
import com.demo.example.adapter.History_Adapter;

public class History_Activity extends AppCompatActivity {
    Toolbar animtoolbar;
    Context context;
    RelativeLayout layout;
    TextView txt_msg;
    TextView txt_title;

    protected int getNumColumns() {
        return 1;
    }

    protected int getNumItems() {
        return 7;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(Integer.MIN_VALUE);
            window.clearFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            window.setStatusBarColor(getResources().getColor(R.color.color1));
        }
        setContentView(R.layout.history_activity);

       /* AdAdmob adAdmob = new AdAdmob(this);
        adAdmob.BannerAd((RelativeLayout) findViewById(R.id.banner), this);
        adAdmob.FullscreenAd_Counter(this);

        this.context = this;

        */
        Toolbar toolbar = findViewById(R.id.toolbar);
        this.animtoolbar = toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        this.animtoolbar.setTitleTextColor(-1);

        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "Titillium-Semibold.otf");
        this.txt_title = findViewById(R.id.txt_title);
        this.txt_msg = findViewById(R.id.txt_msg);
        this.txt_title.setTypeface(createFromAsset);

        RecyclerView recyclerView = findViewById(R.id.notes_list);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(getNumColumns(), 1));

        DataSource dataSource = new DataSource(this);
        dataSource.open();
        List<SingleRow> allRecordsForDaysList = dataSource.getAllRecordsForDaysList();

        if (allRecordsForDaysList.size() > 0) {
            this.txt_msg.setVisibility(View.GONE);
        } else {
            this.txt_msg.setVisibility(View.VISIBLE);
        }

        SingleRow[] singleRowArr = new SingleRow[allRecordsForDaysList.size()];
        for (int i = 0; i < allRecordsForDaysList.size(); i++) {
            SingleRow singleRow = new SingleRow(0L, 0, 0, 0.0f, 0.0f, 0, 0, 0, 0, 0, 0, 0, 0);
            singleRow.setCaloriesBurn(allRecordsForDaysList.get(i).getCaloriesBurn());
            singleRow.setSteps(allRecordsForDaysList.get(i).getSteps());
            singleRow.setStepsForWeekView(allRecordsForDaysList.get(i).getStepsForWeekView());
            singleRow.setDistence(allRecordsForDaysList.get(i).getDistence());
            singleRow.setDaysOfWeek(allRecordsForDaysList.get(i).getDaysOfWeek());
            singleRow.setDaysOfMonth(allRecordsForDaysList.get(i).getDaysOfMonth());
            singleRow.setWeek(allRecordsForDaysList.get(i).getWeek());
            singleRow.setMonth(allRecordsForDaysList.get(i).getMonth());
            singleRow.setYear(allRecordsForDaysList.get(i).getYear());
            singleRow.setMints(allRecordsForDaysList.get(i).getMints());
            singleRowArr[i] = singleRow;
        }

        recyclerView.setAdapter(new History_Adapter(this, getNumItems(), singleRowArr));
    }

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
        }

        if (id == R.id.privacy) {
            Intent intent2 = new Intent(this, Privacy_Policy_activity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent2);
            return true;
        }

        if (id == R.id.rate) {
            if (isOnline()) {
                Intent intent3 = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + this.context.getPackageName()));
                intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent3);
            } else {
                Toast makeText = Toast.makeText(getApplicationContext(), "No Internet Connection..", Toast.LENGTH_SHORT);
                makeText.setGravity(17, 0, 0);
                makeText.show();
            }
            return true;
        }

        if (id == R.id.share) {
            if (isOnline()) {
                Intent intent4 = new Intent(Intent.ACTION_SEND);
                intent4.setType("text/plain");
                intent4.putExtra(Intent.EXTRA_TEXT,
                        "Hi! I'm using a great Step Counter Pedometer Free Calorie Counter application. " +
                                "Check it out: http://play.google.com/store/apps/details?id=" + this.context.getPackageName());
                intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(Intent.createChooser(intent4, "Share with Friends"));
            } else {
                Toast makeText2 = Toast.makeText(getApplicationContext(), "No Internet Connection..", Toast.LENGTH_SHORT);
                makeText2.setGravity(17, 0, 0);
                makeText2.show();
            }
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    public boolean isOnline() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
