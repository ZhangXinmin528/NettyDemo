package com.zxm.nettydemo.util;

import android.util.Log;

import com.zxm.nettydemo.BuildConfig;


/**
 * Created by ZhangXinmin on 2018/5/24.
 * Copyright (c) 2018 . All rights reserved.
 * 日志工具
 */
public class Logger {
    private static final String LOG_TAG = "NettyClient";

    public static final boolean LOG_ENABLE = BuildConfig.LOG_ENABLE;


    /**
     * log.i
     *
     * @param msg
     */
    public static void i(String msg) {
        i(LOG_TAG, msg);
    }

    /**
     * log.i
     *
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg) {
        if (LOG_ENABLE) {
            Log.i(tag, msg);
        }
    }

    /**
     * log.v
     *
     * @param msg
     */
    public static void v(String msg) {
        v(LOG_TAG, msg);
    }

    /**
     * log.v
     *
     * @param tag
     * @param msg
     */
    public static void v(String tag, String msg) {
        if (LOG_ENABLE) {
            Log.v(tag, msg);
        }
    }

    /**
     * log.d
     *
     * @param msg
     */
    public static void d(String msg) {
        d(LOG_TAG, msg);
    }

    /**
     * log.d
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg) {
        if (LOG_ENABLE) {
            Log.d(tag, msg);
        }
    }

    /**
     * log.w
     *
     * @param msg
     */
    public static void w(String msg) {
        w(LOG_TAG, msg);
    }

    /**
     * log.w
     *
     * @param tag
     * @param msg
     */
    public static void w(String tag, String msg) {
        if (LOG_ENABLE) {
            Log.w(tag, msg);
        }
    }

    /**
     * log.e
     *
     * @param msg
     */
    public static void e(String msg) {
        e(LOG_TAG, msg);
    }

    /**
     * log.e
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        if (LOG_ENABLE) {
            Log.e(tag, msg);
        }
    }
}