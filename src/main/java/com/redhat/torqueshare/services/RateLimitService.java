package com.redhat.torqueshare.services;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final LettuceBasedProxyManager<byte[]> proxyManager;

    /* uses ip got from user to supply a bucket config and return the bucket instance
        use ip as key
     */
    public Bucket resolveBucket(String ipAddress) {

        Supplier<BucketConfiguration> configSupplier = () -> BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(5)
                        .refillIntervally(10, Duration.ofHours(1))
                        .build())
                .build();

        return proxyManager.builder()
                .build(("RateLimit:"+ipAddress).getBytes(), configSupplier);
    }
}