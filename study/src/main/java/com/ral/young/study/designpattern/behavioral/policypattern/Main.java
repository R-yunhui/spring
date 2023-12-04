package com.ral.young.study.designpattern.behavioral.policypattern;

/**
 * 责任链模式
 *
 * @author renyunhui
 * @date 2023-12-04 10:03
 * @since 1.0.0
 */
public class Main {

    public static void main(String[] args) {
        /*
         * 什么是责任链模式
         * 责任链模式(Chain of Responsibility Pattern)是指将链中的每一个节点看作是一个对象，每个节点处理的请求均不同，
         * 且每个节点内部自动维护了一个下一个节点对象。当一个请求在链路的头部发出时，会沿着链的路径依次传递给每一个节点对象，直到有对象处理这个请求为止。
         * 责任链模式属于行为型模式。
         *
         * 责任链模式角色
         * 1、抽象处理者(Handler):：定义一个请求处理的方法，并维护一个下一个处理节点的Handler对象
         * 2、具体处理者(ConcreteHandler)：对请求就行处理，只处理自己部分，处理完之后可以进行转发
         *
         * 责任链模式适用场景
         * 责任链模式主要是解耦了请求与处理，用户只需要将请求发送到链上即可，无需关心请求的具体内容和处理细节，请求会自动进行传递直至有节点进行处理。可以适用于如下场景：
         * 1、多个对象可以处理同一请求，但具体由哪个对象处理则在运行时动态决定。
         * 2、在不明确指定接收者的情况下，向多个对象中的一个提交请求
         * 3、可以动态指定一组对象的处理请求。
         *
         * 责任链模式优缺点
         * 优点
         * 1、将请求与处理解耦
         * 2、请求处理者(链路中的节点)只需关注自己感兴趣的请求进行处理，对于不感兴趣或者无法处理的请求直接转发给下一个处理者
         * 3、具备链式传递请求的功能，请求发送者无需知晓链路结构，只需等待请求处理结果
         * 4、链路结构灵活，可以通过改变链路结构动态的新增或者删减责任
         * 5、易于扩展新的请求处理类，符合开闭原则
         * 缺点
         * 1、如果责任链的链路太长或者处理时间过程，会影响性能。
         * 2、如果节点对象存在循环引用时，会造成死循环，导致系统崩溃\
         *
         * 实际使用场景：
         * 1.SpringAop jdk动态代理和Cglib动态代理，处理切面方法的时候使用责任链的方式
         * 2.Servlet 的 FilterChain
         * 3.Spring 的 MockFilterChain 实现 FilterChain
         */
        LoginUser loginUser = new LoginUser();
        loginUser.setUsername("test");
        loginUser.setPassword("123456");
        loginUser.setPermission(1);
        loginUser.setRole(1);

        // 通过建造者模式构建责任链
        LoginCheckHandler loginCheckHandler = new LoginCheckHandler.Builder()
                .addHandler(new AccountCheckHandler())
                .addHandler(new RoleCheckHandler())
                .addHandler(new PermissionCheckHandler())
                .build();

        // 责任链调用
        loginCheckHandler.doHandler(loginUser);
    }
}
