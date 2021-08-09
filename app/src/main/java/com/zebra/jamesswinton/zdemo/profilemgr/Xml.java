
package com.zebra.jamesswinton.zdemo.profilemgr;

import com.zebra.jamesswinton.zdemo.data.DownloadableDemos.DownloadableDemo;
import java.util.List;

public class Xml {

  public static final String ProfileName = "InstallAPK";

  public static String getDownloadAndInstallXML(DownloadableDemo downloadableDemo) {
    String apkDownloadLocation = "/sdcard/Download/" + downloadableDemo.getName() + ".apk";
    return "<wap-provisioningdoc>\n"
        + "  <characteristic type=\"Profile\">\n"
        + "    <parm name=\"ProfileName\" value=\"InstallAPK\"/>\n"
        + "    <characteristic version=\"0.6\" type=\"FileMgr\">\n"
        + "      <parm name=\"FileAction\" value=\"1\" />\n"
        + "      <characteristic type=\"file-details\">\n"
        + "        <parm name=\"TargetAccessMethod\" value=\"2\" />\n"
        + "        <parm name=\"TargetPathAndFileName\" value=\"" + apkDownloadLocation + "\" />\n"
        + "        <parm name=\"SourceAccessMethod\" value=\"1\" />\n"
        + "        <parm name=\"SourceURI\" value=\"" + downloadableDemo.getDownloadLocation() + "\" />\n"
        + "      </characteristic>\n"
        + "    </characteristic>\n"
        + "    <characteristic version=\"4.2\" type=\"AppMgr\">\n"
        + "      <parm name=\"Action\" value=\"Install\" />\n"
        + "      <parm name=\"APK\" value=\"" + apkDownloadLocation + "\" />\n"
        + "    </characteristic>\n"
        + "  </characteristic>\n"
        + "</wap-provisioningdoc>";
  }

  public static String getDownloadAndInstallXML(List<DownloadableDemo> downloadableDemos) {
    StringBuilder xmlBuilder = new StringBuilder();
    xmlBuilder.append("<wap-provisioningdoc>\n");
    xmlBuilder.append("  <characteristic type=\"Profile\">\n");
    xmlBuilder.append("    <parm name=\"ProfileName\" value=\"InstallAPK\"/>\n");
    for (DownloadableDemo downloadableDemo : downloadableDemos) {
      String apkDownloadLocation = "/sdcard/Download/" + downloadableDemo.getName() + ".apk";

      xmlBuilder.append("    <characteristic version=\"0.6\" type=\"FileMgr\">");
      xmlBuilder.append("      <parm name=\"emdk_name\" value=\"download" + downloadableDemos.indexOf(downloadableDemo) + "\" />\n");
      xmlBuilder.append("      <parm name=\"FileAction\" value=\"1\" />\n");
      xmlBuilder.append("      <characteristic type=\"file-details\">\n");
      xmlBuilder.append("        <parm name=\"TargetAccessMethod\" value=\"2\" />\n");
      xmlBuilder.append("        <parm name=\"TargetPathAndFileName\" value=\"").append(apkDownloadLocation).append("\" />\n");
      xmlBuilder.append("        <parm name=\"SourceAccessMethod\" value=\"1\" />\n");
      xmlBuilder.append("        <parm name=\"SourceURI\" value=\"").append(downloadableDemo.getDownloadLocation()).append("\" />\n");
      xmlBuilder.append("      </characteristic>\n");
      xmlBuilder.append("    </characteristic>\n");
      xmlBuilder.append("    <characteristic version=\"4.2\" type=\"AppMgr\">\n");
      xmlBuilder.append("      <parm name=\"emdk_name\" value=\"install" + downloadableDemos.indexOf(downloadableDemo) + "\" />\n");
      xmlBuilder.append("      <parm name=\"Action\" value=\"Install\" />\n");
      xmlBuilder.append("      <parm name=\"APK\" value=\"").append(apkDownloadLocation).append("\" />\n");
      xmlBuilder.append("    </characteristic>\n");
    }
    xmlBuilder.append("  </characteristic>\n");
    xmlBuilder.append("</wap-provisioningdoc>");
    return xmlBuilder.toString();
  }

}
