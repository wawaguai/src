package org.chromium.chrome.browser.suggestions.nav;

import org.chromium.chrome.browser.suggestions.nav.entities.HomeNavWebEntity;
import org.wwg.network.base.BaseResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface IHomeNavWebService {

    @GET("v2.homePage.json")
    Observable<BaseResponse<HomeNavWebEntity>> getHomePageItems();
}
