package com.smallcard.android;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EditActivity extends AppCompatActivity {

    EditText editText;

    FloatingActionButton edit_ok;

    List<Card> list;

    CardAdapter adapter;

    RecyclerView recyclerView;

    ExchangData exchangData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        final Intent intent=getIntent();
        final String title=intent.getStringExtra("title");
        final String text=intent.getStringExtra("text");

        editText=findViewById(R.id.edit_text1);
        edit_ok=findViewById(R.id.edit_ok1);

        if(title!=null && text!=null){
            editText.setText(title+text);
        }

        edit_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                        firstLineText=editText.getText().toString().substring(0,layout.getLineEnd(0)-5);
                        text=editText.getText().toString().substring(layout.getLineEnd(0)-5);
                    }



                    Intent mintent=new Intent(EditActivity.this,MainActivity.class);
                    mintent.putExtra("firstLineText",firstLineText);
                    mintent.putExtra("txt",text);
                    mintent.putExtra("date",dateString);
                    setResult(RESULT_OK,mintent);

                    Toast.makeText(EditActivity.this,"已添加",Toast.LENGTH_SHORT).show();

                    finish();
                }else{

                    Toast.makeText(EditActivity.this,"未输入文本",Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    public interface ExchangData {
        void getData(String title,String text,String date);
    }
}
