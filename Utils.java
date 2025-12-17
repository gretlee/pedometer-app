package com.demo.example.utils;

import android.app.Service;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import java.util.Calendar;
import java.util.Locale;

public class Utils implements TextToSpeech.OnInitListener {
    private static final String TAG = "Utils";
    private static Utils instance;
    private Service mService;
    private boolean mSpeak = true;
    private boolean mSpeakingEngineAvailable = false;
    private TextToSpeech mTts;

    public void ding() {
    }

    private Utils() {
    }

    public static Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }
        return instance;
    }

    public void setService(Service service) {
        this.mService = service;
    }

    public void initTTS() {
        Log.i(TAG, "Initializing TextToSpeech...");
        try {
            this.mTts = new TextToSpeech(this.mService, this);
        } catch (Exception unused) {
        }
    }

    public void shutdownTTS() {
        Log.i(TAG, "Shutting Down TextToSpeech...");
        this.mSpeakingEngineAvailable = false;
        this.mTts.stop();
        this.mTts.shutdown();
        Log.i(TAG, "TextToSpeech Shut Down.");
    }

    public void say(String str) {
        if (this.mSpeak && this.mSpeakingEngineAvailable) {
            if (Build.VERSION.SDK_INT >= 21) {
                TextToSpeech textToSpeech = this.mTts;
                textToSpeech.speak(str, 1, null, hashCode() + "");
                return;
            }
            this.mTts.speak(str, 1, null);
        }
    }

    @Override 
    public void onInit(int i) {
        if (i == 0) {
            int language = this.mTts.setLanguage(Locale.US);
            if (language == -1 || language == -2) {
                Log.e(TAG, "Language is not available.");
                return;
            }
            Log.i(TAG, "TextToSpeech Initialized.");
            this.mSpeakingEngineAvailable = true;
            return;
        }
        Log.e(TAG, "Could not initialize TextToSpeech.");
    }

    public void setSpeak(boolean z) {
        this.mSpeak = z;
    }

    public boolean isSpeakingEnabled() {
        return this.mSpeak;
    }

    public boolean isSpeakingNow() {
        return this.mTts.isSpeaking();
    }

    public static long currentTimeInMillis() {
        return Calendar.getInstance().getTimeInMillis();
    }
}
