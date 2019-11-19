package com.jiang.tvlauncher.servlet;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jiang.tvlauncher.MyAPP;
import com.jiang.tvlauncher.dialog.Loading;
import com.jiang.tvlauncher.entity.Const;
import com.jiang.tvlauncher.entity.Point;
import com.jiang.tvlauncher.entity.Save_Key;
import com.jiang.tvlauncher.entity.TurnOnEntity;
import com.jiang.tvlauncher.server.TimingService;
import com.jiang.tvlauncher.utils.HttpUtil;
import com.jiang.tvlauncher.utils.LogUtil;
import com.jiang.tvlauncher.utils.SaveUtils;
import com.jiang.tvlauncher.utils.Tools;
import com.jiang.tvlauncher.utils.Wifi_APManager;
import com.tencent.bugly.crashreport.CrashReport;
import com.xgimi.business.api.clients.XgimiDeviceClient;
import com.xgimi.business.api.enums.EnumProjectionMode;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: jiangadmin
 * @date: 2017/6/19.
 * @Email: www.fangmu@qq.com
 * @Phone: 186 6120 1018
 * TODO: 开机发送
 */

public class TurnOn_servlet extends AsyncTask<String, Integer, TurnOnEntity> {
    private static final String TAG = "TurnOn_servlet";
    Context context;

    TimeCount timeCount;

    public TurnOn_servlet(Context context) {
        this.context = context;
        timeCount = new TimeCount(3000, 1000);
    }

