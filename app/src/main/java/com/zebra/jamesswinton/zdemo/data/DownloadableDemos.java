package com.zebra.jamesswinton.zdemo.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zebra.jamesswinton.zdemo.R;
import java.util.List;

public class DownloadableDemos {

  @SerializedName("downloadable_demos")
  @Expose
  private List<DownloadableDemo> downloadableDemos;

  public DownloadableDemos(List<DownloadableDemo> downloadableDemos) {
    this.downloadableDemos = downloadableDemos;
  }

  public List<DownloadableDemo> getDownloadableDemos() {
    return downloadableDemos;
  }

  public void setDownloadableDemos(
      List<DownloadableDemo> downloadableDemos) {
    this.downloadableDemos = downloadableDemos;
  }

  public static class DownloadableDemo {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("package_name")
    @Expose
    private String packageName;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("download_location")
    @Expose
    private String downloadLocation;
    @SerializedName("category")
    @Expose
    private String category;

    public DownloadableDemo(String name, String packageName, String description,
        String downloadLocation, String category) {
      this.name = name;
      this.packageName = packageName;
      this.description = description;
      this.downloadLocation = downloadLocation;
      this.category = category;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getPackageName() {
      return packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public String getDownloadLocation() {
      return downloadLocation;
    }

    public void setDownloadLocation(String downloadLocation) {
      this.downloadLocation = downloadLocation;
    }

    public String getCategory() {
      return category;
    }

    public void setCategory(String category) {
      this.category = category;
    }

    public DemoCategory getCategoryEnum(String category) {
      switch (category) {
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

    public int getIcon() {
      switch (getCategoryEnum(category)) {
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
}
