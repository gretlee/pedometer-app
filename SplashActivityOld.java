package com.demo.example.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demo.example.R;



public class SplashActivityOld extends Activity {
    int ads_const;
    SharedPreferences.Editor editor;
    ProgressBar progressBar;
    SharedPreferences spref;

    public Bundle getNonPersonalizedAdsBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("npa", "1");
        return bundle;
    }

    @Override 
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP, Intent.FLAG_ACTIVITY_CLEAR_TOP);
        setContentView(R.layout.splashactivity);
        SharedPreferences sharedPreferences = getSharedPreferences("pref_ads", 0);
        this.spref = sharedPreferences;
        this.editor = sharedPreferences.edit();
        this.ads_const = this.spref.getInt("ads_const", 0);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.progressBar = progressBar;
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.MULTIPLY);
        if (isOnline()) {
            load();
        } else {
            load();
        }
    }

    public boolean isOnline() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    
    public void showdailog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.consent_form);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(false);
        TextView textView = (TextView) dialog.findViewById(R.id.txt_privacy);
        textView.setText(Html.fromHtml("<a href='https://companyapp.wixsite.com/privacypolicy'>Learn how our partners will collect and use data under our Privacy Policy.</a>"));
        textView.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivityOld.this, Privacy_Policy_activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                SplashActivityOld.this.startActivity(intent);
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.lin_yes)).setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                SplashActivityOld.this.editor.putInt("ads_const", 0);
                SplashActivityOld.this.editor.commit();
                dialog.dismiss();
                SplashActivityOld.this.progressBar.setVisibility(View.VISIBLE);
                load();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.lin_no)).setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                SplashActivityOld.this.editor.putInt("ads_const", 1);
                SplashActivityOld.this.editor.commit();
                dialog.dismiss();
                SplashActivityOld.this.progressBar.setVisibility(View.VISIBLE);
                load();
            }
        });
        dialog.show();
    }
    public void load() {
        new Handler().postDelayed(new Runnable() { 
            @Override 
            public void run() {
                doFunc();
            }
        }, 1000L);
    }


    
    public void doFunc() {
        startActivity(new Intent(this, TapToStartActivity.class));
        finish();
    }

    @Override 
    public void onBackPressed() {
        super.onBackPressed();
        ExitApp();
    }

    public void ExitApp() {
        moveTaskToBack(true);
        finish();
        Process.killProcess(Process.myPid());
        System.exit(0);
    }
}
