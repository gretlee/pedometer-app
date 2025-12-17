package com.demo.example;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;


public class StepDetector implements SensorEventListener {
    private static final String TAG = "StepDetector";
    private float[] mLastDiff = new float[6];
    private float[] mLastDirections = new float[6];
    private float[][] mLastExtremes = {new float[6], new float[6]};
    private int mLastMatch = -1;
    private float[] mLastValues = new float[6];
    private float mLimit = 10.0f;
    private float[] mScale = new float[2];
    private ArrayList<StepListener> mStepListeners = new ArrayList<>();
    private float mYOffset = 240.0f;

    @Override 
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public StepDetector() {
        float[] fArr = this.mScale;
        fArr[0] = -12.236594f;
        fArr[1] = -4.0f;
    }

    public void setSensitivity(float f) {
        this.mLimit = f;
    }

    public void addStepListener(StepListener stepListener) {
        this.mStepListeners.add(stepListener);
    }

    @Override 
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        synchronized (this) {
            if (sensor.getType() != 3) {
                boolean z = true;
                char c = sensor.getType() == 1 ? (char) 1 : (char) 0;
                if (c == 1) {
                    float f = 0.0f;
                    for (int i = 0; i < 3; i++) {
                        f += this.mYOffset + (sensorEvent.values[i] * this.mScale[c]);
                    }
                    float f2 = f / 3.0f;
                    float[] fArr = this.mLastValues;
                    float f3 = f2 > fArr[0] ? 1 : f2 < fArr[0] ? -1 : 0;
                    if (f3 == (-this.mLastDirections[0])) {
                        int i2 = f3 > 0.0f ? 0 : 1;
                        float[][] fArr2 = this.mLastExtremes;
                        fArr2[i2][0] = fArr[0];
                        int i3 = 1 - i2;
                        float abs = Math.abs(fArr2[i2][0] - fArr2[i3][0]);
                        if (abs > this.mLimit) {
                            float[] fArr3 = this.mLastDiff;
                            boolean z2 = abs > (fArr3[0] * 2.0f) / 3.0f;
                            boolean z3 = fArr3[0] > abs / 3.0f;
                            if (this.mLastMatch == i3) {
                                z = false;
                            }
                            if (z2 && z3 && z) {
                                Log.i(TAG, "step");
                                Iterator<StepListener> it = this.mStepListeners.iterator();
                                while (it.hasNext()) {
                                    it.next().onStep();
                                }
                                this.mLastMatch = i2;
                            } else {
                                this.mLastMatch = -1;
                            }
                        }
                        this.mLastDiff[0] = abs;
                    }
                    this.mLastDirections[0] = f3;
                    this.mLastValues[0] = f2;
                }
            }
        }
    }
}
