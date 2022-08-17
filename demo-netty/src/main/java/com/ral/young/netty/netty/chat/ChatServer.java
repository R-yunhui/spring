package com.ral.young.netty.netty.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 聊天室服务端
 *
 * @author renyunhui
 * @date 2022-08-17 8:46
 * @since 1.0.0
 */
@Slf4j
public class ChatServer {

    static final int SERVER_PORT = 9000;

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup(8);

        try {
            // 服务端启动
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workGroup).option(ChannelOption.SO_BACKLOG, 1024).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline()
                            // 编解码 handler
                            .addLast("decoder", new StringDecoder()).addLast("encoder", new StringEncoder()).addLast(new ChatServerHandler());
                }
            });

            // 服务端绑定端口
            ChannelFuture channelFuture = serverBootstrap.bind(SERVER_PORT).sync();

            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("=====              =====");
                    log.info("=====              =====");
                    log.info("===== 聊天室启动完毕 =====");
                    log.info("=====              =====");
                    log.info("=====              =====");
                }
            });

        } catch (Exception e) {
            log.error("聊天室异常,errorMsg:{}", e.getMessage(), e);
        }
    }
}
