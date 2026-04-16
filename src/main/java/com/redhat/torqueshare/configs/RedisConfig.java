package com.redhat.torqueshare.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        //redis serializer to convert pozo to json
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();

        RedisSerializationContext.SerializationPair<Object> pair =
                RedisSerializationContext.SerializationPair.fromSerializer(serializer);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(pair)
                .entryTtl(Duration.ofMinutes(10)) // enforced TTL
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}