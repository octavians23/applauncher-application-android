package com.example.applauncherapplication;


import android.app.Application;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.format.Formatter;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ApplicationClass extends Application {

    public static PackageManager manager;
    public static ArrayList<SoftwareApp> softwareApps;

    @Override
    public void onCreate() {
        super.onCreate();

        manager = getPackageManager();
        softwareApps = new ArrayList<>();


        List<PackageInfo> availableActivities = manager.getInstalledPackages(PackageManager.GET_META_DATA);
        for (PackageInfo packageInfo : availableActivities) {
            SoftwareApp app = new SoftwareApp();
            app.setPackageName(packageInfo.packageName);
            app.setAppName((String) packageInfo.applicationInfo.loadLabel(manager));
            app.setAppIcon(packageInfo.applicationInfo.loadIcon(manager));
            app.setCategoryId(packageInfo.applicationInfo.category);
            app.setDescription(packageInfo.applicationInfo.loadDescription(manager));
            softwareApps.add(app);
        }
    }
}
