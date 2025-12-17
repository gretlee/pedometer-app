package com.demo.example;

import java.util.ArrayList;
import java.util.Iterator;

import com.demo.example.utils.Utils;


public class SpeakingTimer implements StepListener {
    float mInterval;
    long mLastSpeakTime = System.currentTimeMillis();
    private ArrayList<Listener> mListeners = new ArrayList<>();
    PedometerSettings mSettings;
    boolean mShouldSpeak;
    Utils mUtils;

    
    public interface Listener {
        void speak();
    }

    @Override 
    public void passValue() {
    }

    public SpeakingTimer(PedometerSettings pedometerSettings, Utils utils) {
        this.mSettings = pedometerSettings;
        this.mUtils = utils;
        reloadSettings();
    }

    public void reloadSettings() {
        this.mShouldSpeak = this.mSettings.shouldSpeak();
        this.mInterval = this.mSettings.getSpeakingInterval();
    }

    @Override 
    public void onStep() {
        long currentTimeMillis = System.currentTimeMillis();
        if ((currentTimeMillis - this.mLastSpeakTime) / 60000.0d >= this.mInterval) {
            this.mLastSpeakTime = currentTimeMillis;
            notifyListeners();
        }
    }

    public void addListener(Listener listener) {
        this.mListeners.add(listener);
    }

    public void notifyListeners() {
        this.mUtils.ding();
        Iterator<Listener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().speak();
        }
    }

    public boolean isSpeaking() {
        return this.mUtils.isSpeakingNow();
    }
}
