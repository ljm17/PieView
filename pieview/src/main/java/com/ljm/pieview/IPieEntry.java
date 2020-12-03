package com.ljm.pieview;

import android.support.annotation.ColorInt;

/**
 * 饼状图数据实体接口
 *
 * @author ljm
 * @date 2020/12/1
 */
public interface IPieEntry {

    /**
     * 板块文字信息
     *
     * @return msg
     */
    String getBlockMsg();

    /**
     * 设置板块文字信息
     *
     * @param  blockMsg 板块文字信息
     */
    void setBlockMsg(String blockMsg);

    /**
     * 板块颜色
     *
     * @return color
     */
    int getBlockColor();

    /**
     * 设置板块颜色（当未设置板块颜色，PieView内部会生成随机色再调用该方法设值）
     */
    void setBlockColor(@ColorInt int blockColor);

    /**
     * 板块数据
     *
     * @return data
     */
    float getBlockData();

    /**
     * 设置板块数据
     *
     * @param data data
     */
    void setBlockData(float data);

    /**
     * 板块是否凸起
     * @return raised
     */
    boolean isBlockRaised();

    /**
     * 设置板块凸起
     *
     * @param raised 是否凸起
     */
    void setBlockRaised(boolean raised);
}
