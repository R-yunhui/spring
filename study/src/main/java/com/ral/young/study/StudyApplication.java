package com.ral.young.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * @author renyunhui
 * @date 2023-11-20 14:21
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = {"com.ral.young.study.cacheconsistency"})
public class StudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyApplication.class, args);
    }
}
