package org.chromium.chrome.browser.externalnav;

import static org.chromium.chrome.browser.externalnav.ExternalNavigationHandler.DEBUG;

import android.content.Intent;

import org.chromium.base.Log;
import org.chromium.chrome.browser.UrlConstants;

public class CqttechExternalNavigationHandler {
    private static final String TAG = "CqttechExtNavHandler";
    private static final String TAO_BAO_REGEX = ".*taobao\\.com(/.*)?";
    private static final String BAI_DU_REGEX = ".*baidu\\.com(/.*)?";
    // private static final String JD_REGEX = ".*taobao\\.com(/.*)?";
    // private static final String BILIBILI_REGEX = ".*taobao\\.com(/.*)?";
    //private final ExternalNavigationDelegate mDelegate;

    CqttechExternalNavigationHandler(ExternalNavigationDelegate delegate) {
        //mDelegate = delegate;
    }

    boolean handleNavigationBeforeExternalIntent(
            ExternalNavigationParams params,
            Intent intent, boolean hasBrowserFallbackUrl,
            String browserFallbackUrl,
            boolean canOpenInExternalApp,
            boolean isExternalProtocol,
            boolean canResolveActivity
    ) {
        String url = params.getUrl();
        if (url.contains(UrlConstants.XK_EXTENSION_STORE)) {
            if (DEBUG) Log.i(TAG, "NO_OVERRIDE: Navigation to XK extension web store : URL");
            return true;
        }

        if (url.matches(TAO_BAO_REGEX) || url.matches(BAI_DU_REGEX)) {
            if (DEBUG) Log.i(TAG, "NO_OVERRIDE: tao bao url");
            if (canOpenInExternalApp && isExternalProtocol && canResolveActivity) {
                //note: do not remind user here, remind when the intent to start activity is ready
                return false;
            }

            return true;
        }

        // do not jump to APP store
        if (url.contains("play.google.com") && url.startsWith("market://")) {
            return true;
        }

        // load external protocol url and some one can handle this, just ask user
        if (isExternalProtocol && canResolveActivity) {
            return false;
        } else {
            return true;
        }
    }
}
