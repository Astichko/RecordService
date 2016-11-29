package com.example.boss.recordservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by BOSS on 29.11.2016.
 */

public class RecordAutoStartService extends Service {

    final private int FIRST_START_DELAY = 0;
    final private int INTERVAL_DELAY = 10000;//10 sec

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
                Toast.makeText(getBaseContext(), "Service is running", Toast.LENGTH_SHORT).show();
                handler.postDelayed(this, INTERVAL_DELAY);
            }
        }, FIRST_START_DELAY);
    }

}
