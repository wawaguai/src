package org.chromium.chrome.browser.suggestions;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.base.SysUtils;
import org.chromium.chrome.R;

import org.chromium.chrome.browser.ntp.snippets.CqttechSnippetArticle;
import org.chromium.chrome.browser.ntp.snippets.SnippetArticle;
import org.wwg.common.MIUIUtils;
import org.wwg.common.ThemeConfig;

public class CqttechSuggestionsBinder implements ISuggestionsBinder {
    private final ImageView mThumbView;
    private final TextView mTitleView;
    private final TextView mDescription;

    @Nullable
    private RequestOptions mOptions;

    public CqttechSuggestionsBinder(
            View cardContainerView,
            SuggestionsUiDelegate uiDelegate,
            boolean isContextual
    ) {
        mThumbView = cardContainerView.findViewById(R.id.thumb);
        mTitleView = cardContainerView.findViewById(R.id.title);
        mDescription = cardContainerView.findViewById(R.id.description);

        if (ThemeConfig.getInstance().isDark()) {
            Resources resources = mTitleView.getResources();
            mTitleView.setTextColor(ApiCompatibilityUtils.getColor(
                    resources,
                    R.color.main_text_dark_color));
        }
    }

    @Override
    public void updateViewInformation(SnippetArticle suggestion) {
        mTitleView.setText(suggestion.mTitle);
        if (suggestion instanceof CqttechSnippetArticle) {
            if (mThumbView != null) {
                if (MIUIUtils.getInstance().isMIUI()) {
                    Glide.with(mThumbView)
                            .load(((CqttechSnippetArticle) suggestion).thumbUrl)
                            .into(mThumbView);
                } else {
                    if (mOptions == null) {
                        int width = mThumbView.getMeasuredWidth();
                        int height = mThumbView.getMeasuredHeight();
                        if (width <= 0 || height <= 0) {
                            mThumbView.measure(
                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.AT_MOST),
                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                        }

                        width = mThumbView.getMeasuredWidth();
                        height = mThumbView.getMeasuredHeight();
                        if (width > 0 && height > 0) {
                            mOptions = RequestOptions.bitmapTransform(
                                    new RoundedCorners(1)
                            ).override(width, height);
                        }
                    }

                    if (mOptions != null) {
                        Glide.with(mThumbView)
                                .load(((CqttechSnippetArticle) suggestion).thumbUrl)
                                .apply(mOptions)
                                .into(mThumbView);
                    } else {
                        Glide.with(mThumbView)
                                .asBitmap()
                                .load(((CqttechSnippetArticle) suggestion).thumbUrl)
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                    }

                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        mThumbView.setImageBitmap(resource);
                                    }
                                });
                    }
                }
            }

            String description = String.format(
                    "%s  %s%s",
                    suggestion.mPublisher,
                    ((CqttechSnippetArticle) suggestion).visits,
                    mDescription.getResources().getString(R.string.photo_picker_browse)
            );
            mDescription.setText(description);
        }
    }

    @Override
    public void updateFieldsVisibility(
            boolean showHeadline, boolean showThumbnail,
            boolean showThumbnailVideoBadge, boolean showSnippet) {
    }

    @Override
    public void updateOfflineBadgeVisibility(boolean visible) {
    }

    @Override
    public void recycle() {
        if (SysUtils.isLowEndDevice() || SysUtils.isCurrentlyLowMemory() || MIUIUtils.getInstance().isMIUI()) {
            if (mThumbView != null) {
                Glide.with(mThumbView).clear(mThumbView);
            }
        }
    }
}
