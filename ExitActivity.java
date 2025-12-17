package com.demo.example.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//import com.demo.example.AdAdmob;
import com.demo.example.R;

public class ExitActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnNo;
    private Button btnYes;
    private LinearLayout lin_rate_yes;
    private Context mContext;

    @Override
    public void onBackPressed() {
        // Disable back button behavior
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.exit_view_layout);

        //AdAdmob adAdmob = new AdAdmob(this);
        //adAdmob.BannerAd((RelativeLayout) findViewById(R.id.banner), this);

        //mContext = this;
        //setupViews();
    }

    private void setupViews() {
        btnYes = findViewById(R.id.btnyes);
        btnNo = findViewById(R.id.btnno);
        lin_rate_yes = findViewById(R.id.lin_rate_yes);

        btnYes.setOnClickListener(this);
        btnNo.setOnClickListener(this);
        lin_rate_yes.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnno) {
            finish();

        } else if (id == R.id.btnyes) {
            sendBroadcast(new Intent(TapToStartActivity.ACTION_CLOSE));
            finish();

        } else if (id == R.id.lin_rate_yes) {
            if (isOnline()) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + mContext.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection..", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }
}
