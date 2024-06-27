package com.ral.young.night.interview.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author renyunhui
 * @date 2024-06-25 9:41
 * @since 1.0.0
 */
@Service
@Slf4j
public class TestService {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private JdbcTemplate jdbcTemplate;

    private static final String redisCacheKey = "order::112233";

    public void testIdempotency() {
        String redisLock = "";
        /*
         * 新增库存记录，保证幂等性
         * 1.先加分布式锁
         * 2.再在数据库判断数据是否存在
         * 3.在执行业务操作新增库存记录
         * 【注】：数据库依然需要设置唯一索引做最后的保障
         *
         * redisson 实现分布式锁： lua 脚本
         * 1.检查指定的锁是否已存在，如果不存在，则创建锁并设置过期时间；
         * 2.如果锁已存在，并且当前线程已持有该锁，则增加锁的持有次数并更新过期时间；
         * 3.如果锁已被其他线程持有，则返回剩余的过期时间。
         */
        RLock lock = redissonClient.getLock(redisLock);
        try {
            if (lock.tryLock(1000, 3000, TimeUnit.MILLISECONDS)) {
                // 加锁成功，判断数据库中是否存在这条数据
                List<Map<String, Object>> maps = jdbcTemplate.queryForList("select * from night_order where id = 1");
                // 如果不存在则新增数据
                if (maps.isEmpty()) {
                    int update = jdbcTemplate.update("insert into night_order(id, name) values(1, 'test')");
                    log.info("新增订单信息完成，count：{}", update);
                } else {
                    log.info("数据库已经存在此数据，执行业务逻辑即可");
                    Thread.sleep(800);
                }
            }
        } catch (InterruptedException e) {
            log.info("中断异常", e);
        } finally {
            lock.unlock();
        }
    }

    public void testWriteThread() throws InterruptedException {
        /*
         * 模拟写线程查询数据库数据进行操作
         *
         * 先删除缓存，在查询数据库，最后在通过异步的方式进行缓存的再次删除，防止这期间被其他的读线程更新了缓存导致数据库和缓存数据不一致
         */
        redisTemplate.delete(redisCacheKey);

        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select * from order where id = 1");

        if (!maps.isEmpty()) {
            String name = (String) maps.get(0).get("name");
            log.info("数据库数据：{}", name);
            // 做一些业务操作
            Thread.sleep(2000);
            // 异步删除缓存，发送 kafka
        }
    }

    public void testReadThread() throws InterruptedException {
        /*
         * 模拟读线程查询数据库数据进行缓存的更新
         */
        String value = redisTemplate.opsForValue().get(redisCacheKey);
        if (StringUtils.isBlank(value)) {
            log.info("缓存未命中，查询数据库");
            List<Map<String, Object>> maps = jdbcTemplate.queryForList("select * from order where id = 1");
            if (!maps.isEmpty()) {
                String name = (String) maps.get(0).get("name");
                redisTemplate.opsForValue().set(redisCacheKey, name);
                log.info("缓存更新完成，value：{}", name);
            }
        } else {
            log.info("缓存命中，value：{}", value);
            // 做一些业务操作
            Thread.sleep(800);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void testRedisTransactionalAndDbTransactional(String key) {
        // 如果外层开启了 spring 事务，并且 redisTemplate 设置了 enableTransactionSupport = true
        // 则 redisTemplate.opsForValue().increment(key) 会在事务提交时才执行，现在获取到的数据为 null
        // 修改方法：
        // 1.手动设置  redisTemplate.setEnableTransactionSupport(false);
        // 2.分开使用 redisTemplate，一个设置 redisTemplate.setEnableTransactionSupport(false); 另一个 redisTemplate.setEnableTransactionSupport(true);
        Long increment = redisTemplate.opsForValue().increment(key);
        Optional.ofNullable(increment).ifPresent(i -> {
            log.info("{} key 自增结果：{}", key, increment);
        });
    }

    public void testRedisTransactional() {
        // 支持 redis 事务
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.multi();

        // 插入命令
        redisTemplate.opsForValue().set("testRedisTransactional", "111");
        redisTemplate.opsForValue().set("testRedisTransactionalTwo", "222");

        redisTemplate.exec();
    }
}
