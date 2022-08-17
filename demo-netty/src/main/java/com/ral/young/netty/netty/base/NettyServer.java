package com.ral.young.netty.netty.base;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * netty 客户端
 *
 * @author renyunhui
 * @date 2022-08-16 16:01
 * @since 1.0.0
 */
@Slf4j
public class NettyServer {

    public static void main(String[] args) {
        // 创建两个线程组 bossGroup 和 workGroup
        // bossGroup 负责处理连接请求 - 内置多个 selector，和指定的线程数量挂钩
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // 默认的线程数量为 cpu 核数的 2倍，真正的和客户端业务交互，由 workGroup 完成 - 内置多个 selector，和指定的线程数量挂钩
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            // 创建服务端的启动对象
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 链式编程配置参数
            // 1.设置两个线程组
            // 2.初始化服务器连接队列的大小，服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接
            // 多个客户端同时来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理
            serverBootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    // 3.对 workerGroup 中的 SocketChannel 设置处理器
                    socketChannel.pipeline().addLast(new NettyServerHandler());
                }
            });

            log.info("netty server start");
            // 绑定一个端口并且同步，生成一个 ChannelFuture 异步对象，通过回调函数判断异步事件的执行情况
            // 启动服务器 - 并绑定端口，bind 是异步操作，sync 是等待异步操作执行完毕
            ChannelFuture channelFuture = serverBootstrap.bind(9000).sync();

            channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
                if (channelFuture1.isSuccess()) {
                    log.info("netty server 监听端口 9000 成功");
                } else {
                    log.info("netty server 监听端口 9000 失败");
                }
            });

            // 等待服务端监听端口关闭，closeFuture 是异步操作
            // 通过 sync 方法同步等待通道关闭处理完成，会阻塞等待通道关闭完成，内部调用的是 Object 的 wait 方法
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("netty server error,errorMsg:{}", e.getMessage(), e);
        }
    }
}
