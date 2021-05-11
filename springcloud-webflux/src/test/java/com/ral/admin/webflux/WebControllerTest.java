package com.ral.admin.webflux;

import com.ral.admin.webflux.controller.WebController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.annotation.Resource;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-11 16:30
 * @Describe:
 * @Modify:
 */
@RunWith(SpringRunner.class)
@WebFluxTest(controllers = WebController.class)
public class WebControllerTest {

    @Resource
    private WebTestClient client;

    @Test
    public void sayHello() {
        client.get().uri("/sayHello").exchange().expectStatus().isOk();
    }
}
