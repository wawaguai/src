package org.wwg.common;

import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MIUIUtils {
    private static class Holder {
        private static final MIUIUtils INSTANCE = new MIUIUtils();
    }

    private final Disposable mDisposable;
    private boolean mIsMIUI = false;

    private MIUIUtils() {
        mDisposable = Single.just(true)
                .map((Function<Boolean, Boolean>) in -> getProp())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        success -> mIsMIUI = success,
                        Throwable::printStackTrace
                );
    }

    public static MIUIUtils getInstance() {
        return Holder.INSTANCE;
    }

    public boolean isMIUI() {
        if (!mIsMIUI) {
            return isXiaomi(DeviceFeature.getBrand());
        }

        return true;
    }

    private boolean getProp() {
        String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
        String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
        String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

        Properties prop = new Properties();
        boolean isMIUI;
        try {
            prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
        } catch (IOException e) {
            e.printStackTrace();

            return isXiaomi(Build.BRAND);
        }
        isMIUI = prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        return isMIUI;
    }

    public void destroy() {
        mDisposable.dispose();
    }

    private boolean isXiaomi(String name) {
        return "xiaomi".equalsIgnoreCase(name) || "redmi".equalsIgnoreCase(name);
    }
}
