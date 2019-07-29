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

public class MainActivity extends AppCompatActivity implements EditFragment.EditTextListener{

    DrawerLayout drawerLayout;

    Toolbar toolbar;

    NavigationView nav;

    public static FloatingActionButton add_card;

    FloatingActionButton edit_ok;

    LinearLayout linearLayout;

    CardView cardView;

    private List<Card> list=new ArrayList<>();

    private CardAdapter adapter;

    String TAG="MainActivity";

    RecyclerView recyclerView;

    EditFragment editFragment;

    int lineend;

    String firstLineText,text,date;

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

        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.menu);
        }

        recyclerView=findViewById(R.id.recycleView) ;
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new CardAdapter(list);
        recyclerView.setAdapter(adapter);

        LoadData();

        //adapter=new CardAdapter(list);
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

    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.frame,fragment);
        transaction.commit();
    }

    //回调
    @SuppressLint("RestrictedApi")
    @Override
    public void sendText(EditText editText) {

        Card card;

        if(editText.length()>0){

            String firstLineText;

            String text;

            add_card.setVisibility(View.VISIBLE);

            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("M月dd日 HH:mm");

            Date date=new Date(System.currentTimeMillis());

            String dateString=simpleDateFormat.format(date);

            Layout layout=editText.getLayout();

            lineend=layout.getLineEnd(0);

            if(editText.getLineCount()==1){
                firstLineText=editText.getText().toString().substring(0,layout.getLineEnd(0));
                text=editText.getText().toString().substring(layout.getLineEnd(0));
            }else{
                firstLineText=editText.getText().toString().substring(0,layout.getLineEnd(0)-4);
                text=editText.getText().toString().substring(layout.getLineEnd(0)-4);
            }

            card=new Card(firstLineText,text,dateString);

            list.add(card);
            adapter.notifyItemInserted(list.size());
            Toast.makeText(MainActivity.this,"已添加",Toast.LENGTH_SHORT).show();
            //adapter.getHolder().cardView.setClickable(false);
            /**添加数据到数据库中**/
            Note note=new Note();
            note.setTitle(firstLineText);
            note.setText(text);
            note.setDate(dateString);
            note.save();

        }else{
            //Toast.makeText(MainActivity.this,"未输入文本",Toast.LENGTH_SHORT).show();
        }

        FragmentManager fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(editFragment).commit();
        add_card.setVisibility(View.VISIBLE);
        editText.setText(null);

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

        List<Note> noteList= DataSupport.findAll(Note.class);
        adapter=new CardAdapter(list);
        if(noteList.size()>0){
            for(Note note : noteList){
                card=new Card(note.getTitle(),note.getText(),note.getDate());
                //list.add(card);
                adapter.addData(card,0);
            }

            recyclerView.setAdapter(adapter);
        }
    }

    @SuppressLint("RestrictedApi")
    public void displayCardText(){

        Card card;

        if((firstLineText+text)!=null){


            card=new Card(firstLineText,text,date);

            //list.add(card);
            adapter=new CardAdapter(list);
            adapter.addData(card,0);
            recyclerView.setAdapter(adapter);
            Toast.makeText(MainActivity.this,"已添加",Toast.LENGTH_SHORT).show();

            /**添加数据到数据库中**/
            Note note=new Note();
            note.setTitle(firstLineText);
            note.setText(text);
            note.setDate(date);
            note.save();

        }else{

            Toast.makeText(MainActivity.this,"未输入文本",Toast.LENGTH_SHORT).show();

        }
    }
}
