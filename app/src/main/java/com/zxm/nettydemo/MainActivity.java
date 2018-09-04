package com.zxm.nettydemo;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zxm.libnetty.Constant;
import com.zxm.libnetty.NettyClient;
import com.zxm.libnetty.listener.SimpleOnConnectStatusListener;
import com.zxm.libnetty.util.HeartUtil;
import com.zxm.libnetty.util.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.zxm.libnetty.Constant.*;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private EditText mIpEt;
    private EditText mPortEt;
    private EditText mMsgEt;
    private TextView mInfoTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initParamsAndValues();
        initViews();
    }

    private void initParamsAndValues() {
        mContext = this;
    }

    private void initViews() {
        mIpEt = findViewById(R.id.et_input_ip);
        mPortEt = findViewById(R.id.et_inout_port);
        mMsgEt = findViewById(R.id.et_inout_msg);
        findViewById(R.id.btn_build_connect).setOnClickListener(this);
        findViewById(R.id.btn_build_close).setOnClickListener(this);
        findViewById(R.id.btn_build_reconnect).setOnClickListener(this);

        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_exit).setOnClickListener(this);
        findViewById(R.id.btn_post_face).setOnClickListener(this);

        mInfoTv = findViewById(R.id.tv_info);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_build_connect:
                //客户端并建立连接
//                final String ip = mIpEt.getText().toString().trim();
//                final String port = mPortEt.getText().toString().trim();

                NettyClient.getInstance()
                        .setConnectStatusListener(new SimpleOnConnectStatusListener() {
                            @Override
                            public void onReceiveData(String data) {
                                super.onReceiveData(data);
                                dispatchCommand(data);
                            }
                        })
                        .onConfig(Config.CONFIG_HOST, Config.CONFIG_PORT)
                        .onConnect();
                break;
            //断开连接
            case R.id.btn_build_close:
                NettyClient.getInstance().onClose();
                break;
            //重新连接
            case R.id.btn_build_reconnect:
                NettyClient.getInstance().onReconnect();
                break;
            case R.id.btn_start://发送开始命令
                NettyClient.getInstance().onPostCommand(Constant.CMD_START);
                break;
            case R.id.btn_exit://发送退出命令
                NettyClient.getInstance().onPostCommand(Constant.CMD_EXIT);
                break;
            case R.id.btn_post_face://发送图片
                sendFaceToServer();
                break;
        }
    }

    /**
     * 原图:以文件的形式
     */
    private void sendFaceFileToServer() {
        final File filePath = new File(HeartUtil.getFaceCacheDir(mContext),
                "test_face.jpg");
        Logger.e("文件路径:" + filePath.getAbsolutePath());
        final byte[] temp = FileIOUtils.readFile2BytesByChannel(filePath);
        Logger.e("byte[] null?" + (temp == null));
        if (temp != null) {
            NettyClient.getInstance().onPostFaceFrame(temp);
        }

    }

    /**
     * 原图：Bitmap
     */
    private void sendFaceToServer() {

        final AssetManager manager = mContext.getAssets();
        if (manager != null) {
            try {
//                InputStream is = manager.open("test_face.jpg");
                InputStream is = manager.open("test_1.jpg");
                Bitmap src = ImageUtil.getBitmap(is);
                final File filePath = new File(HeartUtil.getFaceCacheDir(mContext),
                        "test" + System.currentTimeMillis() + ".jpg");
                Logger.e("文件路径:" + filePath.getAbsolutePath());
//        压缩图片
//        1.按缩放压缩图片640*480-->178kb
//                final Bitmap scaleBitmap = ImageUtil.compressByScale(src, 640, 480, true);
//        2.按质量压缩图片
//                final Bitmap qualityBitmap = ImageUtil.compressByQuality(src, 50, true);
//        3.按采样率压缩
//                final Bitmap sampleBitmap = ImageUtil.compressBySampleSize(src, 640, 480, true);
                final boolean state = ImageUtil.save(src, filePath, Bitmap.CompressFormat.JPEG);
                Logger.e("文件路径状态:" + state);

                NettyClient.getInstance().onPostFaceFrame(src);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 分发接受的数据
     *
     * @param data
     */
    private void dispatchCommand(String data) {
        Logger.d("dispatchCommand()..receive data:" + data);
        switch (data) {
            //开始命令确认
            case CMD_START_CONFIRM:
                //开始人脸识别
                Logger.e("开始命令确认！");
                break;
            //退出命令确认
            case CMD_EXIT_CONFIRM:
                Logger.e("退出命令确认！");
                break;
            //人脸识别异常
            case CMD_FACE_VERIFY_ERROR:
                Logger.e("人脸识别异常！");
                break;
            //心率异常命令
            case CMD_HEART_RATE_ERROR:
                Logger.e("心率异常！");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        NettyClient.getInstance().onShutDown();
        super.onDestroy();
    }
}
