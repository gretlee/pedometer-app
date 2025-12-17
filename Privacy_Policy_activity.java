package com.demo.example.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

//import com.demo.example.AdAdmob;
import com.demo.example.R;

public class Privacy_Policy_activity extends AppCompatActivity {

    private static final String TAG = "Main";
    Context context;
    ImageView img_permission;
    ImageView img_privacy;
    private ProgressDialog progress;
    Toolbar toolbar;
    private WebView webvw;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView(R.layout.privacy_policy);

        //AdAdmob adAdmob = new AdAdmob(this);
        //adAdmob.FullscreenAd_Counter(this);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(Integer.MIN_VALUE);
            window.clearFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            window.setStatusBarColor(getResources().getColor(R.color.color1));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        this.toolbar = toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        this.toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        getSupportActionBar().setTitle("Permission");

        this.context = this;

        this.webvw = findViewById(R.id.webview);
        this.img_permission = findViewById(R.id.img_permission);
        this.img_privacy = findViewById(R.id.img_privacy);

        // Permission dialog
        this.img_permission.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(Privacy_Policy_activity.this.context, android.R.style.Theme_Dialog);
            dialog.requestWindowFeature(1);
            dialog.setContentView(R.layout.permission_dialog);
            dialog.findViewById(R.id.img_close).setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        });

        // Privacy dialog
        this.img_privacy.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(Privacy_Policy_activity.this.context, android.R.style.Theme_Material_Dialog_NoActionBar);
            dialog.requestWindowFeature(1);
            dialog.setContentView(R.layout.privacy_dialog);
            WebView webView = dialog.findViewById(R.id.privacy_webview);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
            webView.loadUrl("https://companyapp.wixsite.com/privacypolicy");
            dialog.findViewById(R.id.img_cancel).setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        });

        this.webvw.getSettings().setJavaScriptEnabled(true);
        this.webvw.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        final AlertDialog create = new AlertDialog.Builder(this).create();
        this.progress = ProgressDialog.show(this, "Please Wait...", "Loading...");

        this.webvw.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                Log.i(TAG, "Processing webview url click...");
                webView.loadUrl(str);
                return true;
            }

            @Override
            public void onPageFinished(WebView webView, String str) {
                Log.i(TAG, "Finished loading URL: " + str);
                if (progress.isShowing()) {
                    progress.dismiss();
                }
            }

            @Override
            public void onReceivedError(WebView webView, int i, String str, String str2) {
                Log.e(TAG, "Error: " + str);
                create.setTitle("Error");
                create.setMessage(str);
                create.setButton("OK", (dialogInterface, i2) -> {});
                create.show();
            }
        });

        this.webvw.loadUrl("https://www.google.com/");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.privacy) {
            Intent intent2 = new Intent(this, Privacy_Policy_activity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent2);
            return true;
        } else if (id == R.id.rate) {
            if (isOnline()) {
                Intent intent3 = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + this.context.getPackageName()));
                intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent3);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "No Internet Connection..", Toast.LENGTH_SHORT);
                toast.setGravity(17, 0, 0);
                toast.show();
            }
            return true;
        } else if (id == R.id.share) {
            if (isOnline()) {
                Intent intent4 = new Intent(Intent.ACTION_SEND);
                intent4.setType("text/plain");
                intent4.putExtra(Intent.EXTRA_TEXT,
                        "Hi! I'm using a great Ringtone Maker and MP3 Cutter application. Check it out:http://play.google.com/store/apps/details?id="
                                + this.context.getPackageName());
                intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(Intent.createChooser(intent4, "Share with Friends"));
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "No Internet Connection..", Toast.LENGTH_SHORT);
                toast.setGravity(17, 0, 0);
                toast.show();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
    }

    public boolean isOnline() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
