package com.hd.screenwaker.appmodel;

public class AppInfo implements Comparable<Object> {
    private long mUpdateTime;
    private String mName;
    private String mIcon;
    private String mPackName;
    private String mActivityName;

    public AppInfo(long mUpdateTime, String mName, String mIcon, String mPackName, String mActivityName) {
        this.mUpdateTime = mUpdateTime;
        this.mName = mName;
        this.mIcon = mIcon;
        this.mPackName = mPackName;
        this.mActivityName = mActivityName;
    }

    public String getmActivityName() {
        return mActivityName;
    }

    public void setmActivityName(String mActivityName) {
        this.mActivityName = mActivityName;
    }

    @Override
    public int compareTo(Object another) {
        AppInfo that = (AppInfo) another;
        return getName().compareTo(that.getName());
    }

    public long getUpdateTime() {
        return mUpdateTime;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getPackName() {
        return mPackName;
    }

    public void setmPackName(String mPackName) {
        this.mPackName = mPackName;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "mUpdateTime=" + mUpdateTime +
                ", mName='" + mName + '\'' +
                ", mIcon='" + mIcon + '\'' +
                ", mPackName='" + mPackName + '\'' +
                ", mActivityName='" + mActivityName + '\'' +
                '}';
    }
}
