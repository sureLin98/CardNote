package com.smallcard.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BaselineLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.litepal.LitePal.getDatabase;

public class MainActivity extends AppCompatActivity{

    DrawerLayout drawerLayout;

    Toolbar toolbar;

    NavigationView nav;

    public static FloatingActionButton add_card;

    CardView cardView;

    private List<Card> list=new ArrayList<>();

    private CardAdapter adapter;

    RecyclerView recyclerView;

    String firstLineText,text,date;

    public static boolean is_widget=false;

    private static final String PREFS_NAME = "com.smallcard.android.NoteWidget";

    private static final String PREF_PREFIX_KEY = "appwidget_";

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setResult(RESULT_CANCELED);

        /**状态栏沉浸**/
        if(Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.tool_bar);
        drawerLayout=findViewById(R.id.drawer_layout);
        nav=findViewById(R.id.nav_view);
        add_card=findViewById(R.id.add_card);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        cardView=findViewById(R.id.card_view);

        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.menu);
        }
        actionBar.setTitle("全部便签");

        recyclerView=findViewById(R.id.recycleView) ;
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new CardAdapter(list);
        recyclerView.setAdapter(adapter);

        LoadData();

        add_card.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,EditActivity.class);
                startActivityForResult(intent,1);
            }
        });

        /**侧滑栏各按钮功能**/
        nav.setCheckedItem(R.id.all_card);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.all_card:
                        Intent intent=new Intent(MainActivity.this,MainActivity.class);
                        startActivity(intent);
                        break;

                    default:
                        break;

                }
                return true;
            }
        });

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu,menu);
        return true;
    }

    /**顶部栏右上角设置按钮**/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;

            default:
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK){
            firstLineText=data.getStringExtra("fLT");
            text=data.getStringExtra("txt");
            date=data.getStringExtra("dateString");
            displayCardText();
        }
    }

    /**载入数据**/
    public void LoadData(){

        Card card;
        list.clear();
        List<Note> noteList= DataSupport.findAll(Note.class);
        adapter=new CardAdapter(list);
        if(noteList.size()>0){
            for(Note note : noteList){
                card=new Card(note.getTitle(),note.getText(),note.getDate());
                adapter.addData(card,0);
            }

            recyclerView.setAdapter(adapter);
        }
    }

    @SuppressLint("RestrictedApi")
    public void displayCardText() {

        Card card;

        if ((firstLineText + text) != null) {

            card = new Card(firstLineText, text, date);

            adapter = new CardAdapter(list);
            adapter.addData(card, 0);
            recyclerView.setAdapter(adapter);
        }
    }

    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, "加载错误");
        return titleValue;
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    public void addWidgetText(String txt){
        final Context context = MainActivity.this;

        String widgetText = txt;
        saveTitlePref(context, mAppWidgetId, widgetText);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        NoteWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    @Override
    protected void onRestart() {
        LoadData();
        super.onRestart();
    }
}
