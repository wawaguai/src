package org.chromium.chrome.browser.ntp.snippets;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.TextUtils;

import org.chromium.chrome.browser.ntp.cards.ItemViewType;
import org.chromium.chrome.browser.ntp.snippets.entities.CqttechArticleContent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CqttechSnippetArticleUtils {

    public static List<CqttechSnippetArticle> mapToCqttechArticles(List<SnippetArticle> articles) {
        List<CqttechSnippetArticle> cqttechSnippetArticles = new ArrayList<>(articles.size());
        for (SnippetArticle article : articles) {
            if (article instanceof CqttechSnippetArticle) {
                cqttechSnippetArticles.add((CqttechSnippetArticle) article);
            }
        }

        return cqttechSnippetArticles;
    }

    public static List<SnippetArticle> mapToSnippetArticles(List<CqttechSnippetArticle> articles) {
        return new ArrayList<>(articles);
    }

    @ItemViewType
    public static int getViewType(@CqttechArticleType int articleType) {
        switch (articleType) {
            case CqttechArticleType.TOP:
                return ItemViewType.BIG_IMAGE_SNIPPET;
            case CqttechArticleType.GRADE_A:
                return ItemViewType.SMALL_IMAGE_SNIPPET;
            case CqttechArticleType.GRADE_B:
                return ItemViewType.NO_IMAGE_SNIPPET;

            default:
                return ItemViewType.UNKNOWN;
        }
    }

    public static CqttechSnippetArticle factoryArticle(
            String publisher,
            CqttechArticleContent content
    ) {
        CqttechSnippetArticle article = makeArticle(
                KnownCategories.ARTICLES, content.title, publisher, content.url
        );

        int number = content.no;
        int articleType;
        if (TextUtils.isEmpty(content.thumb)) {
            articleType = CqttechArticleType.GRADE_B;
        } else if (TextUtils.equals("bilibili", publisher)) {
            articleType = CqttechArticleType.GRADE_A;
        } else if (number == 1 || number == 2 || number == 3) {
            articleType = CqttechArticleType.TOP;
        } else {
            articleType = CqttechArticleType.GRADE_A;
        }

        article.articleType = articleType;
        article.thumbUrl = content.thumb;
        article.thumbBGUrl = content.bgUrl;
        article.mark = content.mark;
        article.number = number;
        article.visits = getVisits(content.videoView, content.score);

        return article;
    }

    private static CqttechSnippetArticle makeArticle(
            int category,
            String title, String publisher, String url
    ) {
        String id = makeSuggestionId(title, publisher, url);

        return new CqttechSnippetArticle(
                category, id, title, publisher, url,
                -1L, -1F, -1L,
                false, Color.TRANSPARENT
        );
    }

    @SuppressLint("DefaultLocale")
    private static String getVisits(String defaultVal, String score) {
        String visits = defaultVal;
        if (TextUtils.isEmpty(visits)) {
            try {
                int parse = Integer.parseInt(score);
                if (parse > 10000) {
                    visits = String.format("%.1f万", parse / 10000.0f);
                } else {
                    visits = score;
                }
            } catch (Throwable throwable) {
                // throwable.printStackTrace();
                visits = score;
            }
        }

        return visits;
    }

    private static String makeSuggestionId(String title, String publisher, String url) {
        return String.valueOf(Objects.hash(title, publisher, url));
    }
}
