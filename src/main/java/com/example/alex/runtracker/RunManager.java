package com.example.alex.runtracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.util.prefs.PreferencesFactory;

/**
 * 管理与LocationManager的通讯
 * Created by Alex on 2015/7/20.
 */
public class RunManager {
    //日志标签
    private static final String TAG = "RunManager";
    /**
     * Intent标识
     */
    public static final String ACTION_LOCATION = "com.example.alex.runtracker.ACTION_LOCATION";
    private static final String TEST_PROVIDER = "TEST_PROVIDER";
    private static final String PREFS_FILE = "runs";
    private static final String PREF_CURRENT_RUN_ID = "RunManager.currentRunId";
    //采用单例模式
    private static RunManager sRunManager;
    private Context mAppContext;
    private LocationManager mLocationManager;
    private RunDataBaseHelper mHelper;
    private SharedPreferences mPrefs;
    private long mCurrentRunId;

    //单例模式中私有的构造函数
    private RunManager(Context context) {
        mAppContext = context;
        mLocationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);
        mHelper = new RunDataBaseHelper(mAppContext);
        mPrefs = mAppContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mCurrentRunId = mPrefs.getLong(PREF_CURRENT_RUN_ID, -1);
    }

    /**
     * 获得RunManager实例
     * @param c 上下文
     * @return RunManager实例
     */
    public static RunManager get(Context c) {
        if (sRunManager == null) {
            sRunManager = new RunManager(c.getApplicationContext());
        }
        return sRunManager;
    }

    //使用PendingIntent获取地理位置数据更新,要求LocationManager在将来某个时点帮忙发送某种类型的Intent
    //这样，即使应用组件，甚至是整个应用进程都销毁了，LocationManager仍会一直发送intent
    //直到要求它停止并按需启动新组件响应它们
    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
    }

    //广播位置
    private void broadcastLocation(Location location){
        Intent broadcast = new Intent(ACTION_LOCATION);
        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        mAppContext.sendBroadcast(broadcast);
    }

    //插入Run数据
    private Run insertRun(){
        Run run = new Run();
        run.setId(mHelper.insertRun(run));
        return run;
    }

    /**
     * 启动定位更新，广播位置
     */
    public void startLocationUpdates() {
        String provide = LocationManager.GPS_PROVIDER;
        if (mLocationManager.getProvider(TEST_PROVIDER) != null &&
                mLocationManager.isProviderEnabled(TEST_PROVIDER)){
            provide = TEST_PROVIDER;
        }
        Log.d(TAG, "Using provider: " + provide);
        Location lastKnown = mLocationManager.getLastKnownLocation(provide);
        if (lastKnown != null){
            lastKnown.setTime(System.currentTimeMillis());
            broadcastLocation(lastKnown);
        }
        PendingIntent pi = getLocationPendingIntent(true);
        //以最小等待时间（0s）和最短等待距离（0m）决定下一次定位数据的更新
        mLocationManager.requestLocationUpdates(provide, 0, 0, pi);
    }

    /**
     * 停止位置更新
     */
    public void stopLocationUpdates() {
        PendingIntent pi = getLocationPendingIntent(false);
        if (pi != null) {
            mLocationManager.removeUpdates(pi);
            pi.cancel();
        }
    }

    /**
     * 位置跟踪状态
     * @return True 正在跟踪<br/> False 停止跟踪
     */
    public boolean isTrackingRun() {
        return getLocationPendingIntent(false) != null;
    }

    public boolean isTrackingRun(Run run) {
        return run != null && run.getId() == mCurrentRunId;
    }

    /**
     * 开始跟踪路线
     * @param run
     */
    public void startTackingRun(Run run){
        mCurrentRunId = run.getId();
        mPrefs.edit().putLong(PREF_CURRENT_RUN_ID, mCurrentRunId).commit();
        startLocationUpdates();
    }

    /**
     * 开始跟踪新的路线
     * @return
     */
    public Run startNewRun(){
        Run run = insertRun();
        startTackingRun(run);
        return run;
    }

    /**
     * 停止跟踪路线
     */
    public void stopRun(){
        stopLocationUpdates();
        mCurrentRunId = -1;
        mPrefs.edit().remove(PREF_CURRENT_RUN_ID).commit();
    }

    /**
     * 插入位置
     * @param loc
     */
    public void insertLocation(Location loc){
        if(mCurrentRunId != -1){
            mHelper.insertLocation(mCurrentRunId, loc);
        } else {
            Log.e(TAG, "Location received with no tracking run; ignoring.");
        }
    }

    /**
     * 根据轨迹id获取轨迹
     * @param id 轨迹id
     * @return 轨迹Run
     */
    public Run getRun(long id){
        Run run = null;
        RunDataBaseHelper.RunCursor cursor = mHelper.queryRun(id);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()){
            run = cursor.getRun();
        }
        cursor.close();
        return run;
    }

    /**
     * 查询轨迹
     * @return 轨迹游标
     */
    public RunDataBaseHelper.RunCursor queryRuns(){
        return mHelper.queryRuns();
    }

    /**
     * 获取轨迹的最近一个位置
     * @param runId 轨迹id
     * @return 位置Location
     */
    public Location getLastLocationForRun(long runId){
        Location location = null;
        RunDataBaseHelper.LocationCursor cursor= mHelper.queryLastLoacationForRun(runId);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()){
            location = cursor.getLocation();
        }
        cursor.close();
        return location;
    }
}
