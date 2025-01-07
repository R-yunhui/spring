package com.ral.young.spring.structure;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.collect.Multiset;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.RangeSet;
import com.google.common.collect.Table;
import com.google.common.collect.TreeRangeMap;
import com.google.common.collect.TreeRangeSet;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author renyunhui
 * @description 好用的一些数据结构
 * @date 2025-01-03 15-14-38
 * @since 1.0.0
 */
@Slf4j
public class GuavaDataStructure {

    public static void main(String[] args) {
        log.info("===== Guava 数据结构 BiMap 示例 =====");
        // 运行BiMap示例
        biMapExample();

        // 运行BiMap性能测试
        biMapPerformanceTest();

        log.info("===== Guava 数据结构 Multimap 示例 =====");
        // 运行 Multimap 示例
        multimapExample();

        // 运行 Multimap 性能测试
        multimapPerformanceTest();

        log.info("===== Guava 数据结构 Table 示例 =====");
        // 运行 Table 示例
        tableExample();

        // 运行 Table 性能测试
        tablePerformanceTest();

        log.info("===== Guava 不可变集合示例 =====");
        immutableCollectionsExample();

        log.info("===== Guava Range集合示例 =====");
        rangeExample();

        log.info("===== Guava Multiset示例 =====");
        multisetExample();

        log.info("===== Guava Cache示例 =====");
        cacheExample();

        log.info("===== Guava BloomFilter示例 =====");
        bloomFilterExample();

        log.info("===== Guava MinMaxPriorityQueue示例 =====");
        minMaxPriorityQueueExample();
    }

    /**
     * BiMap使用示例
     */
    public static void biMapExample() {
        // 创建BiMap实例
        BiMap<String, Integer> biMap = HashBiMap.create();

        // 添加映射关系
        biMap.put("一", 1);
        biMap.put("二", 2);

        // 通过key获取value
        Integer value = biMap.get("一");  // 返回 1
        log.info("通过 key : {} 获取 value : {}", "一", value);

        // 通过value获取key
        String key = biMap.inverse().get(1);  // 返回 "一"
        log.info("通过 value : {} 获取 key : {}", 1, key);

        // 如果尝试添加重复的value，会抛出IllegalArgumentException
        // 如果确实需要覆盖，可以使用forcePut方法
        biMap.forcePut("三", 1);  // 这会覆盖之前的 "一" -> 1 映射
        log.info("通过 key : {} 获取 value : {}", "三", biMap.get("三"));
    }

    public static void biMapPerformanceTest() {
        /*
         * 空间复杂度：
         * O(2N)，因为内部维护了两个HashMap
         * 比普通HashMap多一倍的空间消耗
         * 实际内存占用约为：N (key大小 + value大小 + 对象开销) 2
         *
         * 时间复杂度：
         *  // 主要操作的时间复杂度
         *  BiMap<String, Integer> biMap = HashBiMap.create();
         *
         *  // 插入 O(1)
         *  biMap.put("key", 1);        // 但实际上是2次HashMap操作
         *
         *  // 查找 O(1)
         *  biMap.get("key");           // 正向查找
         *  biMap.inverse().get(1);     // 反向查找
         */
        int testSize = 1000000;

        // 测试BiMap
        BiMap<String, Integer> biMap = HashBiMap.create(testSize);

        // 插入性能测试
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < testSize; i++) {
            biMap.put("key" + i, i);
        }
        long endTime = System.currentTimeMillis();
        log.info("BiMap 插入 {} 个元素耗时: {}ms", testSize, (endTime - startTime));

        // 正向查找测试
        startTime = System.currentTimeMillis();
        for (int i = 0; i < testSize; i++) {
            biMap.get("key" + i);
        }
        endTime = System.currentTimeMillis();
        log.info("BiMap 正向查找 {} 个元素耗时: {}ms", testSize, (endTime - startTime));

