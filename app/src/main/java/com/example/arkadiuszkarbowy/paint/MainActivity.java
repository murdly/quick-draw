package com.example.arkadiuszkarbowy.paint;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements Palette.OnColorChosenListener, Toolkit.OnToolSetListener {
    private static final int SELECT_FILE = 0;
    private static final String STORAGE_TYPE = "resource/folder";
    private static final String IMAGE_STORAGE_PATH = "/paint/";
    private static final String IMAGE_TYPE = ".png";

    private EditText mTitle;
    private CanvasView mCanvasView;
    private Palette mPalette;
    private Toolkit mToolkit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolbar();
        initTools();
        createStorageFolder();
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        String mDefaultTitle = getString(R.string.title);
        mTitle = (EditText) findViewById(R.id.title);
        mTitle.setText(mDefaultTitle);
        mTitle.setSelection(mTitle.getText().length());
    }

    private void initTools() {
        mCanvasView = (CanvasView) findViewById(R.id.canvas);
        LinearLayout container = (LinearLayout) findViewById(R.id.bottom_tools);
        mPalette = new Palette(this, container, this);

        mToolkit = new Toolkit(this, (LinearLayout) findViewById(R.id.toolkit), this);
        mToolkit.init();
    }

    private void createStorageFolder() {
        File folder = new File(Environment.getExternalStorageDirectory() + IMAGE_STORAGE_PATH);
        if (!folder.exists())
            folder.mkdir();
    }

    @Override
    public void draw(boolean fillMode) {
        Tool tool = mToolkit.getSelectedTool();
        tool.setColor(mPalette.getPaintColor());
        mCanvasView.setPaintColor(tool.getPaintColor());
        mCanvasView.setStrokeWidth(tool.getStroke());
        mCanvasView.fill(fillMode);
    }

    @Override
    public void clear() {
        mCanvasView.clear();
    }

    @Override
    public void updateToolColor(int color) {
        Tool current = mToolkit.getSelectedTool();
        current.setColor(color);
        mCanvasView.setPaintColor(current.getPaintColor());
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
                    setToolbarTitleFrom(path);
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
        mCanvasView.onReloadedBitmap(drawableBitmap);
    }

    private void setToolbarTitleFrom(String path) {
        String title = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
        mTitle.setText(title);
    }
}