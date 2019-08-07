package com.smallcard.android;

public class Card {


    String text;

    String date;

    public Card(String text,String date){
        this.text=text;
        this.date=date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
