package com.ljm.pieview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Looper;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The type Pie view.
 * @author ljm
 */
public class PieView extends View {

    /** 外圆画笔*/
    private Paint mOutPaint;

    /** 内透明圆画笔*/
    private Paint mAlphaPaint;

    /** 中心孔画笔*/
    private Paint mHolePaint;

    /** 中心文字画笔*/
    private Paint mCenterTextPaint;

    /** 各块百分比及文字画笔*/
    private Paint mBlockTextPaint;

    /** 起始绘制角度*/
    private float mStartDegree = Config.DEFAULT_START_DEGREE;

    /** 当前角度*/
    private float mCurrentDegree;

    /** 内透明圆透明度*/
    private float mAlpha = Config.DEFAULT_CIRCLE_ALPHA;

    /** 外圆半径*/
    private float mRadius;

    /** 中心孔占外圆半径的百分比*/
    private float mHoleRadiusPercent = Config.DEFAULT_HOLE_RADIUS_PERCENT;

    /** 内透明圆占外圆半径的百分比*/
    private float mAlphaRadiusPercent = Config.DEFAULT_ALPHA_RADIUS_PERCENT;

    /** 凸起板块之间的间距*/
    private int mSpace = Config.DEFAULT_BLOCK_SPACE;

    private List<PieEntry> mData;

    /** 数据总和*/
    private float sumData;

    /** 数据条数*/
    private int count;

    private RectF mRadiusRectF;

    private RectF mAlphaRectF;

    /** 是否展示百分比数*/
    private boolean disPlayPercent = true;

    /** 动画数值*/
    private float mAnimatorValue;

    /** 是否开启动画*/
    private boolean showAnimator = false;

    /** 版块上的字号*/
    private int mBlockTextSize = Config.DEFAULT_BLOCK_TEXT_SIZE;

    /** 版块上文字的颜色*/
    private int mBlockTextColor = Config.DEFAULT_BLOCK_TEXT_COLOR;

    /** 中心字号*/
    private int mCenterTextSize = Config.DEFAULT_CENTER_TEXT_SIZE;

    /** 中心字颜色*/
    private int mCenterTextColor = Config.DEFAULT_CENTER_TEXT_COLOR;

    /** 中心文字*/
    private String mCenterText;

    /** 是否显示中心文字*/
    private boolean showCenterText;

    /** 动画*/
    private ValueAnimator mAnimator;

    /** 动画时长*/
    private int mAnimatorTime = Config.DEFAULT_ANIMATOR_TIME;

    /**
     * Instantiates a new Pie view.
     *
     * @param context the context
     */
    public PieView(Context context) {
        this(context,null,0);
    }

