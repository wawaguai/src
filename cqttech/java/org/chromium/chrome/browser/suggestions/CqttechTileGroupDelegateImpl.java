package org.chromium.chrome.browser.suggestions;

import static org.chromium.chrome.browser.suggestions.nav.IHomeNavWebSource.ICON_BASE_URL;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.chromium.base.Callback;
import org.chromium.chrome.browser.ChromeActivity;
import org.chromium.chrome.browser.ChromeTabbedActivity;
import org.chromium.chrome.browser.UrlConstants;
import org.chromium.chrome.browser.bookmarks.BookmarkUtils;
import org.chromium.chrome.browser.history.HistoryManagerUtils;
import org.chromium.chrome.browser.profiles.Profile;
import org.chromium.chrome.browser.snackbar.SnackbarManager;
import org.chromium.chrome.browser.suggestions.nav.entities.HomeNavEntity;
import org.chromium.chrome.browser.suggestions.nav.HomeNavRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CqttechTileGroupDelegateImpl implements TileGroup.Delegate {
    @Nullable
    private MostVisitedSites.Observer mWrappedObserver;
    private final HomeNavRepository repository = new HomeNavRepository();
    private final SuggestionsNavigationDelegate mNavigationDelegate;
    private final ChromeActivity mActivity;
    @Nullable
    private Disposable disposable;

    private final List<SiteSuggestion> suggestions = new ArrayList<>();

    public CqttechTileGroupDelegateImpl(
            ChromeActivity activity,
            Profile profile,
            SuggestionsNavigationDelegate navigationDelegate,
            SnackbarManager snackbarManager
    ) {
        mActivity = activity;
        mNavigationDelegate = navigationDelegate;
        disposable = repository.requestHomePageItems(activity, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    suggestions.clear();
                    for (HomeNavEntity entity: success) {
                        suggestions.add(new SiteSuggestion(
                                entity.itemName,
                                entity.href,
                                ICON_BASE_URL + entity.logo,
                                TileTitleSource.TITLE_TAG,
                                TileSource.TOP_SITES,
                                TileSectionType.PERSONALIZED,
                                new Date(System.currentTimeMillis())
                        ));
                    }

                    notifyData();
                }, Throwable::printStackTrace);
    }

    @Override
    public void removeMostVisitedItem(Tile tile, Callback<String> removalUndoneCallback) {

    }

    @Override
    public void openMostVisitedItem(int windowDisposition, Tile tile) {
        String url = tile.getUrl();

        if (TextUtils.equals(UrlConstants.CQTTECH_HISTORY_ACTION, url)) {
            HistoryManagerUtils.showHistoryManager(mActivity, mActivity.getActivityTab());
        } else if (TextUtils.equals(UrlConstants.CQTTECH_BOOKMARK_ACTION, url)) {
            BookmarkUtils.showBookmarkManager(mActivity);
        } else {
            mNavigationDelegate.navigateToSuggestionUrl(windowDisposition, url);
        }
    }

    @Override
    public void setMostVisitedSitesObserver(MostVisitedSites.Observer observer, int maxResults) {
        mWrappedObserver = observer;
        if (!suggestions.isEmpty()) {
            notifyData();
        }
    }

    @Override
    public void onLoadingComplete(List<Tile> tiles) {
        notifyData();
//        List<SiteSuggestion> suggestions = new ArrayList<>(
//                MostVisitedSitesBridge.buildSiteSuggestions(
//                        titles,
//                        urls,
//                        sections,
//                        whitelistIconPaths,
//                        titleSources,
//                        sources,
//                        dataGenerationTimesMs
//                )
//        );
//
//        if (mWrappedObserver != null) {
//            mWrappedObserver.onSiteSuggestionsAvailable(suggestions);
//        }
    }

    private void notifyData() {
        if (mWrappedObserver != null) {
            mWrappedObserver.onSiteSuggestionsAvailable(suggestions);
        }
    }

    @Override
    public void destroy() {
        if (disposable != null) {
            disposable.dispose();
        }
    }
}
