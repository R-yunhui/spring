package com.ral.young.netty.nio;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * nio socket server
 *
 * @author renyunhui
 * @date 2022-08-16 10:55
 * @since 1.0.0
 */
@Slf4j
public class NioSocketServer {

    public static void main(String[] args) throws IOException {
        // 创建 nio 的 server socketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9000));

        // 设置 serverSocketChannel 为非阻塞
        serverSocketChannel.configureBlocking(false);

        // 创建 selector - 多路复用器
        // linux 系统下使用 EpollSelectorImpl
        // linux 系统：底层调用了内核函数 epoll_create，向内核申请空间，创建epoll句柄，并返回一个非负数作为文件描述符，用于对epoll接口的所有后续调用。并添加到 epollWrapper 中
        Selector selector = Selector.open();
        // 将 serverSocketChannel 注册到 selector  -  OP_ACCEPT 连接事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        log.info("创建 nio serverSocketChannel 成功,绑定 selector 完成");

        while (true) {
            // 阻塞，等待有事件发生，事件驱动模型 - 有事件响应的时候，会退出阻塞，将响应的事件放到一个集合里面
            // linux 系统：底层调用了内核函数 epoll_ctl - 真正将 ServerSocketChannel 和 Selector 进行绑定，向 epfd 对应的内核 epoll 对象添加，修改，删除对 fd 上 event 的监听
            // 再调用内核函数 epoll_wait，将响应的事件和 selector 进行绑定，通过循环，不断地监听暴露的端口，看哪一个 epfd 可读、可写。
            selector.select();

            log.info("有事件发生:{}", DateUtil.now());

            // 获取 selector 中有事件发生 SelectionKey 集合，进行处理
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()) {
                    // 如果是 OP_ACCEPT
                    ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel socketChannel = server.accept();
                    socketChannel.configureBlocking(false);
                    // 注册读事件，如果需要给客户端发送数据可以注册写事件
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    log.info("客户端连接完成,客户端信息:{}", socketChannel.getLocalAddress());
                } else if (selectionKey.isReadable()) {
                    // 除了连接事件以外的事件，可以使用线程池进行优化，异步处理
                    // 读事件发生
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    // 1kb，使用直接内存的方式读取通道里面的数据
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    int read = socketChannel.read(byteBuffer);
                    if (read != -1) {
                        log.info("接收到的客户端消息:{}", new String(byteBuffer.array(), 0, read));
                    } else {
                        log.info("客户端断开连接");
                        socketChannel.close();
                    }
                }
                // 移除本次处理的事件
                iterator.remove();
            }
        }
    }
}
