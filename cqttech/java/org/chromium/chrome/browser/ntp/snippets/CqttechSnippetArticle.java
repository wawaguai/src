package org.chromium.chrome.browser.ntp.snippets;

public class CqttechSnippetArticle extends SnippetArticle {
    @CqttechArticleType
    public int articleType;
    public String thumbUrl;
    public String thumbBGUrl;
    public String mark;
    public int number;
    public String visits;

    public CqttechSnippetArticle(int category, String idWithinCategory, String title, String publisher, String url,
            long publishTimestamp, float score, long fetchTimestamp, boolean isVideoSuggestion,
            Integer thumbnailDominantColor
    ) {
        super(category, idWithinCategory, title, publisher, url, publishTimestamp, score,
                fetchTimestamp, isVideoSuggestion, thumbnailDominantColor);
    }
}
