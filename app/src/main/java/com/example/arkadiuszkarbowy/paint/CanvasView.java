package com.example.arkadiuszkarbowy.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by arkadiuszkarbowy on 09/09/15.
 */
public class CanvasView extends View {

    private static final int DEFAULT_COLOR = 0xFF000000;
    public static final int ERASER_COLOR = 0xFFFFFFFF;
    public static final int ERASER_STROKE_WIDTH = 80;
    public static final int PENCIL_STROKE_WIDTH = 2;
    public static final int BRUSH_STROKE_WIDTH = 30;

    private Path mPath;
    private Paint mDrawPaint, mCanvasPaint;
    private int mPaintColor;
    private Canvas mCanvas;
    private Bitmap mCanvasBitmap;
    boolean mFillMode = false;

    public CanvasView(Context context, AttributeSet attr) {
        super(context, attr);

        setUp();
    }

    private void setUp() {
        mPath = new Path();
        mCanvasPaint = new Paint(Paint.DITHER_FLAG);
        mPaintColor = DEFAULT_COLOR;
        mDrawPaint = new Paint();
        mDrawPaint.setColor(mPaintColor);
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

    public void onReloadedBitmap(Bitmap bitmap) {
        mCanvasBitmap = bitmap;
        mCanvas = new Canvas(mCanvasBitmap);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        int x = (int) event.getX();
        int y = (int) event.getY();

        if (mFillMode)
            fillEvent(event.getAction(), x, y);
        else
            drawingEvents(event.getAction(), x, y);

        invalidate();
        return true;
    }

    private void fillEvent(int action, int x, int y) {
        if (action == MotionEvent.ACTION_DOWN) {
            int targetColor = mCanvasBitmap.getPixel(x, y);
            if (mPaintColor != targetColor)
                floodFill(mCanvasBitmap, new Point(x, y), targetColor, mPaintColor);
        }
    }

    private boolean drawingEvents(int action, int x, int y) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                mCanvas.drawPath(mPath, mDrawPaint);
                mPath.reset();
                break;
            default:
                return false;
        }
        return true;
    }

    public void setPaintColor(int color) {
        this.mPaintColor = color;
        invalidate();
        mDrawPaint.setColor(mPaintColor);
    }

    public void setStrokeWidth(int width) {
        mDrawPaint.setStrokeWidth(width);
    }

    public void clear() {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        mCanvas.drawRect(0f, 0f, mCanvasBitmap.getWidth(), mCanvasBitmap.getHeight(), paint);
        invalidate();
    }

    public void fill(boolean on) {
        mFillMode = on;
    }

    // stackoverflow - bardzo wolne i niedokladne
    private void floodFill(Bitmap bmp, Point pt, int targetColor, int replacementColor) {
        Queue<Point> q = new LinkedList<Point>();
        q.add(pt);
        while (q.size() > 0) {
            Point n = q.poll();
            if (bmp.getPixel(n.x, n.y) != targetColor)
                continue;

            Point w = n, e = new Point(n.x + 1, n.y);
            while ((w.x > 0) && (bmp.getPixel(w.x, w.y) == targetColor)) {
                bmp.setPixel(w.x, w.y, replacementColor);
                if ((w.y > 0) && (bmp.getPixel(w.x, w.y - 1) == targetColor))
                    q.add(new Point(w.x, w.y - 1));
                if ((w.y < bmp.getHeight() - 1)
                        && (bmp.getPixel(w.x, w.y + 1) == targetColor))
                    q.add(new Point(w.x, w.y + 1));
                w.x--;
            }
            while ((e.x < bmp.getWidth() - 1)
                    && (bmp.getPixel(e.x, e.y) == targetColor)) {
                bmp.setPixel(e.x, e.y, replacementColor);

                if ((e.y > 0) && (bmp.getPixel(e.x, e.y - 1) == targetColor))
                    q.add(new Point(e.x, e.y - 1));
                if ((e.y < bmp.getHeight() - 1)
                        && (bmp.getPixel(e.x, e.y + 1) == targetColor))
                    q.add(new Point(e.x, e.y + 1));
                e.x++;
            }
        }
    }
}
