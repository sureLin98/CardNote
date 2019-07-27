package com.smallcard.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.litepal.LitePal.getDatabase;

public class MainActivity extends AppCompatActivity implements EditFragment.EditTextListener {

    DrawerLayout drawerLayout;

    Toolbar toolbar;

    NavigationView nav;

    public FloatingActionButton add_card;

    FloatingActionButton edit_ok;

    LinearLayout linearLayout;

    CardView cardView;

    private List<Card> list=new ArrayList<>();

    private CardAdapter adapter;

    String TAG="MainActivity";

    RecyclerView recyclerView;

    EditFragment editFragment;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        editFragment=new EditFragment();

        SQLiteDatabase database=LitePal.getDatabase();

        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher_round);
        }

        recyclerView=findViewById(R.id.recycleView) ;
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new CardAdapter(list);

        add_card.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {

                replaceFragment(editFragment);
                add_card.setVisibility(View.GONE);

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

    /**EditText的可编辑性**/
    public void setEditable(EditText editText,boolean mode){
        if(mode){

            editText.setFocusableInTouchMode(mode);

            editText.setFocusable(mode);

            editText.requestFocus();

        }else{

            editText.setFocusable(mode);

            editText.setFocusableInTouchMode(mode);

        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.frame_layout,fragment);
        transaction.commit();
    }

    //回调
    @SuppressLint("RestrictedApi")
    @Override
    public void sendText(EditText editText) {

        Card card;

        if(editText.length()>0){

            add_card.setVisibility(View.VISIBLE);

            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("M月dd日 HH:mm");

            Date date=new Date(System.currentTimeMillis());

            String dateString=simpleDateFormat.format(date);

            Log.d(TAG, "sendText: <---------------------->date="+dateString);

            Layout layout=editText.getLayout();

            String firstLineText=editText.getText().toString().substring(0,layout.getLineEnd(0)-4);

            String text=editText.getText().toString().substring(layout.getLineEnd(0)-4);

            card=new Card(firstLineText,text,dateString);

            list.add(card);
            adapter=new CardAdapter(list);
            recyclerView.setAdapter(adapter);
            Toast.makeText(MainActivity.this,"已添加",Toast.LENGTH_SHORT).show();

            /**添加数据到数据库中**/
            Note note=new Note();
            note.setTitle(firstLineText);
            note.setText(text);
            note.setDate(dateString);
            note.save();

        }else{

            Toast.makeText(MainActivity.this,"未输入文本",Toast.LENGTH_SHORT).show();

        }

        FragmentManager fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(editFragment).commit();
        add_card.setVisibility(View.VISIBLE);
        editText.setText(null);
    }
}
