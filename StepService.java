package com.demo.example;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.demo.example.DB.DataSource;
import com.demo.example.DB.MySQLiteHelper;
import com.demo.example.DB.SingleRow;
import com.demo.example.activity.MainActivity;
import com.demo.example.utils.Utils;


public class StepService extends Service {
    private static final String TAG = "pedometer StepService";
    private DataSource db;
    private ICallback mCallback;
    private float mCalories;
    private CaloriesNotifier mCaloriesNotifier;
    private int mDesiredPace;
    private float mDesiredSpeed;
    private float mDistance;
    private DistanceNotifier mDistanceNotifier;
    private NotificationManager mNM;
    private int mPace;
    private PaceNotifier mPaceNotifier;
    private PedometerSettings mPedometerSettings;
    private Sensor mSensor;
    private SensorManager mSensorManager;
    private SharedPreferences mSettings;
    private SpeakingTimer mSpeakingTimer;
    private float mSpeed;
    private SpeedNotifier mSpeedNotifier;
    private SharedPreferences mState;
    private SharedPreferences.Editor mStateEditor;
    private StepDetector mStepDetector;
    private StepDisplayer mStepDisplayer;
    private int mSteps;
    private Utils mUtils;
    private Timer timer;
    private PowerManager.WakeLock wakeLock;
    boolean isInit = false;
    private final IBinder mBinder = new StepBinder();
    private CaloriesNotifier.Listener mCaloriesListener = new C08745();
    private DistanceNotifier.Listener mDistanceListener = new C08723();
    private PaceNotifier.Listener mPaceListener = new C08712();
    private BroadcastReceiver mReceiver = new C05046();
    private SpeedNotifier.Listener mSpeedListener = new C08734();
    private StepDisplayer.Listener mStepListener = new C08701();

    
    public interface ICallback {
        void caloriesChanged(float f);

        void distanceChanged(float f);

        void paceChanged(int i);

        void speedChanged(float f);

        void stepsChanged(int i);
    }

    
    class C05046 extends BroadcastReceiver {
        C05046() {
        }

