package com.zebra.jamesswinton.zdemo.activities;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKManager.EMDKListener;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.ProfileManager;
import com.zebra.jamesswinton.zdemo.App;
import com.zebra.jamesswinton.zdemo.R;
import com.zebra.jamesswinton.zdemo.adapters.InstallDemosAdapter;
import com.zebra.jamesswinton.zdemo.adapters.InstallDemosAdapter.OnDemoCheckChangedListener;
import com.zebra.jamesswinton.zdemo.data.DownloadableDemos.DownloadableDemo;
import com.zebra.jamesswinton.zdemo.databinding.ActivityInstallDemosBinding;
import com.zebra.jamesswinton.zdemo.profilemgr.ProcessProfile;
import com.zebra.jamesswinton.zdemo.profilemgr.ProcessProfile.OnProfileApplied;
import com.zebra.jamesswinton.zdemo.profilemgr.Xml;
import com.zebra.jamesswinton.zdemo.utils.CustomDialog;
import com.zebra.jamesswinton.zdemo.utils.CustomDialog.DialogType;
import com.zebra.jamesswinton.zdemo.utils.FileUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class InstallDemosActivity extends AppCompatActivity implements OnDemoCheckChangedListener,
    EMDKListener, OnProfileApplied {

  // UI
  private ActivityInstallDemosBinding mDataBinding;

  // Adapter
  private InstallDemosAdapter mInstallDemosAdapter;

  // Holder
  private List<DownloadableDemo> mDemosSelectedForInstallation = new ArrayList<>();

  // Static Variables
  private EMDKManager mEmdkManager = null;
  private ProfileManager mProfileManager = null;

  // Dialog
  private AlertDialog mLoadingDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_install_demos);

    // Init Adapter
    mInstallDemosAdapter = new InstallDemosAdapter(this, App.getInstance().getDownloadableDemos(), this);
    mDataBinding.downloadableDemosList.setLayoutManager(new LinearLayoutManager(this));
    mDataBinding.downloadableDemosList.setAdapter(mInstallDemosAdapter);

    // Init Install Listener
    mDataBinding.installButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mDemosSelectedForInstallation.isEmpty()) {
          Toast.makeText(InstallDemosActivity.this, "No demos selected",
              Toast.LENGTH_LONG).show();
        } else {
          installSelectedApps();
        }
      }
    });

    // Init Skip Listener
    mDataBinding.skipButton.setOnClickListener(v -> {
      returnToMainActivity(false);
    });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mEmdkManager != null) {
      mEmdkManager.release();
      mEmdkManager = null;
    }
  }


  private void installSelectedApps() {
    mLoadingDialog = CustomDialog.createLoadingDialog(this, "Installing application...");
    mLoadingDialog.show();

    // Init EMDK
    EMDKResults emdkManagerResults = EMDKManager.getEMDKManager(this,
        this);

    // Verify EMDK Manager
    if (emdkManagerResults == null ||
        emdkManagerResults.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
      Toast.makeText(this, "Failed to get EMDK Manager!", Toast.LENGTH_LONG)
          .show();
    }
  }

  /******************
   * EMDK Callbacks *
   ******************/

  @Override
  public void onOpened(EMDKManager emdkManager) {
    // Assign EMDK Reference
    mEmdkManager = emdkManager;

    // Get Profile & Version Manager Instances
    mProfileManager = (ProfileManager) mEmdkManager.getInstance(EMDKManager.FEATURE_TYPE.PROFILE);

    // Install Apps
    String downloadAndInstallXml = Xml.getDownloadAndInstallXML(mDemosSelectedForInstallation);
    new ProcessProfile(this, Xml.ProfileName, mProfileManager, this)
        .execute(downloadAndInstallXml);
  }

  @Override
  public void onClosed() {
    // Release EMDK Manager Instance
    if (mEmdkManager != null) {
      mEmdkManager.release();
      mEmdkManager = null;
    }
  }

  /**
   * MX Complete
   */

  @Override
  public void profileApplied() {
    // Update App List
    ListIterator<DownloadableDemo> demoIterator = App.getInstance().getDownloadableDemos().getDownloadableDemos().listIterator();
    while(demoIterator.hasNext()){
      if (FileUtils.isPackageInstalled(this, demoIterator.next().getPackageName())) {
        demoIterator.remove();
      }
    }

    // Remove Dialog
    if (mLoadingDialog != null) {
      mLoadingDialog.dismiss();
      mLoadingDialog = null;
    }

    // Show Result to User
    CustomDialog.showCustomDialog(this,
        DialogType.SUCCESS,
        "Installation Complete",
        "Apps successfully installed",
        "OK", (dialog, which) -> returnToMainActivity(true),
        null, null,
        null, null,
        false);
  }

  @Override
  public void profileError(String e) {
    if (mLoadingDialog != null) {
      mLoadingDialog.dismiss();
      mLoadingDialog = null;
    }

    CustomDialog.showCustomDialog(this,
        DialogType.ERROR,
        "Installation Error",
        "Failed to install application(s): " + e,
        "OK", (dialog, which) -> returnToMainActivity(false),
        null, null,
        null, null,
        false);
  }

  private void returnToMainActivity(boolean installed) {
    Intent mainActivity = new Intent(this, MainActivity.class);
    if (installed) mainActivity.putExtra(MainActivity.EXTRA_RELOAD_APPS_LIST, true);
    startActivity(mainActivity);
    finish();
  }

  /**
   * Interface Callback from Adapter
   */

  @Override
  public void onCheckChanged(DownloadableDemo downloadableDemo, boolean isChecked) {
    if (isChecked) {
      mDemosSelectedForInstallation.add(downloadableDemo);
    } else {
      mDemosSelectedForInstallation.remove(downloadableDemo);
    }
  }

}