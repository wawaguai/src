package org.wwg.common;

import android.content.Context;
import android.text.TextUtils;

import org.chromium.chrome.browser.identity.CqttechIdentificationGenerator;

class DeviceInfo {
    private static final String TAG = "DeviceInfo";

    private static String sDeviceId = "";
    private static String sMac = "";

    public static String getDeviceId() {
        return sDeviceId;
    }

    public static String getShaDeviceId() {
        if (!TextUtils.isEmpty(sDeviceId)) {
            return EncryptUtil.sha256Encrypt(sDeviceId);
        }

        return sDeviceId;
    }

    public static String getMac() {
        return sMac;
    }

    public static void init(Context context) {
        if (TextUtils.isEmpty(sDeviceId)) {
            initWithUUID();
        }
    }

    private static void initWithUUID() {
        sDeviceId = CqttechIdentificationGenerator.getInstance().getUniqueId();
        sMac = "";
    }

//    private static void initWithUM(Context context) {
//        String[] testDeviceInfo = UMConfigure.getTestDeviceInfo(context);
//        if (testDeviceInfo.length > 1) {
//            sDeviceId = testDeviceInfo[0];
//            sMac = testDeviceInfo[1];
//            Log.i(TAG, "UM device id : " + testDeviceInfo[0] + " UM device MAC : " + testDeviceInfo[1]);
//
//            if (TextUtils.isEmpty(sDeviceId)) {
//                sDeviceId = "";
//            }
//
//            if (TextUtils.isEmpty(sMac)) {
//                sMac = "";
//            }
//        }
//    }

}
