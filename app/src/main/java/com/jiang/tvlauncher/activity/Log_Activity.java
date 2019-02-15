package com.jiang.tvlauncher.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.jiang.tvlauncher.R;

/**
 * @author: jiangadmin
 * @date: 2018/9/6
 * @Email: www.fangmu@qq.com
 * @Phone: 186 6120 1018
 * TODO:
 */
public class Log_Activity extends Base_Activity {
    private static final String TAG = "Log_Activity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        TextView textView = findViewById(R.id.log);

    }
}
