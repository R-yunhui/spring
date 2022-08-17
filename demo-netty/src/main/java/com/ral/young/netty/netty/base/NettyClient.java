package com.ral.young.netty.netty.base;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * netty client
 *
 * @author renyunhui
 * @date 2022-08-16 16:40
 * @since 1.0.0
 */
@Slf4j
public class NettyClient {

    public static void main(String[] args) {
        // 客户端的线程组
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            // 客户端启动对象
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new NettyClientHandler());
                }
            });

            log.info("netty client start");
            // 启动客户端去连接服务器端
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9000).sync();

            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("netty client error,errorMsg:{}", e.getMessage(), e);
        }
    }
}
