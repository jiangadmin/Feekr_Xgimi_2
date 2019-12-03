package com.jiang.tvlauncher.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Button;

import com.jiang.tvlauncher.MyAPP;
import com.jiang.tvlauncher.R;
import com.jiang.tvlauncher.entity.Const;
import com.jiang.tvlauncher.servlet.DownUtil;
import com.jiang.tvlauncher.servlet.GetVIP;
import com.jiang.tvlauncher.utils.LogUtil;
import com.jiang.tvlauncher.utils.Tools;

/**
 * @author jiangyao
 * Date: 2019-11-13
 * Email: jiangmr@vip.qq.com
 * TODO: 弹框集合
 */
public class AllDialog {
    private static final String TAG = "AllDialog";

    Context context;

    public AllDialog(Context context) {
        this.context = context;
    }

    /**
     * 启动会员版失败
     */
    public void openKtcpError() {
        Dialog dialog = new Dialog(context, R.style.MyDialog);
        dialog.setContentView(R.layout.dialog_open_ktcp_error);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setCancelable(false);
        Button tryAgain = dialog.findViewById(R.id.btn_1);
        Button openKtcp = dialog.findViewById(R.id.btn_2);
        tryAgain.setOnClickListener(view -> {
            try {
                dialog.dismiss();
                dialog.dismiss();
                dialog.dismiss();
                dialog.dismiss();
                dialog.dismiss();
                dialog.dismiss();
                dialog.dismiss();
                dialog.dismiss();
                dialog.dismiss();
            }catch (Exception e){
                LogUtil.e(TAG,e.getMessage());
            }

            Loading.show(MyAPP.currentActivity(), null);
            new GetVIP(true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        });
        openKtcp.setOnClickListener(view -> {
            dialog.dismiss();
            if (Tools.isAppInstalled(Const.TencentViedo)) {
                //启动应用
                LogUtil.e(TAG, "启动云视听");
                Tools.StartApp(MyAPP.currentActivity(), Const.TencentViedo);

            } else {

                if (TextUtils.isEmpty(Const.云视听Url)) {
                    new AllDialog(context).appDefect();
                } else {
                    Loading.show(MyAPP.currentActivity(), "请稍后");
                    new DownUtil().downLoad(Const.云视听Url, "云视听.apk", true);
                }
            }
        });

        dialog.show();
    }

    /**
     * 应用不存在
     */
    public void appDefect() {
        Dialog dialog = new Dialog(context, R.style.MyDialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_app_defect);
        dialog.findViewById(R.id.btn_1).setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

}
