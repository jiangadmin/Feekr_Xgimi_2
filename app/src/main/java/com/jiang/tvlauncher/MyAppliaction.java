package com.jiang.tvlauncher;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import com.jiang.tvlauncher.entity.Point;
import com.jiang.tvlauncher.entity.Save_Key;
import com.jiang.tvlauncher.servlet.TurnOn_servlet;
import com.jiang.tvlauncher.utils.LogUtil;
import com.jiang.tvlauncher.utils.SaveUtils;
import com.jiang.tvlauncher.utils.Tools;
import com.tencent.bugly.crashreport.CrashReport;
import com.xgimi.business.api.clients.XgimiDeviceClient;
import com.xgimi.business.api.hardwares.DlpKeystoneManager;
import com.xgimi.business.api.projectors.IXgimiProjector;
import com.xgimi.business.api.projectors.XgimiProjectorFactory;

/**
 * Created by  jiang
 * on 2017/7/3.
 * Email: www.fangmu@qq.com
 * Phone：186 6120 1018
 * Purpose:TODO
 * update：
 */

public class MyAppliaction extends Application {
    private static final String TAG = "MyAppliaction";
    public static boolean LogShow = true;
    public static Context context;

    public static boolean IsLineNet = true;//是否是有线网络
    public static String modelNum = "Z6X";
    public static String ID = "";
    public static String SN = XgimiDeviceClient.getMachineId();
    public static String Temp = "FFFFFF";
    public static String WindSpeed = "FFFFFF";
    public static String turnType = "2";//开机类型 1 通电开机 2 手动开机
    Point point;
    public static boolean TurnOnS = false;

    public static Activity activity;

    /**
     * 判定是否是极米设备
     */
    public static boolean isxgimi = false;

    @Override
    public void onCreate() {
        super.onCreate();
//        startService(new Intent(this, TimingService.class));
        context = this;

        //崩溃检测
        CrashReport.initCrashReport(getApplicationContext(), "15b18d3a4c", false);

        LogUtil.e(TAG, "有线连接：" + Tools.isLineConnected());
        Tools.setScreenOffTime(24 * 60 * 60 * 1000);
        LogUtil.e(TAG, "休眠时间：" + Tools.getScreenOffTime());

        SaveUtils.setBoolean(Save_Key.FristTurnOn, true);

        //初始化
        IXgimiProjector xgimiProjector = XgimiProjectorFactory.create();

        //信号源名称
        String inputScourceName = xgimiProjector.getCurrentInputSource();

        LogUtil.e(TAG, "PID：" + XgimiDeviceClient.getMachineId());

        //开机请求
        new TurnOn_servlet(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

//        LogUtil.e(TAG, "左上角X：" + DlpKeystoneManager.INSTANCE.getKeystoneValue(DlpKeystoneManager.POINT_LEFT_TOP).getHorizontalValue());
//        LogUtil.e(TAG, "左上角Y：" + DlpKeystoneManager.INSTANCE.getKeystoneValue(DlpKeystoneManager.POINT_LEFT_TOP).getVerticalValue());
//
//        LogUtil.e(TAG, "左下角X：" + DlpKeystoneManager.INSTANCE.getKeystoneValue(DlpKeystoneManager.POINT_LEFT_BOTTOM).getHorizontalValue());
//        LogUtil.e(TAG, "左下角Y：" + DlpKeystoneManager.INSTANCE.getKeystoneValue(DlpKeystoneManager.POINT_LEFT_BOTTOM).getVerticalValue());
//
//        LogUtil.e(TAG, "右上角X：" + DlpKeystoneManager.INSTANCE.getKeystoneValue(DlpKeystoneManager.POINT_RIGHT_TOP).getHorizontalValue());
//        LogUtil.e(TAG, "右上角Y：" + DlpKeystoneManager.INSTANCE.getKeystoneValue(DlpKeystoneManager.POINT_RIGHT_TOP).getVerticalValue());
//
//        LogUtil.e(TAG, "右上角X：" + DlpKeystoneManager.INSTANCE.getKeystoneValue(DlpKeystoneManager.POINT_RIGHT_BOTTOM).getHorizontalValue());
//        LogUtil.e(TAG, "右上角Y：" + DlpKeystoneManager.INSTANCE.getKeystoneValue(DlpKeystoneManager.POINT_RIGHT_BOTTOM).getVerticalValue());

    }

}
