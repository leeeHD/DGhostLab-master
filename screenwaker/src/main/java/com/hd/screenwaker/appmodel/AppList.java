package com.hd.screenwaker.appmodel;

import java.util.List;

// applications list singleton
public class AppList {
    private List<AppInfo> mList;

    private static AppList sInstance = new AppList();
    private AppList() {}
    public static AppList getInstance() {
        return sInstance;
    }

    public List<AppInfo> getList() {
        return mList;
    }

    public void setList(List<AppInfo> list) {
        mList = list;
    }
}
