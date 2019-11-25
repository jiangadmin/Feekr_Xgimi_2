package com.jiang.tvlauncher.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.jiang.tvlauncher.MyAPP;
import com.jiang.tvlauncher.R;

/**
 * @author jiangyao
 * Date: 2017-8-29
 * Email: jiangmr@vip.qq.com
 * TODO: 警告弹框
 */
public class WarnDialog {

    private static WarningDialog netWarningDialog;

    /**
     * 显示警告框
     */
    public static void showW() {
        if (MyAPP.currentActivity() != null && netWarningDialog == null) {
            netWarningDialog = new WarningDialog(MyAPP.currentActivity());
            try {
                netWarningDialog.setCancelable(false);
                netWarningDialog.setCanceledOnTouchOutside(false);
                netWarningDialog.show();
            } catch (RuntimeException e) {

            }
        }
    }

    /**
     * 关闭
     */
    public static void dismiss() {
        try {
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
    public static class WarningDialog extends Dialog {
        public WarningDialog(@NonNull Context context) {
            super(context, R.style.MyDialog);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.dialog_warning);
        }
    }
}
