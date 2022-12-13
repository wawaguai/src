package org.chromium.chrome.browser.ntp.snippets.web;

import android.content.Context;

import org.chromium.chrome.browser.ntp.snippets.entities.CqttechArticleResponse;

import io.reactivex.Observable;

public interface ICqttechSnippetArticleSource {
    String BASE_URL = "http://soso-appbrowser-api.cqttech.com/";

    /**
     * get home page items.
     * @param context context
     * @param useCache use cache or not
     * @return items
     */
    Observable<CqttechArticleResponse> getArticles(Context context, boolean useCache);
}
