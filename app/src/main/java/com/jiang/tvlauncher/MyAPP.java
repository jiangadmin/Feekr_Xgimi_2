package com.jiang.tvlauncher;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.TvTicketTool.TvTicketTool;
import com.jiang.tvlauncher.entity.Const;
import com.jiang.tvlauncher.entity.Point;
import com.jiang.tvlauncher.entity.Save_Key;
import com.jiang.tvlauncher.servlet.TurnOn_servlet;
import com.jiang.tvlauncher.servlet.VIPCallBack_Servlet;
import com.jiang.tvlauncher.utils.LogUtil;
import com.jiang.tvlauncher.utils.SaveUtils;
import com.jiang.tvlauncher.utils.Tools;
import com.ktcp.video.ktsdk.TvTencentSdk;
import com.ktcp.video.thirdagent.JsonUtils;
import com.ktcp.video.thirdagent.KtcpContants;
import com.ktcp.video.thirdagent.KtcpPaySDKCallback;
import com.ktcp.video.thirdagent.KtcpPaySdkProxy;
import com.tencent.bugly.crashreport.CrashReport;
import com.xgimi.business.api.clients.XgimiDeviceClient;
import com.xgimi.business.api.hardwares.FanAndTemperatureManager;
import com.xgimi.business.api.projectors.XgimiProjectorFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Stack;

/**
 * Created by  jiang
 * on 2017/7/3.
 * Email: www.fangmu@qq.com
 * Phone：186 6120 1018
 * Purpose:TODO
 * update：
 */

public class MyAPP extends Application implements KtcpPaySDKCallback {
    private static final String TAG = "MyAppliaction";
    public static boolean LogShow = true;
    public static Context context;

    public static boolean IsLineNet = true;//是否是有线网络
    public static String modelNum = "Z6X";
    public static String ID = "";
    public static String SN = XgimiDeviceClient.getMachineId();
//        public static String SN = "EKJ9J517DXBJ";
    public static int Temp = 0;
    public static int WindSpeed = 0;
    public static String turnType = "2";//开机类型 1 通电开机 2 手动开机
    Point point;
    public static boolean TurnOnS = false;

    /**
     * 判定是否是极米设备
     */
    public static boolean isxgimi = false;


    /**
     * activity 总数
     */
    public int count = 0;

    /**
     * 收录的Activity
     */
    public static Stack<Activity> store;

    /**
     * 是否在前台
     */
    public static boolean isShow = true;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        store = new Stack<>();
        registerActivityLifecycleCallbacks(new SwitchBackgroundCallbacks());
        //崩溃检测
        CrashReport.initCrashReport(getApplicationContext(), "15b18d3a4c", false);

        LogUtil.e(TAG, "有线连接：" + Tools.isLineConnected());
        Tools.setScreenOffTime(24 * 60 * 60 * 1000);
        LogUtil.e(TAG, "休眠时间：" + Tools.getScreenOffTime());

        SaveUtils.setBoolean(Save_Key.FristTurnOn, true);
        try {
            modelNum = XgimiProjectorFactory.create().getProjectorType();
        } catch (Exception ex) {
            LogUtil.e(TAG, "获取机型失败：" + ex.getMessage());
        }

        //添加监听
        KtcpPaySdkProxy.getInstance().setPaySDKCallback(this);

        //开机请求
        new TurnOn_servlet(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        if (!TextUtils.isEmpty(SN)) {
            isxgimi = true;

            TempWindSpeed();
        }
    }

    public void TempWindSpeed() {
        FanAndTemperatureManager.INSTANCE.registerTemperatureRefreshListener(new FanAndTemperatureManager.ITemperatureRefreshListener() {
            @Override
            public void onRefresh(int serial, int temp) {
                Temp = temp;

                //风扇转速等级
                WindSpeed = FanAndTemperatureManager.INSTANCE.getWindSpeedLevel();
            }
        });
    }

    private int status = -1;//接口状态码
    private String msg;//接口提示信息

    /**
     * @param channel 三方厂商对应的渠道号
     * @param extra   包含guid,QUA，TVPlatform等字段的json字符串
     */
    @Override
    public void doLogin(String channel, String extra) {

        final HashMap<String, Object> loginData = new HashMap<>();
        // FIXME:  获取帐号   需要腾讯处理的错误码和提示请沟通好通知处理
        // status 成功返回 0 失败返回对应错误码 厂商业务错误 fixme  腾旅 902xxx 例如902001登录失败
        // msg  错误提示
        // data json数据

        //vuid登录示例
        loginData.put("loginType", "vu");//登录类型 vu ,qq,wx,ph
        loginData.put("vuid", Const.ktcp_vuid);
        loginData.put("vtoken", Const.ktcp_vtoken);
        loginData.put("accessToken", Const.ktcp_accessToken);


        //大票换小票接口
        TvTicketTool.getVirtualTVSKey(this, false, Long.parseLong(Const.ktcp_vuid), Const.ktcp_vtoken, Const.ktcp_accessToken, new TvTencentSdk.OnTVSKeyListener() {
            @Override
            public void OnTVSKeySuccess(String vusession, int expiredTime) {
                LogUtil.e(TAG, "vusession=" + vusession + ",expiredTime=" + expiredTime);
                status = 0;
                msg = "login success";
                loginData.put("vusession", vusession);
                //通过onLoginResponse 将数据回传给腾讯
                KtcpPaySdkProxy.getInstance().onLoginResponse(status, msg, JsonUtils.addJsonValue(loginData));
            }

            @Override
            public void OnTVSKeyFaile(int failedCode, String failedMsg) {
                LogUtil.e(TAG, "failedCode=" + failedCode + ",msg=" + failedMsg);
                status = failedCode;
                msg = failedMsg;
                KtcpPaySdkProxy.getInstance().onLoginResponse(status, msg, JsonUtils.addJsonValue(loginData));
            }
        });
    }

