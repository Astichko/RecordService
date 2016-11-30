package com.example.boss.recordservice.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by BOSS on 29.11.2016.
 */

public class AppStatsCollectHelper {

    private Context mContext;
    private String mLastApp;
    private String[] appsTargetName;
    final private String WHATS_APP_PACKAGE_NAME = "com.whatsapp";
    final private String SKYPE_APP_PACKAGE_NAME = "com.skype.raider";
    final private String VIBER_APP_PACKAGE_NAME = "com.viber.voip";

    public AppStatsCollectHelper(Context context) {
        mContext = context;
        addAppsName();
    }

    public void addAppsName() {
        appsTargetName = new String[3];
        appsTargetName[0] = WHATS_APP_PACKAGE_NAME;
        appsTargetName[1] = SKYPE_APP_PACKAGE_NAME;
        appsTargetName[2] = VIBER_APP_PACKAGE_NAME;
    }

    public String[] getAppsTargetName(){
        return appsTargetName;
    }


    public String getCurrentAppPackageName() {
        String topPackageName;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            topPackageName = getAppNameForLollipop();
        } else {
            topPackageName = getAppNameForKitKat();
        }

        if (topPackageName == null) {
            topPackageName = mLastApp;
        } else {
            mLastApp = topPackageName;
        }
        return topPackageName;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private String getAppNameForLollipop() {
        String topPackageName = null;
        UsageStatsManager usageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);

        if (stats != null) {
            SortedMap<Long, UsageStats> sortedMap = new TreeMap<>();
            for (UsageStats usageStats : stats) {
                sortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (!sortedMap.isEmpty()) {
                topPackageName = sortedMap.get(sortedMap.lastKey()).getPackageName();
            }
        }
        return topPackageName;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String getAppNameForKitKat() {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        return componentInfo.getPackageName();
    }
}
