package org.chromium.chrome.browser.suggestions;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.base.ContextUtils;
import org.chromium.chrome.R;
import org.chromium.chrome.browser.ntp.TitleUtil;
import org.wwg.common.ThemeConfig;

public class CqttechTileView extends LinearLayout implements CqttechLargeUrlHelper.OnFetchSuccess {
    /** The url currently associated to this tile. */
    private SiteSuggestion mSiteData;

    private TextView mTitleView;
    private ImageView mIconView;

    public CqttechTileView(Context context) {
        this(context, null);
    }

    public CqttechTileView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CqttechTileView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CqttechTileView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mTitleView = findViewById(R.id.tile_view_title);
        mIconView = findViewById(R.id.iv_tile_view_icon);
    }

    /**
     * Initializes the view using the data held by {@code tile}. This should be called immediately
     * after inflation.
     * @param tile The tile that holds the data to populate this view.
     * @param titleLines The number of text lines to use for the tile title.
     */
    public void initialize(Tile tile, int titleLines) {
        mTitleView.setLines(titleLines);
        mSiteData = tile.getData();
        mTitleView.setText(TitleUtil.getTitleForDisplay(tile.getTitle(), tile.getUrl()));
        if (ThemeConfig.getInstance().isDark()) {
            mTitleView.setTextColor(ApiCompatibilityUtils.getColor(
                    getResources(),
                    R.color.main_text_dark_color));
            mIconView.setBackgroundColor(Color.BLACK);
        }
        renderOfflineBadge(tile);
        renderIcon(tile);

        String largeUrl = CqttechLargeUrlHelper.getLargeUrl(tile.getUrl());
        if (!TextUtils.isEmpty(largeUrl)) {
            Glide.with(mIconView).load(largeUrl).into(mIconView);
        } else {
            CqttechLargeUrlHelper.addListener(this);
        }
    }

    /** @return The url associated with this view. */
    public String getUrl() {
        return mSiteData.url;
    }

    public SiteSuggestion getData() {
        return mSiteData;
    }

    /** @return The {@link TileSource} of the tile represented by this TileView */
    public int getTileSource() {
        return mSiteData.source;
    }

    /**
     * Renders the icon held by the {@link Tile} or clears it from the view if the icon is null.
     */
    public void renderIcon(Tile tile) {
        mIconView.setImageDrawable(tile.getIcon());
    }

    /** Shows or hides the offline badge to reflect the offline availability of the {@link Tile}. */
    public void renderOfflineBadge(Tile tile) {
    }

    @Override
    public void onSuccess() {
        String largeUrl = CqttechLargeUrlHelper.getLargeUrl(mSiteData.url);
        if (!TextUtils.isEmpty(largeUrl)) {
            Glide.with(mIconView).load(largeUrl).into(mIconView);
        }
    }
}
