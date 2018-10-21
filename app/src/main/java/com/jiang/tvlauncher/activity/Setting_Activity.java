package com.jiang.tvlauncher.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.jiang.tvlauncher.MyAppliaction;
import com.jiang.tvlauncher.R;
import com.jiang.tvlauncher.dialog.Loading;
import com.jiang.tvlauncher.entity.Const;
import com.jiang.tvlauncher.servlet.SyncDevZoom_Servlet;
import com.jiang.tvlauncher.servlet.Update_Servlet;
import com.jiang.tvlauncher.utils.Tools;

/**
 * @author: jiangadmin
 * @date: 2017/7/3.
 * @Email: www.fangmu@qq.com
 * @Phone: 186 6120 1018
 * TODO: 控制台
 */

public class Setting_Activity extends Base_Activity implements View.OnClickListener {
    private static final String TAG = "Setting_Activity";

    //网络，蓝牙，设置，文件，更新，关于
    LinearLayout setting1, setting2, setting3, setting4, setting5, setting6;

    public static void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, Setting_Activity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        MyAppliaction.activity = this;
        initview();
        initeven();

//        try {
//            MyAppliaction.apiManager.set("setFocusOnOff", "true", null, null, null);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onStop() {

//        try {
//            MyAppliaction.apiManager.set("setFocusOnOff", "false", null, null, null);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
//
//        try {
//            MyAppliaction.apiManager.set("setFocusOnOff", "false", null, null, null);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }

        super.onDestroy();
    }

    private void initview() {
        setting1 =  findViewById(R.id.setting_1);
        setting2 =  findViewById(R.id.setting_2);
        setting3 =  findViewById(R.id.setting_3);
        setting4 =  findViewById(R.id.setting_4);
        setting5 =  findViewById(R.id.setting_5);
        setting6 =  findViewById(R.id.setting_6);
    }

    private void initeven() {
        setting1.setOnClickListener(this);
        setting2.setOnClickListener(this);
        setting3.setOnClickListener(this);
        setting4.setOnClickListener(this);
        setting5.setOnClickListener(this);
        setting6.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //网络设置
            case R.id.setting_1:
                //如果是有线连接
                if (Tools.isLineConnected())
                    //启动到有线连接页面
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                else
                    //启动到无线连接页面
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                break;
            //蓝牙设置
            case R.id.setting_2:
                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS  ));
                break;
            //梯形校正
            case R.id.setting_3:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                ComponentName cn = new ComponentName("com.android.newsettings", "com.android.newsettings.framesettings.kstActivity");
                intent.setComponent(cn);
                startActivityForResult(intent, 7);
//                startActivity(new Intent(getPackageManager().getLaunchIntentForPackage("com.android.newsettings.framesettings.kstActivity")));
                break;
            //文件管理
            case R.id.setting_4:
                startActivity(new Intent(getPackageManager().getLaunchIntentForPackage(Const.资源管理器)));
                break;
            //检测更新
            case R.id.setting_5:
                Loading.show(this, "检查更新");
                new Update_Servlet(this).execute();
                break;
            //关于本机
            case R.id.setting_6:
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("梯形校正");
            builder.setMessage("是否同步数据到服务器？");
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Loading.show(Setting_Activity.this, "同步中···");
                    new SyncDevZoom_Servlet().execute();
                }
            });
            builder.show();
        }
    }
}
