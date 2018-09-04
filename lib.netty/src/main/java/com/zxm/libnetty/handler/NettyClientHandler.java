package com.zxm.libnetty.handler;

import com.zxm.libnetty.listener.OnConnectStatusListener;
import com.zxm.libnetty.listener.OnDataReceiveListener;
import com.zxm.libnetty.util.FormatUtil;
import com.zxm.libnetty.util.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by ZhangXinmin on 2018/8/23.
 * Copyright (c) 2018 . All rights reserved.
 * 处理客户端发往服务器的报文，执行编解码，读取客户端数据，进行业务处理；
 */
@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private static final String TAG = NettyClientHandler.class.getSimpleName();

    private OnConnectStatusListener connectStatusListener;

    private List<OnDataReceiveListener> listeners = new ArrayList<>();

    public NettyClientHandler() {
    }

    public NettyClientHandler(OnConnectStatusListener connectStatusListener) {
        this.connectStatusListener = connectStatusListener;
    }

    //注册
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        Logger.d(TAG, "channel-->[id=" + ctx.channel().id() + "]" + " registered");
    }

    //该方法会在连接被建立并且准备通信时被调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Logger.d(TAG, "channel-->[id=" + ctx.channel().id() + "]" + " active");
        super.channelActive(ctx);
        if (connectStatusListener != null) {
            connectStatusListener.onConnected();

        }
    }

    //该方法在接收服务端数据时调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final ByteBuf byteBuf = (ByteBuf) msg;
        Logger.e(TAG, "数据类型：" + msg.getClass().getSimpleName());
        final byte[] b = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(b);
        Logger.e(TAG, "获取的byte数据：" + Arrays.toString(b));
        final String command = FormatUtil.byte2hex(b);
        Logger.d(TAG, "channel-->[id=" + ctx.channel().id() + "] start to read date:" + command);
        if (connectStatusListener != null) {
            connectStatusListener.onReceiveData(command);
        }
    }

    //断开连接：客户端或者服务器断开连接时会调用
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Logger.d(TAG, "channel-->[id=" + ctx.channel().id() + "] socket channel is inactive");
        if (connectStatusListener != null) {
            connectStatusListener.onDisconnected();
        }
    }

    //出现异常时调用
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        //把出现异常的关联channel关闭
        ctx.close();
        Logger.e(TAG, "channel-->[id=" + ctx.channel().id() + "]" + " got an exception:" + cause.getMessage());
        if (connectStatusListener != null) {
            connectStatusListener.onDisconnected();
        }
    }

    //数据读取完毕
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        Logger.d(TAG, "channel-->[id=" + ctx.channel().id() + "]" + " read complete");
    }

    //解除注册
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        Logger.d(TAG, "channel-->[id=" + ctx.channel().id() + "]" + " unregistered");
    }

    //心跳超时事件：
    //当客户端所有ChannelHandler指定时间内没有write事件时，会触发该方法。
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        Logger.d(TAG, "channel-->[id=" + ctx.channel().id() + "]" + " user event triggered");
    }

    /**
     * 设置连接状态监听
     *
     * @param connectStatusListener
     */
    public void setConnectStatusListener(OnConnectStatusListener connectStatusListener) {
        this.connectStatusListener = connectStatusListener;
    }

    /**
     * 添加数据接收器
     *
     * @param listener
     */
    public void addDataReceiveListener(OnDataReceiveListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }
}
