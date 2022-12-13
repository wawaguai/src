package org.chromium.chrome.browser.suggestions.nav;

import android.content.Context;

import org.chromium.chrome.browser.suggestions.nav.entities.HomeNavWebEntity;
import org.wwg.network.base.BaseResponse;

import io.reactivex.Observable;

public interface IHomeNavWebSource {
    String BASE_URL = "http://xkandroid.xkbrowser.com/browser/";
    String ICON_BASE_URL = "http://xkandroid.xkbrowser.com";
//    String BASE_URL = "http://10.10.30.210:8080/home/";
//    String ICON_BASE_URL = "http://10.10.30.210:8080/home";

    /**
     * get home page items.
     * @param context 多进程架构中，不要轻易使用全局提供的 context。在这里，使用外部传入的context，具体由调用方指定
     * @return items
     */
    Observable<BaseResponse<HomeNavWebEntity>> getHomePageItems(Context context, boolean useCache);
}
