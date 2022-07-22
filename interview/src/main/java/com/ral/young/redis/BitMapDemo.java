package com.ral.young.redis;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * redisson 实现 bitmap
 *
 * @author renyunhui
 * @date 2022-07-22 15:33
 * @since 1.0.0
 */
@Slf4j
public class BitMapDemo {

    public static void main(String[] args) {
        // 使用 bitmap 解决缓存穿透的问题
        Config config = new Config();
        config.useSingleServer().setAddress("redis://49.235.87.36:6379").setPassword("ryh123.0");

        // 构建 redisson
        RedissonClient redissonClient = Redisson.create(config);

        // 初始化布隆过滤器 bitmap
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("nameList");
        //  //初始化布隆过滤器：预计元素为 10000000L ,误差率为 3%，根据这两个参数会计算出底层的bit数组大小
        bloomFilter.tryInit(10000000L, 0.03);

        /*
         * 布隆过滤器就是一个大型的位数组和几个不一样的无偏 hash 函数。所谓无偏就是能够把元素的 hash 值算得比较均匀。
         * 向布隆过滤器中添加 key 时，会使用多个 hash 函数对 key 进行 hash 算得一个整数索引值然后对位数组长度进行取模运算得到一个位置，
         * 每个 hash 函数都会算得一个不同的位置。再把位数组的这几个位置都置为 1 就完成了 add 操作。
         * 向布隆过滤器询问 key 是否存在时，跟 add 一样，也会把 hash 的几个位置都算出来，看看位数组中这几个位置是否都为 1，只要有一个位为 0，那么说明布隆过滤器中这个key 不存在。
         * 如果都是 1，这并不能说明这个 key 就一定存在，只是极有可能存在，因为这些位被置为 1 可能是因为其它的 key 存在所致。如果这个位数组比较稀疏，这个概率就会很大，如果这个位数组比较拥挤，这个概率就会降低。
         * 这种方法适用于数据命中不高、 数据相对固定、 实时性低（通常是数据集较大） 的应用场景， 代码维护较为复杂， 但是缓存空间占用很少。
         */

        bloomFilter.add("mike");

        log.info("当前 bloomFilter 中是否包含 bob : {}", bloomFilter.contains("bob"));
        log.info("当前 bloomFilter 中是否包含 amy : {}", bloomFilter.contains("amy"));
        log.info("当前 bloomFilter 中是否包含 mike : {}", bloomFilter.contains("mike"));

        redissonClient.shutdown();
    }
}
