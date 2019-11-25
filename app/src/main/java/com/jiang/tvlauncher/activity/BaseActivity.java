package com.jiang.tvlauncher.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.jiang.tvlauncher.utils.AnimUtils;

/**
 * @author jiangyao
 * Date: 2017-7-3
 * Email: jiangmr@vip.qq.com
 * TODO: 公共Activity
 */
public class BaseActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        MyAPP.activity = this;
    }

    public void enlargeAnim(View v) {
        AnimUtils.S(v, 1, 1.1F);
        AnimUtils.Z(v, 0, 5);

    }

    public void reduceAnim(View v) {

        AnimUtils.S(v, 1.1F, 1);
        AnimUtils.Z(v, 5, 0);
    }


}
