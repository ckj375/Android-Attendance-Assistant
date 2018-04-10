package com.ckj.worktime.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ckj.worktime.R;

/**
 * 打卡按钮
 * Created by kaijianchen on 2018/3/14.
 */

public class ClockButton extends View {

    private float centerX;
    private float centerY;
    private Paint bgPaint;
    private Paint paint;
    private Paint textPaint;
    private Paint.FontMetrics fontMetrics;
    private float radius;

    public ClockButton(Context context) {
        super(context);
    }

    public ClockButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.clock_button);
        radius = array.getDimension(R.styleable.clock_button_radius, 0);
        bgPaint = new Paint();
        bgPaint.setColor(Color.parseColor("#FFFFFF"));
        paint = new Paint();
        textPaint = new Paint();
        paint.setColor(Color.parseColor("#BFBFBF"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(getResources().getDimension(R.dimen.clock_button_stroke_width));
        textPaint.setColor(Color.parseColor("#2C2C2C"));
        textPaint.setTextSize(getResources().getDimension(R.dimen.clock_button_text_size));
        textPaint.setTextAlign(Paint.Align.CENTER);

        fontMetrics = textPaint.getFontMetrics();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        centerX = width / 2;
        centerY = height / 2;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(centerX, centerY, radius, bgPaint);
        canvas.drawCircle(centerX, centerY, radius, paint);
        canvas.drawText("保存", canvas.getWidth() / 2, canvas.getHeight() / 2 - fontMetrics.ascent / 2, textPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (isInCircle(event)) {
                    paint.setStyle(Paint.Style.FILL);
                } else {
                    paint.setStyle(Paint.Style.STROKE);
                }
                break;
            case MotionEvent.ACTION_UP:
                paint.setStyle(Paint.Style.STROKE);
                break;
        }
        invalidate();

        return super.onTouchEvent(event);
    }

    private boolean isInCircle(MotionEvent event) {
        float evX = event.getX();
        float evY = event.getY();

        float distanceX = Math.abs(evX - centerX);
        float distanceY = Math.abs(evY - centerY);
        int distanceZ = (int) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

        if (distanceZ > radius) {
            return false;
        }

        return true;
    }
}
