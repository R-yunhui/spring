package com.ral.young.spring.demo;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import java.util.Optional;

/**
 * @author renyunhui
 * @description 这是一个JsonPathDemo类
 * @date 2025-01-20 17-24-08
 * @since 1.0.0
 */
public class JsonPathDemo {

    public static void main(String[] args) {
        /*
         * JSONPath 是一种用于从 JSON 文档中提取数据的表达式语法。它提供了一种灵活的方式来访问 JSON 对象中的特定部分。以下是 JSONPath 的主要语法元素：
         * 基本语法
         *   $：根对象/元素。
         *   @：当前节点对象（通常在过滤器中使用）。
         *
         * 子元素和属性访问
         *   . 或 []：子操作符，用于访问子节点。例如，$.store.book 或 $['store']['book']。
         *   ..：递归下降。无论位置如何，都获取所有符合条件的元素。例如，$..author 获取文档中所有的 author。
         *
         * 数组索引和切片操作
         *   [index]：访问数组中的特定索引。索引从 0 开始。例如，$.store.book[0]。
         *   [start:end]：数组切片操作，获取从 start 索引到 end 索引之前的元素。例如，$.store.book[1:3]。
         *   [start:end:step]：步长为 step 的数组切片操作。例如，$.store.book[0:4:2]。
         *   [-index]：从数组末尾开始的反向索引。例如，$.store.book[-1] 是最后一个元素。
         *
         * 过滤表达式
         *   [?(expression)]：过滤表达式，用于筛选数组中满足条件的元素。例如，$.store.book[?(@.price < 10)]。
         *
         * 通配符和其他操作符
         *   *：通配符，匹配所有元素。可以用在对象或数组上。例如，$.store.* 或 $.store.book[*].author。
         *   ,：多个名称或数组索引作为并列引用。例如，$.store.book[0,1] 或 $.store.book[0,1].title。
         *   ()：脚本表达式，用于在路径中使用表达式。例如，$.store.book[(@.length-1)]。
         */
        String json = "{ \"store\": { \"book\": [{ \"category\": \"reference\", \"author\": \"Nigel Rees\", \"title\": \"Sayings of the Century\", \"price\": 8.95 }, { \"category\": \"fiction\", \"author\": \"Evelyn Waugh\", \"title\": \"Sword of Honour\", \"price\": 12.99 }, { \"category\": \"fiction\", \"author\": \"Herman Melville\", \"title\": \"Moby Dick\", \"isbn\": \"0-553-21311-3\", \"price\": 8.99 }, { \"category\": \"fiction\", \"author\": \"J. R. R. Tolkien\", \"title\": \"The Lord of the Rings\", \"isbn\": \"0-395-19395-8\", \"price\": 22.99 }], \"bicycle\": { \"color\": \"red\", \"price\": 19.95 } } }";

        ReadContext ctx = JsonPath.parse(json);

        // 获取所有书的作者属性
        System.out.println("Authors of all books:");
        System.out.println(Optional.ofNullable(ctx.read("$.store.book[*].author")));

        // 获取所有商品的价格属性
        System.out.println("\nPrices of all items:");
        System.out.println(Optional.ofNullable(ctx.read("$..price")));

        // 获取包含 isbn 属性的所有书
        System.out.println("\nBooks with ISBN:");
        System.out.println(Optional.ofNullable(ctx.read("$.store.book[?(@.isbn)]")));

        // 获取价格属性低于 10 的所有书
        System.out.println("\nBooks with price less than 10:");
        System.out.println(Optional.ofNullable(ctx.read("$.store.book[?(@.price < 10)]")));
    }
}
