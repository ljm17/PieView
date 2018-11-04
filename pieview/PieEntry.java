package com.example.administrator.pieview;


public class PieEntry {
    private int color;  //板块颜色
    private int data;   //板块数据
    private String msg;     //板块文字
    private boolean isRaised;   //该板块是否凸起

    public PieEntry() {
    }

    public PieEntry(int data, String msg) {
        this.data = data;
        this.msg = msg;
    }

    public PieEntry(int data, String msg, int color) {
        this.color = color;
        this.data = data;
        this.msg = msg;
    }

    public PieEntry(int data, String msg, boolean isRaised) {
        this.data = data;
        this.msg = msg;
        this.isRaised = isRaised;
    }

    public PieEntry( int data, String msg, int color, boolean isRaised) {
        this.color = color;
        this.data = data;
        this.msg = msg;
        this.isRaised = isRaised;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isRaised() {
        return isRaised;
    }

    public void setRaised(boolean raised) {
        isRaised = raised;
    }
}