        // 反向查找测试
        startTime = System.currentTimeMillis();
        for (int i = 0; i < testSize; i++) {
            biMap.inverse().get(i);
        }
        endTime = System.currentTimeMillis();
        log.info("BiMap 反向查找 {} 个元素耗时: {}ms", testSize, (endTime - startTime));
    }

    /**
     * Multimap使用示例 - 展示一个key对应多个value的用法
     */
    public static void multimapExample() {
        // 创建 ArrayListMultimap 实例（使用List存储值）
        ListMultimap<String, String> studentCourses = ArrayListMultimap.create();

        // 添加映射关系
        studentCourses.put("张三", "数学");
        studentCourses.put("张三", "物理");
        studentCourses.put("张三", "化学");
        studentCourses.put("李四", "英语");
        studentCourses.put("李四", "历史");

        // 获取某个学生的所有课程
        List<String> zhangSanCourses = studentCourses.get("张三");
        log.info("张三的课程: {}", zhangSanCourses);

        // 检查是否包含某个映射
        boolean hasCourse = studentCourses.containsEntry("张三", "数学");
        log.info("张三是否有数学课: {}", hasCourse);

        // 获取所有的 entries
        Collection<Map.Entry<String, String>> entries = studentCourses.entries();
        log.info("所有的课程安排: {}", entries);

        // 获取不同的 key 数量
        int studentCount = studentCourses.keySet().size();
        log.info("学生数量: {}", studentCount);

        // 获取所有的 value 数量（包括重复的）
        int totalCourses = studentCourses.size();
        log.info("总课程数: {}", totalCourses);
    }

    /**
     * Multimap性能测试
     */
    public static void multimapPerformanceTest() {
        /*
         * 使用 ArrayListMultimap 实现，它内部使用 ArrayList 存储多个值
         *  可以为同一个 key 添加多个 value
         *  提供了方便的 API 来获取和操作数据
         *  包含了性能测试代码，展示在大量数据下的表现
         *
         * 主要的操作时间复杂度：
         *  插入：O(1)
         *  查找：O(1) 获取key对应的列表
         *  空间复杂度：O(n)，其中 n 是所有的键值对数量
         */
        int testSize = 100000;
        ListMultimap<String, Integer> multimap = ArrayListMultimap.create();

        // 插入性能测试
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < testSize; i++) {
            multimap.put("key" + (i % 1000), i);  // 创建一些重复的key
        }
        long endTime = System.currentTimeMillis();
        log.info("Multimap 插入 {} 个元素耗时: {}ms", testSize, (endTime - startTime));

        // 查找性能测试
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            multimap.get("key" + i);
        }
        endTime = System.currentTimeMillis();
        log.info("Multimap 查找 1000 个key的所有值耗时: {}ms", (endTime - startTime));

        // 统计信息
        log.info("Multimap 中的key数量: {}", multimap.keySet().size());
        log.info("Multimap 中的总value数量: {}", multimap.size());
    }

    /**
     * Table使用示例 - 展示双键映射结构
     * Table 结构适合表格型数据，比如：
     * 1. 时间段内的价格变化 (日期,时段,价格)
     * 2. 用户在不同设备的登录状态 (用户,设备,状态)
     * 3. 棋盘游戏 (行,列,棋子)
     */
    public static void tableExample() {
        // 创建 HashBasedTable 实例
        Table<String, String, Double> priceTable = HashBasedTable.create();

        // 添加数据 (商品, 时间段, 价格)
        priceTable.put("苹果", "上午", 5.0);
        priceTable.put("苹果", "下午", 3.0);
        priceTable.put("香蕉", "上午", 4.0);
        priceTable.put("香蕉", "下午", 3.5);

        // 1. 获取特定单元格的值
        Double morningApplePrice = priceTable.get("苹果", "上午");
        log.info("苹果上午价格: {}", morningApplePrice);

        // 2. 获取所有行数据
        Map<String, Double> applePrice = priceTable.row("苹果");
        log.info("苹果全天价格: {}", applePrice);

        // 3. 获取所有列数据
        Map<String, Double> morningPrices = priceTable.column("上午");
        log.info("所有商品上午价格: {}", morningPrices);

        // 4. 获取行集合
        Set<String> products = priceTable.rowKeySet();
        log.info("所有商品: {}", products);

        // 5. 获取列集合
        Set<String> timeSlots = priceTable.columnKeySet();
        log.info("所有时间段: {}", timeSlots);

        // 6. 获取单元格集合
        Set<Table.Cell<String, String, Double>> cells = priceTable.cellSet();
        for (Table.Cell<String, String, Double> cell : cells) {
            log.info("商品: {}, 时间段: {}, 价格: {}",
                    cell.getRowKey(), cell.getColumnKey(), cell.getValue());
        }
    }

    /**
     * Table性能测试
     * 测试在大规模数据下的表现
     */
    public static void tablePerformanceTest() {
        /*
         * HashBasedTable的内部实现：
         * 1. 使用两层HashMap: Map<R, Map<C, V>>
         * 2. 时间复杂度：
         *    - 插入：O(1)
         *    - 查找：O(1)
         *    - 删除：O(1)
         * 3. 空间复杂度：O(rc), r=行数, c=列数
         */
        int rowSize = 1000;
        int colSize = 100;
        Table<Integer, Integer, Double> table = HashBasedTable.create();

        // 插入性能测试
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
                table.put(i, j, i * j * 1.0);
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("Table 插入 {} 个元素耗时: {}ms", rowSize * colSize, (endTime - startTime));

        // 查找性能测试
        startTime = System.currentTimeMillis();
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
                table.get(i, j);
            }
        }
        endTime = System.currentTimeMillis();
        log.info("Table 查找 {} 个元素耗时: {}ms", rowSize * colSize, (endTime - startTime));

        // 统计信息
        log.info("Table 行数: {}", table.rowKeySet().size());
        log.info("Table 列数: {}", table.columnKeySet().size());
        log.info("Table 总单元格数: {}", table.size());
    }

    /**
     * 不可变集合示例
     * 展示Guava提供的不可变集合的使用方法
     * 特点：线程安全、不可修改、内存效率高
     */
    public static void immutableCollectionsExample() {
        // 创建不可变List
        ImmutableList<String> list = ImmutableList.of("a", "b", "c");
        log.info("不可变List内容: {}", list);

        // 使用builder模式创建不可变Set
        ImmutableSet<String> set = ImmutableSet.<String>builder()
                .add("x")
                .add("y")
                .add("z")
                .build();
        log.info("不可变Set内容: {}", set);

        // 创建不可变Map
        ImmutableMap<String, Integer> map = ImmutableMap.<String, Integer>builder()
                .put("one", 1)
                .put("two", 2)
                .put("three", 3)
                .build();
        log.info("不可变Map内容: {}", map);

        try {
            // 尝试修改不可变集合会抛出异常
            list.add("d"); // 将抛出 UnsupportedOperationException
        } catch (UnsupportedOperationException e) {
            log.info("尝试修改不可变集合会抛出异常");
        }
    }

    /**
     * Range（范围）集合示例
     * 展示如何使用RangeSet和RangeMap处理范围数据
     * 适用于：成绩分段、时间区间等场景
     */
    public static void rangeExample() {
        // 创建RangeSet
        RangeSet<Integer> rangeSet = TreeRangeSet.create();
        rangeSet.add(Range.closed(1, 10)); // [1,10]
        rangeSet.add(Range.closed(15, 20)); // [15,20]
        rangeSet.add(Range.closedOpen(30, 40)); // [30,40)

        // 检查包含关系
        log.info("RangeSet是否包含5: {}", rangeSet.contains(5));
        log.info("RangeSet是否包含12: {}", rangeSet.contains(12));
        log.info("所有范围: {}", rangeSet);

        // 创建RangeMap
        RangeMap<Integer, String> rangeMap = TreeRangeMap.create();
        rangeMap.put(Range.closedOpen(0, 60), "不及格");
        rangeMap.put(Range.closedOpen(60, 75), "及格");
        rangeMap.put(Range.closedOpen(75, 85), "良好");
        rangeMap.put(Range.closed(85, 100), "优秀");

        // 获取对应分数的等级
        log.info("分数59的等级: {}", rangeMap.get(59));
        log.info("分数85的等级: {}", rangeMap.get(85));
        log.info("分数90的等级: {}", rangeMap.get(90));
    }

    /**
     * Multiset示例
     * 展示如何使用Multiset进行元素计数
     * 适用于：词频统计、字符计数等场景
     */
    public static void multisetExample() {
        // 创建Multiset
        Multiset<String> multiset = HashMultiset.create();

        // 添加元素
        multiset.add("apple");
        multiset.add("apple");
        multiset.add("banana");
        multiset.add("orange");
        multiset.add("orange");
        multiset.add("orange");

        // 获取元素计数
        log.info("apple出现次数: {}", multiset.count("apple"));
        log.info("orange出现次数: {}", multiset.count("orange"));

        // 设置元素计数
        multiset.setCount("banana", 5);
        log.info("设置后banana出现次数: {}", multiset.count("banana"));

        // 遍历唯一元素及其计数
        log.info("所有元素及其计数:");
        for (String element : multiset.elementSet()) {
            log.info("元素: {}, 计数: {}", element, multiset.count(element));
        }
    }

    /**
     * Cache示例
     * 展示Guava的缓存功能
     * 特点：自动过期、容量限制、统计功能
     */
    public static void cacheExample() {
        // 创建缓存
        LoadingCache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(1000) // 最大容量
                .expireAfterWrite(10, TimeUnit.MINUTES) // 写入后10分钟过期
                .recordStats() // 开启统计
                .removalListener(notification -> // 移除监听器
                        log.info("Key: {} 被移除, 原因: {}",
                                notification.getKey(), notification.getCause()))
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) {
                        return "Computed value for " + key; // 模拟从数据源加载
                    }
                });

        try {
            // 使用缓存
            cache.put("key1", "value1");
            String value1 = cache.get("key1");
            log.info("从缓存中获取key1: {}", value1);

            // 获取不存在的值，将触发load方法
            String value2 = cache.get("key2");
            log.info("通过load方法获取key2: {}", value2);

            // 获取缓存统计信息
            log.info("缓存统计: {}", cache.stats());
        } catch (ExecutionException e) {
            log.error("缓存操作异常", e);
        }
    }

    /**
     * BloomFilter示例
     * 展示布隆过滤器的使用
     * 特点：空间效率高、有误判率
     */
    public static void bloomFilterExample() {
        // 创建布隆过滤器
        BloomFilter<String> filter = BloomFilter.create(
                Funnels.stringFunnel(Charset.defaultCharset()),
                500_000, // 预期插入的元素数量
                0.01);   // 期望的误判率

        // 添加元素
        filter.put("test1");
        filter.put("test2");
        filter.put("test3");

        // 检查元素是否存在
        log.info("test1是否可能存在: {}", filter.mightContain("test1"));
        log.info("test4是否可能存在: {}", filter.mightContain("test4"));

        // 演示误判率
        int falsePositives = 0;
        int tests = 10000;
        for (int i = 0; i < tests; i++) {
            String testValue = "value" + i;
            if (filter.mightContain(testValue) && !testValue.equals("test1")
                    && !testValue.equals("test2") && !testValue.equals("test3")) {
                falsePositives++;
            }
        }
        log.info("在{}次测试中，误判次数: {}", tests, falsePositives);
    }

    /**
     * MinMaxPriorityQueue示例
     * 展示双端优先队列的使用
     * 特点：可以同时获取最大值和最小值
     */
    public static void minMaxPriorityQueueExample() {
        // 创建双端优先队列
        MinMaxPriorityQueue<Integer> queue = MinMaxPriorityQueue.maximumSize(10)
                .create();

        // 添加元素
        queue.add(3);
        queue.add(1);
        queue.add(5);
        queue.add(2);
        queue.add(4);

        log.info("队列内容: {}", queue);

        // 获取并移除最小值
        Integer min = queue.pollFirst();
        log.info("最小值: {}", min);

        // 获取并移除最大值
        Integer max = queue.pollLast();
        log.info("最大值: {}", max);

        // 查看当前队列的最小值和最大值
        log.info("当前队列最小值: {}", queue.peekFirst());
        log.info("当前队列最大值: {}", queue.peekLast());

        // 性能测试
        int testSize = 100000;
        MinMaxPriorityQueue<Integer> testQueue = MinMaxPriorityQueue.maximumSize(testSize)
                .create();

        // 插入性能测试
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < testSize; i++) {
            testQueue.add(i);
        }
        long endTime = System.currentTimeMillis();
        log.info("插入{}个元素耗时: {}ms", testSize, (endTime - startTime));

        // 获取最大最小值性能测试
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            testQueue.peekFirst();
            testQueue.peekLast();
        }
        endTime = System.currentTimeMillis();
        log.info("获取最大最小值1000次耗时: {}ms", (endTime - startTime));
    }
}
