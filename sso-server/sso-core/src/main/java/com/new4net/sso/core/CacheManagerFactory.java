package com.new4net.sso.core;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.cache.CacheManager;

public final class CacheManagerFactory implements DisposableBean {
    private static CacheManager CACH_EMANAGER;

    public void setCacheManager(CacheManager cacheManager) {
        CACH_EMANAGER = cacheManager;
    }

    public static CacheManager getCacheManager() {
        return CACH_EMANAGER;
    }

    @Override
    public void destroy() throws Exception {

        CACH_EMANAGER = null;
    }
}