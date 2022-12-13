package org.chromium.chrome.browser.ntp.snippets.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CqttechArticleResponse implements Serializable {
    @SerializedName("code")
    public int code;

    @SerializedName("data")
    public CqttechArticleCategories categories;

    @SerializedName("msg")
    public String message;

    @SerializedName("requestId")
    public String requestId;

    public CqttechArticleResponse() {
    }

    public CqttechArticleResponse(int code, CqttechArticleCategories data, String msg, String requestId) {
        this.code = code;
        this.categories = data;
        this.message = msg;
        this.requestId = requestId;
    }
}
