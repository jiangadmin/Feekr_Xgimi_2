package com.jiang.tvlauncher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jiang.tvlauncher.entity.Save_Key;
import com.jiang.tvlauncher.servlet.TurnOff_servlet;
import com.jiang.tvlauncher.utils.SaveUtils;
import com.jiang.tvlauncher.utils.Tools;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: jiangadmin
 * @date: 2017/6/19.
 * @Email: www.fangmu@qq.com
 * @Phone: 186 6120 1018
 * TODO: 关机广播监听
 */

public class ShutdownReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
            //关机发送请求
            new TurnOff_servlet().execute();

            //记录一次最近关机时间
            SaveUtils.setString(Save_Key.TurnOffTime, Tools.NowTime());

        }
    }
}