package com.example.alex.runtracker;


import android.support.v4.app.Fragment;

/**
 * 轨迹Activity，继承自SingleFragmentActivity
 */
public class RunActivity extends SingleFragmentActivity {

    public static final String EXTRA_RUN_ID = "com.example.alex.runtracker.run_id";
    @Override
    protected Fragment createFragment() {
        long runId = getIntent().getLongExtra(EXTRA_RUN_ID, -1);
        if (runId != -1){
            return RunFragment.newInstance(runId);
        } else {
            return new RunFragment();
        }
    }
}
