package com.zxm.libnetty;

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
     * Shut down the event loop to terminate all threads.
     * <p>
     * Notice:You must do this.
     * </p>
     */
    void onShutDown();

}
