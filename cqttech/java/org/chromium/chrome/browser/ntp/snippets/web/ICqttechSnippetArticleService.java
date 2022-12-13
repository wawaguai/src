package org.chromium.chrome.browser.ntp.snippets.web;

import org.chromium.chrome.browser.ntp.snippets.entities.CqttechArticleResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ICqttechSnippetArticleService {

    @GET("api/v1/hot_list")
    Observable<CqttechArticleResponse> getArticles();
}
