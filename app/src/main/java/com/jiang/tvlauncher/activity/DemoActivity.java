package com.jiang.tvlauncher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.jiang.tvlauncher.MyAPP;
import com.jiang.tvlauncher.R;
import com.jiang.tvlauncher.utils.LogUtil;
import com.xgimi.business.api.beans.SignalBean;
import com.xgimi.business.api.clients.ApiProxyServiceClient;
import com.xgimi.business.api.components.InputSourceStatusChangedReceiver;
import com.xgimi.business.api.enums.EnumInputSource;
import com.xgimi.business.api.projectors.XgimiProjectorFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jiangyao
 * Date: 2019-11-13
 * Email: jiangmr@vip.qq.com
 * TODO: 测试
 */
public class DemoActivity extends BaseActivity implements InputSourceStatusChangedReceiver.IInputStatusChangeHandlerListener {
    private static final String TAG = "DemoActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

    }

    @Override
    public void onInputStatusIn(SignalBean signalBean) {
        String name = signalBean.getName() + "/" + signalBean.getSource() + "/" + signalBean.getSource();
        Toast.makeText(this, name, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInputStatusOut(SignalBean signalBean) {
        String name = signalBean.getName() + "/" + signalBean.getSource() + "/" + signalBean.getSource();
        Toast.makeText(this, name, Toast.LENGTH_LONG).show();

    }

    String name = null;

    public void onClick(View view) {
        SignalBean bean = null;

        switch (view.getTag().toString()) {
            case "setting":
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                return;
            case "getsignal":

                //获取信号源
                List<SignalBean> signalBeans = XgimiProjectorFactory.create().getInputSourceList();
                if (signalBeans != null && signalBeans.size() > 0) {
                    String name = "";
                    for (int i = 0; i < signalBeans.size(); i++) {
                        name += signalBeans.get(i).getName() + "/" + signalBeans.get(i).getSource() + "/" + signalBeans.get(i).getSw() + "\n";
                    }

                    Toast.makeText(this, name, Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this, "没有可选的信号源", Toast.LENGTH_LONG).show();
                }
                return;
            case "hdmi":
                bean = new SignalBean();
                bean.setName("HDMI");
                bean.setSource(EnumInputSource.E_INPUT_SOURCE_HDMI.name());
                bean.setSw("hdmi1");
                break;
            case "hdmi2":
                bean = new SignalBean();
                bean.setName("HDMI");
                bean.setSource(EnumInputSource.E_INPUT_SOURCE_HDMI2.name());
                bean.setSw("hdmi2");
                break;

            case "vga":
                bean = new SignalBean();
                bean.setName("VGA");
                bean.setSource(EnumInputSource.E_INPUT_SOURCE_VGA.name());
                bean.setSw("vga");
                break;

        }

        if (bean != null) {
            XgimiProjectorFactory.create().switchInputSource(bean);
        }


        ApiProxyServiceClient.INSTANCE.binderAidlService(MyAPP.context, new ApiProxyServiceClient.IAidlConnectListener() {
            @Override
            public void onSuccess() {

                Toast.makeText(DemoActivity.this, "AIDL 连接成功", Toast.LENGTH_LONG).show();
                LogUtil.e(TAG, "AIDL 连接成功");
                //设置默认开机信号源
                SignalBean signalBean = new SignalBean();
//                signalBean.setName(name);
//                signalBean.setSource(name);
//                Toast.makeText(DemoActivity.this, "设置信号源:" + name, Toast.LENGTH_LONG).show();
//                ApiProxyServiceClient.INSTANCE.setBootInputSource(signalBean);

                List<SignalBean> signalBeans = new ArrayList<>();


                signalBean = ApiProxyServiceClient.INSTANCE.getBootInputSource(signalBeans);
                Toast.makeText(DemoActivity.this, signalBean.getName() + "/" + signalBean.getSource() + "/" + signalBean.getSw(), Toast.LENGTH_LONG).show();

                //重启
//                ApiProxyServiceClient.INSTANCE.reboot();
                //释放资源
                ApiProxyServiceClient.INSTANCE.release();

            }

            @Override
            public void onFailure(RemoteException e) {
                Toast.makeText(DemoActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

}
