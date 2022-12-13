package org.chromium.chrome.browser.search_engines.web;

import com.google.gson.Gson;

import org.wwg.network.base.ConfigServerConstant;
import org.wwg.network.base.OKHttpUtils;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TemplateUrlAccountSourceImpl implements ITemplateUrlAccountSource {

    @Override
    public Observable<TemplateUrlAccountResponse> getAccounts(String version, String union) {
        String fragment = getFragment(version, union);

        return Observable.just(fragment)
                .map((Function<String, TemplateUrlAccountResponse>) path -> {
                    String url = getUrl(path);
                    Request request = new Request.Builder()
                            .url(url)
                            .method("GET", null)
                            .build();
                    Response response = getOkHttpClient().newCall(request).execute();
                    ResponseBody body = response.body();
                    if (body != null) {
                        return new Gson().fromJson(
                                body.charStream(),
                                TemplateUrlAccountResponse.class);
                    }
                    return new TemplateUrlAccountResponse();
                })
                .subscribeOn(Schedulers.io());
    }

    private OkHttpClient getOkHttpClient() {
        return OKHttpUtils.getDefaultNonCacheClient();
    }

    private String getFragment(String version, String union) {
        return ITemplateUrlAccountService.UNION_ID +
                "=" +
                union +
                "&" +
                ITemplateUrlAccountService.VERSION +
                "=" +
                version;
    }

    private String getUrl(String fragment) {
        //return "https://test-configserver.cqttech.com/202209131515/37be73ce2d1e736908db23385b0d72c7/molars/serviced/sszh/34/3b5329efbdc2bc779c743191d516104d.json";
        return BASE_URL +
                ITemplateUrlAccountService.PATH +
                ITemplateUrlAccountService.APP_ID +
                "=" +
                ConfigServerConstant.APP_ID +
                "&" +
                ITemplateUrlAccountService.KEY_NAME +
                "=" +
                ConfigServerConstant.SEARCH_ENGINE_ACCOUNT_KEY +
                "&" +
                fragment;
    }
}
