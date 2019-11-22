package com.jiang.tvlauncher.servlet;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.jiang.tvlauncher.MyAPP;
import com.jiang.tvlauncher.dialog.Loading;
import com.jiang.tvlauncher.utils.LogUtil;
import com.xgimi.business.api.clients.ApiProxyServiceClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jiangyao
 * on 2017/9/7.
 * Email: www.fangmu@qq.com
 * Phone：186 6120 1018
 * Purpose:TODO 下载
 * update：
 */

public class DownUtil {
    private static final String TAG = "DownUtil";

    ProgressDialog pd;

    public void downLoad(final String path, final String fileName, final boolean showpd) {
        // 进度条对话框
        pd = new ProgressDialog(MyAPP.context);
        pd.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("下载中，精彩马上呈现，请稍后...");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        // 监听返回键--防止下载的时候点击返回
        pd.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//                    Toast.makeText(activity, "正在下载请稍后", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        });
        // Sdcard不可用
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(MyAPP.context, "SD卡不可用~", Toast.LENGTH_SHORT).show();
            Loading.dismiss();

        } else {
            if (showpd)
                if (MyAPP.currentActivity() == null || MyAPP.currentActivity().isDestroyed() || MyAPP.currentActivity().isFinishing()) {
                    LogUtil.e(TAG, "当前活动已经被销毁");
                } else {
                    try {
                        pd.show();
                    } catch (WindowManager.BadTokenException e) {
                        LogUtil.e(TAG, e.getMessage());
                    }
                }

            //下载的子线程
            new Thread() {
                @Override
                public void run() {
                    try {
                        // 在子线程中下载APK文件
                        final File file = getFileFromServer(path, fileName, pd);
                        if(file!=null && file.exists()){
                            //休眠
                            sleep(1000);
                            LogUtil.e(TAG, "文件下载完成:" + fileName);
                            if (showpd && pd!=null){
                                pd.dismiss(); // 结束掉进度条对话框
                            }

                            if(fileName.toLowerCase().contains(".apk")){
                                //是极米设备
                                if (MyAPP.isxgimi) {
                                    //调用极米静默安装
                                    ApiProxyServiceClient.INSTANCE.binderAidlService(MyAPP.context, new ApiProxyServiceClient.IAidlConnectListener() {
                                        @Override
                                        public void onSuccess() {
                                            LogUtil.e(TAG, "连接成功");
                                            ApiProxyServiceClient.INSTANCE.silentInstallPackage(file.getPath(), null);
                                            //释放资源
                                            ApiProxyServiceClient.INSTANCE.release();
                                        }

                                        @Override
                                        public void onFailure(RemoteException e) {

                                        }
                                    });

                                } else {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                                    MyAPP.currentActivity().startActivity(intent);
                                }
                            }else if(fileName.toLowerCase().contains(".zip")){
                                ApiProxyServiceClient.INSTANCE.binderAidlService(MyAPP.context, new ApiProxyServiceClient.IAidlConnectListener() {
                                    @Override
                                    public void onSuccess() {
                                        LogUtil.e(TAG, "AIDL 连接成功");
                                        //附上开机动画
                                        ApiProxyServiceClient.INSTANCE.changeBootAnimation(file.getPath());

                                        //释放资源
                                        ApiProxyServiceClient.INSTANCE.release();
                                    }

                                    @Override
                                    public void onFailure(RemoteException e) {

                                    }
                                });
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.e(TAG, "文件下载失败了" + e.getMessage());

                        Loading.dismiss();
                        if (showpd && pd!=null) {
                            pd.dismiss();
                        }
                    }
                }

            }.start();
        }
    }

    /**
     * 从服务器下载apk
     */
    public static File getFileFromServer(String path, String fileName, ProgressDialog pd) throws Exception {
        // 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        InputStream is = null;
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        File file = null;
        try{
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && (path!=null && path.length()>0)) {
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //设置缓存
                conn.setUseCaches(false);
                // 设置连接主机超时时间
                conn.setConnectTimeout(5 * 1000);

                // 开始连接
                conn.connect();
                if (conn.getResponseCode() == 200) {
                    //更新进度条
                    if (pd!=null){
                        pd.setMax(conn.getContentLength() / 1024);
                    }

                    //创建文件
                    file = new File(Environment.getExternalStorageDirectory().getPath() + "/feekr/Download/", fileName);
                    //判断文件夹是否被创建
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }

                    //获取网络文件流
                    is = conn.getInputStream();
                    fos = new FileOutputStream(file);
                    bis = new BufferedInputStream(is);

                    byte[] buffer = new byte[1024];
                    int len;
                    int total = 0;
                    while ((len = bis.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                        total += len;
                        // 获取当前下载量
                        if (pd != null) {
                            pd.setProgress(total / 1024);
                        }
                    }

                    conn.disconnect();


                } else {
                    LogUtil.e(TAG, "文件下载失败"+path);
                }
                // 关闭连接
                conn.disconnect();
            }else{
                Loading.dismiss();
            }
        }catch(Exception e){
            LogUtil.e(TAG, e.getMessage());
        }finally {
            if (fos != null) {
                fos.close();
            }
            if (bis != null) {
                bis.close();
            }
            if (is != null) {
                is.close();
            }
        }
        return file;
    }
}
