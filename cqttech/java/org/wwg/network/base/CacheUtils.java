package org.wwg.network.base;

import android.content.Context;

import java.io.File;

import okhttp3.Cache;

public class CacheUtils {
    private static final String CACHE_CHILD_DIR = "response";
    private static final long DEFAULT_MAX_CACHE_SIZE =  1024 * 1024 * 10; //10Mb

    public static Cache getDefaultCache(Context context) {
        return getCache(context, CACHE_CHILD_DIR, DEFAULT_MAX_CACHE_SIZE);
    }

    public static Cache getCache(Context context, String path, long maxSize) {
        File cacheFile = new File(context.getCacheDir(), path);
        return new Cache(cacheFile, maxSize);
    }
}
