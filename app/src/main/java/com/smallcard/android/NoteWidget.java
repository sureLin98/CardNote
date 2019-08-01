package com.smallcard.android;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class NoteWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        MainActivity.is_widget=true;
        CharSequence widgetText = MainActivity.loadTitlePref(context, appWidgetId);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.note_widget);
        views.setTextViewText(R.id.text,widgetText);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
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

