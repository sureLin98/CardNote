package com.smallcard.android;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ViewHolder> {

    private Context context;

    private List<ToDo> todoList;

    String TAG="Test";

    public ViewHolder holder;

    public ToDoListAdapter(List<ToDo> list){
        todoList=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(context==null){
            context=viewGroup.getContext();
        }
        View view= LayoutInflater.from(context).inflate(R.layout.todo_layout,viewGroup,false);

        holder=new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final ToDo toDo=todoList.get(i);
        SharedPreferences prf=context.getSharedPreferences("com.smallcard.SettingData",Context.MODE_PRIVATE);

        viewHolder.toDoText.setText(toDo.getTxt());
        viewHolder.checkBox.setChecked(switchToBoolean(toDo.getIsCheck()));

        if(viewHolder.checkBox.isChecked()){
            //设置删除线
            viewHolder.toDoText.setPaintFlags(Paint. STRIKE_THRU_TEXT_FLAG );
            //抗锯齿
            viewHolder.toDoText.getPaint().setAntiAlias(true);
            viewHolder.toDoText.setTextColor(Color.GRAY);
        }

        Integer num=Integer.valueOf(prf.getString("card_transparency","ff"),16);

        //修改透明度
        if(num<16){
            viewHolder.cardView.setCardBackgroundColor(Color.parseColor("#00ffffff"));
        }else{
            viewHolder.cardView.setCardBackgroundColor(Color.parseColor("#"+prf.getString("card_transparency","aa")+"ffffff"));
        }

        final String txt=todoList.get(i).getTxt();

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(viewHolder.checkBox.isChecked()){

                    viewHolder.checkBox.setChecked(false);

                }else{
                    viewHolder.checkBox.setChecked(true);

                }


            }
        });

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    ToDoBook toDoBook=new ToDoBook();
                    toDoBook.setTxt(txt);
                    toDoBook.setCheck(1);
                    toDoBook.save();

                    DataSupport.deleteAll("ToDoBook","txt = ? and check = ?",txt,"0");

                    //设置删除线
                    viewHolder.toDoText.setPaintFlags(Paint. STRIKE_THRU_TEXT_FLAG );

                    viewHolder.toDoText.setTextColor(Color.GRAY);

                }else{

                    ToDoBook toDoBook=new ToDoBook();
                    toDoBook.setTxt(txt);
                    toDoBook.setCheck(0);
                    toDoBook.save();

                    DataSupport.deleteAll("ToDoBook","txt = ? and check = ?",txt,"1");

                    viewHolder.toDoText.setPaintFlags(0);

                    viewHolder.toDoText.setTextColor(Color.parseColor("#cc000000"));

                }
                //抗锯齿
                viewHolder.toDoText.getPaint().setAntiAlias(true);

            }
        });


        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder dialog=new AlertDialog.Builder(context);
                dialog.setTitle("删除");
                dialog.setMessage("是否删除此待办事项？");
                dialog.setCancelable(true);
                dialog.setNegativeButton("取消", null);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeData(i);
                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public void addData(ToDo toDo,int position){
        todoList.add(position,toDo);
        notifyItemInserted(position);
        notifyItemRangeChanged(position,todoList.size());

    }

    public void removeData(int position){
        ToDo toDo=todoList.get(position);
        SQLiteDatabase db= LitePal.getDatabase();
        db.execSQL("delete from ToDoBook where txt='"+toDo.getTxt()+"'");
        todoList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,todoList.size());
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;

        CheckBox checkBox;

        TextView toDoText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView=(CardView) itemView;
            checkBox=itemView.findViewById(R.id.up_coming_checkout_box);
            toDoText=itemView.findViewById(R.id.to_do_text);
        }
    }

    public boolean switchToBoolean(int i){
        if (i==1){
            return  true;
        }else{
            return false;
        }
    }

}
