package org.chromium.chrome.browser.ntp.snippets.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CqttechArticleCategories implements Serializable {
    @SerializedName("list")
    public ArrayList<CqttechArticleClass> list;

    @SerializedName("open_hot_search")
    public boolean isOpenHotSearch;

    @SerializedName("show_hot_search")
    public boolean isShowHotSearch;

    public CqttechArticleCategories() {
    }

    public CqttechArticleCategories(
            ArrayList<CqttechArticleClass> list,
            boolean isOpen, boolean isShow) {
        this.list = list;
        this.isOpenHotSearch = isOpen;
        this.isShowHotSearch = isShow;
    }
}
