package org.wwg.network.base.header;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CachedHeaderInterceptor implements Interceptor {
    private static final String TAG = "CachedHeaderItc";

    private final int mMaxStale;
    private final TimeUnit mMaxStaleTimeUnit;
    private final int mMaxAge;
    private final TimeUnit mMaxAgeTimeUnit;

    public CachedHeaderInterceptor(
            int maxStale,
            TimeUnit maxStaleTimeUnit,
            int maxAge,
            TimeUnit maxAgeTimeUnit
    ) {
        mMaxStale = maxStale;
        mMaxStaleTimeUnit = maxStaleTimeUnit;
        mMaxAge = maxAge;
        mMaxAgeTimeUnit = maxAgeTimeUnit;
    }

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
        Request newRq = requestBuilder.cacheControl(
                new CacheControl.Builder()
                        .maxAge(mMaxAge, mMaxAgeTimeUnit)
                        .maxStale(mMaxStale, mMaxStaleTimeUnit)
                        .build()).build();
        Response response = chain.proceed(newRq);
        Log.i(TAG, "response body : " + response.body());
        return response;
    }
}
