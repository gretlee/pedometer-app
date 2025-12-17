package com.demo.example.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

//import com.demo.example.AdAdmob;
import com.demo.example.DB.MySQLiteHelper;
import com.demo.example.PausableChronometer;
import com.demo.example.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.appbar.MaterialToolbar;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import android.content.pm.PackageManager;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int TIME_DIFFERENCE_THRESHOLD = 60000;
    private static final int LOCATION_PERMISSION_REQUEST = 10;

    private Toolbar animtoolbar;
    private Button btnPause, btnStart, btnStop;
    private Context context;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private LocationManager locationManager;
    private Location oldLocation;
    private LocationListener locationListener;
    private ProgressDialog mDialog;
    private ArrayList<LatLng> points;
    private PausableChronometer timer;
    private SharedPreferences prefs;

    private TextView txtTitle, txtType, txtWalk, txtTime, txtDistance, txtDistanceVal, txtSpeed, txtSpeedVal;
    private LinearLayout linBtn;

    private boolean isPaused = false;

    private final String[] mPermission = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_NETWORK_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(Integer.MIN_VALUE);
        window.clearFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        window.setStatusBarColor(getResources().getColor(R.color.color1));

        setContentView(R.layout.activity_maps);

        context = this;

        /*// Initialize ads
        AdAdmob adAdmob = new AdAdmob(this);
        adAdmob.FullscreenAd_Counter(this);
        */


        // Toolbar setup
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        animtoolbar = toolbar;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        animtoolbar.setTitleTextColor(-1);

        // Typeface setup
        Typeface font = Typeface.createFromAsset(getAssets(), "Titillium-Semibold.otf");

        txtTitle = findViewById(R.id.txt_title);
        txtType = findViewById(R.id.txt_type);
        txtWalk = findViewById(R.id.txt_walk);
        txtTime = findViewById(R.id.txt_timer);
        txtDistance = findViewById(R.id.txt_distance);
        txtDistanceVal = findViewById(R.id.txt_distance_val);
        txtSpeed = findViewById(R.id.txt_speed);
        txtSpeedVal = findViewById(R.id.txt_speed_val);

        txtTitle.setTypeface(font);
        txtType.setTypeface(font);
        txtWalk.setTypeface(font);
        txtTime.setTypeface(font);
        txtDistance.setTypeface(font);
        txtDistanceVal.setTypeface(font);
        txtSpeed.setTypeface(font);
        txtSpeedVal.setTypeface(font);

        btnStart = findViewById(R.id.btn_start);
        btnPause = findViewById(R.id.btn_pause);
        btnStop = findViewById(R.id.btn_stop);
        linBtn = findViewById(R.id.lin_btn);

        btnStart.setTypeface(font);
        btnPause.setTypeface(font);
        btnStop.setTypeface(font);

        timer = findViewById(R.id.timer);
        timer.setTypeface(font);

        prefs = getSharedPreferences("Mapsdata", MODE_PRIVATE);
        points = new ArrayList<>();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Button Actions
        btnStart.setOnClickListener(v -> {
            setStartLocation();
            linBtn.setVisibility(View.VISIBLE);
            btnStart.setVisibility(View.GONE);
            timer.restart();
            txtDistanceVal.setText("");
            txtSpeedVal.setText("");
        });

        btnPause.setOnClickListener(v -> {
            if (!isPaused) {
                timer.pause();
                isPaused = true;
                btnPause.setText("RESUME");
            } else {
                timer.resume();
                isPaused = false;
                btnPause.setText("PAUSE");
            }
        });

        btnStop.setOnClickListener(v -> {
            updateStats(true);
            timer.stop();
            linBtn.setVisibility(View.GONE);
            btnStart.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, mPermission, LOCATION_PERMISSION_REQUEST);
            return;
        }

        mMap.setMyLocationEnabled(true);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String mapType = prefs.getString("map_type", "Normal");
        switch (mapType) {
            case "Hybrid":
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case "Satellite":
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case "Terrain":
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            default:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    private void setStartLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != 0 &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != 0) {
                ActivityCompat.requestPermissions(this, mPermission, LOCATION_PERMISSION_REQUEST);
                return;
            }
            new Thread(() -> {
                runOnUiThread(this::loadInitialLocation);
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeLocationManager() {
        locationListener = new LocationListenerImpl();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == 0 ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == 0) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
            timer.setBase(SystemClock.elapsedRealtime());
            timer.start();
            timer.setOnChronometerTickListener(new SpeedTickListener());
        }
    }

    private void loadInitialLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == 0 ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == 0) {
            mDialog = new ProgressDialog(this);
            mDialog.setMessage("Fetching your starting location...");
            mDialog.setCanceledOnTouchOutside(true);
            mDialog.show();

// Auto-dismiss after 10 seconds if no GPS fix
            new android.os.Handler().postDelayed(() -> {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                    Toast.makeText(this, "Unable to fetch location. Please enable GPS.", Toast.LENGTH_LONG).show();
                }
            }, 10000);


            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000, // 5 sec interval
                    5,    // 5 meters
                    new StartLocationListener(),
                    Looper.getMainLooper()
            );
        }
    }

    private void changeMarker(Location location) {
        if (isBetterLocation(oldLocation, location)) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            points.add(latLng);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(points.get(0)).title("Starting Point"));
            mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here"));
            redrawPath();
            updateStats(false);
        }
        oldLocation = location;
    }

    private void redrawPath() {
        PolylineOptions polyline = new PolylineOptions().width(15f).color(0xffff0000).geodesic(true);
        polyline.addAll(points);
        mMap.addPolyline(polyline);
    }

    private boolean isBetterLocation(Location oldLoc, Location newLoc) {
        if (oldLoc == null) return true;
        boolean newer = newLoc.getTime() > oldLoc.getTime();
        boolean moreAccurate = newLoc.getAccuracy() < oldLoc.getAccuracy();
        return moreAccurate && newer || (moreAccurate && !newer && newLoc.getTime() - oldLoc.getTime() > -TIME_DIFFERENCE_THRESHOLD);
    }

    private double calculateDistance() {
        double distance = 0;
        for (int i = 1; i < points.size(); i++) {
            Location start = new Location("start");
            start.setLatitude(points.get(i - 1).latitude);
            start.setLongitude(points.get(i - 1).longitude);
            Location end = new Location("end");
            end.setLatitude(points.get(i).latitude);
            end.setLongitude(points.get(i).longitude);
            distance += start.distanceTo(end);
        }
        return round(distance / 1000.0, 2, BigDecimal.ROUND_HALF_UP);
    }

    private static double round(double value, int scale, int mode) {
        return new BigDecimal(value).setScale(scale, mode).doubleValue();
    }

    private void updateStats(boolean finalUpdate) {
        double distance = calculateDistance();
        long elapsedTime = SystemClock.elapsedRealtime() - timer.getBase();
        String duration = (String) DateFormat.format("mm:ss", elapsedTime);
        String speed = String.valueOf(round(distance / (((elapsedTime / 1000.0) / 3600.0)), 2, BigDecimal.ROUND_HALF_UP));

        txtDistanceVal.setText(distance + " KM");
        txtSpeedVal.setText(speed + " k/h");

        if (finalUpdate && points.size() > 1) {
            float calories = prefs.getFloat(MySQLiteHelper.COLUMN_CALORIES, 0f);
            float weight = prefs.getFloat("weight_lbs", -1f);
            float newCalories = weight < 0 ? calories : (float) (calories + (weight * 0.75 * distance));
            prefs.edit().putFloat(MySQLiteHelper.COLUMN_CALORIES, newCalories).apply();
        }
    }

    private void fetchStartLocation(Location location) {
        LatLng startLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 15f));
        points.add(startLatLng);
        mMap.addMarker(new MarkerOptions().position(points.get(0)).title("Starting Point"));
        oldLocation = location;
        mDialog.dismiss();
        initializeLocationManager();
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

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
            startActivity(new Intent(this, Privacy_Policy_activity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return true;
        } else if (id == R.id.rate) {
            if (isOnline()) {
                Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName()));
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(rateIntent);
            } else {
                Toast.makeText(this, "No Internet Connection..", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.share) {
            if (isOnline()) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hi! I'm using a great Step Counter Pedometer Free Calorie Counter app. Check it out: http://play.google.com/store/apps/details?id="
                                + getPackageName());
                startActivity(Intent.createChooser(shareIntent, "Share with Friends"));
            } else {
                Toast.makeText(this, "No Internet Connection..", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    // ===== Inner classes =====

    private class LocationListenerImpl implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            changeMarker(location);
        }
        @Override public void onProviderEnabled(String provider) {}
        @Override public void onProviderDisabled(String provider) {}
        @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    private class SpeedTickListener implements Chronometer.OnChronometerTickListener {
        @Override
        public void onChronometerTick(Chronometer chronometer) {
            double speed = round(calculateDistance() / (((SystemClock.elapsedRealtime() - timer.getBase()) / 1000.0) / 3600.0), 2, BigDecimal.ROUND_HALF_UP);
            txtSpeedVal.setText(speed + " k/h");
        }
    }

    private class StartLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            fetchStartLocation(location);
            Log.d("MapStart", "Start location: " + location.getLatitude() + ", " + location.getLongitude());
        }
        @Override public void onProviderEnabled(String provider) {}
        @Override public void onProviderDisabled(String provider) {}
        @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
}
