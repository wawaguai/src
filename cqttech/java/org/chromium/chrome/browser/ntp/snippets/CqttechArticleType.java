package org.chromium.chrome.browser.ntp.snippets;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({CqttechArticleType.TOP,
        CqttechArticleType.GRADE_A,
        CqttechArticleType.GRADE_B})
@Retention(RetentionPolicy.SOURCE)
public @interface CqttechArticleType {
    /**
     * 置顶资讯
     */
    int TOP = 0;

    /**
     * 一般资讯
     */
    int GRADE_A = 1;
    int GRADE_B = 2;
}
