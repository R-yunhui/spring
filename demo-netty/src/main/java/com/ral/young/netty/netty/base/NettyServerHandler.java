package com.ral.young.netty.netty.base;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * NettyServerHandler
 *
 * @author renyunhui
 * @date 2022-08-16 16:19
 * @since 1.0.0
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当通道有读事件发生的时候会回调
     *
     * @param ctx 管道处理上下文
     * @param msg 数据
     * @throws Exception 异常
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("服务端读取客户端的数据,线程:{}", Thread.currentThread().getName());
        ByteBuf byteBuf = (ByteBuf) msg;
        log.info("服务端读取客户端的数据:{}", byteBuf.toString(CharsetUtil.UTF_8));
    }

    /**
     * 当通道读事件处理完成之后回调
     *
     * @param ctx 管道处理上下文
     * @throws Exception 异常
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello client", CharsetUtil.UTF_8);
        ctx.writeAndFlush(byteBuf);
    }
}
