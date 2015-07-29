package com.example.alex.runtracker;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;

/**
 * Created by Alex on 2015/7/23.
 */
public class LastLocationLoader extends DataLoader<Location> {
    private long mRunId;
    public LastLocationLoader(Context context, long runId) {
        super(context);
        mRunId = runId;
    }

    @Override
    public Location loadInBackground() {
        return RunManager.get(getContext()).getLastLocationForRun(mRunId);
    }


}


