package com.smallcard.android;

public class Card {

    String title;

    String text;

    String date;

    public Card(String title, String text,String date){
        this.title=title;
        this.text=text;
        this.date=date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
