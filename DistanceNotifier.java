package com.demo.example;

import android.content.Context;
import android.content.res.Resources;

import com.demo.example.utils.Utils;


public class DistanceNotifier implements StepListener, SpeakingTimer.Listener {
    Context mContext;
    float mDistance = 0.0f;
    boolean mIsMetric;
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

    public DistanceNotifier(Listener listener, PedometerSettings pedometerSettings, Utils utils, Context context) {
        this.mListener = listener;
        this.mUtils = utils;
        this.mSettings = pedometerSettings;
        reloadSettings();
        this.mContext = context;
    }

    public void setDistance(float f) {
        this.mDistance = f;
        notifyListener();
    }

    public void reloadSettings() {
        this.mIsMetric = this.mSettings.isMetric();
        this.mStepLength = this.mSettings.getStepLength();
        notifyListener();
    }

    @Override 
    public void onStep() {
        if (this.mIsMetric) {
            this.mDistance += (float) (this.mStepLength / 100000.0d);
        } else {
            this.mDistance += (float) (this.mStepLength / 63360.0d);
        }
        notifyListener();
    }

    private void notifyListener() {
        this.mListener.valueChanged(this.mDistance);
    }

    @Override 
    public void speak() {
        Resources resources;
        int i;
        if (!this.mSettings.shouldTellDistance() || this.mDistance < 0.001f) {
            return;
        }
        Utils utils = this.mUtils;
        StringBuilder sb = new StringBuilder();
        sb.append(("" + (this.mDistance + 1.0E-6f)).substring(0, 4));
        if (this.mIsMetric) {
            resources = this.mContext.getResources();
            i = R.string.kilometers;
        } else {
            resources = this.mContext.getResources();
            i = R.string.miles;
        }

        sb.append(resources.getString(i));
        utils.say(sb.toString());
    }
}
