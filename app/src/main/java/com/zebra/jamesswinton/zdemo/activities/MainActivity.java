package com.zebra.jamesswinton.zdemo.activities;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.zebra.jamesswinton.zdemo.App;
import com.zebra.jamesswinton.zdemo.R;
import com.zebra.jamesswinton.zdemo.adapters.DemoAppAdapter;
import com.zebra.jamesswinton.zdemo.data.DemoConfigs.DemoApp;
import com.zebra.jamesswinton.zdemo.data.DemoConfigs.DemoCategories;
import com.zebra.jamesswinton.zdemo.databinding.ActivityMainBinding;
import com.zebra.jamesswinton.zdemo.utils.CustomDialog;
import com.zebra.jamesswinton.zdemo.utils.CustomDialog.DialogType;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  // UI
  private ActivityMainBinding mDataBinding;

  // Intent Extra
  public static final String EXTRA_RELOAD_APPS_LIST = "reload-apps-list";

  // Adapter
  private DemoAppAdapter mDemoAppAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

    // Init Toolbar
    setSupportActionBar(mDataBinding.toolbar);

    // Init Adapter
    mDemoAppAdapter = new DemoAppAdapter(this, App.mDemoConfigs.getDemoCategories());
    mDataBinding.demoAppList.setLayoutManager(new LinearLayoutManager(this));
    mDataBinding.demoAppList.setAdapter(mDemoAppAdapter);
  }

  @Override
  protected void onResume() {
    super.onResume();
    // Check if we launched from Download activity and need to refresh
    Intent launchIntent = getIntent();
    if (launchIntent.hasExtra(EXTRA_RELOAD_APPS_LIST)) {
      refreshDemoAppsList();
    }
  }

  private void refreshDemoAppsList() {
    AlertDialog loadingDialog = CustomDialog.createLoadingDialog(this, "Refreshing app list...");
    loadingDialog.show();
    for (DemoCategories demoConfig : App.mDemoConfigs.getDemoCategories()) {
      List<DemoApp> demoApps = new ArrayList<>();
      for (String packageName : demoConfig.getPackagesToDisplay()) {
        try {
          demoApps.add(new DemoApp(MainActivity.this, packageName));
        } catch (NameNotFoundException e) {
          // Nothing to do here, this exception is just a check
        }
      } demoConfig.setDemoApps(demoApps);
    } loadingDialog.dismiss();

    mDemoAppAdapter.updateList(App.mDemoConfigs.getDemoCategories());
  }

  @Override
  public void onBackPressed() {
    CustomDialog.showCustomDialog(this, DialogType.WARN,
        "Confirm Exit",
        "Are you sure you wish to exit this application?",
        "EXIT", (dialog, which) -> System.exit(0),
        "CANCEL", (dialog, which) -> dialog.dismiss(),
        null, null,
        false);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_activity_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.install_app:
        if (App.mDownloadableDemos.getDownloadableDemos() != null
            && !App.mDownloadableDemos.getDownloadableDemos().isEmpty()) {
          startActivity(new Intent(this, InstallDemosActivity.class));
        } else {
          Toast.makeText(this, "Demos already installed", Toast.LENGTH_LONG).show();
        }
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}