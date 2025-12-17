package com.demo.example;

import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;

import com.demo.example.utils.Utils;


public class PaceNotifier implements StepListener, SpeakingTimer.Listener {
    Context mContext;
    int mDesiredPace;
    PedometerSettings mSettings;
    boolean mShouldTellFasterslower;
    Utils mUtils;
    int mCounter = 0;
    private long[] mLastStepDeltas = {-1, -1, -1, -1};
    private int mLastStepDeltasIndex = 0;
    private long mLastStepTime = 0;
    private ArrayList<Listener> mListeners = new ArrayList<>();
    private long mPace = 0;
    private long mSpokenAt = 0;

    
    public interface Listener {
        void paceChanged(int i);

        void passValue();
    }

    @Override 
    public void passValue() {
    }

    public PaceNotifier(PedometerSettings pedometerSettings, Utils utils, Context context) {
        this.mUtils = utils;
        this.mSettings = pedometerSettings;
        this.mDesiredPace = pedometerSettings.getDesiredPace();
        reloadSettings();
        this.mContext = context;
    }

    public void setPace(int i) {
        long j = i;
        this.mPace = j;
        int i2 = (int) (60000.0d / j);
        int i3 = 0;
        while (true) {
            long[] jArr = this.mLastStepDeltas;
            if (i3 < jArr.length) {
                jArr[i3] = i2;
                i3++;
            } else {
                notifyListener();
                return;
            }
        }
    }

    public void reloadSettings() {
        this.mShouldTellFasterslower = this.mSettings.shouldTellFasterslower() && this.mSettings.getMaintainOption() == PedometerSettings.M_PACE;
        notifyListener();
    }

    public void addListener(Listener listener) {
        this.mListeners.add(listener);
    }

    public void setDesiredPace(int i) {
        this.mDesiredPace = i;
    }

    @Override 
    public void onStep() {
        long[] jArr;
        boolean z;
        long currentTimeMillis = System.currentTimeMillis();
        boolean z2 = true;
        this.mCounter++;
        long j = this.mLastStepTime;
        if (j > 0) {
            long[] jArr2 = this.mLastStepDeltas;
            int i = this.mLastStepDeltasIndex;
            jArr2[i] = currentTimeMillis - j;
            this.mLastStepDeltasIndex = (i + 1) % jArr2.length;
            long j2 = 0;
            int i2 = 0;
            while (true) {
                jArr = this.mLastStepDeltas;
                if (i2 >= jArr.length) {
                    z = true;
                    break;
                } else if (jArr[i2] < 0) {
                    z = false;
                    break;
                } else {
                    j2 += jArr[i2];
                    i2++;
                }
            }
            if (!z || j2 <= 0) {
                this.mPace = -1L;
            } else {
                long length = j2 / jArr.length;
                if (length == 0) {
                    length = 1;
                }
                this.mPace = 60000 / length;
                if (this.mShouldTellFasterslower && !this.mUtils.isSpeakingEnabled() && currentTimeMillis - this.mSpokenAt > 3000 && !this.mUtils.isSpeakingNow()) {
                    long j3 = this.mPace;
                    int i3 = this.mDesiredPace;
                    if (((float) j3) < i3 * 0.5f) {
                        this.mUtils.say(this.mContext.getResources().getString(R.string.much_faster));
                    } else if (((float) j3) > i3 * 1.5f) {
                        this.mUtils.say(this.mContext.getResources().getString(R.string.much_slower));
                    } else if (((float) j3) < i3 * 0.7f) {
                        this.mUtils.say(this.mContext.getResources().getString(R.string.faster));
                    } else if (((float) j3) > i3 * 1.3f) {
                        this.mUtils.say(this.mContext.getResources().getString(R.string.slower));
                    } else if (((float) j3) < i3 * 0.9f) {
                        this.mUtils.say(this.mContext.getResources().getString(R.string.little_faster));
                    } else if (((float) j3) > i3 * 1.1f) {
                        this.mUtils.say(this.mContext.getResources().getString(R.string.little_slower));
                    } else {
                        z2 = false;
                    }

                    if (z2) {
                        this.mSpokenAt = currentTimeMillis;
                    }
                }
            }
        }
        this.mLastStepTime = currentTimeMillis;
        notifyListener();
    }

    private void notifyListener() {
        Iterator<Listener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().paceChanged((int) this.mPace);
        }
    }

    @Override 
    public void speak() {
        if (!this.mSettings.shouldTellPace() || this.mPace <= 0) {
            return;
        }
        Utils utils = this.mUtils;
        utils.say(this.mPace + this.mContext.getResources().getString(R.string.steps_per_minute));
    }
}
