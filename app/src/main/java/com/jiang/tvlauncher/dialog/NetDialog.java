package com.jiang.tvlauncher.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;

import com.jiang.tvlauncher.MyAPP;
import com.jiang.tvlauncher.R;
import com.jiang.tvlauncher.utils.LogUtil;
import com.jiang.tvlauncher.utils.Tools;

/**
 * @author jiangyao
 * Date: 2017-8-29
 * Email: jiangmr@vip.qq.com
 * TODO: 网络状态提示
 */

public class NetDialog {
    private static final String TAG = "NetDialog";

    private static NetWarningDialog netWarningDialog;
    private static NetLoadingDialog netLoadingDialog;

    static TimeCount timeCount;

    /**
     * 显示警告框
     */
    public static void showW() {
        if (MyAPP.currentActivity() != null && netWarningDialog == null) {
            netWarningDialog = new NetWarningDialog(MyAPP.currentActivity());
            try {
                netWarningDialog.show();
            } catch (RuntimeException e) {

            }
        }
    }

    /**
     * 显示警告框
     */
    public static void showW(Activity activity) {
        if (netWarningDialog == null) {
            new NetWarningDialog(activity).show();
        } else {
            try {
                netWarningDialog.dismiss();
                netWarningDialog = null;
                new NetWarningDialog(activity).show();
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage());
            }
        }
    }

    /**
     * 显示等待框
     */
    public static void showL() {
        if (MyAPP.currentActivity() != null && netLoadingDialog == null) {
            netLoadingDialog = new NetLoadingDialog(MyAPP.currentActivity());
            netLoadingDialog.show();

            timeCount = new TimeCount(30000, 1000);
            timeCount.start();
        }
    }

    /**
     * 关闭
     */
    public static void dismiss() {
        try {
            //关闭等待框
            if (netLoadingDialog != null) {
                netLoadingDialog.dismiss();
                netLoadingDialog = null;
            }
            //关闭警告框
            if (netWarningDialog != null) {
                netWarningDialog.dismiss();
                netWarningDialog = null;
            }
        } catch (Exception e) {
        }
    }

    /**
     * 警告框
     */
    public static class NetWarningDialog extends Dialog {
        public NetWarningDialog(@NonNull Context context) {
            super(context, R.style.MyDialog);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.dialog_netwarning);
        }
    }

    /**
     * 等待框
     */
    public static class NetLoadingDialog extends Dialog {
        public NetLoadingDialog(@NonNull Context context) {
            super(context, R.style.MyDialog);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.dialog_netloading);
        }
    }


    /**
     * 计时器
     */
    static class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        //倒计时完成
        @Override
        public void onFinish() {
            //等待框依旧没关闭
            if (netLoadingDialog != null) {
                //关闭等待框
                dismiss();
                //判断网络
                if (!Tools.isNetworkConnected())
                    showW();
            }

        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示

        }
    }
}