    /**
     * 订单处理  需要腾讯处理的错误码和提示请沟通好通知处理
     *
     * @param vuserId
     * @param productId
     * @param extra
     */
    @Override
    public void doOrder(String vuserId, String productId, String extra) {

        // status 成功返回0 失败返回对应错误码 厂商业务错误 fixme  腾旅 902xxx 例如902101订购失败
        // msg  错误提示
        // data json数据 { "vuid":"","extra":{"orderId":""}}

        //订单返回结果示例
        HashMap<String, Object> params = new HashMap<>();
        int status = 0;//0 订单处理成功 非0失败
        String msg = "make order success";
        params.put("vuid", vuserId);

        JSONObject orderJson;
        try {
            orderJson = new JSONObject();
            orderJson.put("orderId", "orderId12345678");
        } catch (JSONException e) {
            e.printStackTrace();
            orderJson = new JSONObject();
        }
        params.put("extra", orderJson);

        KtcpPaySdkProxy.getInstance().onOrderResponse(status, msg, JsonUtils.addJsonValue(params));

    }

    /**
     * 接收来自腾讯的通知事件
     *
     * @param eventId 1 接收应用启动事件 由三方APP决定登录时机发起登录 2 帐号登录回调  3 帐号退出回调 4 APP退出回调
     * @param params
     */
    @Override
    public void onEvent(int eventId, String params) {
        LogUtil.e(TAG, "eventId:" + eventId + "params:" + params);
        switch (eventId) {
            //接收应用启动事件 由三方APP决定登录时机发起登录
            case 1:
                KtcpPaySdkProxy.getInstance().onEventResponse(KtcpContants.EVENT_ACCOUNT_LOGIN, "");
                break;
            //帐号登录回调
            case 2:
                //示例  {{"extra":"{\"isVip\":false,\"vuid\":278113277,\"msg\":\"login success\",\"code\":0,\"vuSession\":\"97027a6822cce5220250ef76cd58\"}","eventId":2,"type":3}
                break;
            case 3://退出登录回调
                break;
            case 4://APP退出
                break;
            default:
                break;
        }

        try {
            JSONObject extraObj = JsonUtils.getJsonObj(params);
            int code = extraObj.optInt("code");
            String message = extraObj.optString("msg");

            VIPCallBack_Servlet.TencentVip vip = new VIPCallBack_Servlet.TencentVip();
            vip.setCode(String.valueOf(code));
            vip.setMsg(message);
            vip.setEventId(String.valueOf(eventId));  // 2 账户登录回调 3 退出登录  4 APP退出
            new VIPCallBack_Servlet().execute(vip);
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }

    }


    private class SwitchBackgroundCallbacks implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
//            if(activity instanceof ActivityDetail) {
//                if(store.size() >= MAX_ACTIVITY_DETAIL_NUM){
//                    store.peek().finish(); //移除栈底的详情页并finish,保证商品详情页个数最大为10
//                }
            store.add(activity);
//            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (count == 0) { //后台切换到前台
                isShow = true;
                LogUtil.v(TAG, ">>>>>>>>>>>>>>>>>>>App切到前台");

                CrashReport.putUserData(context, "Application", "App切到前台");

            }
            count++;
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            count--;
            if (count == 0) { //前台切换到后台
                isShow = false;
                LogUtil.v(TAG, ">>>>>>>>>>>>>>>>>>>App切到后台");

            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            store.remove(activity);
        }
    }



    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    public static Activity currentActivity() {
        if (store != null && store.size() > 0) {
            return store.lastElement();
        } else {
            return null;
        }
    }

    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    public static void finishActivity() {
        if (store != null && store.size() > 0) {
            store.lastElement().finish();
        }
    }

    /**
     * 结束指定的Activity
     *
     * @param activity
     */

    public static void finishActivity(Activity activity) {
        if (activity != null && store != null) {
            for (Activity activity1 : store) {
                if (activity1 == activity) {
                    activity1.finish();
                }
            }
        }
    }


    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(Class<?> cls) {
        if (cls != null) {
            for (Activity activity : store) {
                if (activity.getClass().equals(cls)) {
                    finishActivity(activity);
                    break;
                }
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        for (int i = 0, size = store.size(); i < size; i++) {
            if (null != store.get(i)) {
                store.get(i).finish();
            }
        }
        store.clear();
        System.exit(0);
    }



}
