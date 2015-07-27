package com.example.alex.runtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import java.util.Date;

/**
 * 管理轨迹及位置点的数据存取
 * Created by Alex on 2015/7/20.
 */
public class RunDataBaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "runs.sqlit";
    private static final int VERSION = 1;

    private static final String TABLE_RUN = "run";
    private static final String COLUMN_RUN_ID = "_id";
    private static final String COLUMN_RUN_START_DATE = "start_date";

    private static final String TABLE_LOCATION = "location";
    private static final String COLUMN_LOCATION_LATITUDE = "latitude";
    private static final String COLUMN_LOCATION_LONGITUDE = "longitude";
    private static final String COLUMN_LOCATION_ALTITUDE = "altitude";
    private static final String COLUMN_LOCATION_TIMESTAMP = "timestamp";
    private static final String COLUMN_LOCATION_PROVIDER = "provider";
    private static final String COLUMN_LOCATION_RUN_ID = "run_id";

    public RunDataBaseHelper(Context context){
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建轨迹表
        db.execSQL("create table run (_id integer primary key autoincrement, start_date integer)");
        //创建位置点表
        db.execSQL("create table location (timestamp integer, latitude real, longitude real,"
                + "altitude real, provider varchar(100), run_id integer references run(_id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 向线路表中插入数据
     * @param run
     * @return
     */
    public long insertRun(Run run){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RUN_START_DATE, run.getStartDate().getTime());
        return getWritableDatabase().insert(TABLE_RUN, null, cv);
    }

    /**
     * 向位置表中插入数据
     * @param runId
     * @param location
     * @return
     */
    public long insertLocation(long runId, Location location){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LOCATION_LATITUDE, location.getLatitude());
        cv.put(COLUMN_LOCATION_LONGITUDE, location.getLongitude());
        cv.put(COLUMN_LOCATION_ALTITUDE, location.getAltitude());
        cv.put(COLUMN_LOCATION_TIMESTAMP, location.getTime());
        cv.put(COLUMN_LOCATION_PROVIDER, location.getProvider());
        cv.put(COLUMN_LOCATION_RUN_ID, runId);
        return getWritableDatabase().insert(TABLE_LOCATION, null, cv);
    }

    /**
     * 查询轨迹
     * @return 轨迹游标
     */
    public RunCursor queryRuns(){
        Cursor wrapped = getReadableDatabase().query(TABLE_RUN, null, null, null, null, null,
                COLUMN_RUN_START_DATE + " asc");
        return new RunCursor(wrapped);
    }

    /**
     * 根据id查询单个轨迹
     * @param id
     * @return
     */
    public RunCursor queryRun(long id){
        Cursor wrapped = getReadableDatabase().query(TABLE_RUN,
                null, //all columns
                COLUMN_RUN_ID + " = ?", //查询id
                new String[]{String.valueOf(id)}, //id值
                null, //group by
                null, //having
                null, //order by
                "1" //仅返回一行
        );
        return new RunCursor(wrapped);
    }

    public LocationCursor queryLastLoacationForRun(long runId){
        Cursor wrapped = getReadableDatabase().query(TABLE_LOCATION,
                null,
                COLUMN_LOCATION_RUN_ID + " = ?",
                new String[]{String.valueOf(runId)},
                null,
                null,
                COLUMN_LOCATION_TIMESTAMP + " desc",
                "1");
        return new LocationCursor(wrapped);
    }

    /**
     * 轨迹游标类
     */
    public static class RunCursor extends CursorWrapper{
        public RunCursor(Cursor c){
            super(c);
        }

        public Run getRun(){
            if (isBeforeFirst() || isAfterLast()){
                return null;
            }
            Run run = new Run();
            long runId = getLong(getColumnIndex(COLUMN_RUN_ID));
            run.setId(runId);
            long startDate = getLong(getColumnIndex(COLUMN_RUN_START_DATE));
            run.setStartDate(new Date(startDate));
            return run;
        }
    }

    public static class LocationCursor extends CursorWrapper{
        public LocationCursor(Cursor c){
            super(c);
        }

        public Location getLocation(){
            if(isBeforeFirst() || isAfterLast()){
                return null;
            }
            String provider = getString(getColumnIndex(COLUMN_LOCATION_PROVIDER));
            Location loc = new Location(provider);
            loc.setLongitude(getDouble(getColumnIndex(COLUMN_LOCATION_LONGITUDE)));
            loc.setLatitude(getDouble(getColumnIndex(COLUMN_LOCATION_LATITUDE)));
            loc.setAltitude(getDouble(getColumnIndex(COLUMN_LOCATION_ALTITUDE)));
            loc.setTime(getLong(getColumnIndex(COLUMN_LOCATION_TIMESTAMP)));
            return loc;
        }
    }
}
