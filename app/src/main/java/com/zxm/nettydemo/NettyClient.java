package com.zxm.nettydemo;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.zxm.nettydemo.handler.NettyClientHandler;
import com.zxm.nettydemo.listener.OnConnectStatusListener;
import com.zxm.nettydemo.listener.OnDataReceiveListener;
import com.zxm.nettydemo.util.FormatUtil;
import com.zxm.nettydemo.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.CharsetUtil;

/**
 * Created by ZhangXinmin on 2018/8/23.
 * Copyright (c) 2018 . All rights reserved.
 * 1.Please confirm the network is available before use it.使用之前确认网络已连接
 */
public final class NettyClient implements INettyClient {
    private static final String TAG = NettyClient.class.getSimpleName();

    private static final boolean SSL = System.getProperty("ssl") != null;
    private SslContext sslCtx;
    //bootstrap a Channel
    private Bootstrap mBootstrap;
    //capable of I/O operations
    private Channel mChannel;
    private EventLoopGroup mEventLoopGroup;
    private String mHost;
    private int mPort;
    private NettyClientHandler mClientHandler;

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

        if (SSL) {
            try {
                sslCtx = SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            } catch (SSLException e) {
                e.printStackTrace();
                sslCtx = null;
            }
        } else {
            sslCtx = null;
        }
        mClientHandler = new NettyClientHandler();

    }

    /**
     * 设置连接状态监听
     * 在onConfig之前调用该方法
     *
     * @param connectStatusListener
     */
    public INettyClient setConnectStatusListener(@NonNull OnConnectStatusListener connectStatusListener) {
        //添加连接状态监听
        mClientHandler.setConnectStatusListener(connectStatusListener);
        return this;
    }

    @Override
    public INettyClient onConfig(String host, int port) {
        mHost = host;
        mPort = port;
        Logger.d("onConnected()..Socket start to connect:host=[" + mHost + "] port=[" + port + "]");
        //configure
        mEventLoopGroup = new NioEventLoopGroup();
        mBootstrap = new Bootstrap();
        mBootstrap.group(mEventLoopGroup);
//        mBootstrap.option(ChannelOption.TCP_NODELAY,true);//使用一次大数据
        mBootstrap.option(ChannelOption.SO_KEEPALIVE, true);//实现长连接
        //连接超时时间
        mBootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        //指定NIO方式
        mBootstrap.channel(NioSocketChannel.class);
        mBootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                if (sslCtx != null) {
                    pipeline.addLast(sslCtx.newHandler(ch.alloc(), mHost, mPort));
                }
//                pipeline.addLast( new StringEncoder(CharsetUtil.UTF_8));
                pipeline.addLast(mClientHandler);
            }
        });
        return this;
    }

    @Override
    public INettyClient onConnect() {
        //start client
        if (TextUtils.isEmpty(mHost) || mPort == 0) {
            Logger.e("Please configure socket host and port!");
            throw new RuntimeException("Socket host or port is illegal!");
        }
        new Handler()
                .post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mBootstrap.connect(new InetSocketAddress(mHost, mPort))
                                    .addListener(new ChannelFutureListener() {
                                        @Override
                                        public void operationComplete(ChannelFuture future) throws Exception {
                                            if (future != null) {
                                                if (future.isSuccess()) {
                                                    mChannel = future.channel();
                                                    //连接成功
                                                    Logger.d("socket connected success:isActive()?-->"
                                                            + mChannel.isActive());

                                                } else {
                                                    //连接失败
                                                    Logger.d(TAG, "socket connected failed");
                                                }
                                            }
                                        }
                                    })
                                    .sync();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Logger.e("onConnected()..socket connected failed:" + e.getMessage());
                        }
                    }
                });

        return this;
    }

    @Override
    public INettyClient onReconnect() {
        Logger.d("onReconnect()");
        if (mChannel != null) {
            mChannel = null;
        }
        try {

            mChannel = mBootstrap.connect(new InetSocketAddress(mHost, mPort))
                    .sync().channel();

            if (mChannel != null && mChannel.isOpen()) {
                Logger.d("onReconnect()..socket reconnect success..isActive()?-->" + mChannel.isActive());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.d("onReconnect()..socket reconnected failed:" + e.getMessage());
        }

        return this;
    }

    @Override
    public INettyClient onPostCommand(final int command) {
        if (mChannel != null) {
            //获取十六进制串
            final String temp = FormatUtil.algorismToHEXString(command);
            final byte[] b = FormatUtil.hex2byte(temp);
            final ByteBuf msg = Unpooled.buffer(b.length);
            msg.writeBytes(b);
            //结束
            mChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future != null && future.isSuccess()) {
                        Logger.d("onPostCommand()..command[" + command + "] success");
                    }
                }
            });
        }
        return this;
    }

    //主动关闭连接
    @Override
    public INettyClient onClose() {
        Logger.d("onClose()..Socket is going to close..isOpen()?-->" + (mChannel == null ? null : mChannel.isOpen()));
        new Handler()
                .post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (mChannel != null && mChannel.isOpen()) {
                                mChannel.close().sync();
//                                mChannel.closeFuture().sync();//谨慎会ANR
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            // Shut down the event loop to terminate all threads.
                            //因为有重连不需要此操作
                            /*if (mEventLoopGroup != null) {
                                Logger.d("onClose()..shutdownGracefully");
                                mEventLoopGroup.shutdownGracefully();
                            }*/
                        }
                    }
                });

        return this;
    }

    @Override
    public void onShutDown() {
        Logger.d("onShutDown()");
        if (mChannel != null && mChannel.isOpen()) {
            try {
                mChannel.close().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (mEventLoopGroup != null) {
            Logger.d("onShutDown()..shutdownGracefully");
            mEventLoopGroup.shutdownGracefully();
        }
    }

    @Override
    public void addDataReceiveListener(OnDataReceiveListener listener) {
        if (mClientHandler != null) {
            mClientHandler.addDataReceiveListener(listener);
        }
    }
}
