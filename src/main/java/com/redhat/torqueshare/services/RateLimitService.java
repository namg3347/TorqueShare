package com.redhat.torqueshare.services;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final RedisConnectionFactory redisConnectionFactory;

    // We use a ProxyManager to handle the Redis communication
    private LettuceBasedProxyManager<byte[]> getProxyManager() {
        return LettuceBasedProxyManager.builderFor((RedisClient) redisConnectionFactory)
                .build();
    }

    public Bucket resolveBucket(String ipAddress) {
        // limit: 10 tokens per 1 hour
        Supplier<BucketConfiguration> configSupplier = () -> BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(10)
                        .refillIntervally(10, Duration.ofHours(1))
                        .build())
                .build();

        // IP connected to specific bucket in redis
        return getProxyManager().builder().build(ipAddress.getBytes(), configSupplier);
    }
}