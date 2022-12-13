package org.chromium.chrome.browser.externalnav;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.chromium.chrome.R;

import org.chromium.base.Log;
import org.chromium.chrome.browser.ActivityTabProvider;
import org.chromium.chrome.browser.ChromeActivity;
import org.chromium.chrome.browser.snackbar.Snackbar;
import org.chromium.chrome.browser.snackbar.SnackbarManager;
import org.chromium.chrome.browser.tab.Tab;

import java.net.URI;
import java.net.URISyntaxException;

public class CqttechOpenExternalSnackController implements SnackbarManager.SnackbarController {
    private static final String TAG = "cqttechOpenExternal";

    private static final String EXTENSION_REGEX = "chrome://extensions(/.*)?";

    @Nullable
    private Intent mIntent;
    private String mSecondaryDomain;

    public void askUserForOpenExternal(Tab tab, Intent intent) {
        if (dismissIfNeeded(tab, false)) {
            return;
        }

        ChromeActivity activity = tab.getActivity();
        SnackbarManager manager = activity.getSnackbarManager();

        mSecondaryDomain = parserSecondaryDomain(tab);
        mIntent = intent;

        if (manager.isShowing()) {
            return;
        }

        String content = activity.getString(R.string.open_external_title);
        String action = activity.getString(android.R.string.ok);
        manager.showSnackbar(
                Snackbar.make(
                        content, this, Snackbar.TYPE_ACTION, Snackbar.UMA_OPEN_EXTERNAL
                ).setDuration(5000).setSingleLine(false).setAction(action, new Runnable() {
                    @Override
                    public void run() {
                        if (mIntent != null) {
                            activity.startActivity(mIntent);
                        }
                    }
                })
        );
    }

    public boolean dismissIfNeeded(Tab tab, boolean forceDismiss) {
        ChromeActivity activity = tab.getActivity();
        if (activity == null) {
            Log.e(TAG, "activity is null, do nothing!");
            return true;
        }

        SnackbarManager manager = activity.getSnackbarManager();

        if (forceDismiss) {
            dismissInternal(manager);
            return true;
        }

        ActivityTabProvider activityTabProvider = activity.getActivityTabProvider();
        Tab activityTab = activityTabProvider.getActivityTab();
        if (activityTab != tab) {
            dismissInternal(manager);
            Log.w(TAG, "tab not active, dismiss snack bars!");
            return true;
        }

        String domain = parserSecondaryDomain(tab);
        if (!TextUtils.isEmpty(mSecondaryDomain) && !TextUtils.equals(domain, mSecondaryDomain)) {
            dismissInternal(manager);
            Log.w(TAG, "secondary domain changed, dismiss snack bars!");
            return true;
        }

        return false;
    }

    boolean interceptIgnoreExtension(Tab tab, String url) {
        if (tab.isIncognito() && isExtensionUrl(url)) {
            ChromeActivity activity = tab.getActivity();
            SnackbarManager manager = activity.getSnackbarManager();

            if (manager.isShowing()) {
                return true;
            }

            String content = activity.getString(R.string.open_extension_title);
            manager.showSnackbar(
                    Snackbar.make(
                            content, this, Snackbar.TYPE_NOTIFICATION, Snackbar.UMA_OPEN_EXTENSION
                    ).setSingleLine(false)
            );

            return true;
        }

        return false;
    }

    private void dismissInternal(SnackbarManager manager) {
        mIntent = null;
        mSecondaryDomain = "";
        manager.dismissSnackbars(this);
    }

    @Override
    public void onAction(Object actionData) {
        if (actionData instanceof Runnable) {
            ((Runnable) actionData).run();
        }

        mIntent = null;
        mSecondaryDomain = "";
    }

    @Override
    public void onDismissNoAction(Object actionData) {
        mIntent = null;
        mSecondaryDomain = "";
    }

    private String parserSecondaryDomain(Tab tab) {
        String url = tab.getUrl();
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            if (TextUtils.isEmpty(host)) {
                return "";
            }

            String[] splits = host.split("\\.");
            int length = splits.length;
            if (length > 2) {
                return splits[length - 2] + "." + splits[length - 1];
            } else {
                return host;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static boolean isExtensionUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }

        return url.matches(EXTENSION_REGEX);
    }
}
