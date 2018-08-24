package com.zxm.nettydemo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.Executors;

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
        findViewById(R.id.btn_build_disconnect).setOnClickListener(this);
        findViewById(R.id.btn_build_send_msg).setOnClickListener(this);
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
//                NettyClient.getInstance()
//                        .onConnect("10.136.192.135",8081);
                NettyClient.getInstance()
                        .onConnect(ip,Integer.parseInt(port));
                break;
            case R.id.btn_build_disconnect:
                break;
            case R.id.btn_build_send_msg:
                //发送消息到服务器
                break;
            case R.id.btn_build_receive_msg:
                //接收服务器消息
                break;
        }
    }
}
