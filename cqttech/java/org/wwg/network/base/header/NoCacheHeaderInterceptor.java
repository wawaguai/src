package org.wwg.network.base.header;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * force refresh situation
 */
public class NoCacheHeaderInterceptor implements Interceptor {
    private static final String TAG = "NoCacheHeaderItc";

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();
        Map<String, String> header = CommonHeader.getCommonRequestHeader();
        Set<String> keys = header.keySet();
        for (String key : keys) {
            String value = header.get(key);
            if (null == value) {
                value = "";
            }
            requestBuilder.addHeader(key, value);
        }

        Request newRq = requestBuilder
                .cacheControl(new CacheControl.Builder().noCache().build())
                .build();
        Response response = chain.proceed(newRq);
        Log.i(TAG, "response body : " + response.body());
        return response;
    }
}
