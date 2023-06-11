package com.zpf.mall.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述： Redisson 分布式锁
 */
@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient config() {
        Config config = new Config();
        config.useSingleServer().setAddress("${spring.redis.host}" + ":" + "${spring.redis.port}");
        return Redisson.create(config);
    }
}
