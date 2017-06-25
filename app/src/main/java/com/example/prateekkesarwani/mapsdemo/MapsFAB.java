package com.example.prateekkesarwani.mapsdemo;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by prateek.kesarwani on 25/06/17.
 */

public class MapsFAB extends Service {

    public static final String START_SERVICE = "start_service";
    public static final String STOP_SERVICE = "stop_service";

    private WindowManager windowManager;
    private ImageView chatImage;

    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        chatImage = new ImageView(this);
        chatImage.setImageResource(R.drawable.ic_maps);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;
        chatImage.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return false;
                    case MotionEvent.ACTION_UP:

                        // If movement is not made, we are leaving the event for onClicklistener.
                        if (getChangedX(event) == initialX && getChangedY(event) == initialY) {
                            return false;
                        }

                        // If movement is made, then we are consuming event.
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        params.x = getChangedX(event);
                        params.y = getChangedY(event);
                        windowManager.updateViewLayout(chatImage, params);
                        return true;
                }
                return false;
            }

            int getChangedX(MotionEvent event) {
                return initialX + (int) (event.getRawX() - initialTouchX);
            }

            int getChangedY(MotionEvent event) {
                return initialY + (int) (event.getRawY() - initialTouchY);
            }
        });

        chatImage.setOnClickListener(view -> {
                    Intent intent = new Intent(chatImage.getContext(), MapsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
        );

        windowManager.addView(chatImage, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatImage != null) windowManager.removeView(chatImage);
    }
}