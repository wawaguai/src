package org.wwg.common;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DisplayUtils {

    public static int getScreenMetricsWidth(Context context) {
        final DisplayMetrics outMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getScreenMetricsHeight(Context context) {
        final DisplayMetrics outMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }
}
