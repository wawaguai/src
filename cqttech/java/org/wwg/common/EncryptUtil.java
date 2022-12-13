package org.wwg.common;

import android.text.TextUtils;
import android.util.Log;

import org.conscrypt.io.IoUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Locale;

public class EncryptUtil {
    private static final String TAG = "EncryptUtil";

    public static String digestFile(File file) {
        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
            return digestInputStream(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(input);
        }

        return "";
    }

    public static String digestInputStream(InputStream input) {
        DigestInputStream digestInputStream = null;

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            digestInputStream = new DigestInputStream(input, messageDigest);
            byte[] buffer = new byte[256 * 1024];
            //noinspection StatementWithEmptyBody
            while (digestInputStream.read(buffer) > 0);
            byte[] digest = messageDigest.digest();
            String result = bytes2Hex(digest);
            if (!TextUtils.isEmpty(result)) {
                assert result != null;
                return result.toLowerCase(Locale.getDefault());
            }
        } catch (Throwable thr) {
            thr.printStackTrace();
        } finally {
            IoUtils.closeQuietly(digestInputStream);
        }

        return "";
    }

    public static String shortSha(String value) {
        short result = 16; // 取一个固定非零值，最大可能避免重复
        String encrypt = sha256Encrypt(value);
        char[] chars = encrypt.toCharArray();
        for (char ch : chars) {
            result += Short.valueOf(String.valueOf(ch), 16);
        }

        return String.valueOf(result);
    }

    public static String sha256Encrypt(String encryptMsg) {
        String result = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(encryptMsg.getBytes(StandardCharsets.UTF_8));
            result = bytes2Hex(messageDigest.digest());
        } catch (Exception e) {
            Log.e(TAG, "shaEncrypt error = " + e);
        }
        return result;
    }

    private static String bytes2Hex(byte[] bts) {
        if ((bts == null) || (bts.length == 0)) {
            Log.e(TAG, "bytes2Hex bts is empty");
            return null;
        }
        StringBuilder des = new StringBuilder();
        String tmp;
        for (byte bt : bts) {
            tmp = Integer.toHexString(bt & 0xFF);
            if (tmp.length() == 1) {
                des.append("0");
            }
            des.append(tmp);
        }
        return des.toString();
    }
}
