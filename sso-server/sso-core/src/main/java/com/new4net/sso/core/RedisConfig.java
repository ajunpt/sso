package com.new4net.sso.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

    /**
     * 使用自定义的CacheManager
     *
     * @param factory
     * @return
     */
    @Primary
    @Bean
    public CacheManager cacheManager( RedisConnectionFactory factory) {
        RedisCacheWriter writer = RedisCacheWriter.nonLockingRedisCacheWriter(factory);
        //使用JSON序列化器，当缓存对象，会以json的格式存储
        RedisSerializer<Object> serializer = new GenericJackson2JsonRedisSerializer();
        RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair.fromSerializer(serializer);
        //设置缓存过期时间为3600s，即1h
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(pair)
                .entryTtl(Duration.ofSeconds(3600))
                .disableCachingNullValues();
        RedisCacheManager cacheManager = new RedisCacheManager(writer, configuration);
        return cacheManager;
    }
    @Bean
    public CacheManagerFactory cacheManagerFactory(CacheManager cacheManager){
        CacheManagerFactory cacheManagerFactory = new CacheManagerFactory();
        cacheManagerFactory.setCacheManager(cacheManager);
        return cacheManagerFactory;
    }
}