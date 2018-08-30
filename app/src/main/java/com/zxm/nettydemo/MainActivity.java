package com.zxm.nettydemo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zxm.nettydemo.listener.SimpleOnConnectStatusListener;

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
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_build_receive_msg).setOnClickListener(this);
        findViewById(R.id.btn_build_reconnect).setOnClickListener(this);

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
                            public void onReceiveData() {
                                super.onReceiveData();
                            }
                        })
                        .onConfig(ip, Integer.parseInt(port))
                        .onConnect();
                /*NettyClient.getInstance()
                        .setConnectStatusListener(new SimpleOnConnectStatusListener() {
                            @Override
                            public void onReceiveData() {
                                super.onReceiveData();
                            }
                        })
                        .onConfig("10.136.192.255", 8081)
                        .onConnect();*/
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
//                NettyClient.getInstance().onPostCommand("hello");
                NettyClient.getInstance().onPostCommand(Constant.CMD_START);
                break;
            case R.id.btn_build_receive_msg:
                //接收服务器消息
                break;
        }
    }
}
