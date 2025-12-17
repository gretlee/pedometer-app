package com.demo.example;

import android.content.SharedPreferences;

import com.demo.example.utils.Utils;


public class PedometerSettings {
    public static int M_NONE = 1;
    public static int M_PACE = 2;
    public static int M_SPEED = 3;
    SharedPreferences mSettings;

    public PedometerSettings(SharedPreferences sharedPreferences) {
        this.mSettings = sharedPreferences;
    }

    public boolean isMetric() {
        return this.mSettings.getString("units", "imperial").equals("metric");
    }

    public float getStepLength() {
        try {
            return Float.parseFloat(this.mSettings.getString("step_length", "20").trim());
        } catch (NumberFormatException unused) {
            return 0.0f;
        }
    }

    public float getGoal() {
        try {
            return Float.parseFloat(this.mSettings.getString("Goal", "1000").trim());
        } catch (NumberFormatException unused) {
            return 0.0f;
        }
    }

    public float getBodyWeight() {
        try {
            return Float.parseFloat(this.mSettings.getString("body_weight", "50").trim());
        } catch (NumberFormatException unused) {
            return 0.0f;
        }
    }

    public boolean isRunning() {
        return this.mSettings.getString("exercise_type", "running").equals("running");
    }

    public int getMaintainOption() {
        String string = this.mSettings.getString("maintain", "none");
        if (string.equals("none")) {
            return M_NONE;
        }
        if (string.equals("pace")) {
            return M_PACE;
        }
        if (string.equals("speed")) {
            return M_SPEED;
        }
        return 0;
    }

    public int getDesiredPace() {
        return this.mSettings.getInt("desired_pace", 180);
    }

    public float getDesiredSpeed() {
        return this.mSettings.getFloat("desired_speed", 4.0f);
    }

    public void savePaceOrSpeedSetting(int i, float f) {
        SharedPreferences.Editor edit = this.mSettings.edit();
        if (i == M_PACE) {
            edit.putInt("desired_pace", (int) f);
        } else if (i == M_SPEED) {
            edit.putFloat("desired_speed", f);
        }
        edit.apply();
    }

    public boolean shouldSpeak() {
        return this.mSettings.getBoolean("speak", false);
    }

    public float getSpeakingInterval() {
        try {
            return Float.parseFloat(this.mSettings.getString("speaking_interval", "1"));
        } catch (NumberFormatException unused) {
            return 1.0f;
        }
    }

    public boolean shouldTellSteps() {
        return this.mSettings.getBoolean("speak", false) && this.mSettings.getBoolean("tell_steps", false);
    }

    public boolean shouldTellPace() {
        return this.mSettings.getBoolean("speak", false) && this.mSettings.getBoolean("tell_pace", false);
    }

    public boolean shouldTellDistance() {
        return this.mSettings.getBoolean("speak", false) && this.mSettings.getBoolean("tell_distance", false);
    }

    public boolean shouldTellSpeed() {
        return this.mSettings.getBoolean("speak", false) && this.mSettings.getBoolean("tell_speed", false);
    }

    public boolean shouldTellCalories() {
        return this.mSettings.getBoolean("speak", false) && this.mSettings.getBoolean("tell_calories", false);
    }

    public boolean shouldTellFasterslower() {
        return this.mSettings.getBoolean("speak", false) && this.mSettings.getBoolean("tell_fasterslower", false);
    }

    public boolean wakeAggressively() {
        return this.mSettings.getString("operation_level", "run_in_background").equals("wake_up");
    }

    public boolean keepScreenOn() {
        return this.mSettings.getString("operation_level", "run_in_background").equals("keep_screen_on");
    }

    public void saveServiceRunningWithTimestamp(boolean z) {
        SharedPreferences.Editor edit = this.mSettings.edit();
        edit.putBoolean("service_running", z);
        edit.putLong("last_seen", Utils.currentTimeInMillis());
        edit.commit();
    }

    public void saveServiceRunningWithNullTimestamp(boolean z) {
        SharedPreferences.Editor edit = this.mSettings.edit();
        edit.putBoolean("service_running", z);
        edit.putLong("last_seen", 0L);
        edit.commit();
    }

    public void clearServiceRunning() {
        SharedPreferences.Editor edit = this.mSettings.edit();
        edit.putBoolean("service_running", false);
        edit.putLong("last_seen", 0L);
        edit.commit();
    }

    public void setServiceRunning() {
        SharedPreferences.Editor edit = this.mSettings.edit();
        edit.putBoolean("service_running", true);
        edit.commit();
    }

    public boolean isServiceRunning() {
        return this.mSettings.getBoolean("service_running", false);
    }

    public boolean isNewStart() {
        return this.mSettings.getLong("last_seen", 0L) < Utils.currentTimeInMillis() - 600000;
    }
}
