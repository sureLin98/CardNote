package com.smallcard.android;

public class ToDo {

    String txt;

    int isCheck;

    public ToDo(String s,int i){
        txt=s;
        isCheck=i;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public int getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(int isCheck) {
        this.isCheck = isCheck;
    }
}
