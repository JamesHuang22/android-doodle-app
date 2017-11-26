package com.example.jiaheng.doodle;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Jiaheng on 4/23/17.
 */

public class DoodleActivity extends Activity implements View.OnClickListener {

    private String photoPath;
    private DoodleView doodleView;
    private Button undoButton;
    private Button saveButton;
    private Button clearButton;
    private Button blueButton;
    private Button redButton;
    private Button greenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_layout);
        photoPath = getIntent().getStringExtra("photoPath");
        Log.e("AAA", photoPath + "");
        Bitmap bitmap = rotateBitmapByDegree(BitmapFactory.decodeFile(photoPath), getBitmapDegree(photoPath));
        doodleView = (DoodleView) findViewById(R.id.draw_view);
        undoButton = (Button) findViewById(R.id.undo);
        undoButton.setOnClickListener(this);
        saveButton = (Button) findViewById(R.id.save);
        saveButton.setOnClickListener(this);
        clearButton = (Button) findViewById(R.id.clear);
        clearButton.setOnClickListener(this);

        blueButton = (Button) findViewById(R.id.blue);
        blueButton.setOnClickListener(this);
        redButton = (Button) findViewById(R.id.red);
        redButton.setOnClickListener(this);
        greenButton = (Button) findViewById(R.id.green);
        greenButton.setOnClickListener(this);

        doodleView.init(bitmap);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.undo:
                doodleView.undo();
                break;

            case R.id.clear:
                doodleView.clear();
                break;

            case R.id.save:
                Toast.makeText(this, "Saving...", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        doodleView.save();
                        postOnMainThread();
                    }
                }).start();

                break;

            case R.id.red:
                doodleView.setPaintColor(Color.RED);
                break;

            case R.id.blue:
                doodleView.setPaintColor(Color.BLUE);
                break;

            case R.id.green:
                doodleView.setPaintColor(Color.GREEN);
                break;
        }
    }

    private void postOnMainThread() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DoodleActivity.this, "Saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private int getBitmapDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }
}
