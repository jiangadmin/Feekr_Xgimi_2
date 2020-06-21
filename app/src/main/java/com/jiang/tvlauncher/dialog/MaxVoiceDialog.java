package com.jiang.tvlauncher.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jiang.tvlauncher.R;
import com.xgimi.business.api.clients.XgimiDeviceClient;

/**
 * @author jiangyao
 * Date: 2020-6-21
 * Email: jiangmr@vip.qq.com
 * TODO: 最大音量调节
 */

public class MaxVoiceDialog extends Dialog {
    private final String TAG = this.getClass().getSimpleName();

    private ProgressBar bar;
    private TextView barText;

    private Activity activity;

    public MaxVoiceDialog(Activity activity, int theme) {
        super(activity, theme);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_voice);
        initView();
    }

    private void initView() {
        bar = findViewById(R.id.bar);
        barText = findViewById(R.id.bar_text);

        bar.setMax(100);
        //注入当前最大值
        bar.setProgress(XgimiDeviceClient.getMaxVolume());
        barText.setText("当前允许最大音量：" + XgimiDeviceClient.getMaxVolume());

    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_DPAD_LEFT:
                downVoice();
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                upVoice();
                return true;
            case KeyEvent.KEYCODE_BACK:
                dismiss();
                return true;
            case KeyEvent.KEYCODE_HOME:
                dismiss();
                return true;
            default:
                return false;
        }
    }

    /**
     * 加大音量
     */
    public void upVoice() {
        int nowMaxVoice = XgimiDeviceClient.getMaxVolume();
        int nextMaxVoice = nowMaxVoice + 5;
        if (nextMaxVoice < 100) {
            XgimiDeviceClient.setMaxVolume(nextMaxVoice);
        } else {
            XgimiDeviceClient.setMaxVolume(100);
        }

        bar.setProgress(XgimiDeviceClient.getMaxVolume());
        barText.setText("当前允许最大音量：" + XgimiDeviceClient.getMaxVolume());

    }

    /**
     * 降低音量
     */
    public void downVoice() {
        int nowMaxVoice = XgimiDeviceClient.getMaxVolume();
        int nextMaxVoice = nowMaxVoice - 5;
        if (0 > nextMaxVoice) {
            XgimiDeviceClient.setMaxVolume(0);
        } else {
            XgimiDeviceClient.setMaxVolume(nextMaxVoice);
        }

        bar.setProgress(XgimiDeviceClient.getMaxVolume());
        barText.setText("当前允许最大音量：" + XgimiDeviceClient.getMaxVolume());
    }


}