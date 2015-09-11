package com.example.arkadiuszkarbowy.paint;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * Created by arkadiuszkarbowy on 10/09/15.
 */
public class Palette extends View {

    private Context mContext;
    private ImageButton mCurrentColor;
    private OnColorChosenListener mCallback;
    private LinearLayout mPalette;

    public Palette(Context context, LinearLayout container, OnColorChosenListener listener) {
        super(context);
        mContext = context;
        mCallback = listener;
        mPalette = container;
        init();
    }

    public int getPaintColor() {
        return (int) mCurrentColor.getTag();
    }

    public interface OnColorChosenListener {
        void onColorChosen(int color);
    }

    private ImageButton.OnClickListener mOnColorChosenListener = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View newColor) {
            if (newColor != mCurrentColor) {
                setIndicators(mCurrentColor, GONE);
                setIndicators(newColor, VISIBLE);
                mCurrentColor = (ImageButton) newColor;

                int color = (int) newColor.getTag();
                mCallback.onColorChosen(color);
            }
        }
    };

    private void setIndicators(View color, int value) {
        ViewGroup container = (ViewGroup) color.getParent();
        View indicator = container.getChildAt(1);
        indicator.setVisibility(value);
    }


    public void setAvailable(boolean available){
        for(int i = 0; i < mPalette.getChildCount(); i++){
            ViewGroup container = (ViewGroup) mPalette.getChildAt(i);
            View btn = container.getChildAt(0);
            btn.setEnabled(available);
        }
    }

    private void init() {
        int[] colors = getResources().getIntArray(R.array.colors);

        for (int c : colors) {
            ImageButton btn = new ImageButton(mContext);
            btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            btn.setImageResource(R.drawable.color);
            btn.setColorFilter(c);
            btn.setTag(c);
            btn.setOnClickListener(mOnColorChosenListener);

            LinearLayout item = createContainer();
            View indicator = createIndicator();

            item.addView(btn);
            item.addView(indicator);

            mPalette.addView(item);
        }

        mCurrentColor = (ImageButton) ((LinearLayout) mPalette.getChildAt(0)).getChildAt(0);
        setIndicators(mCurrentColor, VISIBLE);
    }

    private LinearLayout createContainer() {
        LinearLayout container = new LinearLayout(mContext);
        container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        container.setOrientation(LinearLayout.VERTICAL);

        return container;
    }

    private View createIndicator() {
        View indicator = new View(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
        params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
        params.gravity = Gravity.CENTER;
        indicator.setVisibility(GONE);
        indicator.setLayoutParams(params);
        indicator.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        return indicator;
    }
}