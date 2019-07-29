package com.smallcard.android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EditActivity extends AppCompatActivity {

    EditText editText;

    FloatingActionButton edit_ok;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        if(Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        */
        setContentView(R.layout.activity_edit);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        final Intent intent=getIntent();
        final String title=intent.getStringExtra("title");
        final String text=intent.getStringExtra("text");

        editText=findViewById(R.id.edit_text1);
        edit_ok=findViewById(R.id.edit_ok1);

        if(title!=null){
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
                        firstLineText=editText.getText().toString().substring(0,layout.getLineEnd(0)-1);
                        text=editText.getText().toString().substring(layout.getLineEnd(0)-1);
                    }



                    //Intent mintent=new Intent(EditActivity.this,MainActivity.class);
                    Intent mintent=new Intent();
                    mintent.putExtra("fLT",firstLineText);
                    mintent.putExtra("txt",text);
                    mintent.putExtra("dateString",dateString);
                    setResult(RESULT_OK,mintent);

                    Toast.makeText(EditActivity.this,"已添加",Toast.LENGTH_SHORT).show();

                    finish();

                }else{

                    Toast.makeText(EditActivity.this,"未输入文本",Toast.LENGTH_SHORT).show();

                    finish();
                }

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

    private void initWindows() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.BLACK);
        }
    }

}