    /**
     * Instantiates a new Pie view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public PieView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    /**
     * Instantiates a new Pie view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public PieView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mOutPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOutPaint.setStyle(Paint.Style.FILL);

        mAlphaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAlphaPaint.setStyle(Paint.Style.FILL);
        mAlphaPaint.setColor(Color.WHITE);

        mHolePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHolePaint.setStyle(Paint.Style.FILL);
        mHolePaint.setColor(Color.WHITE);

        mCenterTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterTextPaint.setFakeBoldText(true);
        mCenterTextPaint.setTextSize(mCenterTextSize);
        mCenterTextPaint.setTextAlign(Paint.Align.CENTER);
        mCenterTextPaint.setColor(mCenterTextColor);

        mBlockTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBlockTextPaint.setFakeBoldText(true);
        mBlockTextPaint.setTextSize(mBlockTextSize);
        mBlockTextPaint.setColor(mBlockTextColor);
        mBlockTextPaint.setTextAlign(Paint.Align.CENTER);

        mCenterText = "PieView";
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.WHITE);

        //平移画布，使（0,0）点至中心，便于计算
        canvas.translate(getWidth() >> 1,getHeight() >> 1);
        calculate();

        if (isShowAnimator()){
            drawByAnim(canvas);
        }else {
            drawByNormal(canvas);
        }
    }

    /**
     * 非动画绘制
     */
    private void drawByNormal(Canvas canvas){
        //循环绘制各模块
        for (int i = 0; i < count; i++){
            if (0 == getData().get(i).getColor()){
                int color = getRandColor();
                mOutPaint.setColor(color);
                getData().get(i).setColor(color);
            }else {
                mOutPaint.setColor(getData().get(i).getColor());
            }
            //计算该板块角度
            float sweepAngle = getData().get(i).getData() / sumData * 360f;
            canvas.save();
            //当前模块角平分线的sin和cos值
            if (getData().get(i).isRaised()){
                float mathCos = (float) (Math.cos((sweepAngle / 2 + mCurrentDegree) / 180f * Math.PI));
                float mathSin = (float) (Math.sin((sweepAngle / 2 + mCurrentDegree) / 180f * Math.PI));
                //若该板块设置凸起，平移画布，使其与周边各块产生间距，并突出
                canvas.translate(mathCos * mSpace, mathSin * mSpace);
            }
            //绘制外圆
            canvas.drawArc(mRadiusRectF, mCurrentDegree, sweepAngle, true, mOutPaint);
            canvas.restore();

            mCurrentDegree += sweepAngle;
        }

        mAlphaPaint.setAlpha((int) (mAlpha * 255));
        canvas.drawCircle(0, 0,mRadius * mAlphaRadiusPercent, mAlphaPaint);
        canvas.drawCircle(0, 0, mRadius * mHoleRadiusPercent, mHolePaint);

        drawTextInner(canvas);

        if (isShowCenterText() && !TextUtils.isEmpty(getCenterText())) {
            //绘制中心文字
            Paint.FontMetrics metrics = mCenterTextPaint.getFontMetrics();
            float textHeight = Math.abs(metrics.descent - metrics.ascent);
            canvas.drawText(mCenterText, 0, textHeight / 2 - metrics.descent, mCenterTextPaint);
        }
    }
    /**
     * 动画绘制
     */
    private void drawByAnim(Canvas canvas){
        //循环绘制各模块
        for (int i = 0; i < count; i++){
            if (0 == getData().get(i).getColor()){
                int color = getRandColor();
                mOutPaint.setColor(color);
                getData().get(i).setColor(color);
            }else {
                mOutPaint.setColor(getData().get(i).getColor());
            }
            //本版块角度
            float sweepAngle = getData().get(i).getData() / sumData * 360f;

            canvas.save();
            float valueAngle = Math.min(sweepAngle, mAnimatorValue - ( mCurrentDegree - mStartDegree));
            if (getData().get(i).isRaised()){
                //当前模块角平分线的sin和cos值
                float mathCos = (float) (Math.cos((sweepAngle / 2 + mCurrentDegree) / 180f * Math.PI));
                float mathSin = (float) (Math.sin((sweepAngle / 2 + mCurrentDegree) / 180f * Math.PI));
                //若该板块设置凸起，平移画布，使其与周边各块产生间距，并突出
                canvas.translate(mathCos * mSpace, mathSin * mSpace);
            }

            //根据动画，本次真实需绘制mCurrentDegree度的外圆
            canvas.drawArc(mRadiusRectF, mCurrentDegree, valueAngle,true, mOutPaint);
            canvas.restore();

            mAlphaPaint.setAlpha((int) (mAlpha * 255));
            canvas.drawArc(mAlphaRectF, mCurrentDegree, valueAngle, true, mAlphaPaint);
            canvas.drawCircle(0, 0,mRadius * mHoleRadiusPercent, mHolePaint);
            //若本次绘制角度大于该版块角度，则进入下一板块，否则结束循环，重新绘制
            if (sweepAngle <= valueAngle){
                mCurrentDegree += sweepAngle;
            }else {
                break;
            }
        }
    }

