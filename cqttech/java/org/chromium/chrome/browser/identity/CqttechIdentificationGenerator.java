package org.chromium.chrome.browser.identity;

import android.content.Context;
import android.text.TextUtils;

import org.chromium.base.ContextUtils;

public class CqttechIdentificationGenerator {
    private static final String ID_SALT = "DeviceInfoSalt";
    private static final String UUID_PREF_KEY = "cqttech.device.identification";

    private static volatile CqttechIdentificationGenerator INSTANCE;
    public static CqttechIdentificationGenerator getInstance() {
        if (INSTANCE == null) {
            synchronized (CqttechIdentificationGenerator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CqttechIdentificationGenerator();
                }
            }
        }

        return INSTANCE;
    }

    private final String mUniqueId;
    private CqttechIdentificationGenerator() {
        Context context = ContextUtils.getApplicationContext();
        SettingsSecureBasedIdentificationGenerator settingGenerator
                = new SettingsSecureBasedIdentificationGenerator(context);
        String uniqueId = settingGenerator.getUniqueId(ID_SALT);
        if (TextUtils.isEmpty(uniqueId)) {
            UuidBasedUniqueIdentificationGenerator uuidGenerator
                    = new UuidBasedUniqueIdentificationGenerator(context, UUID_PREF_KEY);
            uniqueId = uuidGenerator.getUniqueId(ID_SALT);
        }

        mUniqueId = uniqueId;
    }

    public String getUniqueId() {
        return mUniqueId;
    }
}
