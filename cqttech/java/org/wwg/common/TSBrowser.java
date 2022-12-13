package org.wwg.common;

import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

public class TSBrowser {

    @JavascriptInterface
    public String getVersion() {
        return "1.0.6.6";
    }

    @JavascriptInterface
    public void sendAndReceive(String method, JSONObject object, Object function) {
        try {
            object.get("test");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
