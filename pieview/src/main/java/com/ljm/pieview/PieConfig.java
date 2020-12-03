package com.ljm.pieview;

import android.graphics.Color;

/**
 * 饼状图属性参数
 *
 * @author ljm
 * @date 2020/12/2
 */
class PieConfig {

    /** 起始绘制角度*/
    public float startDegree = 180;

    /** 透明圆透明度*/
    public float insideAlpha = 0.4f;

    /** 内透明圆占外圆半径的百分比*/
    public float alphaRadiusPercent = 0.6f;

    /** 中心孔占外圆半径的百分比*/
    public float holeRadiusPercent = 0.5f;

    /** 凸起板块之间的间距*/
    public float blockSpace = 30;

    /** 是否展示百分比数*/
    public boolean disPlayPercent = true;

    /** 版块上的字号*/
    public int blockTextSize = 30;

    /** 版块上文字的颜色*/
    public int blockTextColor = Color.WHITE;

    /** 中心字号*/
    public int centerTextSize = 50;

    /** 中心字颜色*/
    public int centerTextColor = Color.GRAY;

    /** 是否显示中心文字*/
    public boolean showCenterText;

    /** 中心文字*/
    public String centerText = "PieView";

    /** 动画时长*/
    public long animatorDuration = 2000;

}
