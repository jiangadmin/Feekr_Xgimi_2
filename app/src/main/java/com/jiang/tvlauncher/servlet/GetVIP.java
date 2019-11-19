package com.jiang.tvlauncher.servlet;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jiang.tvlauncher.MyAPP;
import com.jiang.tvlauncher.dialog.AllDialog;
import com.jiang.tvlauncher.dialog.Loading;
import com.jiang.tvlauncher.entity.Const;
import com.jiang.tvlauncher.entity.Save_Key;
import com.jiang.tvlauncher.entity.VIP_Entity;
import com.jiang.tvlauncher.utils.HttpUtil;
import com.jiang.tvlauncher.utils.LogUtil;
import com.jiang.tvlauncher.utils.SaveUtils;
import com.jiang.tvlauncher.utils.Tools;
import com.ktcp.video.thirdagent.JsonUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: jiangyao
 * @date: 2018/5/14
 * @Email: www.fangmu@qq.com
 * @Phone: 186 6120 1018
 * TODO: 获取会员账号
 */
public class GetVIP extends AsyncTask<String, Integer, VIP_Entity> {
    private static final String TAG = "GetVIP";

    boolean IsOpen = true;

    public GetVIP(boolean isOpen) {
        IsOpen = isOpen;
    }

    @Override
    protected VIP_Entity doInBackground(String... strings) {
        Map<String, String> map = new HashMap<>();
        VIP_Entity entity;
        if (!TextUtils.isEmpty(MyAPP.SN)) {
            map.put("serialNum", MyAPP.SN);
            map.put("mac", Tools.getMacAddress());
        } else {
            entity = new VIP_Entity();
            entity.setErrorcode(-3);
            entity.setErrormsg("数据缺失");
        }

        String res = HttpUtil.doPost(Const.URL + "tencent/tencentVideoController/getVuidInfo.do", map);

        //空判断
        if (res != null && res.contains(",\"result\":\"\"")) {
            res = res.replaceAll(",\"result\":\"\"", "");
        }

        if (TextUtils.isEmpty(res)) {
            entity = new VIP_Entity();
            entity.setErrorcode(-1);
            entity.setErrormsg("连接服务器失败");
        } else {
            try {
                entity = new Gson().fromJson(res, VIP_Entity.class);
            } catch (Exception e) {
                entity = new VIP_Entity();
                entity.setErrorcode(-2);
                entity.setErrormsg("数据解析失败");
                LogUtil.e(TAG, e.getMessage());
            }
        }
        return entity;
    }

    @Override
    protected void onPostExecute(VIP_Entity entity) {
        super.onPostExecute(entity);
        Loading.dismiss();

        Const.IsGetVip = true;

        switch (entity.getErrorcode()) {
            case 1000:
                HashMap<String, Object> params = new HashMap<>();

                Const.ktcp_vuid = String.valueOf(entity.getResult().getVuid());
                Const.ktcp_vtoken = entity.getResult().getVtoken();
                Const.ktcp_accessToken = entity.getResult().getAccessToken();

                params.put("vuid", entity.getResult().getVuid());
                params.put("vtoken", entity.getResult().getVtoken());
                params.put("accessToken", entity.getResult().getAccessToken());
                params.put("errTip", "");

                SaveUtils.setString(Save_Key.PARAMS, JsonUtils.addJsonValue(params));

                //启动应用
                LogUtil.e(TAG, "启动会员版");
                if (IsOpen) {
                    if (Tools.isAppInstalled(Const.TvViedo)) {
                        Tools.StartApp(MyAPP.currentActivity(), Const.TvViedo);
                    }
                }
                break;
            //003004="无效设备号",表示设备不存在我们数据库中
            case 3004:
                //019006="设备未绑定酒店客户，表示使用VIP账号",标识设备未绑定到酒店
            case 19006:
                //019007="设备未开通超级影视VIP权限"，表示酒店未开通超级影视vip
            case 19007:
                if (Tools.isAppInstalled(Const.TencentViedo)) {
                    //启动应用
                    LogUtil.e(TAG, "启动云视听");
                    Tools.StartApp(MyAPP.currentActivity(), Const.TencentViedo);

                } else {

                    if (TextUtils.isEmpty(Const.云视听Url)) {
                        new AllDialog(MyAPP.currentActivity()).appDefect();
                    } else {
                        Loading.show(MyAPP.currentActivity(), "请稍后");
                        new DownUtil().downLoad(Const.云视听Url, "云视听.apk", true);
                    }
                }
                break;

            default:
                new AllDialog(MyAPP.currentActivity()).openKtcpError();
                break;
        }
    }
}
