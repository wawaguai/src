package org.wwg.umeng;

import android.content.Context;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import org.chromium.base.BuildConfig;
import org.wwg.common.DeviceFeature;

import java.util.Map;

/**
 * 处理事件埋点
 */
public class UmengProxy {
    private static final String TAG = "UmengProxy";
    private static final String OAPM = "oapm";

    /**
     * 处理简单点击事件埋点
     *
     * @param context 上下文
     * @param key     事件key
     */
    public static void onEvent(Context context, String key) {
        if (isDisable()) {
            return;
        }

        MobclickAgent.onEvent(context.getApplicationContext(), key);
    }

    public static void onObjectEvent(Context context, String key, Map<String, Object> value) {
        if (isDisable()) {
            return;
        }

        MobclickAgent.onEventObject(context.getApplicationContext(), key, value);
    }

    // 初始化友盟
    public static void init(Context context, String channel) {
        if (isDisable()) {
            return;
        }
        UMConfigure.init(context, null, channel, UMConfigure.DEVICE_TYPE_PHONE, null);
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        if (OAPM.equals(channel)) {
            UMConfigure.setLogEnabled(true);
            String[] testDeviceInfo = UMConfigure.getTestDeviceInfo(context);
            if (testDeviceInfo.length > 1) {
                Log.i(TAG, "UM device id : " + testDeviceInfo[0] + " UM device MAC : " + testDeviceInfo[1]);
            }
        }
    }

    public static void preInit(Context context, String channel) {
        if (isDisable()) {
            return;
        }

        UMConfigure.preInit(context, null, channel);
    }

    private static boolean isDisable() {
        String channel = DeviceFeature.getBuildFlavor();
        return BuildConfig.DCHECK_IS_ON && OAPM.equals(channel);
    }
}
