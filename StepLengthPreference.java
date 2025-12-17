package com.demo.example.preferences;

import android.content.Context;
import android.util.AttributeSet;
import com.demo.example.R;


public class StepLengthPreference extends EditMeasurementPreference {
    public StepLengthPreference(Context context) {
        super(context);
    }

    public StepLengthPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public StepLengthPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override 
    protected void initPreferenceDetails() {
        this.mTitleResource = R.string.step_length_setting_title;
        this.mMetricUnitsResource = R.string.centimeters;
        this.mImperialUnitsResource = R.string.inches;
    }
}
