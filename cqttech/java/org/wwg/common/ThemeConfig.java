package org.wwg.common;

import static org.chromium.chrome.browser.accessibility.NightModePrefs.PREF_USER_NIGHT_MODE_ENABLED;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.text.TextUtils;

import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.base.ContextUtils;

import org.chromium.chrome.R;

public class ThemeConfig implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String PREF_ACTIVE_THEME = "active_theme";
    private static final String DIAMOND_BLACK_THEME = "Diamond Black";

    private static volatile ThemeConfig INSTANCE;

    public static ThemeConfig getInstance() {
        if (INSTANCE == null) {
            synchronized (ThemeConfig.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ThemeConfig();
                }
            }
        }

        return INSTANCE;
    }

    private boolean mIsDark;
    private ThemeConfig() {
        SharedPreferences preferences = ContextUtils.getAppSharedPreferences();
        updateTheme(preferences);

        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void updateTheme(SharedPreferences preferences) {
        mIsDark = preferences.getBoolean(PREF_USER_NIGHT_MODE_ENABLED, false) ||
                preferences.getString(PREF_ACTIVE_THEME, "").equals(DIAMOND_BLACK_THEME);
    }

    public boolean isDark() {
        return mIsDark;
    }

    @ColorInt
    public int getMainTextColor() {
        Context context = ContextUtils.getApplicationContext();
        Resources resources = context.getResources();
        if (isDark()) {
            return resources.getColor(R.color.main_text_dark_color);
        } else {
            return resources.getColor(R.color.main_text_light_color);
        }
    }

    public ColorStateList getMainVectorColorStateList() {
        Context context = ContextUtils.getApplicationContext();
        Resources resources = context.getResources();
        if (isDark()) {
            return ApiCompatibilityUtils.getColorStateList(resources, R.color.light_mode_tint);
        } else {
            return ApiCompatibilityUtils.getColorStateList(resources, R.color.dark_mode_tint);
        }
    }

    @ColorInt
    public int getMainBackgroundColor() {
        Context context = ContextUtils.getApplicationContext();
        Resources resources = context.getResources();
        if (isDark()) {
            return resources.getColor(R.color.main_background_dark_color);
        } else {
            return resources.getColor(R.color.main_background_light_color);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (TextUtils.equals(PREF_USER_NIGHT_MODE_ENABLED, key) ||
                TextUtils.equals(PREF_ACTIVE_THEME, key)) {
            updateTheme(sharedPreferences);
        }
    }
}
