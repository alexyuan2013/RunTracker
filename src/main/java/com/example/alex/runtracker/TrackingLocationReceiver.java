package com.example.alex.runtracker;

import android.content.Context;
import android.location.Location;
import android.util.Log;

/**
 * 轨迹记录广播
 * Created by Alex on 2015/7/20.
 */
public class TrackingLocationReceiver extends LocationReceiver {

    //日志标签
    private static final String TAG = "LocationReceiver";

    @Override
    protected void onLocationReceived(Context context, Location loc) {
        //调用RunManager将位置写入数据库
        RunManager.get(context).insertLocation(loc);
        Log.d(TAG, "Insert location to sqlite.");
    }
}
