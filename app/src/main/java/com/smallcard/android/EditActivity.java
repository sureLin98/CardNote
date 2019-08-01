package com.smallcard.android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    EditText editText;

    FloatingActionButton edit_ok;

    Toolbar toolbar;

    TextView dateText;

   public static ActionBar actionBar;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit);

        toolbar=findViewById(R.id.toolbar);
        dateText=findViewById(R.id.edit_date);
        setSupportActionBar(toolbar);
        actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        actionBar.setTitle(null);

        final Intent intent=getIntent();
        final String title=intent.getStringExtra("title");
        final String text=intent.getStringExtra("text");
        final String date=intent.getStringExtra("date");

        editText=findViewById(R.id.edit_text1);
        edit_ok=findViewById(R.id.edit_ok1);

        if(title!=null){
            editText.setText(title+text);
            dateText.setText(date);
        }

        edit_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        String firstLineText;

        String text;

        if(editText.length()>0){

            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("M月dd日 HH:mm");

            Date date=new Date(System.currentTimeMillis());

            Layout layout=editText.getLayout();

            String dateString=simpleDateFormat.format(date);
            if(editText.getLineCount()==1){
                firstLineText=editText.getText().toString().substring(0,layout.getLineEnd(0));
                text=editText.getText().toString().substring(layout.getLineEnd(0));
            }else{
                firstLineText=editText.getText().toString().substring(0,layout.getLineEnd(0)-1);
                text=editText.getText().toString().substring(layout.getLineEnd(0)-1);
            }

            Intent mintent=new Intent();
            mintent.putExtra("fLT",firstLineText);
            mintent.putExtra("txt",text);
            mintent.putExtra("dateString",dateString);
            setResult(RESULT_OK,mintent);

            Note note=new Note();
            note.setTitle(firstLineText);
            note.setText(text);
            note.setDate(dateString);
            note.save();

        }
    }

    @Override
    protected void onPause() {
        save();
        super.onPause();
    }
}
