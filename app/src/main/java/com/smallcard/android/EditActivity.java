package com.smallcard.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
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

    FloatingActionButton edit_ok;

    Toolbar toolbar;

    TextView dateText;

    ActionBar actionBar;

    String title,text,dateString;
    int position;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_edit);

        toolbar=findViewById(R.id.toolbar);
        dateText=findViewById(R.id.edit_date);
        setSupportActionBar(toolbar);
        actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        actionBar.setTitle("编辑便签");

        editText=findViewById(R.id.edit_text1);
        edit_ok=findViewById(R.id.edit_ok1);

        SharedPreferences prf=getSharedPreferences("com.smallcard.SettingData",MODE_PRIVATE);

        Intent intent=getIntent();
        title=intent.getStringExtra("title");
        text=intent.getStringExtra("text");
        dateString=intent.getStringExtra("date");
        position=intent.getIntExtra("position",-1);

        if(title!=null){
            editText.setText(title+text);
            dateText.setText(dateString);
        }

        edit_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //自动弹出输入法
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
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

            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("M月dd日 HH:mm");

            Date date=new Date(System.currentTimeMillis());

            Layout layout=editText.getLayout();

            String ndate=simpleDateFormat.format(date);

            if(editText.getLineCount()==1){
                ntitle=editText.getText().toString().substring(0,layout.getLineEnd(0));
                ntext=editText.getText().toString().substring(layout.getLineEnd(0));
            }else{
                ntitle=editText.getText().toString().substring(0,layout.getLineEnd(0)-1);
                ntext=editText.getText().toString().substring(layout.getLineEnd(0)-1);
            }

            if(ntitle.equals(title) && ntext.equals(text)){
                Intent mintent=new Intent();
                mintent.putExtra("fLT",title);
                mintent.putExtra("txt",text);
                mintent.putExtra("dateString",dateString);
                setResult(RESULT_OK,mintent);

            }else{
                Intent mintent=new Intent();
                mintent.putExtra("fLT",ntitle);
                mintent.putExtra("txt",ntext);
                mintent.putExtra("dateString",ndate);
                setResult(RESULT_OK,mintent);

                Note note=new Note();
                note.setTitle(ntitle);
                note.setText(ntext);
                note.setDate(ndate);
                note.save();

                SQLiteDatabase db= LitePal.getDatabase();
                db.execSQL("delete from Note where title='"+title+"'and text='"+text+"'");

            }

        }else{
            Toast.makeText(EditActivity.this,"未输入文本",Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void onPause() {
        save();
        super.onPause();
    }
}
