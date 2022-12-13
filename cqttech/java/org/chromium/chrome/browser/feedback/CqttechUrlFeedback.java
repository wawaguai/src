package org.chromium.chrome.browser.feedback;

import android.net.Uri;

import org.chromium.base.ContextUtils;
import org.wwg.common.DeviceFeature;

import java.util.Arrays;
import java.util.Random;

public class CqttechUrlFeedback {
    private static final String FEEDBACK = "https://support.qq.com/product/428777";
    private static final int MAX_NICKNAME_LENGTH = 6;

    private static final String AVATAR_NUMBER = "feedback.avatar.number";
    private static final String AVATAR = "https://txc.gtimg.com/static/v2/img/avatar/";

    public static String getUrl() {
        String uniqueId = DeviceFeature.getShaDeviceId();
        String nickname = "User" + getNickname(uniqueId);
        String avatar = getAvatar();

        return FEEDBACK + "?nickname=" + nickname + "&avatar=" + avatar + "&openid=" + uniqueId;
    }

    private static String getNickname(String deviceID) {
        int length = deviceID.length();
        if (length < MAX_NICKNAME_LENGTH) {
            char[] supplementChars = new char[MAX_NICKNAME_LENGTH - length];
            Arrays.fill(supplementChars, 'x');
            return deviceID + new String(supplementChars);
        } else {
            return deviceID.substring(0, MAX_NICKNAME_LENGTH);
        }
    }

    private static String getAvatar() {
        int avatarNumber = ContextUtils.getAppSharedPreferences().getInt(AVATAR_NUMBER, -1);
        if (avatarNumber == -1) {
            Random random = new Random();
            avatarNumber = random.nextInt(250) + 1;
            ContextUtils.getAppSharedPreferences()
                    .edit()
                    .putInt(AVATAR_NUMBER, avatarNumber)
                    .apply();
        }

        String avatarUrl = AVATAR + avatarNumber + ".svg";
        return Uri.encode(avatarUrl);
    }
}