    private void drawTextInner(Canvas canvas){
        for (int i = 0; i < count; i++) {
            float sweepAngle = getData().get(i).getData() / sumData * 360f;
            canvas.save();
            //当前模块角平分线的sin和cos值
            float mathCos = (float) (Math.cos((sweepAngle / 2 + mCurrentDegree) / 180f * Math.PI));
            float mathSin = (float) (Math.sin((sweepAngle / 2 + mCurrentDegree) / 180f * Math.PI));

            //若该板块设置凸起，平移画布，使其与周边各块产生间距，并突出
            if (getData().get(i).isRaised()){
                canvas.translate(mathCos * mSpace, mathSin * mSpace);
            }

            String msg = getData().get(i).getMsg();
            Paint.FontMetrics metrics = mBlockTextPaint.getFontMetrics();
            float textRadius = (1 + mAlphaRadiusPercent) * mRadius / 2;

            if (isDisPlayPercent()) {
                //获取文字高度，因水平已居中
                //设置文字格式
                DecimalFormat format = new DecimalFormat("#0.00%");
                String text = format.format(sweepAngle / 360f);
                //绘制模块文字
                if (!TextUtils.isEmpty(msg)) {
                    canvas.drawText(msg, textRadius * mathCos,
                            textRadius * mathSin - Math.abs(metrics.descent), mBlockTextPaint);
                }
                //绘制模块百分比
                canvas.drawText(text, textRadius * mathCos,
                        textRadius * mathSin + Math.abs(metrics.ascent), mBlockTextPaint);
            } else {
                //获取文字高度，因水平已居中
                float textHeight = Math.abs(metrics.descent - metrics.ascent);
                //绘制模块文字
                if (!TextUtils.isEmpty(msg)) {
                    canvas.drawText(msg, textRadius * mathCos,
                            textRadius * mathSin + textHeight / 2 - metrics.descent, mBlockTextPaint);
                }
            }
            canvas.restore();
            mCurrentDegree += sweepAngle;
        }
    }

    private void calculate() {
        mRadius = (Math.min(getWidth() - getPaddingLeft() - getPaddingRight(),
                getHeight() - getPaddingTop() - getPaddingBottom()) >> 1) * 0.85f;
        mCurrentDegree = mStartDegree;
        mRadiusRectF = new RectF(-mRadius, -mRadius, mRadius, mRadius);
        mAlphaRectF = new RectF(-mRadius * mAlphaRadiusPercent, -mRadius * mAlphaRadiusPercent,
                 mRadius * mAlphaRadiusPercent, mRadius * mAlphaRadiusPercent);
    }

