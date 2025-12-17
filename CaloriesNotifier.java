package com.demo.example;

import android.content.Context;

import com.demo.example.utils.Utils;

public class CaloriesNotifier implements StepListener, SpeakingTimer.Listener {
    private static double IMPERIAL_RUNNING_FACTOR = 0.75031498d;
    private static double IMPERIAL_WALKING_FACTOR = 0.517d;
    private static double METRIC_RUNNING_FACTOR = 1.02784823d;
    private static double METRIC_WALKING_FACTOR = 0.708d;
    float mBodyWeight;
    private double mCalories = com.github.mikephil.charting.utils.Utils.DOUBLE_EPSILON;
    Context mContext;
    boolean mIsMetric;
    boolean mIsRunning;
    private Listener mListener;
    PedometerSettings mSettings;
    float mStepLength;
    Utils mUtils;

    
    public interface Listener {
        void passValue();

        void valueChanged(float f);
    }

    @Override 
    public void passValue() {
    }

    public CaloriesNotifier(Listener listener, PedometerSettings pedometerSettings, Utils utils, Context context) {
        this.mListener = listener;
        this.mUtils = utils;
        this.mSettings = pedometerSettings;
        reloadSettings();
        this.mContext = context;
    }

    public void setCalories(float f) {
        this.mCalories = f;
        notifyListener();
    }

    public void reloadSettings() {
        this.mIsMetric = this.mSettings.isMetric();
        this.mIsRunning = this.mSettings.isRunning();
        this.mStepLength = this.mSettings.getStepLength();
        this.mBodyWeight = this.mSettings.getBodyWeight();
        notifyListener();
    }

    public void resetValues() {
        this.mCalories = com.github.mikephil.charting.utils.Utils.DOUBLE_EPSILON;
    }

    public void isMetric(boolean z) {
        this.mIsMetric = z;
    }

    public void setStepLength(float f) {
        this.mStepLength = f;
    }

    @Override 
    public void onStep() {
        double d;
        if (this.mIsMetric) {
            double d2 = this.mCalories;
            double d3 = this.mBodyWeight;
            if (this.mIsRunning) {
                d = METRIC_RUNNING_FACTOR;
            } else {
                d = METRIC_WALKING_FACTOR;
            }
            this.mCalories = (((d * d3) * this.mStepLength) / 100000.0d) + d2;
        } else {
            this.mCalories = ((((this.mIsRunning ? IMPERIAL_RUNNING_FACTOR : IMPERIAL_WALKING_FACTOR) * this.mBodyWeight) * this.mStepLength) / 63360.0d) + this.mCalories;
        }
        notifyListener();
    }

    private void notifyListener() {
        this.mListener.valueChanged((float) this.mCalories);
    }

    @Override 
    public void speak() {
        if (this.mSettings.shouldTellCalories() && this.mCalories > com.github.mikephil.charting.utils.Utils.DOUBLE_EPSILON) {
            Utils utils = this.mUtils;
            utils.say("" + ((int) this.mCalories) + this.mContext.getResources().getString(R.string.calories_burned));

        }
    }
}
