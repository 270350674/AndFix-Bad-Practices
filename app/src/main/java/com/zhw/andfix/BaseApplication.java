package com.zhw.andfix;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.alipay.euler.andfix.patch.PatchManager;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by zhonghw on 2016/3/10.
 */
public class BaseApplication extends Application {

    private static final String TAG = "AndFix";
    public static String VERSION_NAME = "";
    public static PatchManager mPatchManager;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            PackageInfo mPackageInfo = this.getPackageManager().getPackageInfo(
                    this.getPackageName(), 0);
            VERSION_NAME = mPackageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        JPushInterface.init(this);
        initAndFix();
    }


    private void initAndFix() {
        // initialize

        mPatchManager = new PatchManager(this);
        mPatchManager.init(VERSION_NAME);
        Log.d(TAG, "inited.");

        // load patch
        mPatchManager.loadPatch();
//        Log.d(TAG, "apatch loaded.");
    }
}