    private void initValueAnimator(){
        if(null == mAnimator) {
            mAnimator = ValueAnimator.ofFloat(0, 360f);
            mAnimator.setDuration(mAnimatorTime);
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mAnimatorValue = (float) animation.getAnimatedValue();
                    refresh();
                }
            });
            mAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    showAnimator = false;
                    refresh();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        mAnimator.start();
    }

    /**
     * Gets data.
     *
     * @return the data
     */
    public List<PieEntry> getData() {
        if (null == mData) {
            this.mData = new ArrayList<>();
        }
        return this.mData;
    }

    /**
     * Sets data.
     *
     * @param data the data
     */
    public PieView setData(List<PieEntry> data) {
        getData().clear();
        getData().addAll(data);

        count = getData().size();
        //计算数据总和
        sumData = 0;
        for (int i = 0; i < count; i++){
            sumData += getData().get(i).getData();
        }
        return this;
    }

    /**
     * Gets start degree
     *
     * @return the start degree
     */
    public float getStartDegree() {
        return mStartDegree;
    }

    /**
     * Sets start degree.
     *
     * @param startDegree the start degree
     */
    public PieView setStartDegree(float startDegree) {
        this.mStartDegree = startDegree;
        return this;
    }

    /**
     * Gets center alpha
     *
     * @return
     */
    public float getCenterAlpha() {
        return mAlpha;
    }

    /**
     * Sets center alpha
     *
     * @param alpha
     * @return
     */
    public PieView setCenterAlpha(float alpha) {
        this.mAlpha = alpha;
        return this;
    }

    /**
     * Gets hole radius percent.
     *
     * @return the hole radius percent
     */
    public float getHoleRadiusPercent() {
        return mHoleRadiusPercent;
    }

    /**
     * Sets hole radius percent.
     *
     * @param holeRadiusPercent the hole radius percent
     */
    public PieView setHoleRadiusPercent(@FloatRange(from = 0, to = 1.0f) float holeRadiusPercent) {
        this.mHoleRadiusPercent = holeRadiusPercent;
        return this;
    }

    /**
     * Gets alpha radius percent.
     *
     * @return the alpha radius percent
     */
    public float getAlphaRadiusPercent() {
        return mAlphaRadiusPercent;
    }

    /**
     * Sets alpha radius percent.
     *
     * @param alphaRadiusPercent the alpha radius percent
     */
    public PieView setAlphaRadiusPercent(@FloatRange(from = 0, to = 1.0f) float alphaRadiusPercent) {
        this.mAlphaRadiusPercent = alphaRadiusPercent;
        return this;
    }

    /**
     * Gets space.
     *
     * @return the space
     */
    public float getSpace() {
        return mSpace;
    }

    /**
     * Sets space.
     *
     * @param space the space
     */
    public PieView setSpace(@Px int space) {
        this.mSpace = space;
        return this;
    }

    /**
     * Is dis play percent boolean.
     *
     * @return the boolean
     */
    public boolean isDisPlayPercent() {
        return disPlayPercent;
    }

    /**
     * Sets dis play percent.
     *
     * @param disPlayPercent the dis play percent
     */
    public PieView setDisPlayPercent(boolean disPlayPercent) {
        this.disPlayPercent = disPlayPercent;
        return this;
    }

    /**
     * Is show mAnimator boolean.
     *
     * @return the boolean
     */
    public boolean isShowAnimator() {
        return showAnimator;
    }

    /**
     * Sets show mAnimator.
     *
     * @param showAnimator the show mAnimator
     */
    public PieView setShowAnimator(boolean showAnimator) {
        if (Looper.getMainLooper() != Looper.myLooper()){
            //非UI线程设置动画无效
            return this;
        }
        this.showAnimator = showAnimator;
        if (showAnimator){
            post(new Runnable() {
                @Override
                public void run() {
                    initValueAnimator();
                }
            });
        }else if (null != mAnimator && mAnimator.isRunning()){
            mAnimator.cancel();
        }
        return this;
    }

    /**
     * Gets block text size.
     *
     * @return the block text size
     */
    public int getBlockTextSize() {
        return mBlockTextSize;
    }

    /**
     * Sets block text size.
     *
     * @param blockTextSize the block text size
     */
    public PieView setBlockTextSize(@Px int blockTextSize) {
        this.mBlockTextSize = blockTextSize;
        mBlockTextPaint.setTextSize(blockTextSize);
        return this;
    }

    /**
     * Gets block text color.
     *
     * @return the block text color
     */
    public int getBlockTextColor() {
        return mBlockTextColor;
    }

    /**
     * Sets block text color.
     *
     * @param blockTextColor the block text color
     */
    public PieView setBlockTextColor(@ColorInt int blockTextColor) {
        this.mBlockTextColor = blockTextColor;
        mBlockTextPaint.setColor(blockTextColor);
        return this;
    }

    /**
     * Gets center text size.
     *
     * @return the center text size
     */
    public int getCenterTextSize() {
        return mCenterTextSize;
    }

    /**
     * Sets center text size.
     *
     * @param centerTextSize the center text size
     */
    public PieView setCenterTextSize(@Px int centerTextSize) {
        this.mCenterTextSize = centerTextSize;
        mCenterTextPaint.setTextSize(centerTextSize);
        return this;
    }

    /**
     * Gets center text color.
     *
     * @return the center text color
     */
    public int getCenterTextColor() {
        return mCenterTextColor;
    }

    /**
     * Sets center text color.
     *
     * @param centerTextColor the center text color
     */
    public PieView setCenterTextColor(int centerTextColor) {
        this.mCenterTextColor = centerTextColor;
        mCenterTextPaint.setColor(centerTextColor);
        return this;
    }

    /**
     * Gets center text.
     *
     * @return the center text
     */
    public String getCenterText() {
        return mCenterText;
    }

    /**
     * Sets center text.
     *
     * @param centerText the center text
     */
    public PieView setCenterText(String centerText) {
        this.mCenterText = centerText;
        this.showCenterText = true;
        return this;
    }

    /**
     * Is show center text boolean.
     *
     * @return the boolean
     */
    public boolean isShowCenterText() {
        return showCenterText;
    }

    /**
     * Sets show center text.
     *
     * @param showCenterText the show center text
     */
    public PieView setShowCenterText(boolean showCenterText) {
        this.showCenterText = showCenterText;
        return this;
    }

    /**
     * Sets Animator Duration
     *
     * @param duration the animator duration
     */
    public PieView setAnimatorDuration(int duration) {
        this.mAnimatorTime = duration;
        if (mAnimator != null) {
            mAnimator.setDuration(duration);
        }
        return this;
    }

    /**
     * Gets animator duration
     *
     * @return the animator duration
     */
    public int getAnimatorDuration() {
        return mAnimatorTime;
    }

    private int getRandColor(){
        Random random = new Random();
        return 0xff000000 | random.nextInt(0x00ffffff);
    }

    public void refresh(){
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }
}