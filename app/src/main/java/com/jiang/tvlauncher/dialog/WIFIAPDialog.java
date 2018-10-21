package com.jiang.tvlauncher.dialog;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiang.tvlauncher.R;
import com.jiang.tvlauncher.utils.ImageUtils;
import com.jiang.tvlauncher.utils.WifiApUtils;

/**
 * @author: jiangadmin
 * @date: 2017/9/29.
 * @Email: www.fangmu@qq.com
 * @Phone: 186 6120 1018
 * TODO: AP
 */

public class WIFIAPDialog extends Dialog {

    ImageView imageView;
    TextView ssid, pwd;
    Context context;

    public WIFIAPDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wifiap);

        imageView =  findViewById(R.id.qrcode);
        ssid =  findViewById(R.id.ssid);
        pwd =  findViewById(R.id.wifipwd);
//
//        imageView.setImageBitmap(ImageUtils.getQRcode("WIFI:T:WPA;P:\""+SaveUtils.getString(Save_Key.WiFiPwd)+"\";S:"+SaveUtils.getString(Save_Key.WiFiName)+";"));
//        ssid.setText(SaveUtils.getString(Save_Key.WiFiName));
//        pwd.setText(SaveUtils.getString(Save_Key.WiFiPwd));
        getWifiApConfiguration();
    }

    //获取热点配置信息,获取到的始终为当前或者上次设置的热点配置信息
    public void getWifiApConfiguration() {
        WifiConfiguration wifiConfiguration = WifiApUtils.getInstance(context).getWifiApConfiguration();
        if (wifiConfiguration != null) {
            String name = wifiConfiguration.SSID;
            String passWord = wifiConfiguration.preSharedKey;

            imageView.setImageBitmap(ImageUtils.getQRcode("WIFI:T:WPA;P:\"" + passWord + "\";S:" + name + ";"));
            ssid.setText(name);
            pwd.setText(passWord);

        }
    }
}
