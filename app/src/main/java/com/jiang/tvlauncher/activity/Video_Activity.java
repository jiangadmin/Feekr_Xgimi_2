package com.jiang.tvlauncher.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;

import com.jiang.tvlauncher.R;
import com.jiang.tvlauncher.entity.Save_Key;
import com.jiang.tvlauncher.servlet.DownUtil;
import com.jiang.tvlauncher.utils.LogUtil;
import com.jiang.tvlauncher.utils.SaveUtils;
import com.jiang.tvlauncher.utils.Tools;

/**
 * @author jiangyao
 * Date: 2017-8-22
 * Email: jiangmr@vip.qq.com
 * TODO: 视频播放
 */
public class Video_Activity extends BaseActivity {
    private static final String TAG = "Video_Activity";
    private static final String URL = "url";

    VideoView videoView;

    String videoUrl, videoName;

    public static void start(Context context, String url) {
        Intent intent = new Intent();
        intent.setClass(context, Video_Activity.class);
        intent.putExtra(URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        videoView = findViewById(R.id.videoView);

        String videof = Environment.getExternalStorageDirectory().getPath() + "/feekr/Download/" + SaveUtils.getString(Save_Key.NewImageName);

        LogUtil.e(TAG, "视频播放");
        videoUrl = getIntent().getStringExtra(URL);

        videoView.setZOrderOnTop(true);
        //如果有网络
        if (Tools.isNetworkConnected()) {
            videoView.setVideoURI(Uri.parse(videoUrl));
            videoName = Tools.getFileNameWithSuffix(getIntent().getStringExtra(URL));
            SaveUtils.setString(Save_Key.NewVideoName, videoName);
            //下载视频
            new DownUtil().downLoad(videoUrl, videoName, false);
        } else {
            //判断是否有记录
            if (!TextUtils.isEmpty(SaveUtils.getString(Save_Key.NewVideoName))) {
                videoView.setVideoPath(videof);
            } else {
                Toast.makeText(this, "网络异常，请联系服务人员", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                finish();
                return false;
            }
        });
        videoView.start();

    }
}
