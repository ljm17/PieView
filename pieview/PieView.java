package com.example.administrator.pieview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The type Pie view.
 */
public class PieView extends View {
    private Paint mOutPaint;    //外圆画笔
    private Paint mAlphaPaint;  //内透明圆画笔
    private Paint mHolePaint;   //中心孔画笔
    private Paint mCenterTextPaint; //中心文字画笔
    private Paint mBlockTextPaint;   //各块百分比及文字画笔
    private float mStartDegree = 0;  //起始绘制角度
    private float mCurrentDegree;   //当前角度
    private float mAlpha = 0.4f;   //内透明圆透明度
    private float mRadius;  //外圆半径
    private float mHoleRadiusPercent = 0.50f;   //中心孔占外圆半径的百分比
    private float mAlphaRadiusPercent = 0.60f;  //内透明圆占外圆半径的百分比
    private float mSpace = 30;   //各块之间的间距
    private List<PieEntry> mData;
    private float sumData;  //数据总和
    private int count;  //数据条数
    private RectF mRadiusRectF;
    private RectF mAlphaRectF;
    private boolean disPlayPercent = true;  //是否展示百分比数
    private float mAnimatorValue;   //动画数值
    private boolean showAnimator = false;    //是否开启动画
    private int mBlockTextSize = 30;    //版块上的字号
    private int mBlockTextColor = Color.WHITE;  //版块上字的颜色
    private int mCenterTextSize = 50;   //中心字号
    private int mCenterTextColor = Color.GRAY;  //中心字颜色
    private String mCenterText;     //中心文字
    private boolean showCenterText;  //是否显示中心文字
    private ValueAnimator animator;     //动画

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
        //让文字水平居中
        mBlockTextPaint.setTextAlign(Paint.Align.CENTER);

        mCenterText = "PieView";
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        //将画布绘成白色
        canvas.drawColor(Color.WHITE);
        //平移画布，使（0,0）点至中心，便于计算
        canvas.translate(getWidth()/2,getHeight()/2);
        calculate();

