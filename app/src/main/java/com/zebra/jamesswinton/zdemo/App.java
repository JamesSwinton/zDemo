package com.zebra.jamesswinton.zdemo;

import android.app.Application;

import com.zebra.jamesswinton.zdemo.data.DemoConfigs;
import com.zebra.jamesswinton.zdemo.data.DownloadableDemos;

public class App extends Application {

    private DemoConfigs mDemoConfigs;
    private DownloadableDemos mDownloadableDemos;

    private static App INSTANCE = null;
    private boolean isMainActivityRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();

        INSTANCE = this;
    }

    public static App getInstance() {
        return INSTANCE;
    }

    public boolean isMainActivityRunning() {
        return isMainActivityRunning;
    }

    public void setMainActivityRunning(boolean mainActivityRunning) {
        isMainActivityRunning = mainActivityRunning;
    }

    public DemoConfigs getDemoConfigs() {
        return mDemoConfigs;
    }

    public void setDemoConfigs(DemoConfigs mDemoConfigs) {
        this.mDemoConfigs = mDemoConfigs;
    }

    public DownloadableDemos getDownloadableDemos() {
        return mDownloadableDemos;
    }

    public void setDownloadableDemos(DownloadableDemos mDownloadableDemos) {
        this.mDownloadableDemos = mDownloadableDemos;
    }
}
