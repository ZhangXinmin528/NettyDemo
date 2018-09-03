package com.zxm.nettydemo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zxm.libnetty.Constant;
import com.zxm.libnetty.NettyClient;
import com.zxm.libnetty.listener.SimpleOnConnectStatusListener;
import com.zxm.libnetty.util.Logger;

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
        findViewById(R.id.btn_build_receive_msg).setOnClickListener(this);

        mInfoTv = findViewById(R.id.tv_info);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_build_connect:
                //客户端并建立连接
                final String ip = mIpEt.getText().toString().trim();
                final String port = mPortEt.getText().toString().trim();

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
            case R.id.btn_build_receive_msg:
                //接收服务器消息
                break;
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
        }
    }

    @Override
    protected void onDestroy() {
        NettyClient.getInstance().onShutDown();
        super.onDestroy();
    }
}
