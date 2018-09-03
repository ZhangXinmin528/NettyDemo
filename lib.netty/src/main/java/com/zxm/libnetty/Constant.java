package com.zxm.libnetty;

/**
 * Created by ZhangXinmin on 2018/8/23.
 * Copyright (c) 2018 . All rights reserved.
 */
public final class Constant {

    public static final long DELAY_MILLIS = 10000;

    /**
     * 开始命令
     */
    public static final int CMD_START = 0x7FED0101;

    /**
     * 开始命令-->确认指令
     */
    public static final String CMD_START_CONFIRM = "7FED0101";

    /**
     * 人脸识别异常命令
     */
    public static final int CMD_FACE_VERIFY_ERROR = 0x7FEC0101;

    /**
     * 心率异常命令
     */
    public static final int CMD_HEART_RATE_ERROR = 0x7FEC0102;

    /**
     * 退出命令
     */
    public static final int CMD_EXIT = 0x7FEA0101;

    /**
     * 退出命令-->确认指令
     */
    public static final String CMD_EXIT_CONFIRM = "7FEA0101";

}
