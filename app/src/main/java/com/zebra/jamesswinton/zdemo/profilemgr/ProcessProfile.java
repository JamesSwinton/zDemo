package com.zebra.jamesswinton.zdemo.profilemgr;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.ProfileManager;
import com.symbol.emdk.ProfileManager.PROFILE_FLAG;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ProcessProfile extends AsyncTask<String, Void, EMDKResults> {

  // Debugging
  private static final String TAG = "ProcessProfile";

  // Non-Static Variables
  private WeakReference<Context> mContextWeakRef;
  private String mProfileName;
  private ProfileManager mProfileManager;
  private OnProfileApplied mOnProfileApplied;

  //
  private static final String SUCCESSFUL_RESULT = "Profile Applied Successfully";

  public ProcessProfile(Context cx, String profileName, ProfileManager profileManager,
      OnProfileApplied onProfileApplied) {
    this.mProfileName = profileName;
    this.mProfileManager = profileManager;
    this.mOnProfileApplied = onProfileApplied;
    this.mContextWeakRef = new WeakReference<>(cx);
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
  }

  @Override
  protected EMDKResults doInBackground(String... params) {
    // Execute Profile
    return mProfileManager.processProfile(mProfileName, PROFILE_FLAG.SET, params);
  }

  @Override
  protected void onPostExecute(EMDKResults results) {
    super.onPostExecute(results);
    // Log Result
    Log.i(TAG, "Profile Manager Result: " + results.statusCode + " | "
            + results.extendedStatusCode);

    // Notify Class
    String result = parseXML(results.getStatusString());
    if (result.equals(SUCCESSFUL_RESULT)) {
      mOnProfileApplied.profileApplied();
    } else {
      mOnProfileApplied.profileError(result);
    }
  }

  public String parseXML(String statusXMLResponse) {
    // XML handling string
    boolean error = false;
    String errorType = "";
    String parmName = "";
    String errorDescription = "";
    String errorString = "";
    String resultString = "";
    int event;

    try {
      XmlPullParser parser = Xml.newPullParser();
      parser.setInput(new StringReader(statusXMLResponse));
      event = parser.getEventType();
      while (event != XmlPullParser.END_DOCUMENT) {
        String name = parser.getName();
        switch (event) {
          case XmlPullParser.START_TAG:
            if (name.equals("parm-error")) {
              parmName = parser.getAttributeValue(null, "name");
              errorDescription = parser.getAttributeValue(null, "desc");
              errorString += "\n\n (Name: " + parmName + ", Error Description: " + errorDescription + ")\n";
              error = true;
            }

            if (name.equals("characteristic-error")) {
              errorType = parser.getAttributeValue(null, "type");
              errorDescription = parser.getAttributeValue(null, "desc");
              errorString += "\n\n (Type: " + errorType + ", Error Description: " + errorDescription + ")\n";
              error = true;
            }
            break;
          case XmlPullParser.END_TAG:
            break;
        } event = parser.next();

        if (!error) {
          resultString = SUCCESSFUL_RESULT;
        } else {
          resultString = errorString;
        }
      }
    } catch (XmlPullParserException e) {
      resultString = e.getMessage();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return resultString;
  }

  public interface OnProfileApplied {
    void profileApplied();
    void profileError(String e);
  }

}
