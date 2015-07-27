package com.example.alex.runtracker;

import android.support.v4.app.Fragment;

/**
 * 轨迹列表Activity
 * Created by Alex on 2015/7/21.
 */
public class RunListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new RunListFragment();
    }
}
