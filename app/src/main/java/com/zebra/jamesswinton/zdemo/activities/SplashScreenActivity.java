package com.zebra.jamesswinton.zdemo.activities;

import static com.zebra.jamesswinton.zdemo.utils.Constants.FirstRunPref;
import static com.zebra.jamesswinton.zdemo.utils.Constants.PreferenceCategory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.zebra.jamesswinton.zdemo.App;
import com.zebra.jamesswinton.zdemo.R;
import com.zebra.jamesswinton.zdemo.data.DemoConfigs;
import com.zebra.jamesswinton.zdemo.data.DemoConfigs.DemoApp;
import com.zebra.jamesswinton.zdemo.data.DemoConfigs.DemoCategories;
import com.zebra.jamesswinton.zdemo.data.DownloadableDemos;
import com.zebra.jamesswinton.zdemo.data.DownloadableDemos.DownloadableDemo;
import com.zebra.jamesswinton.zdemo.databinding.ActivitySplashScreenBinding;
import com.zebra.jamesswinton.zdemo.utils.CustomDialog;
import com.zebra.jamesswinton.zdemo.utils.CustomDialog.DialogType;
import com.zebra.jamesswinton.zdemo.utils.FileUtils;
import com.zebra.jamesswinton.zdemo.utils.FileUtils.AssetFileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

public class SplashScreenActivity extends AppCompatActivity {

    private final static String TAG = "SplashScreenActivity";

    // UI
    private ActivitySplashScreenBinding mDataBinding;

    // File Names
    private static final String CONFIG_PATH = "/enterprise/usr/";
    private static final String DemoConfigFileName = "demo_config.json";
    private static final String DownloadConfigFileName = "download_config.json";

    // Configs
    private DemoConfigs mDemoConfigs = null;
    private DownloadableDemos mDownloadableDemos = null;

    // Delay
    private final long mStartTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (App.getInstance().isMainActivityRunning()) {
            Log.d(TAG, "App is already running, skipping initialization and go directly to MainActivity");
            startActivityIfNeeded(new Intent(SplashScreenActivity.this, MainActivity.class), 0);
            return;
        }

        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);

        Glide.with(this)
                .load(R.raw.logo)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        // Load Settings
                        loadConfigFilesAsync();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                   DataSource dataSource, boolean isFirstResource) {
                        // Load Settings
                        loadConfigFilesAsync();
                        return false;
                    }
                })
                .into(mDataBinding.gifView);
    }

    private void loadConfigFilesAsync() {
        new Thread(() -> {
            // Create Files List to Load
            File[] assetFiles = new File[]{
                    new File(CONFIG_PATH + DemoConfigFileName),
                    new File(CONFIG_PATH + DownloadConfigFileName)
            };

            // Loop Files and Parse to POJOs
            try {
                for (File file : assetFiles) {
                    if (!file.exists()) {
                        AssetFileUtils.CopyAssetToFile(SplashScreenActivity.this, file.getName(), file);
                    }

                    JsonReader reader = new JsonReader(new FileReader(file));
                    if (file.getName().equals(DemoConfigFileName)) {
                        mDemoConfigs = new Gson().fromJson(reader, DemoConfigs.class);
                    } else {
                        mDownloadableDemos = new Gson().fromJson(reader, DownloadableDemos.class);
                    }
                }

                // Validate Packages
                if (mDemoConfigs == null || mDemoConfigs.getDemoCategories().isEmpty()) {
                    onFailedToLoadConfig(new Exception("No packages to display found"));
                    return;
                }

                // Convert PackageNames into DemoApps
                for (DemoCategories demoConfig : mDemoConfigs.getDemoCategories()) {
                    List<DemoApp> demoApps = new ArrayList<>();
                    for (String packageName : demoConfig.getPackagesToDisplay()) {
                        try {
                            demoApps.add(new DemoApp(SplashScreenActivity.this, packageName));
                        } catch (NameNotFoundException e) {
                            // Nothing to do here, this exception is just a check
                        }
                    }
                    demoConfig.setDemoApps(demoApps);
                }

                // Strip Downloads that are already on device
                ListIterator<DownloadableDemo> demoIterator = mDownloadableDemos.getDownloadableDemos().listIterator();
                while (demoIterator.hasNext()) {
                    if (FileUtils.isPackageInstalled(this, demoIterator.next().getPackageName())) {
                        demoIterator.remove();
                    }
                }

                // Sort Downloads into Alphabetical Order
                Collections.sort(mDownloadableDemos.getDownloadableDemos(),
                        (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));

                // Return Config to Main Thread
                runOnUiThread(this::onConfigLoaded);
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> onFailedToLoadConfig(e));
            }
        }).start();
    }

    private void onConfigLoaded() {
        // Set Objs
        App.getInstance().setDemoConfigs(mDemoConfigs);
        App.getInstance().setDownloadableDemos(mDownloadableDemos);

        // Calculate Splash Screen delay (if we do our calcs too quickly add a delay for a smoother ux)
        long diff = (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - mStartTime));
        long delay = (2 - diff) * 1000;
        new Handler().postDelayed(() -> {
            // Launch Download Page if available, otherwise launch full demo
            SharedPreferences sharedPreferences = getSharedPreferences(PreferenceCategory, MODE_PRIVATE);
            boolean firstRun = sharedPreferences.getBoolean(FirstRunPref, true);
            if (mDownloadableDemos != null && !mDownloadableDemos.getDownloadableDemos().isEmpty() && firstRun) {
                startActivity(new Intent(SplashScreenActivity.this, InstallDemosActivity.class));
            } else {
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            }

            // Update Prefs
            sharedPreferences.edit().putBoolean(FirstRunPref, false).apply();

            // Remove Activity from Backstack
            finish();
        }, delay);

    }

    private void onFailedToLoadConfig(Exception error) {
        CustomDialog.showCustomDialog(this,
                DialogType.ERROR,
                "Config Error",
                "Failed to load config file: " + error.getMessage(),
                "OK", (dialog, which) -> finish(),
                null, null,
                null, null,
                false);
    }

}