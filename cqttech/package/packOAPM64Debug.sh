cp cqttech/package/channel_constants/mainland.xml chrome/android/java/res_chromium/values/channel_constants.xml
echo -n "oapm" > chrome/android/channel/cqttech_channel.txt
gn gen out/android_arm64_debug
autoninja -C out/android_arm64_debug chrome_public_apk
