package org.chromium.chrome.browser.suggestions;

import org.chromium.chrome.browser.ntp.snippets.SnippetArticle;

public interface ISuggestionsBinder {

    void updateViewInformation(SnippetArticle suggestion);

    void updateFieldsVisibility(
            boolean showHeadline,
            boolean showThumbnail,
            boolean showThumbnailVideoBadge,
            boolean showSnippet);

    void updateOfflineBadgeVisibility(boolean visible);

    void recycle();
}
