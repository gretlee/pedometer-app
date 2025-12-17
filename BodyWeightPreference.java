package com.demo.example.preferences;

import android.content.Context;
import android.util.AttributeSet;
import com.demo.example.R;


public class BodyWeightPreference extends EditMeasurementPreference {
    public BodyWeightPreference(Context context) {
        super(context);
    }

    public BodyWeightPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public BodyWeightPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override 
    protected void initPreferenceDetails() {
        this.mTitleResource = R.string.body_weight_setting_title;
        this.mMetricUnitsResource = R.string.kilograms;
        this.mImperialUnitsResource = R.string.pounds;
    }
}
