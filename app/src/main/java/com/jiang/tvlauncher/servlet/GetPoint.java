package com.jiang.tvlauncher.servlet;

import android.os.AsyncTask;

import com.jiang.tvlauncher.entity.Point;
import com.jiang.tvlauncher.utils.LogUtil;
import com.xgimi.business.api.beans.DlpKeystoneBean;
import com.xgimi.business.api.hardwares.DlpKeystoneManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jiangyao
 * Date: 2019-1-22
 * Email: jiangmr@vip.qq.com
 * TODO: 异步读取梯形校正信息
 */
public class GetPoint extends AsyncTask<String, Integer, String> {
    private static final String TAG = "GetPoint";

    @Override
    protected String doInBackground(String... strings) {
        long time = System.currentTimeMillis();

        Point point = new Point();
        LogUtil.e(TAG, "开始读取");

        List<Point.PointBean> beans = new ArrayList<>();

        try {
            DlpKeystoneBean bean0 = DlpKeystoneManager.INSTANCE.getKeystoneValue(DlpKeystoneManager.POINT_LEFT_TOP);
            LogUtil.e(TAG, "读取左上角>X:" + bean0.getHorizontalValue() + "Y:" + bean0.getVerticalValue());
            long time0 = System.currentTimeMillis();
            LogUtil.e(TAG, "结束：" + (time0 - time));
            Point.PointBean pointBean0 = new Point.PointBean();
            pointBean0.setIdx(0);
            pointBean0.setCurrent_x(bean0.getHorizontalValue());
            pointBean0.setCurrent_x(bean0.getVerticalValue());
            beans.add(pointBean0);

            DlpKeystoneBean bean1 = DlpKeystoneManager.INSTANCE.getKeystoneValue(DlpKeystoneManager.POINT_RIGHT_TOP);
            LogUtil.e(TAG, "读取右上角>X:" + bean1.getHorizontalValue() + "Y:" + bean1.getVerticalValue());
            long time1 = System.currentTimeMillis();
            LogUtil.e(TAG, "结束：" + (time1 - time));
            Point.PointBean pointBean1 = new Point.PointBean();
            pointBean1.setIdx(1);
            pointBean1.setCurrent_x(bean1.getHorizontalValue());
            pointBean1.setCurrent_x(bean1.getVerticalValue());
            beans.add(pointBean1);

            DlpKeystoneBean bean2 = DlpKeystoneManager.INSTANCE.getKeystoneValue(DlpKeystoneManager.POINT_LEFT_BOTTOM);
            LogUtil.e(TAG, "读取左下角>X:" + bean2.getHorizontalValue() + "Y:" + bean2.getVerticalValue());
            long time2 =System.currentTimeMillis();
            LogUtil.e(TAG, "结束：" + (time2 - time));
            Point.PointBean pointBean2 = new Point.PointBean();
            pointBean2.setIdx(2);
            pointBean2.setCurrent_x(bean2.getHorizontalValue());
            pointBean2.setCurrent_x(bean2.getVerticalValue());
            beans.add(pointBean2);

            DlpKeystoneBean bean3 = DlpKeystoneManager.INSTANCE.getKeystoneValue(DlpKeystoneManager.POINT_RIGHT_BOTTOM);
            LogUtil.e(TAG, "读取右下角>X:" + bean3.getHorizontalValue() + "Y:" + bean3.getVerticalValue());
            long time3 = System.currentTimeMillis();
            LogUtil.e(TAG, "结束：" + (time3 - time));
            Point.PointBean pointBean3 = new Point.PointBean();
            pointBean3.setIdx(3);
            pointBean3.setCurrent_x(bean3.getHorizontalValue());
            pointBean3.setCurrent_x(bean3.getVerticalValue());
            beans.add(pointBean3);

            point.setPoint(beans);
        }catch (Exception e){
            LogUtil.e(TAG,"极米专属："+e.getMessage());
        }

        return null;
    }
}
