package com.smallcard.android;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.internal.BaselineLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;

    Toolbar toolbar;

    NavigationView nav;

    FloatingActionButton add_card;

    LinearLayout linearLayout;

    CardView cardView;

    private List<Card> list=new ArrayList<>();

    private CardAdapter adapter;

    String TAG="MainActivity";

    Integer i=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher_round);
        }

        final RecyclerView recyclerView=findViewById(R.id.recycleView) ;
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new CardAdapter(list);
        recyclerView.setAdapter(adapter);

        add_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("请编辑卡片");
                final View view=LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_edit,null);
                builder.setView(view);


                builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        EditText title=view.findViewById(R.id.dialog_title);
                        EditText text=view.findViewById(R.id.dialog_text);

                        String textString=text.getText().toString();
                        String titleString=title.getText().toString();

                        Card card;
                        if(title.length()>0){
                            card=new Card(titleString,textString);
                        }else{
                            card=new Card("卡片"+i,textString);
                            i++;
                        }

                        list.add(card);
                        adapter=new CardAdapter(list);
                        recyclerView.setAdapter(adapter);
                        Toast.makeText(MainActivity.this,"已添加",Toast.LENGTH_SHORT).show();

                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
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
}
