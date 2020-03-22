package com.new4net.sso.core;

import org.redisson.Redisson;
import org.redisson.RedissonReactive;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
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
//    @Bean
//    public RedissonClient config(){
//        Config config = new Config();
//        config.useClusterServers()
//                .setScanInterval(2000) // master node change scan interval
//                // use "rediss://" for SSL connection
//                .addNodeAddress("redis://192.168.1.5:17000@192.168.1.5:27000", "redis://192.168.1.5:17001@192.168.1.5:27001",
//                        "redis://192.168.1.5:17002@192.168.1.5:27002", "redis://192.168.1.5:18000@192.168.1.5:28000",
//                        "redis://192.168.1.5:18001@192.168.1.5:28001","redis://192.168.1.5:18002@192.168.1.5:28002",
//                        "redis://192.168.1.34:18000@192.168.1.34:28000", "redis://192.168.1.34:18001@192.168.1.34:28001",
//                        "redis://192.168.1.34:18002@192.168.1.34:28002")
//                .setPassword("TjTaA124#")
//                ;
//        config.setRedissonReferenceEnabled(true);
//        config.setUseLinuxNativeEpoll(true);
//
//        RedissonClient redisson = Redisson.create(config);
//        return redisson;
//    }
}