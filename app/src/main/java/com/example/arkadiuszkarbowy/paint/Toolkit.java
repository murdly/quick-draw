package com.example.arkadiuszkarbowy.paint;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by arkadiuszkarbowy on 14/09/15.
 */
public class Toolkit {
    public static final int ERASER_STROKE_WIDTH = 80;
    public static final int PENCIL_STROKE_WIDTH = 2;
    public static final int BRUSH_STROKE_WIDTH = 30;
    public static final int ERASER_COLOR = 0xFFFFFFFF;

    private Context mContext;
    private ArrayList<Tool> mTools;
    private Tool mCurrentTool;
    private LinearLayout mContainer;
    private OnToolSetListener mCallback;

    public Toolkit(Context context, LinearLayout container, OnToolSetListener listener) {
        mContext = context;
        mContainer = container;
        mTools = new ArrayList<>();
        mCallback = listener;
    }

    void init() {
        mTools.add(new Pencil());
        mTools.add(new Brush());
        mTools.add(new Fill());
        mTools.add(new Eraser());
        mTools.add(new Clear());

        mCurrentTool = mTools.get(0);

        inflateLayout();
    }

    private void inflateLayout() {
        for (Tool t : mTools)
            mContainer.addView(t.mBtn);
    }

    public Tool getSelectedTool() {
        return mCurrentTool;
    }

    public interface OnToolSetListener {
        void draw(boolean fillMode);

        void clear();
    }

    /*
    * CR
    * W tych klasach można ujednolicić tworzenie tego buttonu
    * Najelpiej chyba przenieść to do głownej klasy a w dziedziczących pobierać tylko id dla ikony
    * Metoda onClick również może być tam zaimplementowana a w klassach dziedziczących czy ma być true czy false dla callbacku*/

    private class Pencil extends Tool {

        public Pencil() {
            mStrokeWidth = PENCIL_STROKE_WIDTH;
            mBtn = new ImageButton(mContext);
            mBtn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            mBtn.setImageResource(R.drawable.pencil);
            mBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCurrentTool.resetColor();
            mCurrentTool = this;
            mCallback.draw(false);
        }
    }

    private class Brush extends Tool {

        public Brush() {
            mStrokeWidth = BRUSH_STROKE_WIDTH;
            mBtn = new ImageButton(mContext);
            mBtn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            mBtn.setImageResource(R.drawable.brush);
            mBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCurrentTool.resetColor();
            mCurrentTool = this;
            mCallback.draw(false);
        }
    }

    private class Fill extends Tool {
        public Fill() {
            mBtn = new ImageButton(mContext);
            mBtn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            mBtn.setImageResource(R.drawable.fill);
            mBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCurrentTool.resetColor();
            mCurrentTool = this;
            mCallback.draw(true);
        }
    }

    private class Eraser extends Tool {
        private int colorFilter, paintColor;

        public Eraser() {
            mStrokeWidth = ERASER_STROKE_WIDTH;
            colorFilter = mContext.getResources().getColor(R.color.colorAccent);
            paintColor = ERASER_COLOR;

            mBtn = new ImageButton(mContext);
            mBtn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            mBtn.setImageResource(R.drawable.eraser);
            mBtn.setOnClickListener(this);
        }

        @Override
        public void setColor(int color) {
            super.setColor(colorFilter);
            mPaintColor = paintColor;
        }

        @Override
        public void onClick(View v) {
            mCurrentTool.resetColor();
            mCurrentTool = this;
            mCallback.draw(false);
        }
    }

    private class Clear extends Tool {
        public Clear() {
            mBtn = new ImageButton(mContext);
            mBtn.setColorFilter(DEFAULT_COLOR);
            mBtn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            mBtn.setImageResource(R.drawable.file);
            mBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCallback.clear();
        }
    }
}