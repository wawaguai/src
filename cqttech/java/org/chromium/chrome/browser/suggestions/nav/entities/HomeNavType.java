package org.chromium.chrome.browser.suggestions.nav.entities;

import android.support.annotation.IntDef;

@IntDef({HomeNavType.LINK, HomeNavType.ACTION})
public @interface HomeNavType {
    int LINK = 1;
    int ACTION = 2;
}
