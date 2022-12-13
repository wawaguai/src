package org.chromium.chrome.browser.ntp.snippets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Pair;

import org.chromium.base.Callback;
import org.chromium.base.ContextUtils;
import org.chromium.base.ObserverList;
import org.chromium.chrome.browser.ntp.cards.SuggestionsCategoryInfo;
import org.chromium.chrome.browser.ntp.snippets.entities.CqttechArticleClass;
import org.chromium.chrome.browser.ntp.snippets.entities.CqttechArticleContent;
import org.chromium.chrome.browser.ntp.snippets.web.CqttechSnippetArticleSourceImpl;
import org.chromium.chrome.browser.ntp.snippets.web.ICqttechSnippetArticleSource;
import org.chromium.chrome.browser.profiles.Profile;

import org.chromium.chrome.browser.suggestions.ContentSuggestionsAdditionalAction;
import org.chromium.chrome.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CqttechSnippetsBridge implements SuggestionsSource {
    private final ObserverList<Observer> mObserverList = new ObserverList<>();
    private @CategoryStatus int mCategoryStatus = CategoryStatus.INITIALIZING;
    @Nullable
    private SuggestionsCategoryInfo mCategoryInfo = null;

    private final List<CqttechSnippetArticle> mArticles = new LinkedList<>();
    private Disposable mDisposable;
    private final ICqttechSnippetArticleSource mSnippetArticleSource;

    /*
    public static boolean isCategoryStatusAvailable(@CategoryStatus int status) {
        // Note: This code is duplicated in category_status.cc.
        return status == CategoryStatus.AVAILABLE_LOADING || status == CategoryStatus.AVAILABLE;
    }

    public static boolean isCategoryRemote(@CategoryInt int category) {
        return category > KnownCategories.REMOTE_CATEGORIES_OFFSET;
    }

    public static boolean isCategoryEnabled(@CategoryStatus int status) {
        switch (status) {
            case CategoryStatus.INITIALIZING:
            case CategoryStatus.AVAILABLE:
            case CategoryStatus.AVAILABLE_LOADING:
                return true;
        }
        return false;
    }

    public static boolean isCategoryLoading(@CategoryStatus int status) {
        return status == CategoryStatus.AVAILABLE_LOADING || status == CategoryStatus.INITIALIZING;
    }
    */

    /**
     * Creates a SnippetsBridge for getting snippet data for the current user.
     *
     * @param profile Profile of the user that we will retrieve snippets for.
     */
    public CqttechSnippetsBridge(Profile profile) {
        mSnippetArticleSource = new CqttechSnippetArticleSourceImpl();
        loadArticles();
    }

    private void loadArticles() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }

        mDisposable = getArticles(ContextUtils.getApplicationContext(), true)
                .map(articleMap -> {
                    List<CqttechSnippetArticle> articles = new LinkedList<>();
                    for (Pair<String, List<CqttechArticleContent>> entry: articleMap) {
                        String publisher = entry.first;
                        List<CqttechArticleContent> contents = entry.second;
                        for (CqttechArticleContent content: contents) {
                            articles.add(CqttechSnippetArticleUtils.factoryArticle(publisher, content));
                        }
                    }

                    return articles;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    mArticles.clear();
                    mArticles.addAll(success);
                    mCategoryStatus = CategoryStatus.AVAILABLE;
                    onFullRefreshRequired();
                    //onCategoryStatusChanged(KnownCategories.ARTICLES, mCategoryStatus);
                }, failed -> {
                    mCategoryStatus = CategoryStatus.LOADING_ERROR;
                    onCategoryStatusChanged(KnownCategories.ARTICLES, mCategoryStatus);
                    failed.printStackTrace();
                });
    }

    private Observable<List<Pair<String, List<CqttechArticleContent>>>> getArticles(
            Context context, boolean useCache
    ) {
        return mSnippetArticleSource.getArticles(context, useCache)
                .map(response -> {
                    List<Pair<String, List<CqttechArticleContent>>> contentMap = new LinkedList<>();
                    for (CqttechArticleClass articleClass : response.categories.list) {
                        contentMap.add(new Pair<>(articleClass.name, articleClass.contents));
                    }

                    return contentMap;
                });
    }

    @Override
    public void destroy() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }

        mObserverList.clear();
    }

    @Override
    public boolean areRemoteSuggestionsEnabled() {
        return true;
    }

    @Override
    public void fetchRemoteSuggestions() {
        if (mArticles.isEmpty()) {
            loadArticles();
        }
    }

    @Override
    public int[] getCategories() {
        return new int[] {KnownCategories.ARTICLES};
    }

    @Override
    @CategoryStatus
    public int getCategoryStatus(int category) {
        if (category == KnownCategories.ARTICLES) {
            return mCategoryStatus;
        }
        return CategoryStatus.NOT_PROVIDED;
    }

    @Override
    public SuggestionsCategoryInfo getCategoryInfo(int category) {
        if (mCategoryInfo != null) {
            return mCategoryInfo;
        }

        Resources resources = ContextUtils.getApplicationContext().getResources();
        mCategoryInfo = createSuggestionsCategoryInfo(
                KnownCategories.ARTICLES,
                resources.getString(R.string.ntp_title_no_suggestions),
                resources.getString(R.string.ntp_title_no_suggestions)
        );
        return mCategoryInfo;
    }

    @Override
    public List<SnippetArticle> getSuggestionsForCategory(int category) {
        switch (mCategoryStatus) {
            case CategoryStatus.INITIALIZING:
                break;
            case CategoryStatus.AVAILABLE:
                return CqttechSnippetArticleUtils.mapToSnippetArticles(mArticles);
            case CategoryStatus.LOADING_ERROR:
                loadArticles();
                break;
        }

        return new ArrayList<>();
    }

    @Override
    public void dismissSuggestion(SnippetArticle suggestion) {
    }

    @Override
    public void dismissCategory(@CategoryInt int category) {
    }

    @Override
    public void restoreDismissedCategories() {
    }

    @Override
    public void addObserver(Observer observer) {
        assert observer != null;
        mObserverList.addObserver(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        mObserverList.removeObserver(observer);
    }

    @Override
    public void fetchSuggestions(
            @CategoryInt int category, String[] displayedSuggestionIds,
            Callback<List<SnippetArticle>> successCallback, Runnable failureRunnable
    ) {
        //Log.e("jyh", "load suggestions");
        loadArticles();
    }

    private SuggestionsCategoryInfo createSuggestionsCategoryInfo(
            int category,
            String title,
            String noSuggestionsMessage) {
        return new SuggestionsCategoryInfo(
                category,
                title,
                ContentSuggestionsCardLayout.FULL_CARD,
                ContentSuggestionsAdditionalAction.NONE,
                false,
                noSuggestionsMessage);
    }

    private void onCategoryStatusChanged(@CategoryInt int category, @CategoryStatus int newStatus) {
        for (Observer observer : mObserverList) {
            observer.onCategoryStatusChanged(category, newStatus);
        }
    }

    private void onNewSuggestions(@CategoryInt int category) {
        for (Observer observer : mObserverList) observer.onNewSuggestions(category);
    }

    private void onSuggestionInvalidated(@CategoryInt int category, String idWithinCategory) {
        for (Observer observer : mObserverList) {
            observer.onSuggestionInvalidated(category, idWithinCategory);
        }
    }

    private void onFullRefreshRequired() {
        for (Observer observer : mObserverList) observer.onFullRefreshRequired();
    }

    private void onSuggestionsVisibilityChanged(@CategoryInt int category) {
        for (Observer observer : mObserverList) observer.onSuggestionsVisibilityChanged(category);
    }

    @Override
    public void fetchSuggestionImage(SnippetArticle suggestion, Callback<Bitmap> callback) {
    }

    @Override
    public void fetchSuggestionFavicon(SnippetArticle suggestion, int minimumSizePx,
                                       int desiredSizePx, Callback<Bitmap> callback) {
    }

    @Override
    public void fetchContextualSuggestions(String url, Callback<List<SnippetArticle>> callback) {
    }

    @Override
    public void fetchContextualSuggestionImage(
            SnippetArticle suggestion, Callback<Bitmap> callback) {
    }

    private void setAssetDownloadDataForSuggestion(
            SnippetArticle suggestion, String downloadGuid, String filePath, String mimeType) {
        suggestion.setAssetDownloadData(downloadGuid, filePath, mimeType);
    }

    private void setOfflinePageDownloadDataForSuggestion(
            SnippetArticle suggestion, long offlinePageId) {
        suggestion.setOfflinePageDownloadData(offlinePageId);
    }
}
