package com.example.boss.recordservice.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.boss.recordservice.R;
import com.example.boss.recordservice.utils.AppStatsCollectHelper;

/**
 * Created by BOSS on 29.11.2016.
 */

public class RecordAutoStartService extends Service {

    final private int FIRST_START_DELAY = 0;
    final private int INTERVAL_DELAY = 10000;//10 sec

    private WindowManager windowManager;
    private View floatingView;
    private WindowManager.LayoutParams params;
    private AppStatsCollectHelper appStats;
    private boolean isWidgetVisible = false;
    private boolean isMove;

    @Override
    public void onCreate() {
        initAppStatsCollectHelper();
        initWidget();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getBaseContext(), "Service started", Toast.LENGTH_SHORT).show();
        startMonitoring();
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void startMonitoring() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                displayPackageName();
                comparePackageName();
                handler.postDelayed(this, INTERVAL_DELAY);
            }
        }, FIRST_START_DELAY);
    }

    public void displayPackageName() {
        Toast.makeText(getBaseContext(), appStats.getCurrentAppPackageName(), Toast.LENGTH_SHORT).show();
    }

    public void displayMatch() {
        Toast.makeText(getBaseContext(), "Match to requested app", Toast.LENGTH_SHORT).show();
    }

    public void comparePackageName() {
        String currentPackageName = appStats.getCurrentAppPackageName();
        for (String str : appStats.getAppsTargetName()) {
            if (str.equals(currentPackageName)) {//If Match to requested app
                displayWidget();
            }
        }
    }

    public void initAppStatsCollectHelper() {
        appStats = new AppStatsCollectHelper(this);
    }

    public void callAlertDialog() {
        Intent intent = new Intent(this, ServiceDialog.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void displayWidget() {
        if (!isWidgetVisible) {
            windowManager.addView(floatingView, params);
            isWidgetVisible = true;
        }
    }

    public void initWidget() {
        initView();
        setWidgetListeners();
    }

    public void initView() {
        floatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    public void setWidgetListeners() {
        ImageView closeButton = (ImageView) floatingView.findViewById(R.id.imageClose);
        final ImageView startButton = (ImageView) floatingView.findViewById(R.id.imageStartRecord);
        final ImageView stopButton = (ImageView) floatingView.findViewById(R.id.imageStopRecord);
        final FrameLayout frameLayout = (FrameLayout) floatingView.findViewById(R.id.frameLay);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeWidget();
            }
        });
        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;
                        //get the touch location
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isMove = true;
                        params.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                        params.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatingView, params);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!isMove) {
                            if (stopButton.getVisibility() == View.GONE) {
                                stopButton.setVisibility(View.VISIBLE);
                                startButton.setVisibility(View.GONE);
                            } else {
                                startButton.setVisibility(View.VISIBLE);
                                stopButton.setVisibility(View.GONE);
                            }
                        }
                        isMove = false;
                        break;
                }
                return true;
            }
        });
    }

    public void removeWidget() {
        windowManager.removeView(floatingView);
    }
}
