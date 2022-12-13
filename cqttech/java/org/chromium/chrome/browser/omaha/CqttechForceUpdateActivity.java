package org.chromium.chrome.browser.omaha;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.base.ContentUriUtils;
import org.chromium.base.ContextUtils;
import org.chromium.chrome.browser.FileProviderHelper;
import org.chromium.chrome.browser.base.BaseCqttechFullscreenActivity;

import org.chromium.chrome.R;
import org.chromium.chrome.browser.media.MediaViewerUtils;

import java.io.File;

public class CqttechForceUpdateActivity extends BaseCqttechFullscreenActivity {
    private static final String TAG = "CqttechForceUpdate";

    public static boolean start(Activity activity) {
        SharedPreferences pref = OmahaBase.getSharedPreferences(activity);
        OmahaBase.VersionConfig config = OmahaBase.getVersionConfig(pref);
        String forceUpdateVersion = OmahaBase.getForceUpdateValidVersion(pref);
        Log.i(TAG, "force Update Version " + forceUpdateVersion + "\nversion config " + config);
        if (TextUtils.isEmpty(config.latestVersion)
                || TextUtils.isEmpty(forceUpdateVersion)
                || !TextUtils.equals(forceUpdateVersion, config.latestVersion)) {
            return false;
        }

        String current = VersionNumberGetter.getInstance().getCurrentlyUsedVersion(activity);
        if (VersionNumberGetter.compareVersion(current, forceUpdateVersion) >= 0) {
            return false;
        }

        Context context = ContextUtils.getApplicationContext();
        File cacheDir = context.getExternalCacheDir();
        File file = new File(cacheDir, OmahaBase.FORCE_DOWNLOAD_PATH);
        if (file.exists()) {
            File downloadFile = new File(file, config.latestVersion + ".apk");
            if (downloadFile.exists()) {
                Intent intent = new Intent();
                intent.setClassName(context, CqttechForceUpdateActivity.class.getName());
                activity.startActivity(intent);
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cqttech_force_update);
        ContentUriUtils.setFileProviderUtil(new FileProviderHelper());
    }

    @Override
    protected void onResume() {
        super.onResume();

        startDigest();
    }

    private void startDigest() {
        SharedPreferences preferences = OmahaBase.getSharedPreferences(this);
        OmahaBase.VersionConfig config = OmahaBase.getVersionConfig(preferences);
        if (!TextUtils.isEmpty(config.latestVersion)) {
            Context context = ContextUtils.getApplicationContext();
            File cacheDir = context.getExternalCacheDir();
            File file = new File(cacheDir, OmahaBase.FORCE_DOWNLOAD_PATH);
            if (file.exists()) {
                File downloadFile = new File(file, config.latestVersion + ".apk");
                setupContent(downloadFile, config);
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    private void setupContent(File file, OmahaBase.VersionConfig config) {
        TextView message = findViewById(R.id.message);
        message.setText(config.updateLog);
        findViewById(R.id.cover).setVisibility(View.GONE);

        findViewById(R.id.agree).setOnClickListener(v -> {
            try {
                String absolutePath = file.getAbsolutePath();
                String ext = MimeTypeMap.getFileExtensionFromUrl(absolutePath);
                String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);

                Uri uri = ApiCompatibilityUtils.getUriForDownloadedFile(file);
                Intent viewIntent = MediaViewerUtils.createViewIntentForUri(
                        uri, type, null, null
                );
                startActivity(viewIntent);
            } catch (Throwable thr) {
                thr.printStackTrace();
            }
        });
    }
}
