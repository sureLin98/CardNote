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

    public static String widgetText;

    public static AppWidgetManager awm;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("com.smallcard.WIDGET_TEXT_UPDATE")){
            //接收更新小部件的广播
            widgetText=intent.getStringExtra("new_widget_text");
            MainActivity.saveTitlePref(context,EditActivity.widgetId,widgetText);
            //updateAppWidget(context,awm,EditActivity.widgetId);
            if(views!=null){
                views.setTextViewText(R.id.text,widgetText);
            }

        }else{
            super.onReceive(context, intent);
        }

    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        MainActivity.is_widget=false;

        prf=context.getSharedPreferences("com.smallcard.SettingData",0);

        //部件透明度信息
        int widgetTrans=prf.getInt("widget_trans",0);
        widgetText = MainActivity.loadTitlePref(context, appWidgetId);

        //启动EditActivity
        Intent aintent=new Intent(context,EditActivity.class);
        PendingIntent apendingIntent=PendingIntent.getActivity(context,1,aintent,0);

        if(widgetTrans==0){

            views= new RemoteViews(context.getPackageName(), R.layout.note_widget_transparency);
            views.setOnClickPendingIntent(R.id.widget_relative_layout_transparency,apendingIntent);

        }else if(widgetTrans==1){

            views= new RemoteViews(context.getPackageName(), R.layout.note_widget_translucent);
            views.setOnClickPendingIntent(R.id.widget_relative_layout_tranclucent,apendingIntent);

        }else if(widgetTrans==2){

            views= new RemoteViews(context.getPackageName(), R.layout.note_widget_opaque);
            views.setOnClickPendingIntent(R.id.widget_relative_layout_opaque,apendingIntent);

        }
        //缓存小部件文本数据
        SharedPreferences.Editor editor=prf.edit();
        editor.putString("widget_text",widgetText);
        editor.apply();

        EditActivity.widgetId=appWidgetId;

        if(appWidgetManager!=null){
            awm=appWidgetManager;
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

