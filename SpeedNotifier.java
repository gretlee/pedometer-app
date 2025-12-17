package com.demo.example;

import android.content.Context;
import android.content.res.Resources;

import com.demo.example.utils.Utils;


public class SpeedNotifier implements PaceNotifier.Listener, SpeakingTimer.Listener {
    Context mContext;
    float mDesiredSpeed;
    boolean mIsMetric;
    private Listener mListener;
    PedometerSettings mSettings;
    boolean mShouldTellFasterslower;
    boolean mShouldTellSpeed;
    float mStepLength;
    Utils mUtils;
    int mCounter = 0;
    float mSpeed = 0.0f;
    private long mSpokenAt = 0;

    
    public interface Listener {
        void passValue();

        void valueChanged(float f);
    }

    @Override 
    public void passValue() {
    }

    public SpeedNotifier(Listener listener, PedometerSettings pedometerSettings, Utils utils, Context context) {
        this.mListener = listener;
        this.mUtils = utils;
        this.mSettings = pedometerSettings;
        this.mDesiredSpeed = pedometerSettings.getDesiredSpeed();
        reloadSettings();
        this.mContext = context;
    }

    public void setSpeed(float f) {
        this.mSpeed = f;
        notifyListener();
    }

    public void reloadSettings() {
        this.mIsMetric = this.mSettings.isMetric();
        this.mStepLength = this.mSettings.getStepLength();
        this.mShouldTellSpeed = this.mSettings.shouldTellSpeed();
        this.mShouldTellFasterslower = this.mSettings.shouldTellFasterslower() && this.mSettings.getMaintainOption() == PedometerSettings.M_SPEED;
        notifyListener();
    }

    public void setDesiredSpeed(float f) {
        this.mDesiredSpeed = f;
    }

    private void notifyListener() {
        this.mListener.valueChanged(this.mSpeed);
    }

    @Override 
    public void paceChanged(int i) {
        if (this.mIsMetric) {
            this.mSpeed = ((i * this.mStepLength) / 100000.0f) * 60.0f;
        } else {
            this.mSpeed = ((i * this.mStepLength) / 63360.0f) * 60.0f;
        }
        tellFasterSlower();
        notifyListener();
    }

    private void tellFasterSlower() {
        if (this.mShouldTellFasterslower && this.mUtils.isSpeakingEnabled()) {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - this.mSpokenAt <= 3000 || this.mUtils.isSpeakingNow()) {
                return;
            }
            boolean z = true;
            float f = this.mSpeed;
            float f2 = this.mDesiredSpeed;
            if (f < 0.5f * f2) {
                this.mUtils.say(this.mContext.getResources().getString(R.string.much_faster));
            } else if (f > 1.5f * f2) {
                this.mUtils.say(this.mContext.getResources().getString(R.string.much_slower));
            } else if (f < 0.7f * f2) {
                this.mUtils.say(this.mContext.getResources().getString(R.string.faster));
            } else if (f > 1.3f * f2) {
                this.mUtils.say(this.mContext.getResources().getString(R.string.slower));

            } else if (f < 0.9f * f2) {
                this.mUtils.say(this.mContext.getResources().getString(R.string.little_faster));
            } else if (f > f2 * 1.1f) {
                this.mUtils.say(this.mContext.getResources().getString(R.string.little_slower));
            } else {
                z = false;
            }
            if (z) {
                this.mSpokenAt = currentTimeMillis;
            }
        }
    }

    @Override 
    public void speak() {
        Resources resources;
        int i;
        if (!this.mSettings.shouldTellSpeed() || this.mSpeed < 0.01f) {
            return;
        }
        Utils utils = this.mUtils;
        StringBuilder sb = new StringBuilder();
        sb.append(("" + (this.mSpeed + 1.0E-6f)).substring(0, 4));
        if (this.mIsMetric) {
            resources = this.mContext.getResources();
            i = R.string.kilometers_per_hour;
        } else {
            resources = this.mContext.getResources();
            i = R.string.miles_per_hour;
        }
        sb.append(resources.getString(i));
        utils.say(sb.toString());
    }
}
