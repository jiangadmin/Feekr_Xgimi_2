package com.jiang.tvlauncher.servlet;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jiang.tvlauncher.MyAppliaction;
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
        Map map = new HashMap();
        TurnOnEntity entity;

        if (TextUtils.isEmpty(MyAppliaction.SN)) {
            if (!TextUtils.isEmpty(SaveUtils.getString(Save_Key.SerialNum))) {
                MyAppliaction.turnType = SaveUtils.getString(Save_Key.turnType);
            } else {
                new TurnOn_servlet(context).execute();
                entity = new TurnOnEntity();
                entity.setErrorcode(-3);
                entity.setErrormsg("数据缺失 再来一次");
                return entity;
            }
        }

        map.put("serialNum", MyAppliaction.SN);
        map.put("turnType", MyAppliaction.turnType);
        map.put("modelNum", MyAppliaction.modelNum);

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
            MyAppliaction.TurnOnS = true;

            TurnOnEntity.ResultBean.DevInfoBean devInfoBean = entity.getResult().getDevInfo();
            TurnOnEntity.ResultBean.LaunchBean launchBean = entity.getResult().getLaunch();
            TurnOnEntity.ResultBean.ShadowcnfBean shadowcnfBean = entity.getResult().getShadowcnf();

            //归零
            num = 0;

            Const.ID = devInfoBean.getId();

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

                //上电开机开关
                if (shadowcnfBean.getPowerFlag() == 1) {
                    //上电开机
                    XgimiDeviceClient.setDirectBoot(shadowcnfBean.getPowerTurn() == 1);
                }

                //投影方式开关
                if (shadowcnfBean.getProjectModeFlag() == 1) {

                    switch (shadowcnfBean.getProjectMode()) {
                        case 0:
                            XgimiDeviceClient.setProjectionMode(EnumProjectionMode.Front_Normal);   //正装正投
                            break;
                        case 1:
                            XgimiDeviceClient.setProjectionMode(EnumProjectionMode.Front_Mirror);   //吊装正投
                            break;
                        case 2:
                            XgimiDeviceClient.setProjectionMode(EnumProjectionMode.Reverse_Normal); //正装背投
                            break;
                        case 3:
                            XgimiDeviceClient.setProjectionMode(EnumProjectionMode.Reverse_Mirror); //吊装背投
                            break;
                    }
                }

                //梯形校正开关
                if (shadowcnfBean.getZoomFlag() == 1) {
                    //初始化梯形数据
                    Point point = new Gson().fromJson(s, Point.class);
                    for (int i = 0; i < point.getPoint().size(); i++) {
//                            MyAppliaction.apiManager.set("setKeyStoneByPoint", point.getPoint().get(i).getIdx(), point.getPoint().get(i).getCurrent_x(), point.getPoint().get(i).getCurrent_y(), null);

                    }
                }
            }

            //启动定时服务
            context.startService(new Intent(context, TimingService.class));

            //判断是否是有线连接 & 服务启用同步数据
            if (Tools.isLineConnected() && shadowcnfBean != null
                    && shadowcnfBean.getHotPointFlag() == 1) {
                if (shadowcnfBean.getHotPoint() == 1
                        && shadowcnfBean.getWifi() != null
                        && shadowcnfBean.getWifiPassword() != null) {                //开启热点

                    //获取热点名称&热点密码
                    String SSID = shadowcnfBean.getWifi();
                    String APPWD = shadowcnfBean.getWifiPassword();

                    //存储热点名称&密码
                    SaveUtils.setString(Save_Key.WiFiName, SSID);
                    SaveUtils.setString(Save_Key.WiFiPwd, APPWD);

                    LogUtil.e(TAG, "SSID:" + SSID + "  PassWord:" + APPWD);

                    //打开并设置热点信息.注意热点密码8-32位，只限制了英文密码位数。
                    //使用极米开启/关闭热点接口

//                    try {
//                        String s1 = MyAppliaction.apiManager.set("setOpenWifiAp", SSID, APPWD, null, null);
//                        if (!TextUtils.isEmpty(s1) && Boolean.valueOf(s1.toLowerCase())) {
//                            LogUtil.e(TAG, "热点开机成功！");
//                        }
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
                } else if (shadowcnfBean.getHotPoint() == 0) {            //关闭热点
//                    try {
//                        MyAppliaction.apiManager.set("setCloseWifiAp", null, null, null, null);
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
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
