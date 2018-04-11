package com.yih.tailer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.lang.reflect.Method;

public class TailerActivity extends Activity {

    private ImageView iv;
    private Bitmap baseBitmap;
    private Canvas canvas;
    private Paint paint;

    private int STROKE_WIDTH = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideBottomUIMenu();
        setContentView(R.layout.activity_tailer);

        this.iv = this.findViewById(R.id.iv);
        iv.post(new Runnable() {
            @Override
            public void run() {
                initCanvas();
            }
        });


        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int count = event.getPointerCount();
                for (int i = 0;i < count; ++i) {
                    int x = (int)event.getX(i);
                    int y = (int)event.getY(i);
                    canvas.drawCircle(x, y, STROKE_WIDTH, paint);
                    Log.w("##### ", "INDEX + " + i + ", X = " + x + ", Y = " + y);
                }

                iv.setImageBitmap(baseBitmap);
                return true;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP) { //监控/拦截/屏蔽返回键

            if (STROKE_WIDTH < 25) {
                ++STROKE_WIDTH;
            }

            return true;

        } else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

            if (STROKE_WIDTH > 1) {
                --STROKE_WIDTH;
            }

            return true;

        } else {

            return super.onKeyDown(keyCode, event);
        }


    }




    // 拦截/屏蔽系统Home键要加这个方法

//    public void onAttachedToWindow() {
//
//        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
//
//        super.onAttachedToWindow();
//
//    }

    protected void hideBottomUIMenu() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    private Size getScreenSize(Context context) {
        Size size = null;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            size = new Size(displayMetrics.widthPixels, displayMetrics.heightPixels);
        } catch (Exception e) {
            Log.w("TAILER", "Get Size Error: " + e);
        }
        return size;
    }

    private void initCanvas() {
        Size size = getScreenSize(TailerActivity.this);
        baseBitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(baseBitmap);
        canvas.drawColor(Color.BLACK);
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
        canvas.drawBitmap(baseBitmap, new Matrix(), paint);
        iv.setImageBitmap(baseBitmap);
    }

    class Size {
        int width, height;
        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
}