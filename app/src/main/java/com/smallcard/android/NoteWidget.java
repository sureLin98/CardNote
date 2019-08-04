package com.smallcard.android;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class NoteWidget extends AppWidgetProvider {

    public static String TAG="Test";

    public static SharedPreferences prf;

    private static RemoteViews views;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        MainActivity.is_widget=false;
        prf=context.getSharedPreferences("com.smallcard.SettingData",Context.MODE_PRIVATE);
        int widgetTrans=prf.getInt("widget_trans",0);
        CharSequence widgetText = MainActivity.loadTitlePref(context, appWidgetId);

        if(widgetTrans==0){
            views= new RemoteViews(context.getPackageName(), R.layout.note_widget_transparency);
        }else if(widgetTrans==1){
            views= new RemoteViews(context.getPackageName(), R.layout.note_widget_translucent);
        }else if(widgetTrans==2){
            views= new RemoteViews(context.getPackageName(), R.layout.note_widget_opaque);
        }

        views.setTextViewText(R.id.text,widgetText);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            MainActivity.is_widget=true;
        }
    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {
        MainActivity.deleteTitlePref(context,0);
    }
}

