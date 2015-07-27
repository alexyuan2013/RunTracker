package com.example.alex.runtracker;

import java.util.Date;

/**
 * 运动轨迹
 * Created by Alex on 2015/7/20.
 */
public class Run {


    //轨迹id
    private long mId;

    public void setId(long id) {
        mId = id;
    }

    public long getId() {

        return mId;
    }
    //轨迹日期
    private Date mStartDate;

    public Run(){
        mId = -1;
        mStartDate = new Date();
    }

    public Date getStartDate(){
        return mStartDate;
    }
    public void setStartDate(Date startDate){
        mStartDate = startDate;
    }

    /**
     * 计算定位持续的时间
     * @param endMillis 结束时刻的时间
     * @return 持续时间，单位：秒
     */
    public int getDurationSeconds(long endMillis){
        return (int)((endMillis - mStartDate.getTime())/1000);
    }

    /**
     * 格式化持续时间
     * @param durationSeconds 持续时间，单位：秒
     * @return 格式化的时间格式：hh:mm:ss
     */
    public static String formatDuration(int durationSeconds){
        int seconds = durationSeconds % 60;
        int minutes = ((durationSeconds - seconds) / 60) % 60;
        int hours = (durationSeconds - (minutes * 60) - seconds) / 3600;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
