package com.example.arkadiuszkarbowy.paint;

import android.app.ActionBar;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements Palette.OnColorChosenListener{

    private EditText mTitle;
    private String mDefaultTitle;
    private CanvasView mCanvasView;
    private ImageButton mEraser, mNewFile;
//    private ImageButton mCurrentColor;
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
        mTitle = (EditText)findViewById(R.id.title);
        mTitle.setText(mDefaultTitle);
        mTitle.setSelection(mTitle.getText().length());

        mNewFile = (ImageButton) findViewById(R.id.file);
        mEraser = (ImageButton) findViewById(R.id.eraser);


        mCanvasView = (CanvasView) findViewById(R.id.canvas);

        mNewFile.setOnClickListener(mOnNewFileListener);
        mEraser.setOnClickListener(mOnEraseListener);

        LinearLayout container = (LinearLayout) findViewById(R.id.bottom_tools);
        mPalette = new Palette(this, container, this);
    }


    ImageButton.OnClickListener mOnNewFileListener = new ImageButton.OnClickListener(){
       @Override
       public void onClick(View v) {
           mCanvasView.clear();
        }
    };

    ImageButton.OnClickListener mOnEraseListener = new ImageButton.OnClickListener(){
        @Override
        public void onClick(View v) {
            mCanvasView.erase(true);
        }
    };

    @Override
    public void onColorChosen(int color) {
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
