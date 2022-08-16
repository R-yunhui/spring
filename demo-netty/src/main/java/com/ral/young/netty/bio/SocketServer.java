package com.ral.young.netty.bio;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * bio socket server
 *
 * @author renyunhui
 * @date 2022-08-16 9:04
 * @since 1.0.0
 */
@Slf4j
public class SocketServer {

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), ThreadFactoryBuilder.create().setNamePrefix("test-").build());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(9000)) {
            while (true) {
                log.info("socket server 等待连接.....");

                // 没有客户端连接，也会阻塞在这里
                Socket socketClient = serverSocket.accept();

                log.info("有客户端连接.....");

                // 单线程版本：服务端在同一时刻只能和一个客户端创建连接，进行处理  -  可能导致线程数量过多
                // 使用线程池进行优化，依然存在大量连接阻塞的情况（可能导致连接超时等等问题）
                // 可能客户端连接完成之后，一直没有数据发送，导致线程一直被此客户端连接占用，资源浪费
                dealClientData(socketClient);

                // 每次有一个客户端进行连接，使用一个线程去异步处理
                EXECUTOR.execute(() -> dealClientData(socketClient));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void dealClientData(Socket socketClient) {
        try {
            byte[] bytes = new byte[1024];
            // 读取客户端数据，没有数据的话会阻塞在这里
            int read = socketClient.getInputStream().read(bytes);
            log.info("读取数据完毕.....");
            if (read != -1) {
                log.info("接收到的客户端数据:{}", new String(bytes, 0, read));
            }
            // 返回客户端数据
            socketClient.getOutputStream().write("hello client".getBytes());
            socketClient.getOutputStream().flush();
        } catch (Exception e) {
            log.error("处理客户端消息异常,errorMsg:{}", e.getMessage(), e);
        }
    }
}
