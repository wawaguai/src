package org.chromium.chrome.browser.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.chrome.browser.util.ColorUtils;
import org.wwg.common.ThemeConfig;

public class BaseCqttechFullscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initSystemUIState();
        updateStatusBarColor(getStatusBarColor(), false);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
    }

    protected void initSystemUIState() {
        if (isFinishing()) {
            return;
        }

        int statusBarColor = getStatusBarColor();
        boolean supportsDarkStatusIcons = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        Window window = adapterNavigation();

        View root = window.getDecorView().getRootView();
        if (supportsDarkStatusIcons) {

            boolean needsDarkStatusBarIcons = !ThemeConfig.getInstance().isDark();
            ApiCompatibilityUtils.setSystemUIColor(root, needsDarkStatusBarIcons);
        } else {
            statusBarColor = ColorUtils.getDarkenedColorForStatusBar(statusBarColor);
        }

        ApiCompatibilityUtils.setStatusBarColor(window, statusBarColor);

        int systemUIState = root.getSystemUiVisibility();
        systemUIState |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        systemUIState |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        root.setSystemUiVisibility(systemUIState);
    }

    protected @ColorInt int getStatusBarColor() {
        return ThemeConfig.getInstance().getMainBackgroundColor();
    }

    protected void updateStatusBarColor(@ColorInt int color, boolean forceDark) {
        Window window = getWindow();
        boolean supportsDarkStatusIcons = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        if (supportsDarkStatusIcons) {
            boolean needsDarkStatusBarIcons;
            if (forceDark) {
                needsDarkStatusBarIcons = true;
            } else {
                needsDarkStatusBarIcons = !ColorUtils.shouldUseLightForegroundOnBackground(color);
            }

            View root = window.getDecorView().getRootView();
            ApiCompatibilityUtils.setSystemUIColor(root, needsDarkStatusBarIcons);
        } else {
            color = ColorUtils.getDarkenedColorForStatusBar(color);
        }

        ApiCompatibilityUtils.setStatusBarColor(window, color);
    }

    protected Window adapterNavigation() {
        Window window = getWindow();
        int mainBackgroundColor = ThemeConfig.getInstance().getMainBackgroundColor();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setNavigationBarColor(mainBackgroundColor);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.setNavigationBarDividerColor(mainBackgroundColor);
        }

        return window;
    }
}
