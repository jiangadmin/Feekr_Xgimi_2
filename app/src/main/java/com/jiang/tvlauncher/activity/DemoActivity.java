package com.jiang.tvlauncher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.jiang.tvlauncher.MyAPP;
import com.jiang.tvlauncher.R;
import com.jiang.tvlauncher.utils.LogUtil;
import com.xgimi.business.api.beans.SignalBean;
import com.xgimi.business.api.clients.ApiProxyServiceClient;
import com.xgimi.business.api.enums.EnumInputSource;

/**
 * @author jiangyao
 * Date: 2019-11-13
 * Email: jiangmr@vip.qq.com
 * TODO: 测试
 */
public class DemoActivity extends BaseActivity {
    private static final String TAG = "DemoActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

//        new AllDialog(this).openKtcpError();

//        XgimiDeviceClient.setS


    }

    String name = null;

    public void onClick(View view) {


        switch (view.getTag().toString()) {
            case "setting":
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                return;
            case "hdmi":
                name = EnumInputSource.E_INPUT_SOURCE_HDMI.name();
                break;
            case "hdmi2":
                name = EnumInputSource.E_INPUT_SOURCE_HDMI2.name();
                break;
            case "hdmi3":
                name = EnumInputSource.E_INPUT_SOURCE_HDMI3.name();
                break;
            case "vga":
                name = EnumInputSource.E_INPUT_SOURCE_VGA.name();
                break;
        }

        ApiProxyServiceClient.INSTANCE.binderAidlService(MyAPP.context, new ApiProxyServiceClient.IAidlConnectListener() {
            @Override
            public void onSuccess() {
                LogUtil.e(TAG, "AIDL 连接成功");
                //设置默认开机信号源
                SignalBean signalBean = new SignalBean();
                signalBean.setSource(name);
                Toast.makeText(DemoActivity.this, "设置信号源:" + name, Toast.LENGTH_LONG).show();
                ApiProxyServiceClient.INSTANCE.setBootInputSource(signalBean);
                //重启
                ApiProxyServiceClient.INSTANCE.reboot();
                //释放资源
                ApiProxyServiceClient.INSTANCE.release();

            }

            @Override
            public void onFailure(RemoteException e) {

            }
        });

    }
}
