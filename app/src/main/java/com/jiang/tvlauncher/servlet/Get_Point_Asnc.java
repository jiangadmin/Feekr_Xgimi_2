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
 * @author: jiangadmin
 * @date: 2019/1/22
 * @Email: www.fangmu@qq.com
 * @Phone: 186 6120 1018
 * TODO: 异步读取梯形校正信息
 */
public class Get_Point_Asnc extends AsyncTask<String, Integer, String> {
    private static final String TAG = "Get_Point_Asnc";

    @Override
    protected String doInBackground(String... strings) {
        long time = new Date().getTime();

        Point point = new Point();
        LogUtil.e(TAG, "开始读取");

        List<Point.PointBean> beans = new ArrayList<>();

        DlpKeystoneBean bean0 = DlpKeystoneManager.INSTANCE.getKeystoneValue(DlpKeystoneManager.POINT_LEFT_TOP);
        LogUtil.e(TAG, "读取左上角>X:" + bean0.getHorizontalValue() + "Y:" + bean0.getVerticalValue());
        long time0 = new Date().getTime();
        LogUtil.e(TAG, "结束：" + (time0 - time));
        Point.PointBean pointBean0 = new Point.PointBean();
        pointBean0.setIdx(0);
        pointBean0.setCurrent_x(bean0.getHorizontalValue());
        pointBean0.setCurrent_x(bean0.getVerticalValue());
        beans.add(pointBean0);

        DlpKeystoneBean bean1 = DlpKeystoneManager.INSTANCE.getKeystoneValue(DlpKeystoneManager.POINT_RIGHT_TOP);
        LogUtil.e(TAG, "读取右上角>X:" + bean1.getHorizontalValue() + "Y:" + bean1.getVerticalValue());
        long time1 = new Date().getTime();
        LogUtil.e(TAG, "结束：" + (time1 - time));
        Point.PointBean pointBean1 = new Point.PointBean();
        pointBean1.setIdx(1);
        pointBean1.setCurrent_x(bean1.getHorizontalValue());
        pointBean1.setCurrent_x(bean1.getVerticalValue());
        beans.add(pointBean1);

        DlpKeystoneBean bean2 = DlpKeystoneManager.INSTANCE.getKeystoneValue(DlpKeystoneManager.POINT_LEFT_BOTTOM);
        LogUtil.e(TAG, "读取左下角>X:" + bean2.getHorizontalValue() + "Y:" + bean2.getVerticalValue());
        long time2 = new Date().getTime();
        LogUtil.e(TAG, "结束：" + (time2 - time));
        Point.PointBean pointBean2 = new Point.PointBean();
        pointBean2.setIdx(2);
        pointBean2.setCurrent_x(bean2.getHorizontalValue());
        pointBean2.setCurrent_x(bean2.getVerticalValue());
        beans.add(pointBean2);

        DlpKeystoneBean bean3 = DlpKeystoneManager.INSTANCE.getKeystoneValue(DlpKeystoneManager.POINT_RIGHT_BOTTOM);
        LogUtil.e(TAG, "读取右下角>X:" + bean3.getHorizontalValue() + "Y:" + bean3.getVerticalValue());
        long time3 = new Date().getTime();
        LogUtil.e(TAG, "结束：" + (time3 - time));
        Point.PointBean pointBean3 = new Point.PointBean();
        pointBean3.setIdx(3);
        pointBean3.setCurrent_x(bean3.getHorizontalValue());
        pointBean3.setCurrent_x(bean3.getVerticalValue());
        beans.add(pointBean3);

        point.setPoint(beans);
        return null;
    }
}
