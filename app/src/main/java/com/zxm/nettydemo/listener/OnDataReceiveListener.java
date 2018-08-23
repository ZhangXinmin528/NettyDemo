package com.zxm.nettydemo.listener;

/**
 * Created by ZhangXinmin on 2018/8/23.
 * Copyright (c) 2018 . All rights reserved.
 * 数据接收监听
 */
public interface OnDataReceiveListener {
    void onDataReceive(int mt, String json);
}
