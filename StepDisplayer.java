package com.demo.example;

import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;

import com.demo.example.utils.Utils;


public class StepDisplayer implements StepListener, SpeakingTimer.Listener {
    Context mContext;
    private int mCount = 0;
    private ArrayList<Listener> mListeners = new ArrayList<>();
    PedometerSettings mSettings;
    Utils mUtils;

    
    public interface Listener {
        void passValue();

        void stepsChanged(int i);
    }

    @Override 
    public void passValue() {
    }

    public StepDisplayer(PedometerSettings pedometerSettings, Utils utils, Context context) {
        this.mUtils = utils;
        this.mSettings = pedometerSettings;
        notifyListener();
        this.mContext = context;
    }

    public void setUtils(Utils utils) {
        this.mUtils = utils;
    }

    public void setSteps(int i) {
        this.mCount = i;
        notifyListener();
    }

    @Override 
    public void onStep() {
        this.mCount++;
        notifyListener();
    }

    public void reloadSettings() {
        notifyListener();
    }

    public void addListener(Listener listener) {
        this.mListeners.add(listener);
    }

    public void notifyListener() {
        Iterator<Listener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().stepsChanged(this.mCount);
        }
    }

    @Override 
    public void speak() {
        if (!this.mSettings.shouldTellSteps() || this.mCount <= 0) {
            return;
        }
        Utils utils = this.mUtils;
        utils.say("" + this.mCount + this.mContext.getResources().getString(R.string.steps));
    }
}

