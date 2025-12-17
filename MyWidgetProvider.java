package com.demo.example;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;


public class MyWidgetProvider extends AppWidgetProvider {
    @Override 
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] iArr) {
    }

    @Override 
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        intent.getAction().equals("action");
    }
}
