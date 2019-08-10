package com.smallcard.android;

import org.litepal.crud.DataSupport;

public class ToDoBook extends DataSupport {

    private int id;

    private int check;

    private String txt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }
}
