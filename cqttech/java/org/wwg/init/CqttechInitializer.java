package org.wwg.init;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.chromium.base.ContextUtils;
import org.chromium.chrome.browser.firstrun.FirstRunStatus;
import org.wwg.common.IoUtils;
import org.wwg.common.DeviceFeature;
import org.wwg.common.ThemeConfig;
import org.wwg.umeng.UmengProxy;

import java.io.IOException;
import java.io.InputStream;

import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class CqttechInitializer {
    private static final String TAG = "CqttechInitializer";
    private static final String CHANNEL_PATH = "cqttech_channel.txt";
    //private BehaviorSubject<String> mChannelSubject = BehaviorSubject.createDefault("");
    private String mChannel = "";
    private boolean mPreInitialed = false;

    private static class Holder {
        static final CqttechInitializer INSTANCE = new CqttechInitializer();
    }

    public static CqttechInitializer getInstance() {
        return Holder.INSTANCE;
    }

    private CqttechInitializer() {
        //Schedulers.io().scheduleDirect(() -> {
        AssetManager assetManager = ContextUtils.getApplicationContext().getAssets();
        InputStream stream = null;
        try {
            stream = assetManager.open(CHANNEL_PATH);

            byte[] buffer = new byte[1024];
            int read = stream.read(buffer);
            if (read > 0) {
                mChannel = new String(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(stream);
        }
        //});
    }

    public void preInit(Application application) {
        if (!FirstRunStatus.getFirstRunFlowComplete()) {
            return;
        }

        String channel = mChannel;
        if (channel == null) {
            channel = "";
        }

        UmengProxy.preInit(application, channel);
        mPreInitialed = true;
    }

    public void init() {
        if (!FirstRunStatus.getFirstRunFlowComplete()) {
            return;
        }

        Context appCtx = ContextUtils.getApplicationContext();
        String channel = mChannel;
        Log.i(TAG, "packer channel: " + channel + " pre initialed " + mPreInitialed);
        if (channel == null) {
            channel = "";
        }

        DeviceFeature.initFeature(appCtx, channel);
        ThemeConfig.getInstance();

        if (mPreInitialed) {
            UmengProxy.init(appCtx, channel);
        }
    }

}
