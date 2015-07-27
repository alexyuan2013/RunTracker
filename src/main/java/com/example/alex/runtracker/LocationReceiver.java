package com.example.alex.runtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * 处理数据的接收，继承自BroadcastReceiver
 * Created by Alex on 2015/7/20.
 */
public class LocationReceiver extends BroadcastReceiver {
    //日志标签
    private static final String TAG = "LocationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //获取location extra
        Location loc = (Location) intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
        if (loc != null) {
            onLocationReceived(context, loc);
            return;
        }
        //检查定位服务是否可用
        if(intent.hasExtra(LocationManager.KEY_PROVIDER_ENABLED)){
            boolean enabled = intent.getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, false);
            onProviderEnabledChanged(enabled);
        }
    }

    //处理位置请求
    protected void onLocationReceived(Context context, Location loc) {
        Log.d(TAG, this + " Got location from " + loc.getProvider() + ": "
                + loc.getLatitude() + ", " + loc.getLongitude());
    }

    //处理位置请求是否可用
    protected void onProviderEnabledChanged(boolean enabled){
        Log.d(TAG, "Provider " + (enabled ? "enabled" : "disabled"));
    }
}
