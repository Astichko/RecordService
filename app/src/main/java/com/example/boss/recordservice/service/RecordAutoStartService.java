package com.example.boss.recordservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.boss.recordservice.utils.AppStatsCollectHelper;

/**
 * Created by BOSS on 29.11.2016.
 */

public class RecordAutoStartService extends Service {

    private AppStatsCollectHelper appStats;
    final private int FIRST_START_DELAY = 0;
    final private int INTERVAL_DELAY = 10000;//10 sec

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getBaseContext(), "Service started", Toast.LENGTH_SHORT).show();
        initAppStatsCollectHelper();
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
                displayMatch();
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
}
