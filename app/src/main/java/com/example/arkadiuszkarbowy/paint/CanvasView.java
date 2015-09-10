package com.example.arkadiuszkarbowy.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by arkadiuszkarbowy on 09/09/15.
 */
public class CanvasView extends View {

    private static final int DEFAULT_COLOR = 0xFF000000;
    private static final int ERASER_COLOR = 0xFFFFFFFF;
    private static final int ERASER_STROKE = 80;

    private Path mPath;
    private Paint mDrawPaint, mCanvasPaint;
    private int mPaintColor;
    private Canvas mCanvas;
    private Bitmap mCanvasBitmap;

    public CanvasView(Context context, AttributeSet attr) {
        super(context, attr);

        setUp();
    }

    private void setUp() {
        mPath = new Path();
        mCanvasPaint = new Paint(Paint.DITHER_FLAG);
        mDrawPaint = new Paint();
        mDrawPaint.setColor(DEFAULT_COLOR);
        mDrawPaint.setStrokeWidth(20);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mCanvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
        canvas.drawPath(mPath, mDrawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                mCanvas.drawPath(mPath, mDrawPaint);
                mPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void setPaintColor(int color) {
        this.mPaintColor = color;
        invalidate();
        mDrawPaint.setColor(mPaintColor);
    }

    public void erase(boolean isErase) {
        if (isErase) {
            setPaintColor(ERASER_COLOR);
            mDrawPaint.setStrokeWidth(ERASER_STROKE);
        } else {
            setPaintColor(mPaintColor);
            mDrawPaint.setStrokeWidth(20);
        }

    }

    public void clear() {
        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }
}
