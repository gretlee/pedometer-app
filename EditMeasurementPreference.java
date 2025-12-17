package com.demo.example.preferences;

import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;


public abstract class EditMeasurementPreference extends EditTextPreference {
    protected int mImperialUnitsResource;
    boolean mIsMetric;
    protected int mMetricUnitsResource;
    protected int mTitleResource;

    protected abstract void initPreferenceDetails();

    public EditMeasurementPreference(Context context) {
        super(context);
        initPreferenceDetails();
    }

    public EditMeasurementPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initPreferenceDetails();
    }

    public EditMeasurementPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initPreferenceDetails();
    }

    @Override 
    protected void showDialog(Bundle bundle) {
        this.mIsMetric = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("units", "imperial").equals("metric");
        StringBuilder sb = new StringBuilder();
        sb.append(getContext().getString(this.mTitleResource));
        sb.append(" (");
        sb.append(getContext().getString(this.mIsMetric ? this.mMetricUnitsResource : this.mImperialUnitsResource));
        sb.append(")");
        setDialogTitle(sb.toString());
        try {
            Float.valueOf(getText());
        } catch (Exception unused) {
            setText("20");
        }
        super.showDialog(bundle);
    }

    @Override 
    protected void onAddEditTextToDialogView(View view, EditText editText) {
        editText.setRawInputType(8194);
        super.onAddEditTextToDialogView(view, editText);
    }

    @Override 
    public void onDialogClosed(boolean z) {
        if (z) {
            try {
                Float.valueOf(getEditText().getText().toString());
            } catch (NumberFormatException unused) {
                showDialog(null);
                return;
            }
        }
        super.onDialogClosed(z);
    }
}
