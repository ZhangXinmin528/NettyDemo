package com.zxm.libnetty.listener;

/**
 * Created by ZhangXinmin on 2018/8/23.
 * Copyright (c) 2018 . All rights reserved.
 * 连接异常时触发
 */
public interface OnConnectStatusListener {
    /**
     * 开始通信
     */
    void onConnected();

    /**
     * 开始接收数据
     */
    void onReceiveData(String data);

    /**
     * 断开通信
     */
    void onDisconnected();
}
