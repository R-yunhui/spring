package com.ral.young.study.designpattern.behavioral.policypattern;

/**
 * 通过建造者模式去构建责任链
 *
 * @author renyunhui
 * @date 2023-12-04 10:14
 * @since 1.0.0
 */
public abstract class LoginCheckHandler {

    protected LoginCheckHandler next;

    public void next(LoginCheckHandler handler) {
        this.next = handler;
    }

    public abstract void doHandler(LoginUser loginUser);

    public static class Builder {

        private LoginCheckHandler head;

        private LoginCheckHandler tail;

        public Builder addHandler(LoginCheckHandler handler) {
            if (null == head) {
                // head == null表示第一次添加到队列
                tail = handler;
                head = tail;
                return this;
            }
            // 原tail节点指向新添加进来的节点
            tail.next(handler);
            // 新添加进来的节点设置为tail节点
            tail = handler;
            return this;
        }

        public LoginCheckHandler build() {
            return this.head;
        }
    }
}
