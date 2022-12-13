package org.chromium.chrome.browser.ntp.snippets.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CqttechArticleClass implements Serializable {
    @SerializedName("name")
    public String name;

    @SerializedName("data")
    public List<CqttechArticleContent> contents;

    public CqttechArticleClass() {
    }

    public CqttechArticleClass(String name, List<CqttechArticleContent> data) {
        this.name = name;
        this.contents = data;
    }
}
