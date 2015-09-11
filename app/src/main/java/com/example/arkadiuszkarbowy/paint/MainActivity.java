package com.example.arkadiuszkarbowy.paint;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements Palette.OnColorChosenListener {

    private EditText mTitle;
    private String mDefaultTitle;
    private CanvasView mCanvasView;
    private ImageButton mCurrentTool, mPencil, mBrush, mFill, mEraser, mClear;
    private Palette mPalette;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDefaultTitle = getString(R.string.title);
        mTitle = (EditText) findViewById(R.id.title);
        mTitle.setText(mDefaultTitle);
        mTitle.setSelection(mTitle.getText().length());

        mClear = (ImageButton) findViewById(R.id.clear);
        mEraser = (ImageButton) findViewById(R.id.eraser);
        mPencil = (ImageButton) findViewById(R.id.pencil);
        mBrush = (ImageButton) findViewById(R.id.brush);
        mFill = (ImageButton) findViewById(R.id.fill);

        mCanvasView = (CanvasView) findViewById(R.id.canvas);

        mClear.setOnClickListener(mOnClearListener);
        mEraser.setOnClickListener(mOnEraseListener);
        mPencil.setOnClickListener(mOnPencilListener);
        mBrush.setOnClickListener(mOnBrushListener);
        mFill.setOnClickListener(mOnFillListener);


        LinearLayout container = (LinearLayout) findViewById(R.id.bottom_tools);
        mPalette = new Palette(this, container, this);

        mPencil.performClick();
    }

    private ImageButton.OnClickListener mOnPencilListener = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View pencil) {
            setTool(pencil, CanvasView.PENCIL_STROKE_WIDTH, false, false, true);
        }
    };

    private ImageButton.OnClickListener mOnBrushListener = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View brush) {
            setTool(brush, CanvasView.BRUSH_STROKE_WIDTH, false, false, true);
        }
    };

    private ImageButton.OnClickListener mOnFillListener = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View fill) {
            setTool(fill, -1, true, false, true);
        }
    };

    private ImageButton.OnClickListener mOnEraseListener = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View eraser) {
            setTool(eraser, CanvasView.ERASER_STROKE_WIDTH, false, true, false);
        }
    };

    private ImageButton.OnClickListener mOnClearListener = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCanvasView.clear();
        }
    };

    private void setTool(View tool, int width, boolean fill, boolean eraser, boolean palette) {
        if (mCurrentTool != tool) {
            if (mCurrentTool != null) mCurrentTool.setColorFilter(null);
            mCurrentTool = (ImageButton) tool;

            if (!eraser) {
                mCurrentTool.setColorFilter(mPalette.getPaintColor());
                mCanvasView.setPaintColor(mPalette.getPaintColor());
            } else
                mCanvasView.setPaintColor(CanvasView.ERASER_COLOR);

            mCanvasView.setStrokeWidth(width);
            mCanvasView.fill(fill);

            mPalette.setAvailable(palette);
        }
    }

    @Override
    public void onColorChosen(int color) {
        mCurrentTool.setColorFilter(color);
        mCanvasView.setPaintColor(color);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_open_file) {
            return true;
        } else if (id == R.id.action_save_file) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}