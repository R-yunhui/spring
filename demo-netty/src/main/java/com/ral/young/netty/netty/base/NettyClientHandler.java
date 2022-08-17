package com.ral.young.netty.netty.base;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * NettyClientHandler
 *
 * @author renyunhui
 * @date 2022-08-16 16:44
 * @since 1.0.0
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当客户端连接服务端完成之后会回调
     *
     * @param ctx 管道处理上下文
     * @throws Exception 异常
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello server", CharsetUtil.UTF_8);
        ctx.writeAndFlush(byteBuf);
    }


    /**
     * 当通道有读事件发生的时候会回调
     *
     * @param ctx 管道处理上下文
     * @param msg 数据
     * @throws Exception 异常
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("客户端读取服务端的数据,线程:{}", Thread.currentThread().getName());
        ByteBuf byteBuf = (ByteBuf) msg;
        log.info("客户端读取服务端的数据:{}", byteBuf.toString(CharsetUtil.UTF_8));
    }
}
