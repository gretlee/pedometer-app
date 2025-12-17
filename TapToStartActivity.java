package com.demo.example.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

//import com.demo.example.AdAdmob;
import com.demo.example.R;



public class TapToStartActivity extends AppCompatActivity {
    public static final String ACTION_CLOSE = "ACTION_CLOSE";
    public static final String TAG = "Start_Activity";
    private Context context;
    private FirstReceiver firstReceiver;
    Button ll_start;
    TextView txt_privacy;

    
    @Override 
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.taptostart_activity);




        //AdAdmob adAdmob = new AdAdmob( this);
        //adAdmob.BannerAd((RelativeLayout) findViewById(R.id.banner), this);
        //adAdmob.FullscreenAd_Counter(this);




        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP, Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        this.context = getApplicationContext();

        this.ll_start = (Button) findViewById(R.id.ll_start);
        this.txt_privacy = (TextView) findViewById(R.id.txt_privacy);
        Spanned fromHtml = Html.fromHtml("<a href='https://companyapp.wixsite.com/privacypolicy'>Privacy Policy</a>");
        this.ll_start.setTypeface(Typeface.createFromAsset(getAssets(), "Titillium-Semibold.otf"));
        this.txt_privacy.setText(fromHtml);
        this.txt_privacy.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Intent intent = new Intent(TapToStartActivity.this.context, Privacy_Policy_activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                TapToStartActivity.this.startActivity(intent);
            }
        });
        this.ll_start.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Intent intent = new Intent(TapToStartActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                TapToStartActivity.this.startActivity(intent);
            }
        });
        IntentFilter intentFilter = new IntentFilter(ACTION_CLOSE);
        FirstReceiver firstReceiver = new FirstReceiver();
        this.firstReceiver = firstReceiver;
        registerReceiver(firstReceiver, intentFilter);
    }

    @Override 
    public void onBackPressed() {
        startActivity(new Intent(this.context, ExitActivity.class));
    }

    
    @Override 
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.firstReceiver);
    }

    
    class FirstReceiver extends BroadcastReceiver {
        FirstReceiver() {
        }

        @Override 
        public void onReceive(Context context, Intent intent) {
            Log.e("FirstReceiver", "FirstReceiver");
            if (intent.getAction().equals(TapToStartActivity.ACTION_CLOSE)) {
                TapToStartActivity.this.finish();
            }
        }
    }
}
