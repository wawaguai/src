package org.chromium.chrome.browser.suggestions;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.util.ArraySet;
import android.text.TextUtils;

import org.chromium.base.ContextUtils;
import org.chromium.chrome.browser.suggestions.nav.HomeNavRepository;
import org.chromium.chrome.browser.suggestions.nav.entities.HomeNavEntity;

//import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

class CqttechLargeUrlDelegate {
    private static final String WWW = "www.";
    private static final String BASE_URL = "http://xzandroid.cqttech.com";

    private boolean mFetchSuccess = false;

    @Nullable
    private Disposable mDisposable;

    private final List<HomeNavEntity> mSuggestions = new ArrayList<>();
    private final Set<CqttechLargeUrlHelper.OnFetchSuccess> mFetchListeners = new ArraySet<>();

    void addListener(CqttechLargeUrlHelper.OnFetchSuccess listener) {
        if (mFetchSuccess) {
            return;
        }

        mFetchListeners.add(listener);
    }

    private void notifyListeners() {
        for (CqttechLargeUrlHelper.OnFetchSuccess listener: mFetchListeners) {
            listener.onSuccess();
        }

        mFetchListeners.clear();
    }

    void fetchSuggestions() {
        if (mFetchSuccess) {
            return;
        }

        Context applicationContext = ContextUtils.getApplicationContext();
        HomeNavRepository repository = new HomeNavRepository();
        mDisposable = repository.requestHomePageItems(applicationContext, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    mFetchSuccess = true;
                    mSuggestions.clear();
                    mSuggestions.addAll(success);

                    notifyListeners();
                }, failed -> {
                    failed.printStackTrace();
                    mFetchSuccess = false;
                });
    }

    void destroy() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    String getLargeUrl(String url) {
        if (!mFetchSuccess) {
            return "";
        }

        String domain = parserDomain(url);
        if (TextUtils.isEmpty(domain)) {
            return "";
        }

        for (HomeNavEntity entity: mSuggestions) {
            if (entity.href.contains(domain)) {
                return BASE_URL + entity.logo;
            }
        }

        return "";
    }

    private String parserDomain(String url) {
        try {
            String host = Uri.parse(url).getHost();
            //URI uri = URI.create(url);
            //String host = uri.getHost();
            if (host.startsWith(WWW)) {
                return host.replace(WWW, "");
            }
            return host;
        } catch (Throwable thr) {
            return "";
        }
    }
}
