package com.demo.example.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.demo.example.AdAdmob;
import com.demo.example.R;

import com.demo.example.DB.MySQLiteHelper;
import com.demo.example.Globles;


public class Settings extends PreferenceActivity {
    Context context = this;
    LinearLayout lin_back;
    TextView txt_title;

    @Override 
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
    @Override 
    public void onCreate(Bundle bundle) {
        requestWindowFeature(1);
        super.onCreate(bundle);
        Window window = getWindow();
        window.addFlags(Integer.MIN_VALUE);
        window.clearFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        addPreferencesFromResource(R.xml.preferences);
        setContentView(R.layout.setting);



        //AdAdmob adAdmob = new AdAdmob( this);
        //adAdmob.BannerAd((RelativeLayout) findViewById(R.id.banner), this);
        //adAdmob.FullscreenAd_Counter(this);




        window.setStatusBarColor(getResources().getColor(R.color.color1));
        setTheme(R.style.PreferenceScreen);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lin_back);
        this.lin_back = linearLayout;
        linearLayout.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Settings.this.finish();
            }
        });
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "Titillium-Semibold.otf");
        TextView textView = (TextView) findViewById(R.id.txt_title);
        this.txt_title = textView;
        textView.setTypeface(createFromAsset);
        getPreferenceScreen().findPreference("reseting").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { 
            @Override 
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setTitle(Settings.this.getResources().getString(R.string.rest_it_str));
                builder.setMessage(Settings.this.getResources().getString(R.string.rest_step_this_day));
                builder.setPositiveButton(Settings.this.getResources().getString(R.string.ok_str), new DialogInterface.OnClickListener() { 
                    @Override 
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor edit = Settings.this.getSharedPreferences("state", 0).edit();
                        edit.putInt(MySQLiteHelper.COLUMN_STEPS, 0);
                        edit.putInt("pace", 0);
                        edit.putFloat("distance", 0.0f);
                        edit.putFloat("speed", 0.0f);
                        edit.putFloat(MySQLiteHelper.COLUMN_CALORIES, 0.0f);
                        edit.putInt("stepsOfLastHrDiff", 0).commit();
                        Globles.ResetIt = true;
                    }
                });
                builder.setNegativeButton(Settings.this.getResources().getString(R.string.cancel_str), new DialogInterface.OnClickListener() { 
                    @Override 
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
                return false;
            }
        });
    }
}
