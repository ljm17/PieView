package com.ljm.pieview;

import android.support.annotation.ColorInt;

/**
 * 饼状图数据实体类
 *
 * @author ljm
 */
public class PieEntry implements IPieEntry{

    /** 板块颜色*/
    private int color;

    /** 板块数据*/
    private float data;

    /** 板块文字*/
    private String msg;

    /** 该板块是否凸起*/
    private boolean raised;


    public PieEntry(float data, String msg) {
        this.data = data;
        this.msg = msg;
    }

    public PieEntry(float data, String msg, @ColorInt int color) {
        this.color = color;
        this.data = data;
        this.msg = msg;
    }

    public PieEntry(float data, String msg, boolean isRaised) {
        this.data = data;
        this.msg = msg;
        this.raised = isRaised;
    }

    public PieEntry(float data, String msg, @ColorInt int color, boolean isRaised) {
        this.color = color;
        this.data = data;
        this.msg = msg;
        this.raised = isRaised;
    }

    @Override
    public String getBlockMsg() {
        return msg;
    }

    @Override
    public void setBlockMsg(String blockMsg) {
        this.msg = blockMsg;
    }

    @Override
    public int getBlockColor() {
        return color;
    }

    @Override
    public void setBlockColor(int blockColor) {
        this.color = blockColor;
    }

    @Override
    public float getBlockData() {
        return data;
    }

    @Override
    public void setBlockData(float data) {
        this.data = data;
    }

    @Override
    public boolean isBlockRaised() {
        return raised;
    }

    @Override
    public void setBlockRaised(boolean raised) {
        this.raised = raised;
    }
}