        @Override 
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                StepService.this.unregisterDetector();
                StepService.this.registerDetector();
                if (StepService.this.mPedometerSettings.wakeAggressively()) {
                    StepService.this.wakeLock.release();
                    StepService.this.acquireWakeLock();
                }
            }
        }
    }

    
    private class DelayedTask extends TimerTask {
        private DelayedTask() {
        }

        @Override 
        public void run() {
            StepService.this.updateDB();
        }
    }

    
    public class StepBinder extends Binder {
        public StepBinder() {
        }

        public StepService getService() {
            return StepService.this;
        }
    }

    
    class C08701 implements StepDisplayer.Listener {
        C08701() {
        }

        @Override 
        public void stepsChanged(int i) {
            if (Globles.ResetIt) {
                Globles.ResetIt = false;
                StepService.this.resetValues();
            }
            StepService.this.mSteps = i;
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(StepService.this.getApplicationContext());
            ComponentName componentName = new ComponentName(StepService.this.getApplicationContext(), MyWidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
            StepService stepService = StepService.this;
            stepService.mStateEditor = stepService.mState.edit();
            StepService.this.mStateEditor.putInt(MySQLiteHelper.COLUMN_STEPS, StepService.this.mSteps);
            StepService.this.mStateEditor.putInt("pace", StepService.this.mPace);
            StepService.this.mStateEditor.putFloat("distance", StepService.this.mDistance);
            StepService.this.mStateEditor.putFloat("speed", StepService.this.mSpeed);
            StepService.this.mStateEditor.putFloat(MySQLiteHelper.COLUMN_CALORIES, StepService.this.mCalories);
            StepService.this.mStateEditor.commit();
            if (appWidgetIds != null) {
                for (int i2 = 0; i2 < appWidgetIds.length; i2++) {
                    RemoteViews remoteViews = new RemoteViews(StepService.this.getPackageName(), (int) R.layout.widget_layout);
                    remoteViews.setTextViewText(R.id.stepsInWidget2, i + "");
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("%.3f", Float.valueOf(StepService.this.mDistance)));
                    Resources resources = StepService.this.getResources();
                    boolean isMetric = StepService.this.mPedometerSettings.isMetric();
                    int i3 = R.string.kilometers_per_hour;
                    sb.append(resources.getString(isMetric ? R.string.kilometers_per_hour : R.string.miles_per_hour));
                    remoteViews.setTextViewText(R.id.distance_value, sb.toString());
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(String.format("%.1f", Float.valueOf(StepService.this.mSpeed)));
                    Resources resources2 = StepService.this.getResources();
                    if (!StepService.this.mPedometerSettings.isMetric()) {
                        i3 = R.string.miles_per_hour;
                    }
                    sb2.append(resources2.getString(i3));
                    remoteViews.setTextViewText(R.id.speed_value, sb2.toString());
                    remoteViews.setTextViewText(R.id.pace_value, StepService.this.mPace + " " + StepService.this.getResources().getString(R.string.steps_per_minute));
                    remoteViews.setTextViewText(R.id.calories_value, String.format("%.1f", Float.valueOf(StepService.this.mCalories)) + " " + StepService.this.getResources().getString(R.string.calories_burned));
                    appWidgetManager.updateAppWidget(componentName, remoteViews);
                }
            }
            passValue();
        }

        @Override 
        public void passValue() {
            if (StepService.this.mCallback != null) {
                StepService.this.mCallback.stepsChanged(StepService.this.mSteps);
            }
        }
    }

    
    class C08712 implements PaceNotifier.Listener {
        C08712() {
        }

        @Override 
        public void paceChanged(int i) {
            StepService.this.mPace = i;
            passValue();
        }

        @Override 
        public void passValue() {
            if (StepService.this.mCallback != null) {
                StepService.this.mCallback.paceChanged(StepService.this.mPace);
            }
        }
    }

    
    class C08723 implements DistanceNotifier.Listener {
        C08723() {
        }

        @Override 
        public void valueChanged(float f) {
            StepService.this.mDistance = f;
            passValue();
        }

        @Override 
        public void passValue() {
            if (StepService.this.mCallback != null) {
                StepService.this.mCallback.distanceChanged(StepService.this.mDistance);
            }
        }
    }

    
    class C08734 implements SpeedNotifier.Listener {
        C08734() {
        }

        @Override 
        public void valueChanged(float f) {
            StepService.this.mSpeed = f;
            passValue();
        }

        @Override 
        public void passValue() {
            if (StepService.this.mCallback != null) {
                StepService.this.mCallback.speedChanged(StepService.this.mSpeed);
            }
        }
    }

    
    class C08745 implements CaloriesNotifier.Listener {
        C08745() {
        }

        @Override 
        public void valueChanged(float f) {
            StepService.this.mCalories = f;
            passValue();
        }

        @Override 
        public void passValue() {
            if (StepService.this.mCallback != null) {
                StepService.this.mCallback.caloriesChanged(StepService.this.mCalories);
            }
        }
    }

    @Override 
    public void onCreate() {
        Log.i(TAG, "[SERVICE] onCreate");
        super.onCreate();
        this.db = new DataSource(this);
        Timer timer = new Timer();
        this.timer = timer;
        timer.schedule(new DelayedTask(), 300L, 5000L);
        this.mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        this.mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        this.mPedometerSettings = new PedometerSettings(this.mSettings);
        this.mState = getSharedPreferences("state", 0);
        Utils utils = Utils.getInstance();
        this.mUtils = utils;
        utils.setService(this);
        this.mUtils.initTTS();
        this.isInit = true;
        acquireWakeLock();
        this.mStepDetector = new StepDetector();
        this.mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        registerDetector();
        registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.SCREEN_OFF"));
        StepDisplayer stepDisplayer = new StepDisplayer(this.mPedometerSettings, this.mUtils, this);
        this.mStepDisplayer = stepDisplayer;
        int i = this.mState.getInt(MySQLiteHelper.COLUMN_STEPS, 0);
        this.mSteps = i;
        stepDisplayer.setSteps(i);
        this.mStepDisplayer.addListener(this.mStepListener);
        this.mStepDetector.addStepListener(this.mStepDisplayer);
        PaceNotifier paceNotifier = new PaceNotifier(this.mPedometerSettings, this.mUtils, this);
        this.mPaceNotifier = paceNotifier;
        int i2 = this.mState.getInt("pace", 0);
        this.mPace = i2;
        paceNotifier.setPace(i2);
        this.mPaceNotifier.addListener(this.mPaceListener);
        this.mStepDetector.addStepListener(this.mPaceNotifier);
        DistanceNotifier distanceNotifier = new DistanceNotifier(this.mDistanceListener, this.mPedometerSettings, this.mUtils, this);
        this.mDistanceNotifier = distanceNotifier;
        float f = this.mState.getFloat("distance", 0.0f);
        this.mDistance = f;
        distanceNotifier.setDistance(f);
        this.mStepDetector.addStepListener(this.mDistanceNotifier);
        SpeedNotifier speedNotifier = new SpeedNotifier(this.mSpeedListener, this.mPedometerSettings, this.mUtils, this);
        this.mSpeedNotifier = speedNotifier;
        float f2 = this.mState.getFloat("speed", 0.0f);
        this.mSpeed = f2;
        speedNotifier.setSpeed(f2);
        this.mPaceNotifier.addListener(this.mSpeedNotifier);
        CaloriesNotifier caloriesNotifier = new CaloriesNotifier(this.mCaloriesListener, this.mPedometerSettings, this.mUtils, this);
        this.mCaloriesNotifier = caloriesNotifier;
        float f3 = this.mState.getFloat(MySQLiteHelper.COLUMN_CALORIES, 0.0f);
        this.mCalories = f3;
        caloriesNotifier.setCalories(f3);
        this.mStepDetector.addStepListener(this.mCaloriesNotifier);
        SpeakingTimer speakingTimer = new SpeakingTimer(this.mPedometerSettings, this.mUtils);
        this.mSpeakingTimer = speakingTimer;
        speakingTimer.addListener(this.mStepDisplayer);
        this.mSpeakingTimer.addListener(this.mPaceNotifier);
        this.mSpeakingTimer.addListener(this.mDistanceNotifier);
        this.mSpeakingTimer.addListener(this.mSpeedNotifier);
        this.mSpeakingTimer.addListener(this.mCaloriesNotifier);
        this.mStepDetector.addStepListener(this.mSpeakingTimer);
        reloadSettings();
        Toast makeText = Toast.makeText(this, getText(R.string.started), Toast.LENGTH_SHORT);
        makeText.setGravity(17, 0, 0);
        makeText.show();
    }

    public void updateDB() {
        Calendar calendar = Calendar.getInstance();
        SingleRow singleRow = new SingleRow(0L, 0, 0, 0.0f, 0.0f, 0, 0, 0, 0, 0, 0, 0, 0);
        SharedPreferences sharedPreferences = getSharedPreferences("state", 0);
        int i = calendar.get(6) - sharedPreferences.getInt("diffrenceBetweenDays", calendar.get(6));
        singleRow.setCaloriesBurn(this.mCalories);
        singleRow.setDistence(this.mDistance);
        singleRow.setSteps(this.mSteps);
        singleRow.setStepsForWeekView(sharedPreferences.getInt(MySQLiteHelper.COLUMN_STEPS, 0));
        if (i != 0) {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putInt(MySQLiteHelper.COLUMN_STEPS, 0);
            edit.putInt("pace", 0);
            edit.putFloat("distance", 0.0f);
            edit.putFloat("speed", 0.0f);
            edit.putFloat(MySQLiteHelper.COLUMN_CALORIES, 0.0f);
            edit.commit();
            resetValues();
            int i2 = sharedPreferences.getInt(MySQLiteHelper.COLUMN_STEPS, 0);
            this.mSteps = i2;
            singleRow.setSteps(i2);
            sharedPreferences.edit().putInt("diffrenceBetweenDays", calendar.get(6)).commit();
            sharedPreferences.edit().putInt("stepsOfLastHrDiff", 0).commit();
        }
        singleRow.setMints(calendar.get(12));
        singleRow.setHours(calendar.get(11));
        singleRow.setWeek(calendar.get(3));
        singleRow.setDaysOfWeek(calendar.get(7));
        singleRow.setDaysOfMonth(calendar.get(5));
        singleRow.setDaysOfYear(calendar.get(6));
        singleRow.setMonth(calendar.get(2));
        singleRow.setYear(calendar.get(1));
        this.db.open();
        this.db.upDateRecord(singleRow, sharedPreferences);
    }

    @Override 
    public int onStartCommand(Intent intent, int i, int i2) {
        Log.i(TAG, "[SERVICE] onStart");
        int[] appWidgetIds = AppWidgetManager.getInstance(getApplicationContext()).getAppWidgetIds(new ComponentName(getApplicationContext(), MyWidgetProvider.class));
        if (appWidgetIds != null) {
            for (int i3 = 0; i3 < appWidgetIds.length; i3++) {
                new RemoteViews(getApplicationContext().getPackageName(), (int) R.layout.widget_layout).setOnClickPendingIntent(R.id.layout_widget, PendingIntent.getBroadcast(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
            }
            return Service.START_NOT_STICKY;
        }
        return Service.START_NOT_STICKY;
    }

    @Override 
    public void onDestroy() {
        Log.i(TAG, "[SERVICE] onDestroy");
        if (this.isInit) {
            this.mUtils.shutdownTTS();
        }
        this.isInit = false;
        unregisterReceiver(this.mReceiver);
        unregisterDetector();
        SharedPreferences.Editor edit = this.mState.edit();
        this.mStateEditor = edit;
        edit.putInt(MySQLiteHelper.COLUMN_STEPS, this.mSteps);
        this.mStateEditor.putInt("pace", this.mPace);
        this.mStateEditor.putFloat("distance", this.mDistance);
        this.mStateEditor.putFloat("speed", this.mSpeed);
        this.mStateEditor.putFloat(MySQLiteHelper.COLUMN_CALORIES, this.mCalories);
        this.mStateEditor.commit();
        this.mNM.cancel(R.string.app_name);
        this.wakeLock.release();
        this.mSensorManager.unregisterListener(this.mStepDetector);
        Timer timer = this.timer;
        if (timer != null) {
            timer.cancel();
        }
        Toast makeText = Toast.makeText(this, getText(R.string.stopped), Toast.LENGTH_SHORT);
        makeText.setGravity(17, 0, 0);
        makeText.show();
        super.onDestroy();
    }

    
    public void registerDetector() {
        Sensor defaultSensor = this.mSensorManager.getDefaultSensor(1);
        this.mSensor = defaultSensor;
        this.mSensorManager.registerListener(this.mStepDetector, defaultSensor, 0);
    }

    
    public void unregisterDetector() {
        this.mSensorManager.unregisterListener(this.mStepDetector);
    }

    @Override 
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "[SERVICE] onBind");
        return this.mBinder;
    }

    public void registerCallback(ICallback iCallback) {
        this.mCallback = iCallback;
    }

    public void setDesiredPace(int i) {
        this.mDesiredPace = i;
        PaceNotifier paceNotifier = this.mPaceNotifier;
        if (paceNotifier != null) {
            paceNotifier.setDesiredPace(i);
        }
    }

    public void setDesiredSpeed(float f) {
        this.mDesiredSpeed = f;
        SpeedNotifier speedNotifier = this.mSpeedNotifier;
        if (speedNotifier != null) {
            speedNotifier.setDesiredSpeed(f);
        }
    }

    public void reloadSettings() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.mSettings = defaultSharedPreferences;
        StepDetector stepDetector = this.mStepDetector;
        if (stepDetector != null) {
            stepDetector.setSensitivity(Float.valueOf(defaultSharedPreferences.getString("sensitivity", "10")).floatValue());
        }
        StepDisplayer stepDisplayer = this.mStepDisplayer;
        if (stepDisplayer != null) {
            stepDisplayer.reloadSettings();
        }
        PaceNotifier paceNotifier = this.mPaceNotifier;
        if (paceNotifier != null) {
            paceNotifier.reloadSettings();
        }
        DistanceNotifier distanceNotifier = this.mDistanceNotifier;
        if (distanceNotifier != null) {
            distanceNotifier.reloadSettings();
        }
        SpeedNotifier speedNotifier = this.mSpeedNotifier;
        if (speedNotifier != null) {
            speedNotifier.reloadSettings();
        }
        CaloriesNotifier caloriesNotifier = this.mCaloriesNotifier;
        if (caloriesNotifier != null) {
            caloriesNotifier.reloadSettings();
        }
        SpeakingTimer speakingTimer = this.mSpeakingTimer;
        if (speakingTimer != null) {
            speakingTimer.reloadSettings();
        }
    }

    public void resetValues() {
        this.mStepDisplayer.setSteps(0);
        this.mPaceNotifier.setPace(0);
        this.mDistanceNotifier.setDistance(0.0f);
        this.mSpeedNotifier.setSpeed(0.0f);
        this.mCaloriesNotifier.setCalories(0.0f);
        this.mState.edit().putInt("stepsOfLastHrDiff", 0).commit();
    }

    
    public void acquireWakeLock() {
        int i;
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (this.mPedometerSettings.wakeAggressively()) {
            i = 268435462;
        } else {
            i = this.mPedometerSettings.keepScreenOn() ? 6 : 1;
        }
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock newWakeLock = powerManager.newWakeLock(i, TAG);
        this.wakeLock = newWakeLock;
        newWakeLock.acquire();
    }
}
