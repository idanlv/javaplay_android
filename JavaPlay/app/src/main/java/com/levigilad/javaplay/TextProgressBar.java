package com.levigilad.javaplay;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class TextProgressBar extends ProgressBar {
    private Paint mTextPaint;
    private String mText;

    public TextProgressBar(Context context) {
        super(context);
        mTextPaint = new Paint();
    }

    public TextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTextPaint = new Paint();
        loadAttributes(context, attrs);
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTextPaint = new Paint();

        loadAttributes(context, attrs);
    }

    private void loadAttributes(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.TextProgressBar);
        mText = attributes.getString(R.styleable.TextProgressBar_text);
        mTextPaint.setColor(attributes.getColor(R.styleable.TextProgressBar_color, Color.BLACK));
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        // First draw the regular progress bar, then custom draw our mText
        super.onDraw(canvas);
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(mText, 0, mText.length(), bounds);
        int x = getWidth() / 2 - bounds.centerX();
        int y = getHeight() / 2 - bounds.centerY();
        canvas.drawText(mText, x, y, mTextPaint);
    }

    public synchronized void setText(String mText) {
        this.mText = mText;
        drawableStateChanged();
    }

    public void setTextColor(int color) {
        mTextPaint.setColor(color);
        drawableStateChanged();
    }
}
