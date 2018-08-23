package com.zxm.nettydemo.handler;

import android.support.annotation.IdRes;

import com.zxm.nettydemo.listener.OnConnectStatusListener;
import com.zxm.nettydemo.listener.OnDataReceiveListener;
import com.zxm.nettydemo.util.Logger;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by ZhangXinmin on 2018/8/23.
 * Copyright (c) 2018 . All rights reserved.
 * 处理客户端发往服务器的报文，执行编解码，读取客户端数据，进行业务处理；
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private static final String TAG = NettyClientHandler.class.getSimpleName();

    private OnConnectStatusListener connectStatusListener;

    private List<OnDataReceiveListener> listeners = new ArrayList<>();

    //该方法会在连接被建立并且准备通信时被调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Logger.d(TAG, "channel-->[id=" + ctx.channel().id() + "]" + " active");
        super.channelActive(ctx);
    }

    //该方法在接收数据时调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        final String body = new String(req, "UTF-8");
        //需要和后台同事进行协商确定数据校验方法；
        Logger.d(TAG, "channel-->[id=" + ctx.channel().id() + "] start to read date:" + body);
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

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        Logger.d(TAG, "channel-->[id=" + ctx.channel().id() + "]" + " read complete");
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