    @Override
    protected TurnOnEntity doInBackground(String... strings) {
        Map<String,String> map = new HashMap<>();
        TurnOnEntity entity;

        if (TextUtils.isEmpty(MyAPP.SN)) {
            if (!TextUtils.isEmpty(SaveUtils.getString(Save_Key.SerialNum))) {
                MyAPP.turnType = SaveUtils.getString(Save_Key.turnType);
            } else {
                if (context != null) {
                    new TurnOn_servlet(context).execute();
                }
                entity = new TurnOnEntity();
                entity.setErrorcode(-3);
                entity.setErrormsg("数据缺失 再来一次");
                return entity;
            }
        }

        map.put("serialNum", MyAPP.SN);
        map.put("turnType", MyAPP.turnType);
        map.put("modelNum", MyAPP.modelNum);

        map.put("systemVersion", Build.VERSION.INCREMENTAL);
        map.put("androidVersion", Build.VERSION.RELEASE);

        String res = HttpUtil.doPost(Const.URL + "dev/devTurnOffController/turnOn.do", map);

        if (TextUtils.isEmpty(res)) {
            entity = new TurnOnEntity();
            entity.setErrorcode(-1);
            entity.setErrormsg("连接服务器失败");
        } else {
            try {
                entity = new Gson().fromJson(res, TurnOnEntity.class);
            } catch (Exception e) {
                entity = new TurnOnEntity();
                entity.setErrorcode(-2);
                entity.setErrormsg("数据解析失败");
                LogUtil.e(TAG, e.getMessage());
            }
        }

        LogUtil.e(TAG, "=======================================================================================");
        if (entity != null && entity.getErrormsg() != null)
            LogUtil.e(TAG, entity.getErrormsg());
//        Toast.makeText(context, "开机请求返回："+entity.getErrormsg(), Toast.LENGTH_SHORT).show();
        LogUtil.e(TAG, "=======================================================================================");

        if (entity.getErrorcode() == 1000) {
            MyAPP.TurnOnS = true;

            TurnOnEntity.ResultBean.DevInfoBean devInfoBean = entity.getResult().getDevInfo();
            TurnOnEntity.ResultBean.LaunchBean launchBean = entity.getResult().getLaunch();
            TurnOnEntity.ResultBean.ShadowcnfBean shadowcnfBean = entity.getResult().getShadowcnf();

            //归零
            num = 0;

            Const.ID = devInfoBean.getId();
            CrashReport.setUserId(String.valueOf(devInfoBean.getId()));
            //存储ID
            SaveUtils.setString(Save_Key.ID, String.valueOf(devInfoBean.getId()));

            //存储密码
            if (shadowcnfBean != null && shadowcnfBean.getShadowPwd() != null) {
                SaveUtils.setString(Save_Key.Password, shadowcnfBean.getShadowPwd());
            } else {
                SaveUtils.setString(Save_Key.Password, null);
            }
            if (launchBean != null) {
                //更改开机动画
                if (!TextUtils.isEmpty(launchBean.getMediaUrl())) {
                    LogUtil.e(TAG, launchBean.getMediaUrl());
                    SaveUtils.setString(Save_Key.BootAn, launchBean.getMediaUrl());
                }

                //方案类型（1=开机，2=屏保，3=互动）
                if (launchBean.getLaunchType() == 1) {
                    //非空判断
                    if (!TextUtils.isEmpty(launchBean.getMediaUrl())) {
                        switch (launchBean.getMediaType()) {
                            //图片
                            case 1:
                                SaveUtils.setBoolean(Save_Key.NewImage, true);
                                SaveUtils.setBoolean(Save_Key.NewVideo, false);
                                SaveUtils.setString(Save_Key.NewImageUrl, launchBean.getMediaUrl());
                                break;
                            //视频
                            case 2:
                                SaveUtils.setBoolean(Save_Key.NewVideo, true);
                                SaveUtils.setBoolean(Save_Key.NewImage, false);
                                SaveUtils.setString(Save_Key.NewVideoUrl, launchBean.getMediaUrl());
                                break;
                        }
                    }
                }
            }

            String s = devInfoBean.getZoomVal();
            LogUtil.e(TAG, "梯形数据:" + s);

            if (shadowcnfBean != null) {
                //心跳时间
                SaveUtils.setInt(Save_Key.Timming, shadowcnfBean.getMonitRate());

                boolean directboot = shadowcnfBean.getPowerTurn() == 1;

                LogUtil.e(TAG, "上电开机：" + directboot);
                //上电开机开关
                try {
                    XgimiDeviceClient.setDirectBoot(directboot);
                } catch (Exception e) {
                    LogUtil.e(TAG, "极米特有功能" + e.getMessage());
                }


                //投影方式开关
                if (shadowcnfBean.getProjectModeFlag() == 1) {

                    switch (shadowcnfBean.getProjectMode()) {
                        //正装正投
                        case 0:
                            try {
                                XgimiDeviceClient.setProjectionMode(EnumProjectionMode.Front_Normal);
                            } catch (UnsatisfiedLinkError e) {
                                LogUtil.e(TAG, "极米特有功能" + e.getMessage());
                            }
                            break;
                        //吊装正投
                        case 1:
                            XgimiDeviceClient.setProjectionMode(EnumProjectionMode.Front_Mirror);
                            break;
                        //正装背投
                        case 2:
                            XgimiDeviceClient.setProjectionMode(EnumProjectionMode.Reverse_Normal);
                            break;
                        //吊装背投
                        case 3:
                            XgimiDeviceClient.setProjectionMode(EnumProjectionMode.Reverse_Mirror);
                            break;
                    }

                }


            }

            //启动定时服务
            context.startService(new Intent(context, TimingService.class));

            //判断是否是有线连接 & 服务启用同步数据
            if (Tools.isLineConnected() && shadowcnfBean != null
                    && shadowcnfBean.getHotPointFlag() == 1) {

                //获取热点名称&热点密码
                String SSID = shadowcnfBean.getWifi();
                String APPWD = shadowcnfBean.getWifiPassword();

                //存储热点名称&密码
                SaveUtils.setString(Save_Key.WiFiName, SSID);
                SaveUtils.setString(Save_Key.WiFiPwd, APPWD);

                LogUtil.e(TAG, "SSID:" + SSID + "  PassWord:" + APPWD);

                //首先关闭热点
                new Wifi_APManager(context).closeWifiAp();

                if (shadowcnfBean.getHotPoint() == 1
                        && shadowcnfBean.getWifi() != null
                        && shadowcnfBean.getWifiPassword() != null) {

                    //开启热点
                    new Wifi_APManager(context).startWifiAp(SSID, APPWD, false);

                }
            }

        } else if (entity.getErrorcode() == -2) {
            LogUtil.e(TAG, entity.getErrormsg());

        } else {
            timeCount.start();
            LogUtil.e(TAG, "失败了" + entity.getErrormsg());
        }

        return entity;
    }

    @Override
    protected void onPostExecute(TurnOnEntity entity) {
        super.onPostExecute(entity);
        Const.Nets = false;
        Loading.dismiss();

        switch (entity.getErrorcode()) {
            case 1000:
                EventBus.getDefault().post("update");
                break;
        }
    }

    public static int num = 0;

    /**
     * 计时器
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        //倒计时完成
        @Override
        public void onFinish() {
            num++;
            //再次启动
            new TurnOn_servlet(context).execute();

        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示

        }
    }
}
