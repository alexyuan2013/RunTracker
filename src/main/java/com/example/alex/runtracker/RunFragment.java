package com.example.alex.runtracker;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.Loader;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 轨迹Fragment
 * Created by Alex on 2015/7/20.
 */
public class RunFragment extends Fragment {
    private Button mStartButton, mStopButton, mBMapButton;
    private TextView mStartedTextView, mLatitudeTextView, mLongitudeTextView,
            mAltitudeTextView, mDurationTextView;

    private RunManager mRunManager;
    private Run mRun;
    private Location mLastLocation;
    //接收位置广播
    private BroadcastReceiver mLocationReceiver = new LocationReceiver() {
        @Override
        protected void onLocationReceived(Context context, Location loc) {
            if(!mRunManager.isTrackingRun(mRun)){
                return;
            }
            mLastLocation = loc;
            if (isVisible()) {
                updateUI();
            }
        }

        @Override
        protected void onProviderEnabledChanged(boolean enabled) {
            int toastText = enabled ? R.string.gps_enabled : R.string.gps_disabled;
            Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
        }
    };

    private static final String ARG_RUN_ID = "RUN_ID";
    private static final int LOAD_RUN = 0;
    private static final int LOAD_LOCATION = 1;

    /**
     * 根据id创建轨迹
     * @param runId 轨迹id
     * @return 轨迹Fragment
     */
    public static RunFragment newInstance(long runId){
        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunFragment rf = new RunFragment();
        rf.setArguments(args);
        return rf;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //保持Fragment实例
        setRetainInstance(true);
        //获取一个轨迹管理实例
        mRunManager = RunManager.get(getActivity());
        Bundle args = getArguments();
        if( args != null){
            long id = args.getLong(ARG_RUN_ID, -1);
            if(id != -1){
                //mRun = mRunManager.getRun(id);
                LoaderManager lm = getLoaderManager();
                lm.initLoader(LOAD_RUN, args, new RunLoaderCallbacks());
                //mLastLocation = mRunManager.getLastLocationForRun(id);
                lm.initLoader(LOAD_LOCATION, args, new LocationLoaderCallbacks());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_run, container, false);

        mStartedTextView = (TextView) view.findViewById(R.id.run_startedTextView);
        mLatitudeTextView = (TextView) view.findViewById(R.id.run_latitudeTextView);
        mLongitudeTextView = (TextView) view.findViewById(R.id.run_longitudeTextView);
        mAltitudeTextView = (TextView) view.findViewById(R.id.run_altitudeTextView);
        mDurationTextView = (TextView) view.findViewById(R.id.run_durationTextView);

        mStartButton = (Button) view.findViewById(R.id.run_startButton);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mRunManager.startLocationUpdates();
//                mRun = new Run();
                if (mRun == null){
                    mRun = mRunManager.startNewRun();
                } else {
                    mRunManager.startTackingRun(mRun);
                }
                updateUI();
            }
        });

        mStopButton = (Button) view.findViewById(R.id.run_stopButton);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mRunManager.stopLocationUpdates();
                mRunManager.stopRun();
                updateUI();
            }
        });

        mBMapButton = (Button)view.findViewById(R.id.map_button);
        mBMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),RunMapActivity.class);
                i.putExtra(RunMapActivity.EXTRA_RUN_ID, mRun.getId());
                startActivity(i);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //注册位置广播接收
        getActivity().registerReceiver(mLocationReceiver,
                new IntentFilter(RunManager.ACTION_LOCATION));
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mLocationReceiver);
        super.onStop();
    }

    //根据轨迹跟踪状态跟新UI
    private void updateUI() {
        boolean started = mRunManager.isTrackingRun();
        boolean trackingRun = mRunManager.isTrackingRun(mRun);
        if (mRun != null){
            mStartedTextView.setText(mRun.getStartDate().toString());
        }
        int durationSeconds = 0;
        if(mRun != null && mLastLocation != null){
            durationSeconds = mRun.getDurationSeconds(mLastLocation.getTime());
            mLatitudeTextView.setText(Double.toString(mLastLocation.getLatitude()));
            mLongitudeTextView.setText(Double.toString(mLastLocation.getLongitude()));
            mAltitudeTextView.setText(Double.toString(mLastLocation.getAltitude()));
            mBMapButton.setEnabled(true);
        } else {
            mBMapButton.setEnabled(false);
        }
        mDurationTextView.setText(Run.formatDuration(durationSeconds));
        mStartButton.setEnabled(!started);
        mStopButton.setEnabled(started && trackingRun);
    }


    private class RunLoaderCallbacks implements LoaderManager.LoaderCallbacks<Run> {

        @Override
        public Loader<Run> onCreateLoader(int id, Bundle args) {
            return new RunLoader(getActivity(), args.getLong(ARG_RUN_ID));
        }

        @Override
        public void onLoadFinished(Loader<Run> loader, Run data) {
            mRun = data;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<Run> loader) {

        }
    }

    private class LocationLoaderCallbacks implements LoaderManager.LoaderCallbacks<Location>{

        @Override
        public Loader<Location> onCreateLoader(int id, Bundle args) {
            return new LastLocationLoader(getActivity(), args.getLong(ARG_RUN_ID));
        }

        @Override
        public void onLoadFinished(Loader<Location> loader, Location data) {
            mLastLocation = data;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<Location> loader) {

        }
    }
}
