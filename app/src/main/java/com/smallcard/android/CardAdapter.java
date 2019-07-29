package com.smallcard.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private Context context;

    private List<Card> cardList;

    public ViewHolder holder;

    public CardAdapter(List<Card> list){
        cardList=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(context==null){
            context=viewGroup.getContext();
        }
        View view= LayoutInflater.from(context).inflate(R.layout.card,viewGroup,false);

        holder=new ViewHolder(view);



        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Card card=cardList.get(i);
        final int position=i;
        viewHolder.title.setText(card.title);
        viewHolder.text.setText(card.text);
        viewHolder.date.setText(card.date);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,EditActivity.class);
                intent.putExtra("title",card.title);
                intent.putExtra("text",card.text);
                context.startActivity(intent);
                removeData(position);
            }
        });

        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder dialog=new AlertDialog.Builder(context);
                dialog.setTitle("删除便签");
                dialog.setMessage("是否删除此便签？");
                dialog.setCancelable(true);
                dialog.setNegativeButton("取消", null);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeData(position);
                    }
                });
                dialog.show();

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public ViewHolder getHolder() {
        return holder;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;

        TextView title;

        TextView text;

        TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView=(CardView)itemView;
            title=itemView.findViewById(R.id.title);
            text=itemView.findViewById(R.id.text);
            date=itemView.findViewById(R.id.date);
        }
    }

    public void addData(Card card,int position){
        cardList.add(position,card);
        notifyItemInserted(position);
        notifyItemRangeChanged(0,cardList.size());

    }

    public void removeData(int position){
        Card card=cardList.get(position);
        SQLiteDatabase db=LitePal.getDatabase();
        db.execSQL("delete from Note where title='"+card.title+"'and text='"+card.text+"'");
        cardList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0,cardList.size());
    }
}
