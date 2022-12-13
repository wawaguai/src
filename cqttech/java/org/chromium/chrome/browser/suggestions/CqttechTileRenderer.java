package org.chromium.chrome.browser.suggestions;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.chromium.base.Log;
import org.chromium.chrome.R;
import org.chromium.chrome.browser.favicon.IconType;

import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.base.VisibleForTesting;
import org.chromium.chrome.browser.favicon.LargeIconBridge;
import org.chromium.chrome.browser.util.ViewUtils;
import org.chromium.chrome.browser.widget.RoundedIconGenerator;
import org.wwg.common.ThemeConfig;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CqttechTileRenderer {
    private static final String TAG = "CqttechTileRenderer";

    private final int mTitleLinesCount;
    private final int mDesiredIconSize;
    private final int mMinIconSize;
    private final int mIconCornerRadiusDp;

    @LayoutRes
    private final int mLayout;

    private final Resources mResources;
    private final ImageFetcher mImageFetcher;
    private final RoundedIconGenerator mIconGenerator;

    public CqttechTileRenderer(Context context, int titleLines, ImageFetcher imageFetcher) {
        mImageFetcher = imageFetcher;
        mTitleLinesCount = titleLines;

        mResources = context.getResources();
        final float density = mResources.getDisplayMetrics().density;

        int iconTextSize = mResources.getDimensionPixelSize(R.dimen.cqttech_tile_text_size);
        int iconTextSizeDp = Math.round(iconTextSize / density);

        int iconCornerRadius = mResources.getDimensionPixelSize(R.dimen.cqttech_tile_icon_corner_radius);
        mIconCornerRadiusDp = Math.round(iconCornerRadius / density);

        mDesiredIconSize = mResources.getDimensionPixelSize(R.dimen.cqttech_tile_icon_size);
        int desiredIconSizeDp =
                Math.round(mDesiredIconSize / density);

        mMinIconSize = mResources.getDimensionPixelSize(R.dimen.cqttech_tile_min_icon_size);

        int iconColor = ApiCompatibilityUtils.getColor(
                mResources, R.color.default_favicon_background_color);

        mIconGenerator = new RoundedIconGenerator(
                mResources, desiredIconSizeDp, desiredIconSizeDp,
                mIconCornerRadiusDp, iconColor, iconTextSizeDp);

        mLayout = getLayout();
    }

    /**
     * Renders tile views in the given {@link ViewGroup}, reusing existing tile views where
     * possible because view inflation and icon loading are slow.
     * @param parent The layout to render the tile views into.
     * @param sectionTiles Tiles to render.
     * @param setupDelegate Delegate used to setup callbacks and listeners for the new views.
     */
    public void renderTileSection(
            List<Tile> sectionTiles, CqttechTileGridLayout parent, TileGroup.TileSetupDelegate setupDelegate) {
        // fetch large icons
        CqttechLargeUrlHelper.fetchSuggestions();

        // Map the old tile views by url so they can be reused later.
        Map<SiteSuggestion, CqttechTileView> oldTileViews = new HashMap<>();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            CqttechTileView tileView = (CqttechTileView) parent.getChildAt(i);
            oldTileViews.put(tileView.getData(), tileView);
        }

        // Remove all views from the layout because even if they are reused later they'll have to be
        // added back in the correct order.
        parent.removeAllViews();

        for (Tile tile : sectionTiles) {
            CqttechTileView tileView = oldTileViews.get(tile.getData());
            if (tileView == null) {
                tileView = buildTileView(tile, parent, setupDelegate);
            }

            parent.addTileView(tileView);
            parent.invalidate();
            parent.requestLayout();
        }
    }

    /**
     * Inflates a new tile view, initializes it, and loads an icon for it.
     * @param tile The tile that holds the data to populate the new tile view.
     * @param parentView The parent of the new tile view.
     * @param setupDelegate The delegate used to setup callbacks and listeners for the new view.
     * @return The new tile view.
     */
    @VisibleForTesting
    CqttechTileView buildTileView(
            Tile tile, ViewGroup parentView, TileGroup.TileSetupDelegate setupDelegate) {
        CqttechTileView tileView = (CqttechTileView) LayoutInflater.from(parentView.getContext())
                .inflate(mLayout, parentView, false);
        tileView.initialize(tile, mTitleLinesCount);

        // Note: It is important that the callbacks below don't keep a reference to the tile or
        // modify them as there is no guarantee that the same tile would be used to update the view.
        if (TextUtils.isEmpty(CqttechLargeUrlHelper.getLargeUrl(tile.getUrl()))) {
            fetchIcon(tile.getData(), setupDelegate.createIconLoadCallback(tile));
        }

        TileGroup.TileInteractionDelegate delegate = setupDelegate.createInteractionDelegate(tile);
        tileView.setOnClickListener(delegate);
        tileView.setOnCreateContextMenuListener(delegate);

        return tileView;
    }

    @LayoutRes
    private int getLayout() {
        return R.layout.cqttech_home_nav_item_layout;
    }

    public void updateIcon(SiteSuggestion siteData, LargeIconBridge.LargeIconCallback iconCallback) {
        mImageFetcher.makeLargeIconRequest(siteData.url, mMinIconSize, iconCallback);
    }

    public void setTileIconFromBitmap(Tile tile, Bitmap icon) {
        /*
        RoundedBitmapDrawable roundedIcon = ViewUtils.createRoundedBitmapDrawable(icon,
                Math.round(mIconCornerRadiusDp * mResources.getDisplayMetrics().density
                        * icon.getWidth() / mDesiredIconSize));
        roundedIcon.setAntiAlias(true);
        roundedIcon.setFilterBitmap(true);

        tile.setIcon(roundedIcon);
        */
        tile.setIcon(new BitmapDrawable(mResources, icon));
        tile.setType(TileVisualType.ICON_REAL);
    }

    public void setTileIconFromColor(Tile tile, int fallbackColor, boolean isFallbackColorDefault) {
        mIconGenerator.setBackgroundColor(fallbackColor);
        Bitmap icon = mIconGenerator.generateIconForUrl(tile.getUrl());
        tile.setIcon(new BitmapDrawable(mResources, icon));
        tile.setType(
                isFallbackColorDefault ? TileVisualType.ICON_DEFAULT : TileVisualType.ICON_COLOR);
    }

    private void fetchIcon(
            final SiteSuggestion siteData, final LargeIconBridge.LargeIconCallback iconCallback) {
        if (siteData.whitelistIconPath.isEmpty()) {
            mImageFetcher.makeLargeIconRequest(siteData.url, mMinIconSize, iconCallback);
            return;
        }

        new TileAsyncTask(
                mImageFetcher, iconCallback, siteData, mMinIconSize
        ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class TileAsyncTask extends AsyncTask<Void, Void, Bitmap> {
        private final WeakReference<ImageFetcher> mFetcherRef;
        private final WeakReference<LargeIconBridge.LargeIconCallback> mCallbackRef;
        private final WeakReference<SiteSuggestion> mSuggestionRef;
        private final int mMinIconSize;

        TileAsyncTask(
                ImageFetcher fetcher,
                LargeIconBridge.LargeIconCallback callback,
                SiteSuggestion suggestion,
                int minIconSize
        ) {
            mFetcherRef = new WeakReference<>(fetcher);
            mCallbackRef = new WeakReference<>(callback);
            mSuggestionRef = new WeakReference<>(suggestion);
            mMinIconSize = minIconSize;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            SiteSuggestion suggestion = mSuggestionRef.get();
            if (suggestion == null) {
                return null;
            }

            Bitmap bitmap = BitmapFactory.decodeFile(suggestion.whitelistIconPath);
            if (bitmap == null) {
                Log.d(TAG, "Image decoding failed: %s", suggestion.whitelistIconPath);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap icon) {
            ImageFetcher fetcher = mFetcherRef.get();
            LargeIconBridge.LargeIconCallback callback = mCallbackRef.get();
            SiteSuggestion suggestion = mSuggestionRef.get();
            if (fetcher == null || callback == null || suggestion == null) {
                return;
            }

            if (icon == null) {
                fetcher.makeLargeIconRequest(suggestion.url, mMinIconSize, callback);
            } else {
                callback.onLargeIconAvailable(
                        icon, Color.BLACK, false, IconType.INVALID
                );
            }
        }
    }
}
