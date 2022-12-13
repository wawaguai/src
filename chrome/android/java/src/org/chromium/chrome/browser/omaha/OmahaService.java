// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chrome.browser.omaha;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.IntentService;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;

import org.chromium.base.ContextUtils;
import org.chromium.base.Log;
import org.chromium.base.ThreadUtils;
import org.chromium.chrome.browser.download.DownloadController;
import org.chromium.components.background_task_scheduler.BackgroundTask;
import org.chromium.components.background_task_scheduler.BackgroundTaskSchedulerFactory;
import org.chromium.components.background_task_scheduler.TaskIds;
import org.chromium.components.background_task_scheduler.TaskInfo;
import org.chromium.components.background_task_scheduler.TaskParameters;
import org.conscrypt.io.IoUtils;
import org.wwg.common.EncryptUtil;
import org.wwg.network.base.OKHttpUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Manages scheduling and running of the Omaha client code.
 * Delegates out to either an {@link IntentService} or {@link JobService}, as necessary.
 */
public class OmahaService extends OmahaBase implements BackgroundTask {
    private static final String TAG = "omaha";

    private static class OmahaClientDelegate extends CqttechOmahaDelegate {
        public OmahaClientDelegate(Context context) {
            super(context);
        }

        @Override
        public void scheduleService(long currentTimestampMs, long nextTimestampMs) {
            if (Build.VERSION.SDK_INT < OmahaBase.MIN_API_JOB_SCHEDULER) {
                getScheduler().createAlarm(OmahaClient.createIntent(getContext()), nextTimestampMs);
                Log.i(TAG, "Scheduled using AlarmManager and IntentService");
            } else {
                final long delay = nextTimestampMs - currentTimestampMs;
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (scheduleJobService(getContext(), delay)) {
                            Log.i(TAG, "Scheduled using JobService");
                        } else {
                            Log.e(TAG, "Failed to schedule job");
                        }
                    }
                });
            }
        }
    }

    private static final Object DELEGATE_LOCK = new Object();
    private static OmahaService sInstance;

    @Nullable
    public static OmahaService getInstance(Context context) {
        synchronized (DELEGATE_LOCK) {
            if (sInstance == null) sInstance = new OmahaService(context);
            return sInstance;
        }
    }

    private AsyncTask<Void, Void, Void> mJobServiceTask;

    /** Used only by {@link BackgroundTaskScheduler}. */
    public OmahaService() {
        this(ContextUtils.getApplicationContext());
    }

    private OmahaService(Context context) {
        super(new OmahaClientDelegate(context));
    }

    /**
     * Trigger the {@link BackgroundTaskScheduler} immediately.
     * Must only be called by {@link OmahaBase#onForegroundSessionStart}.
     */
    static void startServiceImmediately(Context context) {
        if (Build.VERSION.SDK_INT < OmahaBase.MIN_API_JOB_SCHEDULER) {
            context.startService(OmahaClient.createIntent(context));
        } else {
            scheduleJobService(context, 0);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public boolean onStartTask(
            Context context, TaskParameters parameters, final TaskFinishedCallback callback) {
        mJobServiceTask = new AsyncTask<Void, Void, Void>() {
            @Override
            public Void doInBackground(Void... params) {
                run();
                return null;
            }

            @Override
            public void onPostExecute(Void result) {
                callback.taskFinished(false);
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        return false;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public boolean onStopTask(Context context, TaskParameters taskParameters) {
        if (mJobServiceTask != null) {
            mJobServiceTask.cancel(false);
            mJobServiceTask = null;
        }
        return false;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void reschedule(Context context) {
        // Needs appropriate implementation.
    }

    /**
     * Schedules the Omaha code to run at the given time.
     * @param context Context to use.
     * @param delayMs How long to wait until the job should be triggered.
     */
    @TargetApi(Build.VERSION_CODES.M)
    static boolean scheduleJobService(Context context, long delayMs) {
        long latency = Math.max(0, delayMs);

        TaskInfo taskInfo = TaskInfo.createOneOffTask(TaskIds.OMAHA_JOB_ID, OmahaService.class,
                                            latency, latency)
                                    .build();
        return BackgroundTaskSchedulerFactory.getScheduler().schedule(context, taskInfo);
    }

    // actually using get do request
    @Override
    protected boolean generateAndPostRequest(long currentTimestamp, String sessionID) {
        boolean succeeded = false;
        try {
            // Send the request to the server & wait for a response.
            String response = getRequest();

            // Parse out the response.
            Gson gson = new Gson();
            CqttechOmahaDelegate.CqttechVersionConfig config = gson.fromJson(
                    response, CqttechOmahaDelegate.CqttechVersionConfig.class
            );

            if (!TextUtils.isEmpty(config.data.versionNumber) &&
                    !TextUtils.isEmpty(config.data.link)) {
                mVersionConfig = new VersionConfig(
                        config.data.versionNumber,
                        config.data.link,
                        config.data.log,
                        config.data.md5
                );

                boolean forceUpdate = TextUtils.equals("2", config.data.updateType); // 2 means force update; 1 otherwise
                Context context = ContextUtils.getApplicationContext();
                String current = VersionNumberGetter.getInstance().getCurrentlyUsedVersion(context);
                if (VersionNumberGetter.compareVersion(current, config.data.versionNumber) >= 0) {
                    forceUpdate = false;
                }

                if (forceUpdate) {
                    mForceUpdate = config.data.versionNumber;
                }

                succeeded = true;

                Log.i(TAG, "check update success: ", config.toString());

                if (forceUpdate) {
                    tryDownloadAPK(mVersionConfig);
                }
            }
        } catch (RequestFailureException e) {
            Log.e(TAG, "Failed to contact server: ", e);
        }
        return onResponseReceived(succeeded);
    }

    private String getRequest() throws RequestFailureException {
        String response = null;

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = createConnection();
            response = OmahaBase.sendRequestToServer(urlConnection, null);
        } catch (IllegalAccessError e) {
            throw new RequestFailureException("Caught an IllegalAccessError:", e);
        } catch (IllegalArgumentException e) {
            throw new RequestFailureException("Caught an IllegalArgumentException:", e);
        } catch (IllegalStateException e) {
            throw new RequestFailureException("Caught an IllegalStateException:", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return response;
    }

    @Override
    protected void tryDownloadAPK(VersionConfig config) {
        AndroidSchedulers.mainThread().scheduleDirect(
                () -> DownloadController.requestFileAccessPermission(
                        result -> {
                            if (result) {
                                Schedulers.io().scheduleDirect(() -> {
                                    File file = doDownload(config);
                                    if (verifyAPK(file, config.md5)) {
                                        Context context = ContextUtils.getApplicationContext();
                                        setForceUpdateValidVersion(context, config.latestVersion);
                                    }
                                });
                            }
                        }));
    }

    private File doDownload(VersionConfig config) {
        File downloadFile = getDownloadFile(config);
        if (downloadFile == null) {
            return null;
        }

        OkHttpClient client = OKHttpUtils.getDefaultNonCacheClient();
        Request request = new Request.Builder().url(config.downloadUrl).get().build();

        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            if (body != null) {
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(downloadFile);
                byte[] buffer = new byte[256 * 1024];
                int c;
                while ((c = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, c);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "download force update apk failed: " + e);
        } finally {
            IoUtils.closeQuietly(outputStream);
            IoUtils.closeQuietly(inputStream);
        }

        return downloadFile;
    }

    private boolean verifyAPK(File download, String md5) {
        String digest = EncryptUtil.digestFile(download);
        return TextUtils.equals(digest, md5);
    }

    private @Nullable File getDownloadFile(VersionConfig config) {
        Context context = ContextUtils.getApplicationContext();
        File cacheDir = context.getExternalCacheDir();
        File file = new File(cacheDir, FORCE_DOWNLOAD_PATH);
        if (!file.exists()) {
            if (!file.mkdir()) {
                Log.e(TAG, "make force update apk dir failed");
                return null;
            }
        }

        File downloadFile = new File(file, config.latestVersion + ".apk");

        if (downloadFile.exists()) {
            String digest = EncryptUtil.digestFile(downloadFile);
            if (TextUtils.equals(digest, config.md5)) {
                return null;
            } else {
                if (downloadFile.delete()) {
                    Log.i(TAG, "delete exist download apk");
                }
            }
        }

        return downloadFile;
    }

    private void doDownloadV1(VersionConfig config) {
        Context context = ContextUtils.getApplicationContext();
        File downloadFile = getDownloadFile(config);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(config.downloadUrl));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDestinationUri(Uri.fromFile(downloadFile));

        DownloadManager downloadManager = (DownloadManager)
                context.getSystemService(Context.DOWNLOAD_SERVICE);
        long id = downloadManager.enqueue(request);
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(new DownloadReceiver(id), filter);

        Log.i(TAG, "enqueue download task!");
    }

    private static class DownloadReceiver extends BroadcastReceiver {
        private final long mId;
        DownloadReceiver(long id) {
            mId = id;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Context applicationContext = ContextUtils.getApplicationContext();
            DownloadManager downloadManager = (DownloadManager)
                    applicationContext.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(mId);
            Cursor cursor = downloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (index < 0) {
                    cursor.close();
                    return;
                }

                int status = cursor.getInt(index);
                switch (status) {
                    case DownloadManager.STATUS_SUCCESSFUL:
                        Log.i(TAG, "download force update APK success!");
                        applicationContext.unregisterReceiver(this);
                        break;

                    case DownloadManager.STATUS_FAILED:
                        Log.e(TAG, "download force update APK failed!");
                        applicationContext.unregisterReceiver(this);
                        File cacheDir = applicationContext.getExternalCacheDir();
                        File file = new File(cacheDir, FORCE_DOWNLOAD_PATH);
                        if (file.exists()) {
                            File[] children = file.listFiles();
                            if (children != null) {
                                for (File child: children) {
                                    Log.i(TAG, "delete " + file + " result: " + child.delete());
                                }
                            }
                        }
                        break;

                    default:
                        break;
                }

                cursor.close();
            }
        }
    }
}
