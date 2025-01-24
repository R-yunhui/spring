package com.ral.young.spring.demo.converter.main;

import com.ral.young.spring.demo.converter.service.FormatConversionService;

/**
 * @author renyunhui
 * @description 这是一个Example类
 * @date 2025-01-24 16-15-00
 * @since 1.0.0
 */
public class Example {

    public static void main(String[] args) {
        FormatConversionService service = new FormatConversionService(4);

        try {
            // 批量转换文件
            service.batchConvert(
                    "C:\\Users\\Administrator\\Desktop\\学习中心v1.5\\已标注样例数据",
                    "C:\\Users\\Administrator\\Desktop\\学习中心v1.5\\已标注样例数据\\json-xml-output",
                    true  // true表示JSON转XML，false表示XML转JSON
            );

            // 等待
            Thread.sleep(20 * 1000L);

            // 批量转换文件
            service.batchConvert(
                    "C:\\Users\\Administrator\\Desktop\\学习中心v1.5\\已标注样例数据\\json-xml-output",
                    "C:\\Users\\Administrator\\Desktop\\学习中心v1.5\\已标注样例数据\\xml-json-output",
                    false  // true表示JSON转XML，false表示XML转JSON
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // 关闭服务
            service.shutdown(true);
        }
    }
}
