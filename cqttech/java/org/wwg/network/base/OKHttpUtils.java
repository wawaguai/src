package org.wwg.network.base;

import org.chromium.base.ContextUtils;
import org.wwg.network.base.header.CachedHeaderInterceptor;
import org.wwg.network.base.header.NoCacheHeaderInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * default cache dir, default
 */
public class OKHttpUtils {
    private static final int DEFAULT_TIME_OUT_SECOND = 10;

    public static OkHttpClient getDefaultNonCacheClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIME_OUT_SECOND, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIME_OUT_SECOND, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(new NoCacheHeaderInterceptor())
                .addInterceptor(getHttpLoggingInterceptor())
                .cache(CacheUtils.getDefaultCache(ContextUtils.getApplicationContext()))
                .build();
    }

    public static OkHttpClient getDefaultCacheClient() {
        return getCacheClient(
                10,
                TimeUnit.MINUTES,
                1,
                TimeUnit.DAYS,
                CacheUtils.getDefaultCache(ContextUtils.getApplicationContext())
        );
    }

    public static OkHttpClient getCacheClient(
            int maxStale, TimeUnit maxStaleTimeUnit,
            int maxAge, TimeUnit maxAgeTimeUnit,
            String cachePath, long maxCacheSize
    ) {
        Cache cache = CacheUtils.getCache(
                ContextUtils.getApplicationContext(),
                cachePath,
                maxCacheSize
        );

        return new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIME_OUT_SECOND, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIME_OUT_SECOND, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(new CachedHeaderInterceptor(
                        maxStale,
                        maxStaleTimeUnit,
                        maxAge,
                        maxAgeTimeUnit)
                )
                .addInterceptor(getHttpLoggingInterceptor())
                .cache(cache)
                .build();
    }

    public static OkHttpClient getCacheClient(
            int maxStale,
            TimeUnit maxStaleTimeUnit,
            int maxAge,
            TimeUnit maxAgeTimeUnit,
            Cache cache
    ) {
        return new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIME_OUT_SECOND, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIME_OUT_SECOND, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(new CachedHeaderInterceptor(
                        maxStale,
                        maxStaleTimeUnit,
                        maxAge,
                        maxAgeTimeUnit)
                )
                .addInterceptor(getHttpLoggingInterceptor())
                .cache(cache)
                .build();
    }

    private static Interceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return interceptor;
    }
}
