package com.zxm.nettydemo;

import android.telephony.VisualVoicemailService;

import com.zxm.nettydemo.listener.OnDataReceiveListener;

/**
 * Created by ZhangXinmin on 2018/8/23.
 * Copyright (c) 2018 . All rights reserved.
 */
public interface INettyClient {

    /**
     * Configure socket client
     *
     * @param host
     * @param port
     * @return INettyClient
     */
    INettyClient onConfig(String host, int port);

    /**
     * Client build the connection with the server;
     *
     * @return INettyClient
     */
    INettyClient onConnect();

    /**
     * Client build the connection with the server;
     *
     * @return INettyClient
     */
    INettyClient onReconnect();

    /**
     * send command to server
     *
     * @param command
     * @return
     */
    INettyClient onPostCommand(int command);

    /**
     * initiatively cut down the connection between the C/S
     *
     * @return INettyClient
     */
    INettyClient onClose();

    /**
     * 添加数据接收监听器
     *
     * @param listener
     */
    void addDataReceiveListener(OnDataReceiveListener listener);
}
