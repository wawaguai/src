package org.chromium.chrome.browser.suggestions;

public class CqttechLargeUrlHelper {

    public static void fetchSuggestions() {
        DelegateHolder.DELEGATE.fetchSuggestions();
    }

    public static String getLargeUrl(String url) {
        return DelegateHolder.DELEGATE.getLargeUrl(url);
    }

    public static void destroy() {
        DelegateHolder.DELEGATE.destroy();
    }

    public static void addListener(CqttechLargeUrlHelper.OnFetchSuccess listener) {
        DelegateHolder.DELEGATE.addListener(listener);
    }

    public interface OnFetchSuccess {
        void onSuccess();
    }

    private static class DelegateHolder {
        final static CqttechLargeUrlDelegate DELEGATE = new CqttechLargeUrlDelegate();
    }

    private CqttechLargeUrlHelper() {
    }
}
