package com.example.healthmanagement.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;
import com.example.healthmanagement.R;

public class SportStepView extends View {
    private float angleLength = 270.0f;//angleLength 表示进度弧的总角度，这里初始化为 270.0f 度。
    private int animationLength = PathInterpolatorCompat.MAX_NUM_POINTS;//animationLength 用于设置动画的时长相关参数，初始化为 PathInterpolatorCompat.MAX_NUM_POINTS 。
    private float borderWidth = ((float) dipToPx(14.0f));//borderWidth 表示绘制进度弧的边框宽度，通过 dipToPx 方法将 14.0f dp（设备无关像素）转换为实际像素值。
    private float currentAngleLength = 0.0f;//currentAngleLength 记录当前进度弧绘制的角度长度，初始为 0.0f ，会随着步数更新而变化。
    private float numberTextSize = 0.0f;//numberTextSize 用于存储显示步数数字的文本大小，初始为 0.0f ，会根据步数长度动态调整。
    private float startAngle = 135.0f;//startAngle 表示进度弧开始绘制的角度，这里是 135.0f 度。
    private String stepNumber = "0";//stepNumber 用于存储当前显示的步数，初始化为 "0" 。

    public SportStepView(Context context) {
        super(context);
    }

    public SportStepView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SportStepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onDraw(Canvas canvas) {  //onDraw 方法是视图绘制的核心方法，每次视图需要重绘时都会被调用。
        super.onDraw(canvas);
        float centerX = (float) (getWidth() / 2);
        float f = this.borderWidth;
        RectF rectF = new RectF(0.0f + f, f, (centerX * 2.0f) - f, (2.0f * centerX) - f);
        drawArcYellow(canvas, rectF);
        drawArcRed(canvas, rectF);
        drawTextNumber(canvas, centerX);
        drawTextStepString(canvas, centerX);
    }

    private void drawArcYellow(Canvas canvas, RectF rectF) {
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.colorPrimaryTint));
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(this.borderWidth);
        canvas.drawArc(rectF, this.startAngle, this.angleLength, false, paint);
    }

    private void drawArcRed(Canvas canvas, RectF rectF) {
        Paint paintCurrent = new Paint();
        paintCurrent.setStrokeJoin(Paint.Join.ROUND);
        paintCurrent.setStrokeCap(Paint.Cap.ROUND);
        paintCurrent.setStyle(Paint.Style.STROKE);
        paintCurrent.setAntiAlias(true);
        paintCurrent.setStrokeWidth(this.borderWidth);
        paintCurrent.setColor(getResources().getColor(R.color.colorPrimary));
        canvas.drawArc(rectF, this.startAngle, this.currentAngleLength, false, paintCurrent);
    }

    private void drawTextNumber(Canvas canvas, float centerX) {
        Paint vTextPaint = new Paint();
        vTextPaint.setTextAlign(Paint.Align.CENTER);
        vTextPaint.setAntiAlias(true);
        vTextPaint.setTextSize(this.numberTextSize);
        vTextPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, 0));
        vTextPaint.setColor(getResources().getColor(R.color.colorPrimary));
        Rect bounds_Number = new Rect();
        String str = this.stepNumber;
        vTextPaint.getTextBounds(str, 0, str.length(), bounds_Number);
        canvas.drawText(this.stepNumber, centerX, (float) ((getHeight() / 2) + (bounds_Number.height() / 2)), vTextPaint);
    }

    private void drawTextStepString(Canvas canvas, float centerX) {
        Paint vTextPaint = new Paint();
        vTextPaint.setTextSize((float) dipToPx(16.0f));
        vTextPaint.setTextAlign(Paint.Align.CENTER);
        vTextPaint.setAntiAlias(true);
        vTextPaint.setColor(getResources().getColor(R.color.colorGray));
        Rect bounds = new Rect();
        vTextPaint.getTextBounds("当前步数", 0, "当前步数".length(), bounds);
        canvas.drawText("当前步数", centerX, (float) ((getHeight() / 2) + bounds.height() + getFontHeight(this.numberTextSize)), vTextPaint);
    }

    public int getFontHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Rect bounds_Number = new Rect();
        String str = this.stepNumber;
        paint.getTextBounds(str, 0, str.length(), bounds_Number);
        return bounds_Number.height();
    }

    private int dipToPx(float dip) {
        return (int) ((dip * getContext().getResources().getDisplayMetrics().density) + (((float) (dip >= 0.0f ? 1 : -1)) * 0.5f));
    }

    public void setCurrentCount(int totalStepNum, int currentCounts) {
        if (currentCounts > totalStepNum) {
            currentCounts = totalStepNum;
        }
        float scalePrevious = ((float) Integer.valueOf(this.stepNumber).intValue()) / ((float) totalStepNum);
        float f = this.angleLength;
        setAnimation(scalePrevious * f, f * (((float) currentCounts) / ((float) totalStepNum)), this.animationLength);
        this.stepNumber = String.valueOf(currentCounts);
        setTextSize(currentCounts);
    }

    private void setAnimation(float start, float current, int length) {
        ValueAnimator progressAnimator = ValueAnimator.ofFloat(start, current);
        progressAnimator.setDuration((long) length);
        progressAnimator.setTarget(Float.valueOf(this.currentAngleLength));
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                SportStepView.this.currentAngleLength = ((Float) animation.getAnimatedValue()).floatValue();
                SportStepView.this.invalidate();
            }
        });
        progressAnimator.start();
    }

    public void setTextSize(int num) {
        int length = String.valueOf(num).length();
        if (length <= 4) {
            this.numberTextSize = (float) dipToPx(50.0f);
        } else if (length > 4 && length <= 6) {
            this.numberTextSize = (float) dipToPx(40.0f);
        } else if (length > 6 && length <= 8) {
            this.numberTextSize = (float) dipToPx(30.0f);
        } else if (length > 8) {
            this.numberTextSize = (float) dipToPx(25.0f);
        }
    }
}
