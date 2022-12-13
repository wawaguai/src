cp cqttech/package/channel_constants/overseas.xml chrome/android/java/res_chromium/values/channel_constants.xml
echo -n "honor" > chrome/android/channel/cqttech_channel.txt
gn gen out/android_arm64_release
autoninja -C out/android_arm64_release chrome_public_apk
