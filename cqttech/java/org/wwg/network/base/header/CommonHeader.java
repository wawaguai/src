package org.wwg.network.base.header;

import android.util.ArrayMap;

import org.wwg.common.DeviceFeature;
import org.wwg.common.ThemeConfig;

import java.util.Locale;
import java.util.Map;

public class CommonHeader {
    private static final String DEVICE_ID = "deviceId";
    private static final String MAC_ADDRESS = "macAddress";

    private static final String ANDROID_VERSION = "androidVersion";
    private static final String BRAND = "brand";
    private static final String PRODUCT = "product";

    private static final String LANGUAGE = "language";

    private static final String APP_VERSION = "appVersion";
    private static final String BUILD_FLAVOR = "buildFlavor";
    private static final String SKIN_TYPE = "skinType";

    public static Map<String, String> getCommonRequestHeader() {
        Map<String, String> headers = new ArrayMap<>();
        headers.put(DEVICE_ID, DeviceFeature.getShaDeviceId());
        headers.put(MAC_ADDRESS, DeviceFeature.getMac());

        headers.put(ANDROID_VERSION, DeviceFeature.getAndroidVersion());
        headers.put(BRAND, DeviceFeature.getBrand());
        headers.put(PRODUCT, DeviceFeature.getProduct());

        headers.put(LANGUAGE, getLanguage());
        headers.put(APP_VERSION, DeviceFeature.getAppVersion());
        headers.put(BUILD_FLAVOR, DeviceFeature.getBuildFlavor());

        headers.put(SKIN_TYPE, String.valueOf(getSkinType()));

        return headers;
    }

    public static Map<String, String> getVersionRequestHeader() {
        Map<String, String> headers = new ArrayMap<>();
        headers.put(DEVICE_ID, DeviceFeature.getShaDeviceId());
        headers.put(MAC_ADDRESS, DeviceFeature.getMac());

        headers.put(ANDROID_VERSION, DeviceFeature.getAndroidVersion());
        headers.put(BRAND, DeviceFeature.getBrand());
        headers.put(PRODUCT, DeviceFeature.getProduct());

        headers.put(LANGUAGE, getLanguage());
        headers.put(APP_VERSION, DeviceFeature.getAppVersionName());
        headers.put(BUILD_FLAVOR, DeviceFeature.getBuildFlavor());
        headers.put(SKIN_TYPE, String.valueOf(getSkinType()));

        return headers;
    }

    private static int getSkinType() {
        boolean nightEnable = ThemeConfig.getInstance().isDark();
        return nightEnable ? 1 : 0;
    }

    private static String getLanguage() {
        Locale locale = Locale.getDefault();
        return locale.toString();
    }
}
