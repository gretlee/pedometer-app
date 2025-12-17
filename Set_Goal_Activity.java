package com.demo.example.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

//import com.demo.example.AdAdmob;
import com.demo.example.R;

import com.demo.example.PedometerSettings;


public class Set_Goal_Activity extends AppCompatActivity {
    Typeface font;
    Typeface font1;
    private PedometerSettings mPedometerSettings;
    private SharedPreferences mSettings;
    int temp;

    
    @Override 
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.setgoal_dialog);

        //AdAdmob adAdmob = new AdAdmob( this);
        //adAdmob.BannerAd((RelativeLayout) findViewById(R.id.banner), this);
        //adAdmob.FullscreenAd_Counter(this);





        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP, Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        this.mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        this.mPedometerSettings = new PedometerSettings(this.mSettings);
        this.mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        this.mPedometerSettings = new PedometerSettings(this.mSettings);
        this.temp = Math.round(Float.parseFloat(this.mSettings.getString("Goal", "1000").trim()));
        this.font = Typeface.createFromAsset(getAssets(), "Titillium-Semibold.otf");
        this.font1 = Typeface.createFromAsset(getAssets(), "Titillium-Regular.otf");
        final EditText editText = (EditText) findViewById(R.id.edtgoal);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lin_dial_cancle);
        LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.lin_dial_setgoal);
        ((TextView) findViewById(R.id.txt_dial_title)).setTypeface(this.font);
        ((TextView) findViewById(R.id.txt_cancle)).setTypeface(this.font);
        ((TextView) findViewById(R.id.txt_setgoal)).setTypeface(this.font);
        editText.setTypeface(this.font1);
        if (this.temp == 0) {
            editText.setText("");
        } else {
            editText.setText(this.temp + "");
        }
        linearLayout.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Set_Goal_Activity.this.finish();
            }
        });
        linearLayout2.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Set_Goal_Activity.this.mSettings.edit().putString("Goal", editText.getText().toString()).commit();
                Intent intent = new Intent(Set_Goal_Activity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Set_Goal_Activity.this.startActivity(intent);
            }
        });
    }

    @Override 
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}
