package org.chromium.chrome.browser.search_engines.web;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TemplateUrlAccountResponse {

    @SerializedName("params")
    private List<Account> mAccounts;

    public List<Account> getAccounts() {
        return mAccounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.mAccounts = accounts;
    }

    public static class Account {
        @SerializedName("key")
        private String mKey;

        @SerializedName("value")
        private String mValue;

        @SerializedName("keyword")
        private String mKeyword;

        public String getKey() {
            return mKey;
        }

        public void setKey(String key) {
            this.mKey = key;
        }

        public String getValue() {
            return mValue;
        }

        public void setValue(String value) {
            this.mValue = value;
        }

        public String getKeyword() {
            return mKeyword;
        }

        public void setKeyword(String keyword) {
            this.mKeyword = keyword;
        }
    }
}
