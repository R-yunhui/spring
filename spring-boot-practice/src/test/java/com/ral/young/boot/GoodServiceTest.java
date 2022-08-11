package com.ral.young.boot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author renyunhui
 * @date 2022-08-03 9:47
 * @since 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GoodServiceTest {

    @Resource
    private RestTemplate restTemplate;

    @Test
    public void queryAllGoods() {
        // 模拟并发
        int size = 50;
        for (int i = 0; i < size; i++) {
            restTemplate.exchange("http://127.0.0.1:8080/goods/queryAllGoods", HttpMethod.GET, null, Void.class);
        }
    }
}
