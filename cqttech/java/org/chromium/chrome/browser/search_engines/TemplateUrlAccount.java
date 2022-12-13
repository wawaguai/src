package org.chromium.chrome.browser.search_engines;

public class TemplateUrlAccount {
    private final String mKeyword;
    private final String mAccount;

    public TemplateUrlAccount(String keyword, String account) {
        mKeyword = keyword;
        mAccount = account;
    }

    public String getKeyword() {
        return mKeyword;
    }

    public String getAccount() {
        return mAccount;
    }
}
