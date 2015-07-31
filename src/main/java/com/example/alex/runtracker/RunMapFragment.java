package com.example.alex.runtracker;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.util.ArrayList;


/**
 * Created by Alex on 2015/7/29.
 */
public class RunMapFragment extends SupportMapFragment implements LoaderManager.LoaderCallbacks, BaiduMap.OnMapLoadedCallback {
    private static final String ARG_RUN_ID = "RUN_ID";
    private static final int LOAD_LOCATION = 0;

    private BaiduMap mBaiduMap;
    private MapView mMapView;
    private RunDataBaseHelper.LocationCursor mLocationCursor;

    public static RunMapFragment newInstance(long runId){
        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunMapFragment rf = new RunMapFragment();
        rf.setArguments(args);
        return rf;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle args = getArguments();
        if (args != null){
            long runId = args.getLong(ARG_RUN_ID, -1);
            if (runId != -1){
                LoaderManager lm = getLoaderManager();
                lm.initLoader(LOAD_LOCATION, args, this);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = super.onCreateView(layoutInflater, viewGroup, bundle);
        mBaiduMap = getBaiduMap();
        mBaiduMap.setMyLocationEnabled(true);
        return view;
    }

    private void updateUI(){
        if(mBaiduMap == null || mLocationCursor ==null){
            return;
        }

        PolylineOptions line = new PolylineOptions();
        ArrayList<LatLng> points = new ArrayList<LatLng>();
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
        mLocationCursor.moveToFirst();
        while (!mLocationCursor.isAfterLast()){
            Location loc = mLocationCursor.getLocation();
            LatLng latLng= new LatLng(loc.getLatitude(), loc.getLongitude());
            points.add(GPSToBaiduCoordinate(latLng));
            latLngBuilder.include(GPSToBaiduCoordinate(latLng));
            mLocationCursor.moveToNext();
        }
        line.points(points);
        mBaiduMap.addOverlay(line);
        MyLocationData locationData = new MyLocationData.Builder()
                .accuracy(100.0f)
                .direction(100)
                .latitude(points.get(0).latitude)
                .longitude(points.get(0).longitude)
                .build();
        mBaiduMap.setMyLocationData(locationData);
        LatLng firstLatLng = new LatLng(locationData.latitude, locationData.longitude);
        LatLng baiduLatLng = GPSToBaiduCoordinate(firstLatLng);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(baiduLatLng);
        mBaiduMap.animateMapStatus(update);
        //Display display = getActivity().getWindowManager().getDefaultDisplay();
        //LatLngBounds latLngBounds = latLngBuilder.build();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        long runId = args.getLong(ARG_RUN_ID, -1);
        return new LocationListCursorLoader(getActivity(), runId);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        mLocationCursor = (RunDataBaseHelper.LocationCursor)data;
        updateUI();
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mLocationCursor.close();
        mLocationCursor = null;
    }

    private LatLng GPSToBaiduCoordinate(LatLng latLng){
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(latLng);
        return converter.convert();
    }

    @Override
    public void onMapLoaded() {
//        MapStatus mapStatus = new MapStatus.Builder()
//                .target(new LatLng(31,118))
//                .zoom(14)
//                .build();
//        MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(mapStatus);
//        mBaiduMap.animateMapStatus(update);
    }
}
