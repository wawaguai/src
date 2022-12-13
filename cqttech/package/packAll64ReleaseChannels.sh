cp cqttech/package/channel_constants/mainland.xml chrome/android/java/res_chromium/values/channel_constants.xml
#define channels
channels_array=(
_101
_360
ali
baidu
#huawei
#lenovo
official
oppo
#shjz
#tpy
vivo
xiaomi
#yingyongbao
#zgc
honor
)

for i in ${channels_array[@]}; do
  echo $i
#  awk $i > /media/zcsd/6678-ADAD/kiwiBrowser/src/chrome/android/channel/cqttech_channel.txt
  echo -n $i > chrome/android/channel/cqttech_channel.txt
#  sed -i 's/\n//g' /media/zcsd/6678-ADAD/kiwiBrowser/src/chrome/android/channel/cqttech_channel.txt
  gn gen out/android_arm64_release
  autoninja -C out/android_arm64_release chrome_public_apk
done
