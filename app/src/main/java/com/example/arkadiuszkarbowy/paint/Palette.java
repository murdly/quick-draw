package com.example.arkadiuszkarbowy.paint;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
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


    public interface OnColorChosenListener {
        void onColorChosen(int color);
    }

    ImageButton.OnClickListener mOnColorChosenListener = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View colorBtn) {
            if (colorBtn != mCurrentColor) {
                setIndicators(mCurrentColor, GONE);
                setIndicators(colorBtn, VISIBLE);
                mCurrentColor = (ImageButton) colorBtn;

                int color = (int) colorBtn.getTag();
                mCallback.onColorChosen(color);

            }
        }
    };

    private void setIndicators(View color, int value) {
        ViewGroup container = (ViewGroup) color.getParent();
        View indicator = container.getChildAt(1);
        indicator.setVisibility(value);
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

            LinearLayout container = createContainer();
            View indicator = createIndicator();

            container.addView(btn);
            container.addView(indicator);

            mPalette.addView(container);
        }

        mCurrentColor = (ImageButton) ((LinearLayout) mPalette.getChildAt(0)).getChildAt(0);
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
