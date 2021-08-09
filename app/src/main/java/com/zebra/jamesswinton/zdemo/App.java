package com.zebra.jamesswinton.zdemo;

import android.app.Application;
import com.zebra.jamesswinton.zdemo.data.DemoConfigs;
import com.zebra.jamesswinton.zdemo.data.DownloadableDemos;

public class App extends Application {

  public static DemoConfigs mDemoConfigs;
  public static DownloadableDemos mDownloadableDemos;

  @Override
  public void onCreate() {
    super.onCreate();
  }
}
