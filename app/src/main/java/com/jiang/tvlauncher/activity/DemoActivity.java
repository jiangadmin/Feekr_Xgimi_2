package com.jiang.tvlauncher.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jiang.tvlauncher.R;
import com.jiang.tvlauncher.dialog.AllDialog;

/**
 * @author jiangyao
 * Date: 2019-11-13
 * Email: jiangmr@vip.qq.com
 * TODO: 测试
 */
public class DemoActivity extends BaseActivity {
    private static final String TAG = "DemoActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        new AllDialog(this).openKtcpError();
    }
}
