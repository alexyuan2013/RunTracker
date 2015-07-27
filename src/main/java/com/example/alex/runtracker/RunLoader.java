package com.example.alex.runtracker;

import android.content.Context;

/**
 * Created by Alex on 2015/7/23.
 */
public class RunLoader extends DataLoader<Run> {
    private long mRunId;
    public RunLoader(Context context, long runId) {
        super(context);
        mRunId = runId;
    }

    @Override
    public Run loadInBackground() {
        return RunManager.get(getContext()).getRun(mRunId);
    }
}
