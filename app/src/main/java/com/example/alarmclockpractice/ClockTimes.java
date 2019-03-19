package com.example.alarmclockpractice;

import java.io.Serializable;

public class ClockTimes implements Serializable {
    private String mHour;
    private String mMinute;
    private Boolean mAm;
    private Boolean mOn = true;

    ClockTimes(String hour, String minute, Boolean ampm){
        mHour = hour;
        mMinute = minute;
        mAm = ampm;
    }

    public Boolean getmAm() {
        return mAm;
    }

    public String getmHour() {
        return mHour;
    }

    public String getmMinute() {
        return mMinute;
    }

    public Boolean getmOn() {
        return mOn;
    }

    public void setmAm(Boolean mAm) {
        this.mAm = mAm;
    }

    public void setmHour(String mHour) {
        this.mHour = mHour;
    }

    public void setmMinute(String mMinute) {
        this.mMinute = mMinute;
    }

    public void setmOn(Boolean mOn) {
        this.mOn = mOn;
    }
}
