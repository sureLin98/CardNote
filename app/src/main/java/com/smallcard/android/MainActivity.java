package com.smallcard.android;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.SearchView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity{

    DrawerLayout drawerLayout;

    Toolbar toolbar;

    NavigationView nav;

    FloatingActionButton add_card;

    CardView cardView;

    private List<Card> list=new ArrayList<>();

    private List<ToDo> list2=new ArrayList<>();

    private CardAdapter adapter;

    RecyclerView recyclerView,toDoRecycleView;

    String firstLineText,text,date;

    LinearLayout mainLinearLayout;

    public static boolean is_widget=false;

    private static final String PREFS_NAME = "com.smallcard.android.NoteWidget";

    private static final String PREF_PREFIX_KEY = "appwidget_";

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    RelativeLayout relativeLayout;

    final int IMAGE_REQUEST_CODE=2;

    RadioGroup layout_RG;

    RadioButton linearlayout,gridlayout,pubulayout;

    SharedPreferences.Editor editor;

    SharedPreferences prf;

    SeekBar cardSeekBar;

    RadioButton transparency,translucent,opaque;

    RadioGroup widgetRG;

    SearchView searchView;

    SearchView.SearchAutoComplete searchAutoComplete;

    ViewPager viewPager;

    View toDoListView,noteListView;

    boolean isToDo=false;

    ToDoListAdapter toDoListAdapter;

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

        //初始化设置信息的缓存
        prf=getSharedPreferences("com.smallcard.SettingData",MODE_PRIVATE);
        editor=prf.edit();

        LitePal.getDatabase();

        initViewPager();

        if(is_widget){
            Toast.makeText(this,"请选择要添加到桌面的便签",Toast.LENGTH_SHORT).show();
        }

        toolbar=findViewById(R.id.tool_bar);
        drawerLayout=findViewById(R.id.drawer_layout);
        nav=findViewById(R.id.nav_view);
        add_card=findViewById(R.id.add_card);
        setSupportActionBar(toolbar);
        final ActionBar actionBar=getSupportActionBar();
        cardView=findViewById(R.id.card_view);
        recyclerView=noteListView.findViewById(R.id.recycleView) ;
        toDoRecycleView=toDoListView.findViewById(R.id.to_do_recycleView);
        mainLinearLayout=findViewById(R.id.linear_layout);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        toDoRecycleView.setItemAnimator(new DefaultItemAnimator());

        final View navHeaderView=nav.getHeaderView(0);

        layout_RG=navHeaderView.findViewById(R.id.layout_RG);
        linearlayout=navHeaderView.findViewById(R.id.linearlayout_radio_button);
        gridlayout=navHeaderView.findViewById(R.id.gridlayout_radio_button);
        pubulayout=navHeaderView.findViewById(R.id.pubulayout_radio_button);
        cardSeekBar=navHeaderView.findViewById(R.id.seek_bar);
        cardSeekBar.setMax(255);
        widgetRG=navHeaderView.findViewById(R.id.widget_RG);
        transparency=navHeaderView.findViewById(R.id.transparency);
        translucent=navHeaderView.findViewById(R.id.translucent);
        opaque=navHeaderView.findViewById(R.id.opaque);

        toDoRecycleView.setLayoutManager(new LinearLayoutManager(this));

        //请求权限
        applyWritePermission();

        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.menu);
        }
        actionBar.setTitle("便签");

        if(prf.getString("card_layout","linearlayout").equals("gridlayout")){
            GridLayoutManager layoutManager=new GridLayoutManager(this,2);
            recyclerView.setLayoutManager(layoutManager);
            gridlayout.setChecked(true);

        }else if(prf.getString("card_layout","linearlayout").equals("pubulayout")){
            StaggeredGridLayoutManager staggeredGridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(staggeredGridLayoutManager);
            pubulayout.setChecked(true);
        }else{
            LinearLayoutManager layoutManager=new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            linearlayout.setChecked(true);
        }

        //判断当前的View是便签还是待办
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if(viewPager.getChildAt(i)==toDoListView){
                    isToDo=true;
                    searchView.setVisibility(View.GONE);
                    actionBar.setTitle("待办");
                }else{
                    isToDo=false;
                    searchView.setVisibility(View.VISIBLE);
                    actionBar.setTitle("便签");
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        LoadData();
        LoadToDo();

        add_card.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                final List<ToDo> lists=new ArrayList<>();

                if(isToDo){

                    //创建待办事项
                    AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);

                    dialog.setTitle("创建待办事项");

                    final View dialogView=getLayoutInflater().inflate(R.layout.dialog_layout,null);

                    dialog.setView(dialogView);

                    final EditText et=dialogView.findViewById(R.id.dialog_edit_text);

                    //点击创建后保存待办事项到ToDoBook表中，再加载出来
                    dialog.setPositiveButton("创建", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(et.length()>0){
                                String toDoTxt=et.getText().toString();

                                list2.add(new ToDo(toDoTxt,0));

                                ToDoBook toDoBook=new ToDoBook();
                                toDoBook.setTxt(toDoTxt);
                                toDoBook.setCheck(0);
                                toDoBook.save();

                                Log.d("Test", "onClick: todotxt="+toDoTxt);

                                LoadToDo();
                            }else {
                                Toast.makeText(MainActivity.this,"未输入文本",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    dialog.setNegativeButton("取消",null);

                    dialog.show();

                    et.setFocusable(true);
                    et.setFocusableInTouchMode(true);
                    et.requestFocus();

                }else{
                    Intent intent=new Intent(MainActivity.this,EditActivity.class);
                    intent.putExtra("is_edit_widget_text",false);
                    startActivityForResult(intent,1);
                }

                //LoadToDo();

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
                    LoadToDo();
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

        layout_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.linearlayout_radio_button){
                    editor.putString("card_layout","linearlayout");
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    LoadData();
                }else if(checkedId==R.id.gridlayout_radio_button){
                    editor.putString("card_layout","gridlayout");
                    recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,2));
                    LoadData();
                }else if(checkedId==R.id.pubulayout_radio_button){
                    editor.putString("card_layout","pubulayout");
                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                    LoadData();
                }
                editor.apply();
            }
        });
    }

    public void initViewPager(){
        viewPager=findViewById(R.id.view_pager);
        List<View> viewList=new ArrayList<>();
        toDoListView=getLayoutInflater().inflate(R.layout.to_do_list_layout,null);
        noteListView=getLayoutInflater().inflate(R.layout.note_layout,null);
        viewList.add(noteListView);
        viewList.add(toDoListView);
        ViewPagerAdapter toDoListAdapter=new ViewPagerAdapter(viewList);
        viewPager.setAdapter(toDoListAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu,menu);
        MenuItem searchItem=menu.findItem(R.id.search_view);
        searchView=(SearchView) searchItem.getActionView();
        searchView.setQueryHint("搜索便签. . .");
        searchAutoComplete=searchView.findViewById(R.id.search_src_text);
        //searchAutoComplete.setTextColor(Color.WHITE);
        setSearchListener();
        return super.onCreateOptionsMenu(menu);
    }

    //搜索
    public void setSearchListener(){
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                LoadData();
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            //查找便签
            @Override
            public boolean onQueryTextChange(String newText) {
                Card card;
                List<Card> findList=new ArrayList<>();
                List<Note> findNote=DataSupport.where("text like ?","%"+newText+"%").find(Note.class);
                //Log.d("Test", "onQueryTextChange: newText="+newText);
                adapter=new CardAdapter(findList);
                if(findNote.size()>0){
                    for(Note note : findNote){
                        card=new Card(note.getText(),note.getDate());
                        adapter.addData(card,0);
                    }
                }

                if(prf.getString("card_layout","linearlayout").equals("pubulayout")) {
                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(staggeredGridLayoutManager);
                }

                recyclerView.setAdapter(adapter);
                return false;
            }
        });
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
                        //Log.d("Test", "onActivityResult: 1");
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
                card=new Card(note.getText(),note.getDate());
                adapter.addData(card,0);
            }
            recyclerView.setAdapter(adapter);
        }
    }

    public void LoadToDo(){

        ToDo toDo;
        list2.clear();
        toDoListAdapter=new ToDoListAdapter(list2);
        List<ToDoBook> toDoBookList=DataSupport.findAll(ToDoBook.class);
        if(toDoBookList.size()>0){
            for(ToDoBook tdb : toDoBookList){
                toDo=new ToDo(tdb.getTxt(),tdb.getCheck());
                toDoListAdapter.addData(toDo,0);
            }

            toDoRecycleView.setAdapter(toDoListAdapter);
        }

    }

    //显示卡片
    @SuppressLint("RestrictedApi")
    public void displayCardText() {

        Card card;

        if ((firstLineText + text) != null) {

            card = new Card(text, date);

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
        searchView.onActionViewCollapsed();
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
            mainLinearLayout.setBackground(Drawable.createFromPath(prf.getString("image_path",null)));
        }else{
            mainLinearLayout.setBackgroundColor(Color.parseColor("#eeeeee"));
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        is_widget=false;
        super.onDestroy();
    }
}
