package com.ral.young.practice.function;

import java.util.function.Predicate;

/**
 * {@link java.util.function.Predicate}
 * <p>
 * 当需要对某种类型的数据进行判断，从而得到一个boolean值结果，这时可以使用java.util.function.Predicate<T> 接口
 *
 * @author renyunhui
 * @date 2023-05-25 14:33
 * @since 1.0.0
 */
public class PredicateDemo {

    public static void main(String[] args) {
        PredicateDemo predicateDemo = new PredicateDemo();
        String msg = "111";
        predicateDemo.testPredicateOne(o -> o.length() > 1, msg);
        predicateDemo.testPredicateTwo(o1 -> o1.length() > 1, o2 -> o2.contains("1"), msg);
        predicateDemo.testPredicateThree(o1 -> o1.length() > 5, o2 -> !o2.contains("1"), msg);
        predicateDemo.testPredicateFour(o1 -> o1.length() > 1, msg);

    }

    public void testPredicateOne(Predicate<String> predicate, String msg) {
        // 执行具体的函数进行判断操作
        boolean test = predicate.test(msg);
        System.out.println("testPredicate result：" + test);
    }

    public void testPredicateTwo(Predicate<String> predicateOne, Predicate<String> predicateTwo, String msg) {
        // 两个断言函数的结果取 &&
        boolean test = predicateOne.and(predicateTwo).test(msg);
        System.out.println("testPredicateTwo result：" + test);
    }

    public void testPredicateThree(Predicate<String> predicateOne, Predicate<String> predicateTwo, String msg) {
        // 两个断言函数的结果取 ||
        boolean test = predicateOne.or(predicateTwo).test(msg);
        System.out.println("testPredicateThree result：" + test);
    }

    public void testPredicateFour(Predicate<String> predicate, String msg) {
        // 断言函数结果取 !
        boolean test = predicate.negate().test(msg);
        System.out.println("testPredicateFour result：" + test);
    }
}
