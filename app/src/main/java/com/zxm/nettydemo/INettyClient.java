package com.zxm.nettydemo;

import android.telephony.VisualVoicemailService;

import com.zxm.nettydemo.listener.OnDataReceiveListener;

/**
 * Created by ZhangXinmin on 2018/8/23.
 * Copyright (c) 2018 . All rights reserved.
 */
public interface INettyClient {
    /**
     * Client build the connection with the server;
     *
     * @param host
     * @param port
     */
    void onConnect(String host, int port);

    /**
     * Client send message to server.
     *
     * @param mt
     * @param msg
     * @param delayed
     */
    void sendMessage(int mt, String msg, long delayed);

    /**
     * Client build the connection with the server;
     */
    void onReconnect();

    /**
     * 添加数据接收监听器
     *
     * @param listener
     */
    void addDataReceiveListener(OnDataReceiveListener listener);
}
