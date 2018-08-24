package com.zxm.nettydemo;

import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.print.PrinterId;
import android.text.InputFilter;
import android.text.TextUtils;
import android.widget.PopupMenu;

import com.zxm.nettydemo.handler.NettyClientHandler;
import com.zxm.nettydemo.listener.OnConnectStatusListener;
import com.zxm.nettydemo.listener.OnDataReceiveListener;
import com.zxm.nettydemo.util.Logger;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * Created by ZhangXinmin on 2018/8/23.
 * Copyright (c) 2018 . All rights reserved.
 */
public final class NettyClient implements INettyClient, OnConnectStatusListener {
    private static final String TAG = NettyClient.class.getSimpleName();
    private final String ACTION_SEND_TYPE = "action_send_type";
    private final String ACTION_SEND_MSG = "action_send_msg";
    private final int MESSAGE_INIT = 0x1;
    private final int MESSAGE_CONNECT = 0x2;
    private final int MESSAGE_SEND = 0x3;

    //bootstrap a Channel
    private Bootstrap mBootstrap;
    //capable of I/O operations
    private Channel mChannel;
    private String mHost;
    private int mPort;
    private HandlerThread workThread;
    private Handler mWorkHandler;
    private NettyClientHandler mClientHandler;

    private Handler.Callback mWorkHandlerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_INIT:
                    NioEventLoopGroup group = new NioEventLoopGroup();
                    mBootstrap = new Bootstrap();
                    mBootstrap.channel(NioSocketChannel.class);
                    mBootstrap.group(group);
                    mBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new LineBasedFrameDecoder(Integer.MAX_VALUE));
                            pipeline.addLast(mClientHandler);

                        }
                    });
                    break;
                case MESSAGE_CONNECT:
                    if (TextUtils.isEmpty(mHost) || mPort == 0) {
                        throw new RuntimeException("Socket host or port is illegal!");
                    }
                    try {
                        mChannel = mBootstrap.connect(new InetSocketAddress(mHost, mPort))
                                .sync().channel();
                    } catch (InterruptedException e) {
                        Logger.e("handle mesage..socket connected failed:" + e.getMessage());
                        e.printStackTrace();
                        //进行重新连接
                    }
                    break;
                case MESSAGE_SEND:
                    final Bundle bundle = msg.getData();
                    if (bundle != null) {
                        final String content = bundle.getString(ACTION_SEND_MSG, "");
                        final int type = bundle.getInt(ACTION_SEND_TYPE, -1);
                        Logger.d("handle mesage..socket send message to server:content=[" + content + "] type=[" + type + "]");

                        if (mChannel != null && mChannel.isOpen()) {
                            try {
                                //需要和后台协商数据传输方式
                                mChannel.writeAndFlush(content).sync();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                Logger.e(TAG, "Socket send message failed!");
                            }
                        }
                    }
                    break;
            }
            return true;
        }
    };
    private static NettyClient INSTANCE;

    public static synchronized NettyClient getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NettyClient();
        }
        return INSTANCE;
    }

    private NettyClient() {
        initParams();
    }

    private void initParams() {
        workThread = new HandlerThread(NettyClient.class.getName());
        workThread.start();
        mWorkHandler = new Handler(workThread.getLooper(), mWorkHandlerCallback);
        mClientHandler = new NettyClientHandler();
        //添加连接状态监听
        mClientHandler.setConnectStatusListener(this);
        mWorkHandler.sendEmptyMessage(MESSAGE_INIT);
    }

    @Override
    public void onConnect(String host, int port) {
        mHost = host;
        mPort = port;
        Logger.d("onConnect()..Socket start to connect:host=[" + mHost + "] port=[" + port + "]");
        mWorkHandler.sendEmptyMessage(MESSAGE_CONNECT);
    }

    @Override
    public void sendMessage(int mt, String msg, long delayed) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Logger.d("sendMessage()..socket send message to server:content=[" + msg + "] type=[" + mt + "]");
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        message.what = MESSAGE_SEND;
        bundle.putString(ACTION_SEND_MSG, msg);
        bundle.putInt(ACTION_SEND_TYPE, mt);
        message.setData(bundle);
        mWorkHandler.sendMessageDelayed(message, delayed);
    }

    @Override
    public void onReconnect() {
        Logger.d("onReconnect()..Socket on reconnect!");
        mWorkHandler.sendEmptyMessageDelayed(MESSAGE_CONNECT, Constant.DELAY_MILLIS);
    }

    @Override
    public void addDataReceiveListener(OnDataReceiveListener listener) {
        if (mClientHandler != null) {
            mClientHandler.addDataReceiveListener(listener);
        }
    }

    @Override
    public void onDisconnected() {
        Logger.d("onDisconnected()..Socket disconnect!");
        //重新建立连接
        onReconnect();
    }
}
