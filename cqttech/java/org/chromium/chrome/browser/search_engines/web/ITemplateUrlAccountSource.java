package org.chromium.chrome.browser.search_engines.web;

import io.reactivex.Observable;

public interface ITemplateUrlAccountSource {
    String BASE_URL = "https://cfg-dcapi.cqttech.com";

    Observable<TemplateUrlAccountResponse> getAccounts(
            String version,
            String union
    );
}
