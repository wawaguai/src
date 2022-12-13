package org.chromium.chrome.browser.ntp.cards;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.chromium.chrome.browser.ntp.ContextMenuManager;
import org.chromium.chrome.browser.ntp.LogoView;
import org.chromium.chrome.browser.ntp.snippets.CqttechSnippetArticleViewHolder;
import org.chromium.chrome.browser.offlinepages.OfflinePageBridge;
import org.chromium.chrome.browser.suggestions.SuggestionsRecyclerView;
import org.chromium.chrome.browser.suggestions.SuggestionsUiDelegate;
import org.chromium.chrome.browser.suggestions.TileGroup;
import org.chromium.chrome.browser.widget.displaystyle.UiConfig;

public class CqttechNewTabPageAdapter extends NewTabPageAdapter {
    private final ContextMenuManager mContextMenuManager;
    private final SuggestionsUiDelegate mUiDelegate;
    private final UiConfig mUiConfig;
    private final OfflinePageBridge mOfflinePageBridge;

    private SuggestionsRecyclerView mRecyclerView;

    public CqttechNewTabPageAdapter(
            SuggestionsUiDelegate uiDelegate, @Nullable View aboveTheFoldView,
            @Nullable LogoView logoView, UiConfig uiConfig,
            OfflinePageBridge offlinePageBridge, ContextMenuManager contextMenuManager,
            @Nullable TileGroup.Delegate tileGroupDelegate) {
        super(uiDelegate, aboveTheFoldView, logoView, uiConfig, offlinePageBridge,
                contextMenuManager, tileGroupDelegate);

        mContextMenuManager = contextMenuManager;
        mUiDelegate = uiDelegate;
        mUiConfig = uiConfig;
        mOfflinePageBridge = offlinePageBridge;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public NewTabPageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ItemViewType.BIG_IMAGE_SNIPPET:
            case ItemViewType.SMALL_IMAGE_SNIPPET:
            case ItemViewType.NO_IMAGE_SNIPPET:
                return new CqttechSnippetArticleViewHolder(
                        mRecyclerView, mContextMenuManager, mUiDelegate,
                        mUiConfig, mOfflinePageBridge,
                        viewType
                );

            default:
                break;
        }

        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public int getFirstSnippetPosition() {
        int firstPos = getFirstValidCqttechSnippetPosition();
        if (firstPos > 0) {
            return firstPos;
        }

        return super.getFirstSnippetPosition();
    }

    protected int getFirstValidCqttechSnippetPosition() {
        int count = getItemCount();
        for (int i = 0; i < count; i++) {
            if (matchCqttechSnippetType(getItemViewType(i))) {
                return i;
            }
        }

        return RecyclerView.NO_POSITION;
    }

    protected boolean matchCqttechSnippetType(@ItemViewType int type) {
        return type == ItemViewType.BIG_IMAGE_SNIPPET ||
                type == ItemViewType.SMALL_IMAGE_SNIPPET ||
                type == ItemViewType.NO_IMAGE_SNIPPET;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (mRecyclerView == recyclerView) return;

        mRecyclerView = (SuggestionsRecyclerView) recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        mRecyclerView = null;
    }
}
