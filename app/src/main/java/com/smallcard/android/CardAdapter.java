package com.smallcard.android;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.List;
import java.util.concurrent.Callable;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private Context context;

    private List<Card> cardList;

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
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Card card=cardList.get(i);
        viewHolder.title.setText(card.title);
        viewHolder.text.setText(card.text);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;

        EditText title;

        EditText text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView=(CardView)itemView;
            title=itemView.findViewById(R.id.title);
            text=itemView.findViewById(R.id.text);
        }
    }
}
