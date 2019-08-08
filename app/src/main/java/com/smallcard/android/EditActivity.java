package com.smallcard.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    EditText editText;

    Toolbar toolbar;

    TextView dateText;

    ActionBar actionBar;

    String title,text,dateString;

    int position;

    TextView textNum;

    public static int widgetId=0;

    boolean editWidgetText=false;

    String widgetText;
    String wti=null,wtx=null;

    boolean isDelete;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_edit);

        isDelete=false;

        textNum=findViewById(R.id.text_num);
        toolbar=findViewById(R.id.toolbar);
        dateText=findViewById(R.id.edit_date);
        setSupportActionBar(toolbar);
        actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.left_row);
            actionBar.setHomeButtonEnabled(true);
        }
        actionBar.setTitle("编辑便签");
        toolbar.setTitleTextColor(Color.WHITE);

        editText=findViewById(R.id.edit_text1);

        SharedPreferences prf=getSharedPreferences("com.smallcard.SettingData",MODE_PRIVATE);

        Intent intent=getIntent();
        text=intent.getStringExtra("text");
        dateString=intent.getStringExtra("date");
        int num=intent.getIntExtra("text_num",0);
        position=intent.getIntExtra("position",-1);
        editWidgetText=intent.getBooleanExtra("is_edit_widget_text",true);

        //Log.d("Test", "onCreate: loadtext="+MainActivity.loadTitlePref(EditActivity.this,widgetId)+"\n"+MainActivity.is_widget);

        if(!editWidgetText){
            if(text!=null){
                editText.setText(text);
                dateText.setText(dateString);
            }
            textNum.setText(String.valueOf(num));
        }else{
            //显示并编辑小部件文本
            SharedPreferences widgetPrf=getSharedPreferences("com.smallcard.SettingData",0);
            widgetText=widgetPrf.getString("widget_text",null);
            //Log.d("Test", "EditActivity>>>>onCreate: widget_text="+widgetText);
            if (widgetText != null) {
                int n=widgetText.length();
                editText.setText(widgetText);
                textNum.setText(String.valueOf(n));
                Log.d("Test", ">>>>>>>>>>onCreate: widgettext"+widgetText);

            }else{
                Toast.makeText(EditActivity.this,"未知错误",Toast.LENGTH_SHORT);
            }

        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = editText.getText().toString();
                textNum.setText(String.valueOf(content.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //自动弹出输入法
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;

            case R.id.delete:
                isDelete=true;
                SQLiteDatabase db= LitePal.getDatabase();
                db.execSQL("delete from Note where text='"+editText.getText().toString()+"'");
                finish();
                break;

            case R.id.save:
                finish();
                break;

            default:
                break;
        }
        return true;
    }

    public void save(){

        String ntitle;

        String ntext;

        if(editText.length()>0){

            //获取时间ndate,标题ntitle，正文ntext
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("M月dd日 HH:mm");

            Date date=new Date(System.currentTimeMillis());

            Layout layout=editText.getLayout();

            String ndate=simpleDateFormat.format(date);

            /*
            if(editText.getLineCount()==1){
                ntitle=editText.getText().toString().substring(0,layout.getLineEnd(0));
                ntext=editText.getText().toString().substring(layout.getLineEnd(0));
            }else{
                ntitle=editText.getText().toString().substring(0,layout.getLineEnd(0)-1);
                ntext=editText.getText().toString().substring(layout.getLineEnd(0)-1);
            }
            */
            ntext=editText.getText().toString();

            if(ntext.equals(text) || ntext.equals(widgetText)){

                //文本未改动时不用保存直接返回原文本
                Intent mintent=new Intent();
                mintent.putExtra("fLT",title);
                mintent.putExtra("txt",text);
                mintent.putExtra("dateString",dateString);
                setResult(RESULT_OK,mintent);

            }else{

                //文本有改动则删除原文本，保存当前文本到数据库中并将新内容返回
                Intent mintent=new Intent();
                mintent.putExtra("txt",ntext);
                mintent.putExtra("dateString",ndate);
                setResult(RESULT_OK,mintent);

                Note note=new Note();
                note.setText(ntext);
                note.setDate(ndate);
                note.save();

                SQLiteDatabase db= LitePal.getDatabase();
                db.execSQL("delete from Note where text='"+text+"'");

                if(editWidgetText){

                    Intent updateWidgetText=new Intent("com.smallcard.WIDGET_TEXT_UPDATE");
                    updateWidgetText.putExtra("new_widget_text",editText.getText().toString());
                    sendBroadcast(updateWidgetText);

                    Log.d("Test", "save: EditActivity广播已发送>>>>>>>>>>>>");

                    db.execSQL("delete from Note where text='"+widgetText+"'");
                }

            }

        }else{
            Toast.makeText(EditActivity.this,"未输入文本",Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void onPause() {
        if(!isDelete){
            save();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //save();
        super.onDestroy();
    }
}
