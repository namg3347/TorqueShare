package com.redhat.torqueshare.configs;

import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Bucket4jConfig {

    @Value("${spring.data.redis.url}")
    private String redisUrl;

    // allows bucket-4j in the springboot app to connect and talk to redis(SET,GET etc)
    @Bean
    public RedisClient redisClient() {
        return RedisClient.create(redisUrl.trim());
    }

    /*uses the redis client to talk to redis as bucket are not in app
      they are saved in redis and used as proxy
    * */
    @Bean
    public LettuceBasedProxyManager<byte[]> proxyManager(RedisClient redisClient) {
        return LettuceBasedProxyManager.builderFor(redisClient)
                .build();
    }
}