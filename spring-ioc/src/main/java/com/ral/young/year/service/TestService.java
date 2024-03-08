package com.ral.young.year.service;

/**
 *
 * @author renyunhui
 * @date 2024-03-07 10:08
 * @since 1.0.0
 */
public class TestService {

    private String testName;

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public void test() {
        System.out.println("test service");
    }
}
