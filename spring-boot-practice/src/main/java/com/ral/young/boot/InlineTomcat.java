package com.ral.young.boot;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletRegistration;
import java.util.Objects;

/**
 * 内嵌 tomcat 示例
 *
 * @author renyunhui
 * @date 2022-07-12 14:48
 * @since 1.0.0
 */
@Slf4j
public class InlineTomcat {

    public static void main(String[] args) {
        Tomcat tomcat = new Tomcat();
        String baseDir = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        tomcat.setPort(8090);

        try {
            Context context = tomcat.addContext("/ryh", baseDir);
            context.addServletContainerInitializer((c, servletContext) -> {
                ServletRegistration.Dynamic inlineServlet = servletContext.addServlet("inlineServlet", new MyServlet());
                inlineServlet.addMapping("/test");
            }, null);

            // 启动 tomcat
            tomcat.start();
        } catch (Exception e) {
            log.error("启动内嵌tomcat 失败,errorMsg:{}", e, e);
        }

        // 挂起 tomcat
        tomcat.getServer().await();
    }
}
