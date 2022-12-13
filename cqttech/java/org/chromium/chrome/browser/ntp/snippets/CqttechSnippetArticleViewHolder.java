package org.chromium.chrome.browser.ntp.snippets;

import android.annotation.SuppressLint;
import android.support.annotation.LayoutRes;

import org.chromium.chrome.R;

import org.chromium.chrome.browser.ntp.ContextMenuManager;
import org.chromium.chrome.browser.ntp.cards.ItemViewType;
import org.chromium.chrome.browser.offlinepages.OfflinePageBridge;
import org.chromium.chrome.browser.suggestions.CqttechSuggestionsBinder;
import org.chromium.chrome.browser.suggestions.ISuggestionsBinder;
import org.chromium.chrome.browser.suggestions.SuggestionsRecyclerView;
import org.chromium.chrome.browser.suggestions.SuggestionsUiDelegate;
import org.chromium.chrome.browser.widget.displaystyle.UiConfig;

public class CqttechSnippetArticleViewHolder extends SnippetArticleViewHolder {

    public CqttechSnippetArticleViewHolder(
            SuggestionsRecyclerView parent,
            ContextMenuManager contextMenuManager,
            SuggestionsUiDelegate uiDelegate,
            UiConfig uiConfig,
            OfflinePageBridge offlinePageBridge,
            @ItemViewType int type
    ) {
        super(parent, contextMenuManager, uiDelegate, uiConfig, offlinePageBridge, getLayoutRes(type));
    }

    @SuppressLint("SwitchIntDef")
    @LayoutRes
    private static int getLayoutRes(@ItemViewType int type) {
        switch (type) {
            case ItemViewType.BIG_IMAGE_SNIPPET:
                return R.layout.cqttech_snippet_article_big_image;
            case ItemViewType.SMALL_IMAGE_SNIPPET:
                return R.layout.cqttech_snippet_article_small_image;
            case ItemViewType.NO_IMAGE_SNIPPET:
                return R.layout.cqttech_snippet_article_no_image;

            default:
                throw new IllegalArgumentException("unknown type: " + type);
        }
    }

    @Override
    protected ISuggestionsBinder createBinder(SuggestionsUiDelegate uiDelegate) {
        return new CqttechSuggestionsBinder(itemView, uiDelegate, false);
    }
}
