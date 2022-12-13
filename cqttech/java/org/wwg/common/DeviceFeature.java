package org.wwg.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import org.chromium.base.ContextUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class DeviceFeature {
    private static final int START = 30000;

    public static final int PROCESSOR_ARCHITECTURE_ARM = 5;
    public static final int PROCESSOR_ARCHITECTURE_ARM64 = 12;

    public static final String CHANNEL_101_NAME = "_101";
    public static final int CHANNEL_101_VALUE = START + 1;
    public static final String CHANNEL_360_NAME = "_360";
    public static final int CHANNEL_360_VALUE = START + 2;
    public static final String CHANNEL_ALI_NAME = "ali";
    public static final int CHANNEL_ALI_VALUE = START + 3;
    public static final String CHANNEL_BAIDU_NAME = "baidu";
    public static final int CHANNEL_BAIDU_VALUE = START + 4;
    public static final String CHANNEL_HUAWEI_NAME = "huawei";
    public static final int CHANNEL_HUAWEI_VALUE = START + 5;
    public static final String CHANNEL_LENOVO_NAME = "lenovo";
    public static final int CHANNEL_LENOVO_VALUE = START + 6;
    public static final String CHANNEL_OFFICIAL_NAME = "official";
    public static final int CHANNEL_OFFICIAL_VALUE = START + 7;
    public static final String CHANNEL_OPPO_NAME = "oppo";
    public static final int CHANNEL_OPPO_VALUE = START + 8;
    public static final String CHANNEL_SHJZ_NAME = "shjz";
    public static final int CHANNEL_SHJZ_VALUE = START + 9;
    public static final String CHANNEL_TPY_NAME = "tpy";
    public static final int CHANNEL_TPY_VALUE = START + 10;
    public static final String CHANNEL_VIVO_NAME = "vivo";
    public static final int CHANNEL_VIVO_VALUE = START + 11;
    public static final String CHANNEL_XIAOMI_NAME = "xiaomi";
    public static final int CHANNEL_XIAOMI_VALUE = START + 12;
    public static final String CHANNEL_YYB_NAME = "yingyongbao";
    public static final int CHANNEL_YYB_VALUE = START + 13;
    public static final String CHANNEL_ZGC_NAME = "zgc";
    public static final int CHANNEL_ZGC_VALUE = START + 14;
    public static final String CHANNEL_HONOR_NAME = "honor";
    public static final int CHANNEL_HONOR_VALUE = START + 15;

    private static final Map<String, Integer> sChannels = new ArrayMap<>();
    static {
        sChannels.put(CHANNEL_101_NAME, CHANNEL_101_VALUE);
        sChannels.put(CHANNEL_360_NAME, CHANNEL_360_VALUE);
        sChannels.put(CHANNEL_ALI_NAME, CHANNEL_ALI_VALUE);
        sChannels.put(CHANNEL_BAIDU_NAME, CHANNEL_BAIDU_VALUE);
        sChannels.put(CHANNEL_HUAWEI_NAME, CHANNEL_HUAWEI_VALUE);
        sChannels.put(CHANNEL_LENOVO_NAME, CHANNEL_LENOVO_VALUE);
        sChannels.put(CHANNEL_OFFICIAL_NAME, CHANNEL_OFFICIAL_VALUE);
        sChannels.put(CHANNEL_OPPO_NAME, CHANNEL_OPPO_VALUE);
        sChannels.put(CHANNEL_SHJZ_NAME, CHANNEL_SHJZ_VALUE);
        sChannels.put(CHANNEL_TPY_NAME, CHANNEL_TPY_VALUE);
        sChannels.put(CHANNEL_VIVO_NAME, CHANNEL_VIVO_VALUE);
        sChannels.put(CHANNEL_XIAOMI_NAME, CHANNEL_XIAOMI_VALUE);
        sChannels.put(CHANNEL_YYB_NAME, CHANNEL_YYB_VALUE);
        sChannels.put(CHANNEL_ZGC_NAME, CHANNEL_ZGC_VALUE);
        sChannels.put(CHANNEL_HONOR_NAME, CHANNEL_HONOR_VALUE);
    }

    private static final String TAG = "DeviceFeature";
    private final static String OAPM = "oapm";

    private static String sAndroidVersion;
    private static String sBrand;
    private static String sProduct;
    private static String sAppVersionName;
    private static int sAppVersionCode;
    private static String sBuildFlavor;
    private static String sPackageName;

    public static void initFeature(Context context, String flavor) {
        sAndroidVersion = String.valueOf(Build.VERSION.SDK_INT);
        sBrand = Build.MANUFACTURER;
        sProduct = Build.PRODUCT;

        Log.d(TAG, "initFeature: sBuildFlavor = " + sBuildFlavor);
        if (TextUtils.isEmpty(flavor)) {
            sBuildFlavor = "oapm";
        } else {
            sBuildFlavor = flavor;
        }

        initPackageVersion(context);
        DeviceInfo.init(context);
        sPackageName = context.getPackageName();

        String charset = StandardCharsets.UTF_8.name();
        try {
            sAndroidVersion = URLEncoder.encode(sAndroidVersion, charset);
        } catch (Exception e) {
            sAndroidVersion = "";
            e.printStackTrace();
        }

        try {
            sBrand = URLEncoder.encode(sBrand, charset);
        } catch (Exception e) {
            sBrand = "";
            e.printStackTrace();
        }

        try {
            sProduct = URLEncoder.encode(sProduct, charset);
        } catch (Exception e) {
            sProduct = "";
            e.printStackTrace();
        }

        try {
            sAppVersionName = URLEncoder.encode(sAppVersionName, charset);
        } catch (Exception e) {
            sAppVersionName = "";
            e.printStackTrace();
        }

        try {
            sBuildFlavor = URLEncoder.encode(sBuildFlavor, charset);
        } catch (Exception e) {
            sBuildFlavor = "";
            e.printStackTrace();
        }

        try {
            sPackageName = URLEncoder.encode(sPackageName, charset);
        } catch (Exception e) {
            sPackageName = "";
            e.printStackTrace();
        }
    }

    @Deprecated
    public static String getDeviceId() {
        return DeviceInfo.getDeviceId();
    }

    public static String getShaDeviceId() {
        String shaDeviceId = DeviceInfo.getShaDeviceId();
        if (shaDeviceId.length() > 32) {
            shaDeviceId = shaDeviceId.substring(0, 32);
        }

        return shaDeviceId;
    }

    public static String getMac() {
        return DeviceInfo.getMac();
    }

    public static String getAndroidVersion() {
        return sAndroidVersion;
    }

    public static String getBrand() {
        return sBrand;
    }

    public static String getProduct() {
        return sProduct;
    }

    /**
     * 这里返回的是 version name
     */
    public static String getAppVersion() {
        return String.valueOf(sAppVersionCode);
    }

    public static int getAppVersionCode() {
        return sAppVersionCode;
    }

    public static String getAppVersionName() {
        return sAppVersionName;
    }

    public static String getBuildFlavor() {
        return sBuildFlavor;
    }

    public static int getFlavorValue() {
        String flavor = DeviceFeature.sBuildFlavor;
        if (TextUtils.isEmpty(flavor)) {
            return -1;
        }

        Integer integer = sChannels.get(flavor);
        if (integer == null) {
            return -1;
        }

        return integer;
    }

    public static boolean isPackageForTest() {
        return OAPM.equals(sBuildFlavor);
    }

    public static String getPackageName() {
        return sPackageName;
    }

    public static String getAppId() {
        String in = sPackageName;
        if (TextUtils.isEmpty(in)) {
            in = ContextUtils.getApplicationContext().getPackageName();
        }

        return EncryptUtil.shortSha(in);
    }

    public static boolean is64Bit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Process.is64Bit();
        }

        String property = System.getProperty("ro.product.cpu.abi");
        if (TextUtils.isEmpty(property)) {
            return false;
        }

        return property.contains("arm64");
    }

    public static int getCPUArch() {
        if (is64Bit()) {
            return PROCESSOR_ARCHITECTURE_ARM64;
        }

        return PROCESSOR_ARCHITECTURE_ARM;
    }

    private static void initPackageVersion(Context context) {
        sAppVersionName = "";
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getApplicationContext().getPackageName(), 0);
            if (packageInfo != null) {
                sAppVersionName = packageInfo.versionName;
                sAppVersionCode = packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            // Do nothing, versionCode stays -1
        }
    }
}
