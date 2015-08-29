package com.example.scott.bonus.customcheckbox;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Scott on 15/8/26.
 */
public class TouchCheckBox extends View {
    private Paint mCirclePaint;
    private Paint mCorrectPaint;
    private int radius;                    //圆的半徑
    private int width, height;             //元件寬高
    private int cx, cy;                    //圓心座標
    private float[] points = new float[6]; //對號的三個點的座標
    private float correctProgress;
    private float downY;
    private boolean isChecked;
    private boolean toggle;
    private boolean isAnim;
    private int animDurtion = 150;

    private OnCheckedChangeListener listener;
    private int unCheckColor = Color.GRAY;
    private int circleColor = Color.RED;

    public TouchCheckBox(Context context) {
        this(context, null);
    }

    public TouchCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TouchCheckBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    /**
     * 初始化
     * @param context
     */
    private void init(Context context) {

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(Color.RED);
        mCirclePaint.setStyle(Paint.Style.FILL);

        mCorrectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCorrectPaint.setColor(Color.WHITE);
        mCorrectPaint.setStyle(Paint.Style.FILL);
        mCorrectPaint.setStrokeWidth(dip2px(context, 2));
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChecked) {
                    hideCorrect();
                } else {
                    showCheck();
                }
            }
        });
    }

    /**
     * 設置當前選中狀態
     * @param checked
     */
    public void setChecked(boolean checked){
        if (isChecked && !checked) {
            hideCorrect();
        } else if(!isChecked && checked) {
            showCheck();
        }
    }

    /**
     * 返回當前選中狀態
     * @return
     */
    public boolean isChecked(){
        return isChecked;
    }

    /**
     * 確定尺寸座標
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = width = Math.min(w - getPaddingLeft() - getPaddingRight(),h - getPaddingBottom() - getPaddingTop());
        cx = w / 2;
        cy = h / 2;

        float r = height / 2f;
        points[0] = r / 2f + getPaddingLeft();
        points[1] = r + getPaddingTop();

        points[2] = r * 5f / 6f + getPaddingLeft();
        points[3] = r + r / 3f + getPaddingTop();

        points[4] = r * 1.5f +getPaddingLeft();
        points[5] = r - r / 3f + getPaddingTop();
        radius = (int) (height * 0.125f);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        float f = (radius -height * 0.125f) / (height * 0.5f);
        mCirclePaint.setColor(evaluate(f,unCheckColor,circleColor));
        canvas.drawCircle(cx, cy, radius, mCirclePaint);

        if(correctProgress > 0) {
            if(correctProgress < 1/3f) {
                float x = points[0] + (points[2] - points[0]) * correctProgress;
                float y = points[1] + (points[3] - points[1]) * correctProgress;
                canvas.drawLine(points[0], points[1], x, y, mCorrectPaint);
            }else {
                float x = points[2] + (points[4] - points[2]) * correctProgress;
                float y = points[3] + (points[5] - points[3]) * correctProgress;
                canvas.drawLine(points[0], points[1], points[2], points[3], mCorrectPaint);
                canvas.drawLine(points[2] - 3f, points[3], x, y, mCorrectPaint);
            }
        }
    }


    /**
     * 設置圓的顏色
     * @param color
     */
    public void setCircleColor(int color){
        circleColor = color;
    }

    /**
     * 設置對號的顏色
     * @param color
     */
    public void setCorrectColor(int color){
        mCorrectPaint.setColor(color);
    }

    /**
     * 設置未選中時的顏色
     * @param color
     */
    public void setUnCheckColor(int color){
        unCheckColor = color;
    }

    private int evaluate(float fraction, int startValue, int endValue) {
        int startInt = startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return ((startA + (int) (fraction * (endA - startA))) << 24)
                | ((startR + (int) (fraction * (endR - startR))) << 16)
                | ((startG + (int) (fraction * (endG - startG))) << 8)
                | ((startB + (int) (fraction * (endB - startB))));
    }

    /**
     * 處理觸摸事件觸發動畫
     */
    /*private class OnChangeStatusListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.i("Touch","Touch");
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dy = event.getRawY() - downY;
                    if (Math.abs(dy) >= 0) { //滑过一半触发
                        toggle = true;
                    } else {
                        toggle = false;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (toggle) {
                        if (isChecked) {
                            hideCorrect();
                        } else {
                            showCheck();
                        }
                    }
                    break;
            }
            return true;
        }
    }*/

    private void showUnChecked() {
        if (isAnim) {
            return;
        }

        isAnim = true;
        ValueAnimator va = ValueAnimator.ofFloat(0, 1).setDuration(animDurtion);
        va.setInterpolator(new LinearInterpolator());
        va.start();
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue(); // 0f ~ 1f
                radius = (int) ((1 - value) * height * 0.375f + height * 0.125f);
                if (value >= 1) {
                    isChecked = false;
                    isAnim = false;
                    if(listener!=null){
                        listener.onCheckedChanged(TouchCheckBox.this,false);
                    }
                }
                invalidate();
            }
        });
    }

    private void showCheck() {
        if (isAnim) {
            return;
        }
        isAnim = true;
        ValueAnimator va = ValueAnimator.ofFloat(0, 1).setDuration(animDurtion);
        va.setInterpolator(new LinearInterpolator());
        va.start();
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue(); // 0f ~ 1f
                radius = (int) (value * height * 0.37f + height * 0.125f);
                if (value >= 1) {
                    isChecked = true;
                    isAnim = false;
                    if(listener!=null){
                        listener.onCheckedChanged(TouchCheckBox.this,true);
                    }
                    showCorrect();
                }
                invalidate();
            }
        });
    }

    private void showCorrect() {
        if (isAnim) {
            return;
        }
        isAnim = true;
        ValueAnimator va = ValueAnimator.ofFloat(0, 1).setDuration(animDurtion);
        va.setInterpolator(new LinearInterpolator());
        va.start();
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue(); // 0f ~ 1f
                correctProgress = value;
                invalidate();
                if(value>=1){
                    isAnim = false;
                }
            }
        });
    }
    private void hideCorrect() {
        if (isAnim) {
            return;
        }
        isAnim = true;
        ValueAnimator va = ValueAnimator.ofFloat(0, 1).setDuration(animDurtion);
        va.setInterpolator(new LinearInterpolator());
        va.start();
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue(); // 0f ~ 1f
                correctProgress = 1-value;
                invalidate();
                if(value>=1){
                    isAnim = false;
                    showUnChecked();
                }
            }
        });
    }
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener){
        this.listener = listener;
    }
    public interface OnCheckedChangeListener{
        void onCheckedChanged(View buttonView, boolean isChecked);
    }

    /**
     * 根據手機的解析度從dp的單位轉換成px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根據手機的解析度px的單位轉換成dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
