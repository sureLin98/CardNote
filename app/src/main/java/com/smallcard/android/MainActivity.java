package com.smallcard.android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
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

    LinearLayout linearLayout;

    public static boolean is_widget=false;

    private static final String PREFS_NAME = "com.smallcard.android.NoteWidget";

    private static final String PREF_PREFIX_KEY = "appwidget_";

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    RelativeLayout relativeLayout;

    final int IMAGE_REQUEST_CODE=2;

    Switch grid_layout_switch;

    SharedPreferences.Editor editor;

    SharedPreferences prf;

    SeekBar cardSeekBar,widgetSeekBar;

    RadioButton transparency,translucent,opaque;

    RadioGroup widgetRG;

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

        if(is_widget){
            Toast.makeText(this,"请选择要添加到桌面的便签",Toast.LENGTH_SHORT).show();
        }

        toolbar=findViewById(R.id.tool_bar);
        drawerLayout=findViewById(R.id.drawer_layout);
        nav=findViewById(R.id.nav_view);
        add_card=findViewById(R.id.add_card);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        cardView=findViewById(R.id.card_view);
        recyclerView=findViewById(R.id.recycleView) ;
        linearLayout=findViewById(R.id.linear_layout);

        final View navHeaderView=nav.getHeaderView(0);

        grid_layout_switch=navHeaderView.findViewById(R.id.chang_to_gridLayout);
        cardSeekBar=navHeaderView.findViewById(R.id.seek_bar);
        cardSeekBar.setMax(255);
        widgetRG=navHeaderView.findViewById(R.id.widget_RG);
        transparency=navHeaderView.findViewById(R.id.transparency);
        translucent=navHeaderView.findViewById(R.id.translucent);
        opaque=navHeaderView.findViewById(R.id.opaque);

        //初始化设置信息的缓存
        prf=getSharedPreferences("com.smallcard.SettingData",MODE_PRIVATE);
        editor=prf.edit();
        grid_layout_switch.setChecked(prf.getBoolean("grid_layout_switch_status",false));

        //请求权限
        applyWritePermission();

        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.menu);
        }
        actionBar.setTitle("全部便签");

        if(prf.getBoolean("grid_layout_switch_status",false)){
            GridLayoutManager layoutManager=new GridLayoutManager(this,2);
            recyclerView.setLayoutManager(layoutManager);
        }else{
            LinearLayoutManager layoutManager=new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
        }

        LoadData();

        add_card.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,EditActivity.class);
                startActivityForResult(intent,1);
            }
        });

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        //小部件透明度选项
        switch (prf.getInt("widget_trans",0)){
            case 0:
                transparency.setChecked(true);
                break;
            case 1:
                translucent.setChecked(true);
                break;
            case 2:
                opaque.setChecked(true);
                break;
        }
        widgetRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.transparency){
                    editor.putInt("widget_trans",0);
                }else if(checkedId==R.id.translucent){
                    editor.putInt("widget_trans",1);
                }else if(checkedId==R.id.opaque){
                    editor.putInt("widget_trans",2);
                }
                editor.apply();
            }
        });

        String p=prf.getString("card_transparency_status","88");
        cardSeekBar.setProgress(Integer.valueOf(p,16));

        cardSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress!=0){
                    editor.putString("card_transparency",Integer.toHexString(progress));
                    adapter=new CardAdapter(list);
                    editor.apply();
                    LoadData();
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putString("card_transparency_status",Integer.toHexString(seekBar.getProgress()));
                editor.apply();
            }

        });

        relativeLayout=navHeaderView.findViewById(R.id.image_set);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_REQUEST_CODE);

                }else{

                    Toast.makeText(MainActivity.this,"缺少必要权限",Toast.LENGTH_SHORT).show();

                }

            }
        });

        grid_layout_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    GridLayoutManager layoutManager=new GridLayoutManager(MainActivity.this,2);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setLayoutManager(layoutManager);
                    adapter=new CardAdapter(list);
                    recyclerView.setAdapter(adapter);
                    LoadData();
                    editor.putBoolean("grid_layout_switch_status",true);
                }else {
                    LinearLayoutManager layoutManager=new LinearLayoutManager(MainActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    adapter=new CardAdapter(list);
                    recyclerView.setAdapter(adapter);
                    LoadData();
                    editor.putBoolean("grid_layout_switch_status",false);
                }

                editor.apply();

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu,menu);
        return true;
    }

    /**顶部栏按钮**/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;

            case R.id.about:
                Intent startAbout=new Intent(MainActivity.this,AboutActivity.class);
                startActivity(startAbout);
                break;

            default:
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            switch (requestCode){
                case 1:
                    if(resultCode==RESULT_OK) {
                        Log.d("Test", "onActivityResult: 1");
                        firstLineText = data.getStringExtra("fLT");
                        text = data.getStringExtra("txt");
                        date = data.getStringExtra("dateString");
                        displayCardText();
                    }
                    break;

                case IMAGE_REQUEST_CODE:

                    if(resultCode==RESULT_OK){

                        String imagePath = null;
                        Uri uri = data.getData();
                        if (DocumentsContract.isDocumentUri(this,uri)){
                            //如果document类型是U日，则通过document id处理
                            String docId = DocumentsContract.getDocumentId(uri);
                            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                                String id = docId.split(":")[1];//解析出数字格式id
                                String selection = MediaStore.Images.Media._ID + "=" + id;
                                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
                            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                                imagePath = getImagePath(contentUri,null);
                            }
                        }else if ("content".equalsIgnoreCase(uri.getScheme())){
                            //如果是普通类型 用普通方法处理
                            imagePath = getImagePath(uri,null);
                        }else if ("file".equalsIgnoreCase(uri.getScheme())){
                            //如果file类型位uri直街获取图片路径即可
                            imagePath = uri.getPath();
                        }
                        editor.putString("image_path",imagePath);
                        editor.apply();
                        Toast.makeText(MainActivity.this,"设置完成",Toast.LENGTH_SHORT).show();
                    }

                    break;

                default:
                    break;
            }
    }

    //获取图片路径
    private String getImagePath(Uri uri, String selection){
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**载入数据**/
    public void LoadData(){

        Card card;
        list.clear();
        adapter=new CardAdapter(list);
        List<Note> noteList= DataSupport.findAll(Note.class);
        if(noteList.size()>0){
            for(Note note : noteList){
                card=new Card(note.getTitle(),note.getText(),note.getDate());
                adapter.addData(card,0);
            }

            recyclerView.setAdapter(adapter);
        }
    }

    //显示卡片
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

    public void applyWritePermission(){
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= 23) {
            int check = ContextCompat.checkSelfPermission(this, permissions[0]);
            if (check == PackageManager.PERMISSION_GRANTED) {

            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else {

        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {
            Toast.makeText(this, "无法获取必要权限", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        if(prf.getString("image_path",null)!=null){
            linearLayout.setBackground(Drawable.createFromPath(prf.getString("image_path",null)));
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        is_widget=false;
        super.onDestroy();
    }
}
