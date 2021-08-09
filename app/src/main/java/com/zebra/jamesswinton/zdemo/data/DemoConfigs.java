package com.zebra.jamesswinton.zdemo.data;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zebra.jamesswinton.zdemo.R;
import java.util.List;

public class DemoConfigs {

  @SerializedName("demo_configs")
  @Expose
  private List<DemoCategories> demoCategories = null;

  public DemoConfigs(List<DemoCategories> demoConfigs) {
    this.demoCategories = demoConfigs;
  }

  public List<DemoCategories> getDemoCategories() {
    return demoCategories;
  }

  public void setDemoCategories(List<DemoCategories> demoCategories) {
    this.demoCategories = demoCategories;
  }

  public static class DemoCategories {

    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("category_description")
    @Expose
    private String categoryDescription;
    @SerializedName("packages_to_display")
    @Expose
    private List<String> packagesToDisplay = null;
    // Do Not Convert in GSON
    private List<DemoApp> demoApps;

    public DemoCategories(String category, String categoryDescription, List<String> packagesToDisplay) {
      setCategory(category);
      this.categoryDescription = categoryDescription;
      this.packagesToDisplay = packagesToDisplay;
    }

    public DemoCategories(String category, String categoryDescription,
        List<String> packagesToDisplay,
        List<DemoApp> demoApps) {
      this.category = category;
      this.categoryDescription = categoryDescription;
      this.packagesToDisplay = packagesToDisplay;
      this.demoApps = demoApps;
    }

    public DemoCategory getCategoryEnum() {
      return convertCategoryToEnum(category);
    }

    public String getCategory() {
      return category;
    }

    public DemoCategory convertCategoryToEnum(String categoryString) {
      switch (categoryString) {
        case "Scan":
          return DemoCategory.SCANNING;
        case "NFC":
          return DemoCategory.NFC;
        case "Print":
          return DemoCategory.PRINT;
        case "Bluetooth":
          return DemoCategory.BLUETOOTH;
        case "Utils":
          return DemoCategory.UTILITIES;
        case "Third Party":
          return DemoCategory.THIRD_PARTY;
        case "Misc":
        default:
          return DemoCategory.MISC;
      }
    }

    public void setCategory(String category) {
      this.category = category;
    }

    public String getCategoryDescription() {
      return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
      this.categoryDescription = categoryDescription;
    }

    public List<String> getPackagesToDisplay() {
      return packagesToDisplay;
    }

    public void setPackagesToDisplay(List<String> packagesToDisplay) {
      this.packagesToDisplay = packagesToDisplay;
    }

    public List<DemoApp> getDemoApps() {
      return demoApps;
    }

    public void setDemoApps(
        List<DemoApp> demoApps) {
      this.demoApps = demoApps;
    }

    public int getIcon() {
      switch(getCategoryEnum()) {
        case SCANNING:
          return R.drawable.ic_scanner;
        case NFC:
          return R.drawable.ic_nfc;
        case PRINT:
          return R.drawable.ic_printer;
        case BLUETOOTH:
          return R.drawable.ic_bluetooth;
        case UTILITIES:
          return R.drawable.ic_utils;
        case THIRD_PARTY:
          return R.drawable.ic_third_party;
        case MISC:
        default:
          return R.drawable.ic_other;
      }
    }

  }

  public static class DemoApp {

    private Drawable appIcon;
    private String appLabel, appDesc;
    private Intent launchIntent;

    public DemoApp(Context cx, String packageName) throws NameNotFoundException {
      this.launchIntent = cx.getPackageManager().getLaunchIntentForPackage(packageName);
      this.appLabel = cx.getPackageManager().getApplicationLabel(cx.getPackageManager().getApplicationInfo(packageName, 0)).toString();
      this.appIcon = cx.getPackageManager().getApplicationIcon(packageName);
    }

    public DemoApp(String appLabel, String appDesc, Drawable appIcon, Intent launchIntent) {
      this.appIcon = appIcon;
      this.appLabel = appLabel;
      this.appDesc = appDesc;
      this.launchIntent = launchIntent;
    }

    public Drawable getAppIcon() {
      return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
      this.appIcon = appIcon;
    }

    public String getAppLabel() {
      return appLabel;
    }

    public void setAppLabel(String appLabel) {
      this.appLabel = appLabel;
    }

    public String getAppDesc() {
      return appDesc;
    }

    public void setAppDesc(String appDesc) {
      this.appDesc = appDesc;
    }

    public Intent getLaunchIntent() {
      return launchIntent;
    }

    public void setLaunchIntent(Intent launchIntent) {
      this.launchIntent = launchIntent;
    }

  }
}
