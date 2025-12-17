package com.demo.example.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

//import com.demo.example.AdAdmob;
import com.demo.example.R;
import com.demo.example.DB.MySQLiteHelper;
import com.demo.example.PedometerSettings;
import com.demo.example.StepService;
import com.demo.example.communicator;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements communicator {

    private static final int CALORIES_MSG = 5;
    private static final int DISTANCE_MSG = 3;
    private static final int PACE_MSG = 2;
    private static final int SPEED_MSG = 4;
    private static final int STEPS_MSG = 1;

    public static boolean mIsRunning;

    private Activity activity;
    private Context context;

    private ProgressBar donutProgress;
    private Typeface font, font1;

    private CircleImageView img_add_photo_main;
    private ImageView img_playstop;

    private LinearLayout lin_back, lin_bmi, lin_calorie_chart, lin_gps, lin_history,
            lin_playstop, lin_setgoal, lin_setting, lin_step_chart;

    private int mCaloriesValue, mStepValue, mPaceValue;
    private float mDistanceValue, mSpeedValue;

    private TextView txt_bmi, txt_btn_goal, txt_calorie, txt_counter, txt_goal, txt_goal_hint,
            txt_gps, txt_height, txt_history, txt_name, txt_progress, txt_step, txt_target, txt_title,
            txt_weight;

    private TextView mCaloriesValueView, mDistanceValueView, mPaceValueView, mSpeedValueView;

    private SharedPreferences pref, mSettings;
    private PedometerSettings mPedometerSettings;
    private StepService mService;
    private boolean isBound = false;

    private final StepService.ICallback mCallback = new StepService.ICallback() {
        @Override
        public void stepsChanged(int i) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, i, 0));
        }

        @Override
        public void paceChanged(int i) {
            mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, i, 0));
        }

        @Override
        public void distanceChanged(float f) {
            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int) (f * 1000.0f), 0));
        }

        @Override
        public void speedChanged(float f) {
            mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG, (int) (f * 1000.0f), 0));
        }

        @Override
        public void caloriesChanged(float f) {
            mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int) f, 0));
        }
    };

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = ((StepService.StepBinder) iBinder).getService();
            mService.registerCallback(mCallback);
            mService.reloadSettings();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message message) {
            switch (message.what) {
                case STEPS_MSG:
                    mStepValue = message.arg1;
                    donutProgress.setProgress(mStepValue);
                    txt_progress.setText(String.valueOf(mStepValue));
                    respond(getString(R.string.steps_value, mStepValue));
                    break;

                case PACE_MSG:
                    mPaceValue = message.arg1;
                    mPaceValueView.setText(mPaceValue <= 0
                            ? getString(R.string.steps_per_minute)
                            : getString(R.string.steps_per_minute_value, mPaceValue));
                    break;

                case DISTANCE_MSG:
                    mDistanceValue = message.arg1 / 1000.0f;
                    String distUnit = mPedometerSettings.isMetric() ? getString(R.string.kilometers) : getString(R.string.miles);
                    mDistanceValueView.setText(mDistanceValue <= 0
                            ? getString(R.string.distance_text, 0.0f, distUnit)
                            : getString(R.string.distance_text, mDistanceValue, distUnit));
                    break;

                case SPEED_MSG:
                    mSpeedValue = message.arg1 / 1000.0f;
                    String speedUnit = mPedometerSettings.isMetric() ? getString(R.string.kilometers_per_hour) : getString(R.string.miles_per_hour);
                    mSpeedValueView.setText(mSpeedValue <= 0
                            ? getString(R.string.speed_text, 0.0f, speedUnit)
                            : getString(R.string.speed_text, mSpeedValue, speedUnit));
                    break;

                case CALORIES_MSG:
                    mCaloriesValue = message.arg1;
                    mCaloriesValueView.setText(mCaloriesValue <= 0
                            ? getString(R.string.calories_value, 0)
                            : getString(R.string.calories_value, mCaloriesValue));
                    break;

                default:
                    super.handleMessage(message);
            }
        }
    };

    @Override
    public void respond(String str) {
        // kept empty intentionally (logic intact)
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //AdAdmob.FullscreenAd_Counter(this);

        context = this;
        activity = this;

        font = Typeface.createFromAsset(getAssets(), "Titillium-Semibold.otf");
        font1 = Typeface.createFromAsset(getAssets(), "Titillium-Regular.otf");

        if (!checkPermission()) requestPermission();

        initializeViews();
        loadUserPreferences();
        setupClickListeners();

        int goalSteps = Math.round(Float.parseFloat(mSettings.getString("Goal", "1000").trim()));
        donutProgress.setMax(goalSteps);
        txt_goal.setText(String.format("/%d", goalSteps));
        txt_progress.setText(String.valueOf(donutProgress.getProgress()));

        if (mPedometerSettings.isServiceRunning()) {
            startStepService();
            bindStepService();
        } else {
            restoreSavedState();
        }
    }

    private void initializeViews() {
        lin_setgoal = findViewById(R.id.lin_setgoal);
        lin_playstop = findViewById(R.id.lin_playstop);
        lin_step_chart = findViewById(R.id.lin_step_chart);
        lin_calorie_chart = findViewById(R.id.lin_calorie_chart);
        lin_bmi = findViewById(R.id.lin_bmi);
        lin_history = findViewById(R.id.lin_history);
        lin_gps = findViewById(R.id.lin_gps);
        img_playstop = findViewById(R.id.img_playstop);
        img_add_photo_main = findViewById(R.id.img_add_photo_main);
        lin_back = findViewById(R.id.lin_back);
        lin_setting = findViewById(R.id.lin_setting);

        txt_name = findViewById(R.id.txt_main_name);
        txt_progress = findViewById(R.id.txt_main_progress);
        txt_goal = findViewById(R.id.txt_main_goal);
        txt_weight = findViewById(R.id.txt_main_weight);
        txt_height = findViewById(R.id.txt_main_height);
        txt_step = findViewById(R.id.txt_step);
        txt_calorie = findViewById(R.id.txt_calorie);
        txt_bmi = findViewById(R.id.txt_bmi);
        txt_history = findViewById(R.id.txt_history);
        txt_goal_hint = findViewById(R.id.txt_goal_hint);
        txt_counter = findViewById(R.id.txt_counter);
        txt_gps = findViewById(R.id.txt_gps);
        txt_title = findViewById(R.id.txt_title);
        txt_btn_goal = findViewById(R.id.txt_btn_goal);
        txt_target = findViewById(R.id.txttarget);

        donutProgress = findViewById(R.id.lin_progress);
        mPaceValueView = findViewById(R.id.pace_value);
        mDistanceValueView = findViewById(R.id.distance_value);
        mSpeedValueView = findViewById(R.id.speed_value);
        mCaloriesValueView = findViewById(R.id.calories_value);

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new PedometerSettings(mSettings);

        TextView[] boldTexts = {txt_step, txt_calorie, txt_bmi, txt_history, txt_goal_hint,
                txt_counter, txt_name, txt_title, txt_btn_goal, txt_gps};
        for (TextView t : boldTexts) t.setTypeface(font);

        TextView[] regularTexts = {txt_height, txt_weight, txt_goal, txt_progress, txt_target};
        for (TextView t : regularTexts) t.setTypeface(font1);
    }

    private void loadUserPreferences() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        String photo = pref.getString("img_photo", "");
        if (photo.isEmpty()) img_add_photo_main.setImageResource(R.drawable.add_img);
        else img_add_photo_main.setImageBitmap(decodeBase64(photo));

        String name = pref.getString("Name", "");
        txt_name.setText(name.isEmpty() ? getString(R.string.name_placeholder) : name);

        float weight = pref.getFloat("Weight", 0.0f);
        txt_weight.setText(weight == 0.0f ? "" : getString(R.string.label_weight_value, weight));

        float height = pref.getFloat("Height", 0.0f);
        txt_height.setText(height == 0.0f ? "" : getString(R.string.label_height_value, height));
    }

    private void setupClickListeners() {
        lin_back.setOnClickListener(v -> finish());
        lin_setting.setOnClickListener(v -> startActivity(new Intent(this, Settings.class)));

        img_add_photo_main.setOnClickListener(v -> startActivity(new Intent(this, Set_Profile_Activity.class)));
        lin_setgoal.setOnClickListener(v -> startActivity(new Intent(this, Set_Goal_Activity.class)));
        lin_step_chart.setOnClickListener(v -> startActivity(new Intent(this, Chart_Step_Activity.class)));
        lin_calorie_chart.setOnClickListener(v -> startActivity(new Intent(this, Chart_Calorie_Activity.class)));
        lin_bmi.setOnClickListener(v -> startActivity(new Intent(this, BMI_Calculator_Activity.class)));
        lin_history.setOnClickListener(v -> startActivity(new Intent(this, History_Activity.class)));
        lin_gps.setOnClickListener(v -> startActivity(new Intent(this, MapsActivity.class)));

        lin_playstop.setOnClickListener(v -> {
            if (mIsRunning) {
                unbindStepService();
                stopStepService();
                img_playstop.setImageResource(R.drawable.btn_play);
                txt_counter.setText(getString(R.string.play_counter));
            } else {
                startStepService();
                bindStepService();
                img_playstop.setImageResource(R.drawable.stop_icon);
                txt_counter.setText(getString(R.string.play_counter));
            }
        });
    }

    private void restoreSavedState() {
        SharedPreferences sharedPreferences = getSharedPreferences("state", MODE_PRIVATE);
        mPaceValueView.setText(getString(R.string.steps_per_minute_value,
                sharedPreferences.getInt("pace", 0)));
        mDistanceValueView.setText(getString(R.string.distance_text,
                sharedPreferences.getFloat("distance", 0.0f),
                getString(R.string.kilometers)));
        mSpeedValueView.setText(getString(R.string.speed_text,
                sharedPreferences.getFloat("speed", 0.0f),
                getString(R.string.kilometers_per_hour)));
        mCaloriesValueView.setText(getString(R.string.calories_value,
                (int) sharedPreferences.getFloat(MySQLiteHelper.COLUMN_CALORIES, 0.0f)));
        donutProgress.setProgress(sharedPreferences.getInt(MySQLiteHelper.COLUMN_STEPS, 0));
    }

    public static Bitmap decodeBase64(String str) {
        byte[] decode = Base64.decode(str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decode, 0, decode.length);
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE") == 0;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(activity, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void startStepService() {
        if (mIsRunning) return;
        mSettings.edit().putBoolean("service_running", true).apply();
        mIsRunning = true;
        startService(new Intent(this, StepService.class));
    }

    public void bindStepService() {
        isBound = true;
        bindService(new Intent(this, StepService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbindStepService() {
        if (isBound) unbindService(mConnection);
        isBound = false;
    }

    public void stopStepService() {
        if (mService != null) stopService(new Intent(this, StepService.class));
        mSettings.edit().putBoolean("service_running", false).apply();
        mIsRunning = false;
    }

    public void resetValues(boolean resetSharedPrefs) {
        if (mService == null || !mIsRunning) {
            mPaceValueView.setText("0");
            mDistanceValueView.setText("0");
            mSpeedValueView.setText("0");
            mCaloriesValueView.setText("0");
            if (resetSharedPrefs) {
                SharedPreferences.Editor editor = getSharedPreferences("state", MODE_PRIVATE).edit();
                editor.putInt(MySQLiteHelper.COLUMN_STEPS, 0);
                editor.putInt("pace", 0);
                editor.putFloat("distance", 0.0f);
                editor.putFloat("speed", 0.0f);
                editor.putFloat(MySQLiteHelper.COLUMN_CALORIES, 0.0f);
                editor.apply();
            }
        } else {
            mService.resetValues();
        }
    }
}
