package org.chromium.chrome.browser.omaha;

import android.content.Context;
import android.os.Build;

import com.google.gson.annotations.SerializedName;

import org.wwg.common.DeviceFeature;
import org.wwg.network.base.ConfigServerConstant;

public abstract class CqttechOmahaDelegate extends OmahaDelegateBase {

    public CqttechOmahaDelegate(Context context) {
        super(context);
    }

    @Override
    protected RequestGenerator createRequestGenerator(Context context) {
        return new CqttechRequestGenerator(context);
    }

    static class CqttechRequestGenerator extends RequestGenerator {
        private static final String URL = "http://update-xkapp.xkbrowser.com/api/v1/update/check";
        private static final String TEST_URL = "http://10.10.30.40:9913/api/v1/update/check";

        CqttechRequestGenerator(Context context) {
            super(context);
        }

        @Override
        public String getServerUrl() {
            return URL + getQuery();
        }

        private String getQuery() {
            return "?version=" +
                    DeviceFeature.getAppVersionName() +
                    "&union=" +
                    DeviceFeature.getFlavorValue() +
                    "&os=" +
                    Build.VERSION.SDK_INT +
                    "&arch=" +
                    DeviceFeature.getCPUArch() +
                    "&bit_type=" +
                    (DeviceFeature.is64Bit() ? 64 : 32) +
                    "&appid=" +
                    ConfigServerConstant.APP_ID +
                    "&id=" +
                    DeviceFeature.getShaDeviceId();
        }

        // --------------- unused code start ------------->

        @Override
        protected String getAppIdHandset() {
            return "";
        }

        @Override
        protected String getAppIdTablet() {
            return "";
        }

        @Override
        protected String getBrand() {
            return "";
        }

        @Override
        protected String getClient() {
            return "";
        }
    }

    public static class CqttechVersionConfig {
        @SerializedName("code")
        public String code;
        @SerializedName("msg")
        public String message;
        @SerializedName("data")
        public Data data;

        public static class Data {
            @SerializedName("update_id")
            public String updateId;
            @SerializedName("version_name")
            public String versionName;
            @SerializedName("version_num")
            public String versionNumber;
            @SerializedName("lnk")
            public String link;
            @SerializedName("md5")
            public String md5;
            @SerializedName("file_size")
            public String size;
            @SerializedName("crc32")
            public String crc;
            @SerializedName("update_type")
            public String updateType;
            @SerializedName("startup_type")
            public String startupType;
            @SerializedName("update_log")
            public String log;

            @Override
            public String toString() {
                return "Data{" +
                        "updateId='" + updateId + '\'' +
                        ", versionName='" + versionName + '\'' +
                        ", versionNumber='" + versionNumber + '\'' +
                        ", link='" + link + '\'' +
                        ", md5='" + md5 + '\'' +
                        ", size='" + size + '\'' +
                        ", crc='" + crc + '\'' +
                        ", updateType='" + updateType + '\'' +
                        ", startupType='" + startupType + '\'' +
                        ", log='" + log + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "CqttechVersionConfig{" +
                    "code='" + code + '\'' +
                    ", message='" + message + '\'' +
                    ", data=" + data.toString() +
                    '}';
        }
    }
}
