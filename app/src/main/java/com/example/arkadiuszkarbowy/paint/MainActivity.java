package com.example.arkadiuszkarbowy.paint;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements Palette.OnColorChosenListener {
    private static final int SELECT_FILE = 0;
    private static final String STORAGE_TYPE = "resource/folder";
    private static final String IMAGE_STORAGE_PATH = "/paint/";
    private static final String IMAGE_TYPE = ".png";

    private EditText mTitle;
    private CanvasView mCanvasView;
    private ImageButton mCurrentTool;
    private Palette mPalette;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolbar();
        initTools();
        createStorageFolder();
    }

    private void setUpToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        String mDefaultTitle = getString(R.string.title);
        mTitle = (EditText) findViewById(R.id.title);
        mTitle.setText(mDefaultTitle);
        mTitle.setSelection(mTitle.getText().length());
    }

    private void initTools(){
        mCanvasView = (CanvasView) findViewById(R.id.canvas);
        LinearLayout container = (LinearLayout) findViewById(R.id.bottom_tools);
        mPalette = new Palette(this, container, this);

        ImageButton mPencil = (ImageButton) findViewById(R.id.pencil);
        mPencil.setOnClickListener(mOnPencilListener);
        findViewById(R.id.brush).setOnClickListener(mOnBrushListener);
        findViewById(R.id.fill).setOnClickListener(mOnFillListener);
        findViewById(R.id.eraser).setOnClickListener(mOnEraseListener);
        findViewById(R.id.clear).setOnClickListener(mOnClearListener);

        mPencil.performClick();
    }

    private void createStorageFolder() {
        File folder = new File(Environment.getExternalStorageDirectory() + IMAGE_STORAGE_PATH);

        if (!folder.exists()) {
            folder.mkdir();
        }
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
                onColorChosen(mPalette.getPaintColor());
            } else {
                mCurrentTool.setColorFilter(getResources().getColor(R.color.colorAccent));
                mCanvasView.setPaintColor(CanvasView.ERASER_COLOR);
            }
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
            showFileChooser();
            return true;
        } else if (id == R.id.action_save_file) {
            saveImageToStorage();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showFileChooser() {
        Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory() + IMAGE_STORAGE_PATH);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(selectedUri, STORAGE_TYPE);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, getString(R.string.msg_choose)),
                    SELECT_FILE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, getString(R.string.msg_manager),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageToStorage() {
        mCanvasView.setDrawingCacheEnabled(true);
        mCanvasView.buildDrawingCache();
        Bitmap bitmap = mCanvasView.getDrawingCache();
        save(bitmap);
        mCanvasView.setDrawingCacheEnabled(false);
    }


    private void save(Bitmap bitmapImage) {
        String filename = mTitle.getText().toString();
        File path = new File(Environment.getExternalStorageDirectory(),
                IMAGE_STORAGE_PATH + filename + IMAGE_TYPE);

        if (!path.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(path);
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Toast.makeText(this, getString(R.string.msg_saved) + " " + filename,
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.msg_filename), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_FILE:
                if (resultCode == RESULT_OK) {
                    String path = getPathFromURI(data.getData());
                    loadImageFromStorage(path);
                    setTitleFrom(path);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getPathFromURI(Uri contentUri) {
        Cursor cursor = managedQuery(contentUri, new String[]{MediaStore.Audio.Media.DATA},
                null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void loadImageFromStorage(String path) {
        Bitmap loadedBitmap = BitmapFactory.decodeFile(path);
        Bitmap drawableBitmap = loadedBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mCanvasView.onLoadedImageFromStorage(drawableBitmap);
    }

    private void setTitleFrom(String path) {
        String title = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
        mTitle.setText(title);
    }
}