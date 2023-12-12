package com.ral.young.study.cacheconsistency;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * 验证 mysql 和 redis 的缓存一致性问题
 *
 * @author renyunhui
 * @date 2023-12-11 16:19
 * @since 1.0.0
 */
@Service
@Slf4j
public class CacheConsistencyService implements ApplicationRunner {

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;
    @Resource
    private JdbcTemplate jdbcTemplate;

    public String getOrderInfo(Long orderId) {
        // 1.先检查缓存中是否存在数据
        String orderInfo = (String) redisTemplate.opsForValue().get(orderId);
        if (StrUtil.isBlank(orderInfo)) {
            // 2.尝试从数据库中获取
            Map<String, Object> map = jdbcTemplate.queryForList("select order_info from order_data where id = ?", orderId).stream().findFirst().orElse(null);
            if (MapUtil.isEmpty(map)) {
                return null;
            }

            orderInfo = (String) map.get("orderInfo");
            if (StrUtil.isNotBlank(orderInfo)) {
                // 3.更新缓存
                redisTemplate.opsForValue().set(orderId, orderInfo);
            }
        }
        return orderInfo;
    }

    public void updateOrderInfo(Long orderId, String orderInfo) {
        // 1.先更新缓存
        redisTemplate.opsForValue().set(orderId, orderInfo);

        // 2.在更新数据库
        int update = jdbcTemplate.update("update order_data set order_info = ? where id = ? ", orderInfo, orderId);
        log.info("更新完成,更新的数量为:" + update);
    }

    public void saveOderInfo(Long orderId, String orderInfo) {
        int update = jdbcTemplate.update("insert into order_data (id, order_info) values (?, ?) ", orderId, orderInfo);
        log.info("新增完成,新增的数量为:" + update);

        redisTemplate.opsForValue().set(orderId, orderInfo);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        int size = 5;
        List<Long> idList = new ArrayList<>(size);
        IntStream.range(0, size).forEach(i -> idList.add(IdUtil.getSnowflakeNextId()));

        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0, n = 50; i < n; i++) {
            new Thread(() -> {
                int index = ThreadLocalRandom.current().nextInt(size);
                long orderId = idList.get(index);
                // 模拟多线程并发访问
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                String orderInfo = getOrderInfo(orderId);
                log.info("{} 获取到的订单信息：{}，订单ID：{}", Thread.currentThread().getName(), orderInfo, orderId);
            }).start();
        }

        Thread.sleep(3000);
        // 主线程一旦减少门栓的数量，则阻塞的线程立刻开始执行任务·
        countDownLatch.countDown();
    }
}
