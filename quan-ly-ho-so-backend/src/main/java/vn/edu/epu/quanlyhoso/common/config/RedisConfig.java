package vn.edu.epu.quanlyhoso.common.config;

import java.time.Duration;

import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import com.fasterxml.jackson.databind.ObjectMapper;

import vn.edu.epu.quanlyhoso.dashboard.dto.DashboardResponse;

@Configuration
@EnableCaching
public class RedisConfig implements CachingConfigurer {

    public static final String DASHBOARD_CACHE = "dashboard";
    public static final Duration DASHBOARD_TTL = Duration.ofMinutes(5);

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(RedisConfig.class);

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheCustomizer(ObjectMapper objectMapper) {
        Jackson2JsonRedisSerializer<DashboardResponse> dashboardSerializer = new Jackson2JsonRedisSerializer<>(
                objectMapper, DashboardResponse.class);

        return builder -> builder
                .transactionAware()
                .withCacheConfiguration(DASHBOARD_CACHE, RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(DASHBOARD_TTL)
                        .disableCachingNullValues()
                        .serializeValuesWith(SerializationPair.fromSerializer(dashboardSerializer)));
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.warn("Redis GET lỗi (cache={}, key={}): {}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                log.warn("Redis Put lỗi (cache = {}, key={}): {}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.warn("Redis Evict lỗi (cache = {}, key={}): {}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.warn("Redis Clear lỗi (cache = {}, key={}): {}", cache.getName(), e.getMessage());
            }
        };
    }

}
