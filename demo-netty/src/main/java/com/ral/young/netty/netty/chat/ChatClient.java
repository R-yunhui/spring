package com.ral.young.netty.netty.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/**
 * 聊天室客户端
 *
 * @author renyunhui
 * @date 2022-08-17 8:46
 * @since 1.0.0
 */
@Slf4j
public class ChatClient {

    public static void main(String[] args) {
        EventLoopGroup workGroup = new NioEventLoopGroup(8);

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(workGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline()
                            // 编解码器
                            .addLast("decoder", new StringDecoder()).addLast("encoder", new StringEncoder()).addLast(new ChatClientHandler());
                }
            });

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9000).sync();

            Scanner scanner = new Scanner(System.in);
            log.info("客户端连接服务端成功");
            while (scanner.hasNextLine()) {
                channelFuture.channel().writeAndFlush(scanner.nextLine());
            }
        } catch (Exception e) {
            log.error("聊天室客户端异常,errorMsg:{}", e.getMessage(), e);
        }
    }
}
