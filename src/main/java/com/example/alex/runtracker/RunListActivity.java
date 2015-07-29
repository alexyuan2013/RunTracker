package com.example.alex.runtracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.baidu.mapapi.SDKInitializer;

/**
 * 轨迹列表Activity
 * Created by Alex on 2015/7/21.
 */
public class RunListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new RunListFragment();
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        SDKInitializer.initialize(getApplicationContext());
//    }
}
