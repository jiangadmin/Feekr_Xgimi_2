package com.jiang.tvlauncher.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jiang.tvlauncher.R;
import com.jiang.tvlauncher.entity.Save_Key;
import com.jiang.tvlauncher.servlet.DownUtil;
import com.jiang.tvlauncher.utils.FileUtils;
import com.jiang.tvlauncher.utils.ImageUtils;
import com.jiang.tvlauncher.utils.LogUtil;
import com.jiang.tvlauncher.utils.SaveUtils;
import com.jiang.tvlauncher.utils.Tools;
import com.xgimi.business.api.beans.SignalBean;
import com.xgimi.business.api.enums.EnumInputSource;
import com.xgimi.business.api.projectors.XgimiProjectorFactory;

import java.io.File;

/**
 * @author jiangyao
 * Date: 2019-11-22
 * Email: jiangmr@vip.qq.com
 * TODO: 信号源切换确认
 */
public class InputSourceActivity extends BaseActivity {
    private static final String TAG = "InputSourceActivity";
    private static final String URL = "url";
    private static final String TYPE = "type";

    public static void start(Context context, int type, String url) {
        Intent intent = new Intent();
        intent.setClass(context, InputSourceActivity.class);
        intent.putExtra(URL, url);
        intent.putExtra(TYPE, type);
        context.startActivity(intent);
    }

    ImageView imageView;

    String imageUrl, imageName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = findViewById(R.id.imageView);
        LogUtil.e(TAG, "图片展示");

        String imgf = Environment.getExternalStorageDirectory().getPath() + "/feekr/Download/" + SaveUtils.getString(Save_Key.NewImageName);

        //如果有网络
        if (Tools.isNetworkConnected()) {
            imageUrl = getIntent().getStringExtra(URL);
            imageName = Tools.getFileNameWithSuffix(getIntent().getStringExtra(URL));
            if (TextUtils.isEmpty(imageUrl)) {
                finish();
                return;
            }
            //加载网络图片
            Glide.with(this).load(imageUrl).into(imageView);
            SaveUtils.setString(Save_Key.NewImageName, imageName);

            //检查本地图片是否存在
            if (!FileUtils.checkFileExists(imageName)) {
                //下载网络图片
                new DownUtil().downLoad(imageUrl, imageName, false);
            }
        } else {
            //判断是否有记录
            if (!TextUtils.isEmpty(SaveUtils.getString(Save_Key.NewImageName))) {
                imageView.setImageBitmap(ImageUtils.getBitmap(new File(imgf)));
            } else {
                Toast.makeText(this, "网络异常，请联系服务人员", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            SignalBean bean = null;
            switch (getIntent().getIntExtra(TYPE, 5)) {
                case 5:
                    bean = new SignalBean();
                    bean.setName("HDMI");
                    bean.setSource(EnumInputSource.E_INPUT_SOURCE_HDMI.name());
                    bean.setSw("hdmi1");
                    break;
                case 6:
                    bean = new SignalBean();
                    bean.setName("HDMI");
                    bean.setSource(EnumInputSource.E_INPUT_SOURCE_HDMI2.name());
                    bean.setSw("hdmi2");
                    break;
            }

            if (bean != null) {
                XgimiProjectorFactory.create().switchInputSource(bean);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
