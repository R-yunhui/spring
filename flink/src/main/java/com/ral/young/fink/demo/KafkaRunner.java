package com.ral.young.fink.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * TODO
 *
 * @author renyunhui
 * @date 2023-10-07 14:01
 * @since 1.0.0
 */
@Component
@Slf4j
public class KafkaRunner implements ApplicationRunner {


    @Override
    public void run(ApplicationArguments args) {

    }

    public static void main(String[] args) {
        try {
            // 创建执行环境
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

            // 创建数据源
            DataStream<String> stream = env.addSource(new SourceFunction<String>() {
                private volatile boolean isRunning = true;

                @Override
                public void run(SourceContext<String> ctx) throws Exception {
                    Random random = new Random();
                    while (isRunning) {
                        Thread.sleep(10);
                        long timestamp = System.currentTimeMillis() - random.nextInt(5) * 1000;
                        String str = "key" + random.nextInt(10) + "," + timestamp;
                        ctx.collectWithTimestamp(str, timestamp);
                        ctx.emitWatermark(new Watermark(timestamp));
                    }
                }

                @Override
                public void cancel() {
                    isRunning = false;
                }
            });


            // 将数据源解析成二元组（key, timestamp）
            DataStream<Tuple2<String, Long>> parsedStream = stream.map((String line) -> {
                String[] parts = line.split(",");
                return new Tuple2<>(parts[0], Long.parseLong(parts[1]));
            }).returns(Types.TUPLE(Types.STRING, Types.LONG));

            // 设置事件时间和水位线
            DataStream<Tuple2<String, Long>> withTimestampsAndWatermarks = parsedStream.assignTimestampsAndWatermarks(new AscendingTimestampExtractor<Tuple2<String, Long>>() {
                @Override
                public long extractAscendingTimestamp(Tuple2<String, Long> element) {
                    return element.f1;
                }
            });

            // 按键值进行分组
            KeyedStream<Tuple2<String, Long>, Tuple> keyedStream = withTimestampsAndWatermarks.keyBy(0);

            // 每5秒钟统计最近一分钟的数据（使用滚动时间窗口）
            WindowedStream<Tuple2<String, Long>, Tuple, TimeWindow> windowedStream = keyedStream.window(TumblingEventTimeWindows.of(Time.minutes(1)));

            // 进行聚合计算
            DataStream<Tuple2<String, Long>> resultStream = windowedStream.reduce((Tuple2<String, Long> v1, Tuple2<String, Long> v2) -> new Tuple2<>(v1.f0, v1.f1 + v2.f1));

            // 输出结果
            resultStream.print();

            // 启动作业
            env.execute("Demo");
        } catch (Exception e) {
            log.error("处理数据失败,errorMsg:", e);
        }
    }
}


