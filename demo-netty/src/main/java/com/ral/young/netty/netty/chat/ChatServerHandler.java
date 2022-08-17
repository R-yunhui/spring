package com.ral.young.netty.netty.chat;

import cn.hutool.core.date.DateUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * char server handler
 *
 * @author renyunhui
 * @date 2022-08-17 8:55
 * @since 1.0.0
 */
@Slf4j
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 客户端和服务端建立连接会调用此方法
     *
     * @param ctx 管道处理程序上下文
     * @throws Exception 异常
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String msg = "[ 客户端 ] : " + channel.remoteAddress() + " 上线了 , 上线时间 " + DateUtil.now();
        CHANNEL_GROUP.forEach(channel1 -> {
            if (!channel.equals(channel1)) {
                channel1.writeAndFlush(msg);
            }
        });
        // 将上线的客户端加入集合中
        CHANNEL_GROUP.add(channel);
        log.info(msg);
    }

    /**
     * 客户端和服务端断开连接调用此方法
     *
     * @param ctx 管道处理程序上下文
     * @throws Exception 异常
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String msg = "[ 客户端 ] : " + channel.remoteAddress() + " 下线了, 下线时间 " + DateUtil.now();
        CHANNEL_GROUP.forEach(channel1 -> {
            if (!channel.equals(channel1)) {
                channel1.writeAndFlush(msg);
            }
        });
        // 移除下线的客户端
        CHANNEL_GROUP.remove(channel);
        log.info(msg);
    }

    /**
     * 处理客户端发送的消息
     *
     * @param ctx 管道处理程序上下文
     * @param msg 客户端发送的消息
     * @throws Exception 异常
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();
        CHANNEL_GROUP.forEach(channel1 -> {
            if (channel.equals(channel1)) {
                channel1.writeAndFlush("[ 自己 ] : " + "发送了消息 : " + msg + "\n");
            } else {
                channel1.writeAndFlush("[ 客户端 ] : " + channel1.remoteAddress() + " 发送了消息 : " + msg + "\n");
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("netty run error,errorMsg:{}", cause.getMessage());
        ctx.channel().closeFuture().sync();
    }
}
