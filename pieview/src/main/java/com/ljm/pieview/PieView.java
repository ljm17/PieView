package com.ljm.pieview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Looper;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v4.content.ContextCompat;
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
 *
 * @author ljm
 */
public class PieView extends View {

    /** 饼状图画笔*/
    private Paint mPaint;

    /** 文字画笔*/
    private Paint mTextPaint;

    /** 外圆半径*/
    private float mRadius;

    private List<IPieEntry> mData = new ArrayList<>();

    /** 数据总和*/
    private float sumData;

    private RectF mRadiusRectF;

    private RectF mAlphaRectF;

    /** 动画数值*/
    private float mAnimatorValue;

    /** 动画*/
    private ValueAnimator mAnimator;

    /** decimal format*/
    private final DecimalFormat mFormat = new DecimalFormat("#0.00%");

    private final PieConfig mConfig = new PieConfig();

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
        initConfig(attrs);
    }

    private void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setFakeBoldText(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mRadiusRectF = new RectF();
        mAlphaRectF = new RectF();
    }

    private void initConfig(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PieView);
            mConfig.startDegree = typedArray.getFloat(R.styleable.PieView_startDegree, 180);
            mConfig.insideAlpha = typedArray.getFloat(R.styleable.PieView_insideAlpha, 0.4f);
            mConfig.alphaRadiusPercent = typedArray.getFloat(R.styleable.PieView_alphaRadiusPercent, 0.6f);
            mConfig.holeRadiusPercent = typedArray.getFloat(R.styleable.PieView_holeRadiusPercent, 0.5f);
            int space = typedArray.getDimensionPixelSize(R.styleable.PieView_blockSpace, -1);
            if (space > 0) {
                mConfig.blockSpace = space;
            }
            mConfig.disPlayPercent = typedArray.getBoolean(R.styleable.PieView_disPlayPercent, true);
            mConfig.blockTextSize = typedArray.getDimensionPixelSize(R.styleable.PieView_blockTextSize, 30);
            mConfig.centerTextSize = typedArray.getDimensionPixelSize(R.styleable.PieView_centerTextSize, 50);


            int blockTextColorRes = typedArray.getResourceId(R.styleable.PieView_blockTextColor, -1);
            int blockTextColor = typedArray.getColor(R.styleable.PieView_blockTextColor, -1);
            if (blockTextColorRes != -1) {
                mConfig.blockTextColor = ContextCompat.getColor(getContext(), blockTextColorRes);
            } else if (blockTextColor != -1) {
                mConfig.blockTextColor = blockTextColor;
            }

            int centerTextColorRes = typedArray.getResourceId(R.styleable.PieView_centerTextColor, -1);
            int centerTextColor = typedArray.getColor(R.styleable.PieView_centerTextColor, -1);
            if (centerTextColorRes != -1) {
                mConfig.centerTextColor = ContextCompat.getColor(getContext(), centerTextColorRes);
            } else if (centerTextColor != -1){
                mConfig.centerTextColor = centerTextColor;
            }

            mConfig.showCenterText = typedArray.getBoolean(R.styleable.PieView_showCenterText, false);
            String centerText = typedArray.getString(R.styleable.PieView_centerText);
            if (!TextUtils.isEmpty(centerText)) {
                mConfig.centerText = centerText;
            }
            boolean isShowAnim = typedArray.getBoolean(R.styleable.PieView_showAnimator, false);
            int duration = typedArray.getInt(R.styleable.PieView_animatorDuration, 2000);
            if (duration > 0) {
                mConfig.animatorDuration = duration;
            }
            typedArray.recycle();

            if (isShowAnim) {
                startAnimator();
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.WHITE);

        //平移画布，使（0,0）点至中心，便于计算
        canvas.translate(getWidth() >> 1,getHeight() >> 1);
        calculate();

        drawPie(canvas);
        if (mAnimator == null || !mAnimator.isRunning()) {
            drawTextInner(canvas);
            if (mConfig.showCenterText && !TextUtils.isEmpty(mConfig.centerText)) {
                drawCenterText(canvas);
            }
        }
    }

    /**
     * 计算尺寸信息
     */
    private void calculate() {
        mRadius = (Math.min(getWidth() - getPaddingLeft() - getPaddingRight(),
                getHeight() - getPaddingTop() - getPaddingBottom()) >> 1) * 0.85f;
        mRadiusRectF.set(-mRadius, -mRadius, mRadius, mRadius);
        mAlphaRectF.set(-mRadius * mConfig.alphaRadiusPercent, -mRadius * mConfig.alphaRadiusPercent,
                mRadius * mConfig.alphaRadiusPercent, mRadius * mConfig.alphaRadiusPercent);
    }

    /**
     * 绘制饼状图
     *
     * @param canvas 画布
     */
    private void drawPie(Canvas canvas) {
        if (mAnimator == null || !mAnimator.isRunning()) {
            mAnimatorValue = 360;
        }
        float currentDegree = mConfig.startDegree;
        //循环绘制各block
        for (IPieEntry entry : mData) {
            if (0 == entry.getBlockColor()) {
                int color = getRandColor();
                entry.setBlockColor(color);
            }
            mPaint.setAlpha(255);
            mPaint.setColor(entry.getBlockColor());
            //计算该板块角度
            float sweepAngle = entry.getBlockData() / sumData * 360f;

            float valueAngle = Math.min(sweepAngle, mAnimatorValue - currentDegree + mConfig.startDegree);
            canvas.save();
            if (entry.isBlockRaised()){
                //当前模块角平分线的sin和cos值
                float mathCos = (float) (Math.cos((sweepAngle / 2 + currentDegree) / 180f * Math.PI));
                float mathSin = (float) (Math.sin((sweepAngle / 2 + currentDegree) / 180f * Math.PI));
                //若该板块设置凸起，平移画布，使其与周边各块产生间距，并突出
                canvas.translate(mathCos * mConfig.blockSpace, mathSin * mConfig.blockSpace);
            }

            //根据进度，本次真实需绘制mCurrentDegree度的外圆
            canvas.drawArc(mRadiusRectF, currentDegree, valueAngle, true, mPaint);
            canvas.restore();

            mPaint.setColor(Color.WHITE);
            mPaint.setAlpha((int) (mConfig.insideAlpha * 255));
            canvas.drawArc(mAlphaRectF, currentDegree, valueAngle, true, mPaint);
            //若本次绘制角度大于该版块角度，则进入下一板块，否则结束循环，重新绘制
            if (sweepAngle <= valueAngle){
                currentDegree += sweepAngle;
            }else {
                break;
            }
        }
        mPaint.setAlpha(255);
        canvas.drawCircle(0, 0, mRadius * mConfig.holeRadiusPercent, mPaint);
    }

    /**
     * 绘制各板块文字信息
     *
     * @param canvas 画布
     */
    private void drawTextInner(Canvas canvas){
        mTextPaint.setTextSize(mConfig.blockTextSize);
        mTextPaint.setColor(mConfig.blockTextColor);
        float currentDegree = mConfig.startDegree;
        for (IPieEntry entry : mData) {
            float sweepAngle = entry.getBlockData() / sumData * 360f;
            canvas.save();
            //当前模块角平分线的sin和cos值
            float mathCos = (float) (Math.cos((sweepAngle / 2 + currentDegree) / 180f * Math.PI));
            float mathSin = (float) (Math.sin((sweepAngle / 2 + currentDegree) / 180f * Math.PI));

            //若该板块设置凸起，平移画布，使其与周边各块产生间距，并突出
            if (entry.isBlockRaised()){
                canvas.translate(mathCos * mConfig.blockSpace, mathSin * mConfig.blockSpace);
            }

            String msg = entry.getBlockMsg();
            Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
            float textRadius = (1 + mConfig.alphaRadiusPercent) * mRadius / 2;

            if (mConfig.disPlayPercent) {
                //获取文字高度，因水平已居中
                //设置文字格式
                String text = mFormat.format(sweepAngle / 360f);
                //绘制模块文字
                if (!TextUtils.isEmpty(msg)) {
                    canvas.drawText(msg, textRadius * mathCos,
                            textRadius * mathSin - metrics.descent, mTextPaint);
                }
                //绘制模块百分比
                canvas.drawText(text, textRadius * mathCos,
                        textRadius * mathSin - metrics.ascent, mTextPaint);
            } else {
                //获取文字高度，因水平已居中
                float textHeight = metrics.descent - metrics.ascent;
                //绘制模块文字
                if (!TextUtils.isEmpty(msg)) {
                    canvas.drawText(msg, textRadius * mathCos,
                            textRadius * mathSin + textHeight / 2 - metrics.descent, mTextPaint);
                }
            }
            canvas.restore();
            currentDegree += sweepAngle;
        }
    }

    /**
     * 绘制中心文字
     *
     * @param canvas 画布
     */
    private void drawCenterText(Canvas canvas) {
        mTextPaint.setTextSize(mConfig.centerTextSize);
        mTextPaint.setColor(mConfig.centerTextColor);
        Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
        float textHeight = metrics.descent - metrics.ascent;
        canvas.drawText(mConfig.centerText, 0, textHeight / 2 - metrics.descent, mTextPaint);
    }

    /**
     * 开始动画
     */
    public void startAnimator(){
        if(mAnimator == null) {
            mAnimator = ValueAnimator.ofFloat(0, 360f);
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mAnimatorValue = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
        }
        mAnimator.setDuration(mConfig.animatorDuration);
        mAnimator.start();
    }

    /**
     * 取消动画
     */
    public void cancelAnimator() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }

    /**
     * 获取数据
     *
     * @return data
     */
    public List<IPieEntry> getData() {
        return mData;
    }

    /**
     * 设置数据
     *
     * @param data the data
     */
    public PieView setData(List<? extends IPieEntry> data) {
        mData.clear();
        mData.addAll(data);

        //计算数据总和
        sumData = 0;
        for (IPieEntry entry : mData){
            sumData += entry.getBlockData();
        }
        return this;
    }

    /**
     * 设置起始饼状图绘制角度
     *
     * @param startDegree the start degree
     */
    public PieView setStartDegree(float startDegree) {
        this.mConfig.startDegree = startDegree;
        return this;
    }

    /**
     * 设置内部透明圆透明度
     *
     * @param alpha thi inside ring alpha
     */
    public PieView setCenterAlpha(float alpha) {
        this.mConfig.insideAlpha = alpha;
        return this;
    }

    /**
     * 设置中心孔半径占外圆半径比重
     *
     * @param holeRadiusPercent the hole radius percent
     */
    public PieView setHoleRadiusPercent(@FloatRange(from = 0, to = 1.0f) float holeRadiusPercent) {
        this.mConfig.holeRadiusPercent = holeRadiusPercent;
        return this;
    }

    /**
     * 设置内部透明圆半径占外圆半径比重
     *
     * @param alphaRadiusPercent the alpha radius percent
     */
    public PieView setAlphaRadiusPercent(@FloatRange(from = 0, to = 1.0f) float alphaRadiusPercent) {
        this.mConfig.alphaRadiusPercent = alphaRadiusPercent;
        return this;
    }

    /**
     * 设置重点突出板块间隙大小
     *
     * @param space the space
     */
    public PieView setSpace(@Px int space) {
        this.mConfig.blockSpace = space;
        return this;
    }

    /**
     * 设置是否展示板块百分比
     *
     * @param disPlayPercent the dis play percent
     */
    public PieView setDisPlayPercent(boolean disPlayPercent) {
        this.mConfig.disPlayPercent = disPlayPercent;
        return this;
    }

    /**
     * 设置板块文字字号
     *
     * @param blockTextSize the block text size
     */
    public PieView setBlockTextSize(@Px int blockTextSize) {
        this.mConfig.blockTextSize = blockTextSize;
        return this;
    }

    /**
     * 设置板块文字颜色
     *
     * @param blockTextColor the block text color
     */
    public PieView setBlockTextColor(@ColorInt int blockTextColor) {
        this.mConfig.blockTextColor = blockTextColor;
        return this;
    }

    /**
     * 设置中心文字字号
     *
     * @param centerTextSize the center text size
     */
    public PieView setCenterTextSize(@Px int centerTextSize) {
        this.mConfig.centerTextSize = centerTextSize;
        return this;
    }

    /**
     * 设置中心文字颜色
     *
     * @param centerTextColor the center text color
     */
    public PieView setCenterTextColor(int centerTextColor) {
        this.mConfig.centerTextColor = centerTextColor;
        return this;
    }

    /**
     * 设置中心文字
     *
     * @param centerText the center text
     */
    public PieView setCenterText(String centerText) {
        this.mConfig.centerText = centerText;
        this.mConfig.showCenterText = true;
        return this;
    }

    /**
     * 设置是否展示中心文字
     *
     * @param showCenterText the show center text
     */
    public PieView setShowCenterText(boolean showCenterText) {
        this.mConfig.showCenterText = showCenterText;
        return this;
    }

    /**
     * 设置动画时长
     *
     * @param duration the animator duration
     */
    public PieView setAnimatorDuration(long duration) {
        this.mConfig.animatorDuration = duration;
        return this;
    }

    private int getRandColor() {
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