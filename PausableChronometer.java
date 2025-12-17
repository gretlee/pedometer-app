package com.demo.example;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.Chronometer;


public class PausableChronometer extends Chronometer {
    private long mTimeWhenPaused;

    public PausableChronometer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mTimeWhenPaused = 0L;
    }

    public void restart() {
        reset();
        start();
    }

    public void reset() {
        stop();
        setBase(SystemClock.elapsedRealtime());
        this.mTimeWhenPaused = 0L;
    }

    public void resume() {
        setBase(SystemClock.elapsedRealtime() - this.mTimeWhenPaused);
        start();
    }

    public void pause() {
        stop();
        this.mTimeWhenPaused = SystemClock.elapsedRealtime() - getBase();
    }
}
