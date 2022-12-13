package org.chromium.chrome.browser.search_engines.web;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ITemplateUrlAccountService {
    String PATH = "/api/v1/cs/configdata?";
    String VERSION = "version";
    String UNION_ID = "union_id";
    String APP_ID = "app_id";
    String KEY_NAME = "keyname";

    @GET(PATH)
    Observable<TemplateUrlAccountResponse> getAccounts(
            @Query(VERSION) String version,
            @Query(UNION_ID) String union,
            @Query(APP_ID) String appId,
            @Query(KEY_NAME) String key
    );
}