        if (showAnimator){
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
        for (int i=0;i<count;i++){
            if (0 == getData().get(i).getColor()){
                int color = getRandColor();
                mOutPaint.setColor(color);
                getData().get(i).setColor(color);
            }else {
                mOutPaint.setColor(getData().get(i).getColor());
            }
            //计算该板块角度
            float sweepAngle = getData().get(i).getData()/sumData*360f;
            //保存当前画布状态
            canvas.save();
            //当前模块角平分线的sin和cos值
            if (getData().get(i).isRaised()){
                float mathCos = (float) (Math.cos((sweepAngle/2+mCurrentDegree)/180f*Math.PI));
                float mathSin = (float) (Math.sin((sweepAngle/2+mCurrentDegree)/180f*Math.PI));
                //若该板块设置凸起，平移画布，使其与周边各块产生间距，并突出
                canvas.translate(mathCos*mSpace, mathSin*mSpace);
            }
            //绘制外圆
            canvas.drawArc(mRadiusRectF,mCurrentDegree,sweepAngle,true,mOutPaint);
            //恢复平移前的状态
            canvas.restore();

            mCurrentDegree += sweepAngle;
        }

        mAlphaPaint.setAlpha((int) (mAlpha*255));
        canvas.drawCircle(0,0,mRadius*mAlphaRadiusPercent,mAlphaPaint);
        canvas.drawCircle(0,0,mRadius*mHoleRadiusPercent,mHolePaint);

        drawTextInner(canvas);

        if (showCenterText) {
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
        for (int i=0;i<count;i++){
            if (0 == getData().get(i).getColor()){
                int color = getRandColor();
                mOutPaint.setColor(color);
                getData().get(i).setColor(color);
            }else {
                mOutPaint.setColor(getData().get(i).getColor());
            }
            //本版块角度
            float sweepAngle = getData().get(i).getData()/sumData*360f;

            canvas.save();
            float valueAngle = Math.min(sweepAngle, mAnimatorValue-(mCurrentDegree-mStartDegree));
            if (getData().get(i).isRaised()){
                //当前模块角平分线的sin和cos值
                float mathCos = (float) (Math.cos((sweepAngle/2+mCurrentDegree)/180f*Math.PI));
                float mathSin = (float) (Math.sin((sweepAngle/2+mCurrentDegree)/180f*Math.PI));
                //若该板块设置凸起，平移画布，使其与周边各块产生间距，并突出
                canvas.translate(mathCos*mSpace, mathSin*mSpace);
            }
            //绘制外圆
            //根据动画，本次真实需绘制角度
            canvas.drawArc(mRadiusRectF,mCurrentDegree,valueAngle,true,mOutPaint);
            canvas.restore();

            mAlphaPaint.setAlpha((int) (mAlpha*255));
            canvas.drawArc(mAlphaRectF,mCurrentDegree,valueAngle,true,mAlphaPaint);
            canvas.drawCircle(0,0,mRadius*mHoleRadiusPercent,mHolePaint);
            //若本次绘制角度大于该版块角度，则进入下一板块，否则结束循环，重新绘制
            if (sweepAngle <= valueAngle){
                mCurrentDegree += sweepAngle;
            }else {
                break;
            }
        }
    }

    private void drawTextInner(Canvas canvas){
        if (disPlayPercent) {
            for (int i = 0; i < count; i++) {
                float sweepAngle = getData().get(i).getData() / sumData * 360f;
                canvas.save();
                //当前模块角平分线的sin和cos值
                float mathCos = (float) (Math.cos((sweepAngle / 2 + mCurrentDegree) / 180f * Math.PI));
                float mathSin = (float) (Math.sin((sweepAngle / 2 + mCurrentDegree) / 180f * Math.PI));
                if (getData().get(i).isRaised()){
                    //若该板块设置凸起，平移画布，使其与周边各块产生间距，并突出
                    canvas.translate(mathCos * mSpace, mathSin * mSpace);
                }

                //获取文字高度，因水平已居中
                //设置文字格式
                DecimalFormat format = new DecimalFormat("#0.00%");
                String text = format.format(sweepAngle / 360f);
                Paint.FontMetrics metrics = mBlockTextPaint.getFontMetrics();
                float textRadius = (1 + mAlphaRadiusPercent) * mRadius / 2;
                //绘制模块文字
                canvas.drawText(getData().get(i).getMsg(), textRadius * mathCos,
                        textRadius * mathSin - Math.abs(metrics.descent), mBlockTextPaint);
                //绘制模块百分比
                canvas.drawText(text, textRadius * mathCos,
                        textRadius * mathSin + Math.abs(metrics.ascent), mBlockTextPaint);
                canvas.restore();
                mCurrentDegree += sweepAngle;
            }
        }else {
            for (int i = 0; i < count; i++) {
                float sweepAngle = getData().get(i).getData() / sumData * 360f;
                canvas.save();
                //当前模块角平分线的sin和cos值
                float mathCos = (float) (Math.cos((sweepAngle / 2 + mCurrentDegree) / 180f * Math.PI));
                float mathSin = (float) (Math.sin((sweepAngle / 2 + mCurrentDegree) / 180f * Math.PI));
                if (getData().get(i).isRaised()){
                    //若该板块设置凸起，平移画布，使其与周边各块产生间距，并突出
                    canvas.translate(mathCos * mSpace, mathSin * mSpace);
                }
                //获取文字高度，因水平已居中
                String text = getData().get(i).getMsg();
                Paint.FontMetrics metrics = mBlockTextPaint.getFontMetrics();
                float textHeight = Math.abs(metrics.descent - metrics.ascent);
                float textRadius = (1 + mAlphaRadiusPercent) * mRadius / 2;
                //绘制模块文字
                canvas.drawText(text, textRadius * mathCos,
                        textRadius * mathSin + textHeight / 2 - metrics.descent, mBlockTextPaint);
                canvas.restore();
                mCurrentDegree += sweepAngle;
            }
        }
    }

    private void calculate() {
        mRadius = Math.min(getWidth()-getPaddingLeft()-getPaddingRight(),
                getHeight()-getPaddingTop()-getPaddingBottom())/2*0.85f;
        mCurrentDegree = mStartDegree;
        mRadiusRectF = new RectF(-mRadius,-mRadius,mRadius,mRadius);
        mAlphaRectF = new RectF(-mRadius*mAlphaRadiusPercent,-mRadius*mAlphaRadiusPercent,
                mRadius*mAlphaRadiusPercent,mRadius*mAlphaRadiusPercent);
        count = getData().size();
        //计算数据总和
        sumData = 0;
        for (int i=0;i<count;i++){
            sumData += getData().get(i).getData();
        }
    }

    private void initValueAnimator(){
        if(null == animator) {
            animator = ValueAnimator.ofFloat(0, 360f);
            animator.setDuration(3000);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mAnimatorValue = (float) animation.getAnimatedValue();
                    refresh();
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
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
        animator.start();
    }

    /**
     * Gets data.
     *
     * @return the data
     */
    public List<PieEntry> getData() {
        if (null == mData)
            this.mData = new ArrayList<>();
        return this.mData;
    }

    /**
     * Sets data.
     *
     * @param data the data
     */
    public void setData(List<PieEntry> data) {
        getData().clear();
        getData().addAll(data);
    }

    /**
     * Gets start degree.
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
    public void setStartDegree(float startDegree) {
        this.mStartDegree = startDegree;
        refresh();
    }

    public float getAlpha() {
        return mAlpha;
    }

    public void setAlpha(float alpha) {
        this.mAlpha = alpha;
        refresh();
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
    public void setHoleRadiusPercent(float holeRadiusPercent) {
        this.mHoleRadiusPercent = holeRadiusPercent;
        refresh();
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
    public void setAlphaRadiusPercent(float alphaRadiusPercent) {
        this.mAlphaRadiusPercent = alphaRadiusPercent;
        refresh();
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
    public void setSpace(float space) {
        this.mSpace = space;
        refresh();
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
    public void setDisPlayPercent(boolean disPlayPercent) {
        this.disPlayPercent = disPlayPercent;
        refresh();
    }

    /**
     * Is show animator boolean.
     *
     * @return the boolean
     */
    public boolean isShowAnimator() {
        return showAnimator;
    }

    /**
     * Sets show animator.
     *
     * @param showAnimator the show animator
     */
    public void setShowAnimator(boolean showAnimator) {
        if (Looper.getMainLooper() != Looper.myLooper()){
            //非UI线程设置动画无效
            return;
        }
        this.showAnimator = showAnimator;
        if (showAnimator){
            initValueAnimator();
        }else {
            if (null != animator && animator.isRunning())
                animator.cancel();
        }
        refresh();
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
    public void setBlockTextSize(int blockTextSize) {
        this.mBlockTextSize = blockTextSize;
        mBlockTextPaint.setTextSize(blockTextSize);
        refresh();
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
    public void setBlockTextColor(int blockTextColor) {
        this.mBlockTextColor = blockTextColor;
        mBlockTextPaint.setColor(blockTextColor);
        refresh();
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
    public void setCenterTextSize(int centerTextSize) {
        this.mCenterTextSize = centerTextSize;
        mCenterTextPaint.setTextSize(centerTextSize);
        refresh();
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
    public void setCenterTextColor(int centerTextColor) {
        this.mCenterTextColor = centerTextColor;
        mCenterTextPaint.setColor(centerTextColor);
        refresh();
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
    public void setCenterText(String centerText) {
        this.mCenterText = centerText;
        refresh();
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
    public void setShowCenterText(boolean showCenterText) {
        this.showCenterText = showCenterText;
        refresh();
    }

    private int getRandColor(){
        Random random = new Random();
        return 0xff000000 | random.nextInt(0x00ffffff);
    }

    private void refresh(){
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }
}
