package com.demo.example.preferences;

import android.content.Context;
import android.util.AttributeSet;
import com.demo.example.R;


public class GoalPrefrences extends EditMeasurementPreference {
    public GoalPrefrences(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public GoalPrefrences(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public GoalPrefrences(Context context) {
        super(context);
    }

    @Override 
    protected void initPreferenceDetails() {
        this.mTitleResource = R.string.Step_goal_setting_title;
        this.mMetricUnitsResource = R.string.steps_unit;
        this.mImperialUnitsResource = R.string.steps_unit;
    }
}
