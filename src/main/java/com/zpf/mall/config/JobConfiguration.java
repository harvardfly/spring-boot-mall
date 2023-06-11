package com.zpf.mall.config;

import com.zpf.mall.service.OrderService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 描述： 定时任务 + 分布式锁redissonLock
 */
@Component
public class JobConfiguration {

    @Autowired
    OrderService orderService;

    @Autowired
    RedissonClient redissonClient;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void cancelOrders() {
        RLock redissonLock = redissonClient.getLock("redissonLock");
        boolean b = redissonLock.tryLock();
        if (b) {
            try {
                System.out.println("redisson锁+1");
                orderService.cancel("1");
            } finally {
                redissonLock.unlock();
                System.out.println("redisson锁已释放");
            }
        } else {
            System.out.println("redisson锁未获取到");
        }
    }
}
