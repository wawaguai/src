package org.chromium.chrome.browser.suggestions.nav.entities;

import android.support.annotation.IntDef;

@IntDef({GroupId.INVALID, GroupId.VALID_22_1})
public @interface GroupId {
    int INVALID = 0;
    int VALID_22_1 = 1;
}
