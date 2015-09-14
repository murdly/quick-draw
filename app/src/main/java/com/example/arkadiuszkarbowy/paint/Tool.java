package com.example.arkadiuszkarbowy.paint;

import android.widget.ImageButton;

/**
 * Created by arkadiuszkarbowy on 14/09/15.
 */
public abstract class Tool implements ImageButton.OnClickListener {
    public static final int DEFAULT_COLOR = 0xFFFFFFFF;
    public int mPaintColor;
    public ImageButton mBtn;
    public int mStrokeWidth = -1;
    public String mTag;

    public void resetColor() {
        mBtn.setColorFilter(null);
    }

    public void setColor(int color) {
        mPaintColor = color;
        mBtn.setColorFilter(color);
    }

    public int getPaintColor() {
        return mPaintColor;
    }

    public int getStroke() {
        return mStrokeWidth;
    }
}