package org.chromium.chrome.browser.search_engines;

import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import org.chromium.chrome.browser.search_engines.web.TemplateUrlAccountResponse;
import org.chromium.chrome.browser.search_engines.web.TemplateUrlAccountSourceImpl;
import org.wwg.common.DeviceFeature;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class TemplateUrlAccountDelegate {
    private static final String CHANGED = "changed";
    private static final String LOADED = "loaded";

    private final TemplateUrlAccountSourceImpl mTemplateUrlAccountSource;
    private final BehaviorSubject<List<TemplateUrlAccount>> mTemplateUrlAccountSubject;
    private final BehaviorSubject<String> mTemplateUrlObserverSubject;

    @Nullable
    private Disposable mAccountApply;
    @Nullable
    private Disposable mAccountRequest;

    public TemplateUrlAccountDelegate() {
        mTemplateUrlAccountSubject = BehaviorSubject.create();
        mTemplateUrlObserverSubject = BehaviorSubject.create();
        mTemplateUrlAccountSource = new TemplateUrlAccountSourceImpl();

        updateAccounts();
        requestAccount();
    }

    private void updateAccounts() {
        if (mAccountApply != null) {
            mAccountApply.dispose();
        }

        mAccountApply = Observable.combineLatest(
                mTemplateUrlAccountSubject,
                mTemplateUrlObserverSubject,
                (requestAccounts, s) -> {
                    int size = requestAccounts.size();
                    String[] keywords = new String[size];
                    String[] accounts = new String[size];

                    for (int idx = 0; idx < size; idx++) {
                        TemplateUrlAccount requestAccount = requestAccounts.get(idx);
                        keywords[idx] = requestAccount.getKeyword();
                        accounts[idx] = requestAccount.getAccount();
                    }

                    return new Pair<>(keywords, accounts);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> TemplateUrlService.getInstance().updateAccount(
                        success.first,
                        success.second),
                        failed -> {
                            failed.printStackTrace();
//                            if (DeviceFeature.isPackageForTest()) {
//                                throw new RuntimeException("failed: " + failed);
//                            }
                        });
    }

    public void onTemplateURLServiceChanged() {
        mTemplateUrlObserverSubject.onNext(CHANGED);
    }

    public void onTemplateUrlServiceLoaded() {
        mTemplateUrlObserverSubject.onNext(LOADED);
    }

    public void destroy() {
        if (mAccountRequest != null) {
            mAccountRequest.dispose();
        }

        if (mAccountApply != null) {
            mAccountApply.dispose();
        }
    }

    private void requestAccount() {
        String version = DeviceFeature.getAppVersionName();
        String flavor = String.valueOf(DeviceFeature.getFlavorValue());

        mAccountRequest = mTemplateUrlAccountSource.getAccounts(version, flavor)
                .map(response -> {
                    List<TemplateUrlAccount> result = new ArrayList<>();
                    if (response != null) {
                        List<TemplateUrlAccountResponse.Account> accounts = response.getAccounts();
                        for (TemplateUrlAccountResponse.Account account : accounts) {
                            String sb = account.getKey() + "=" + account.getValue();
                            result.add(new TemplateUrlAccount(account.getKeyword(), sb));
                        }
                    }

                    return result;
                })
                .subscribeOn(Schedulers.io())
                .subscribe(
                        mTemplateUrlAccountSubject::onNext,
                        failed -> {
                            failed.printStackTrace();
//                            if (DeviceFeature.isPackageForTest()) {
//                                mTemplateUrlAccountSubject.onError(failed);
//                                throw new RuntimeException("failed: " + failed);
//                            }
                        });
    }

}
