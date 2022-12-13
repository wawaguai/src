package org.chromium.chrome.browser.suggestions.nav;

import android.content.Context;

import org.chromium.chrome.browser.suggestions.nav.entities.HomeNavWebEntity;
import org.wwg.network.base.BaseResponse;
import org.wwg.network.base.OKHttpUtils;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeNavWebSourceImpl implements IHomeNavWebSource {

    @Override
    public Observable<BaseResponse<HomeNavWebEntity>> getHomePageItems(
            Context context,
            boolean useCache
    ) {
        IHomeNavWebService service = new Retrofit.Builder()
                .client(getOkHttpClient(useCache))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
                .create(IHomeNavWebService.class);

        return service.getHomePageItems();
    }

    /**
     * 有网络时直接请求服务端，无网络时使用缓存
     *
     * @return OKHttpClient
     */
    private OkHttpClient getOkHttpClient(boolean useCache) {
        if (useCache) {
            return OKHttpUtils.getDefaultCacheClient();
        } else {
            return OKHttpUtils.getDefaultNonCacheClient();
        }
    }
}
