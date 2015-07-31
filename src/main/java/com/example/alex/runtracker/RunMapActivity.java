package com.example.alex.runtracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by Alex on 2015/7/29.
 */
public class RunMapActivity extends SingleFragmentActivity {

    public static final String EXTRA_RUN_ID = "com.example.alex.runtracker.run_id";

    @Override
    protected Fragment createFragment() {
        long runId = getIntent().getLongExtra(EXTRA_RUN_ID, -1);
        if( runId != -1){
            return RunMapFragment.newInstance(runId);
        } else {
            return new RunMapFragment();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //百度地图在加载前必须要进行的初始化操作
        SDKInitializer.initialize(getApplicationContext());
    }
}
