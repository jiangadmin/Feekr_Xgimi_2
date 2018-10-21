package com.jiang.tvlauncher.entity;

import java.util.List;

/**
 * Created by wwwfa
 * on 2017/8/21.
 * Email: www.fangmu@qq.com
 * Phone：186 6120 1018
 * Purpose:TODO 梯形数据
 * update：
 */
public class Point {


    /**
     * version : point_keystone
     * point : [{"current_x":0,"current_y":0,"idx":0,"max_x":100,"max_y":100,"min_x":0,"min_y":0},{"current_x":-100,"current_y":100,"idx":1,"max_x":0,"max_y":100,"min_x":-100,"min_y":0},{"current_x":-100,"current_y":-100,"idx":3,"max_x":0,"max_y":0,"min_x":-100,"min_y":-100},{"current_x":100,"current_y":-100,"idx":2,"max_x":100,"max_y":0,"min_x":0,"min_y":-100}]
     */

    private String version;
    private List<PointBean> point;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<PointBean> getPoint() {
        return point;
    }

    public void setPoint(List<PointBean> point) {
        this.point = point;
    }

    public static class PointBean {
        /**
         * current_x : 0
         * current_y : 0
         * idx : 0
         * max_x : 100
         * max_y : 100
         * min_x : 0
         * min_y : 0
         */

        private int current_x;
        private int current_y;
        private int idx;
        private int max_x;
        private int max_y;
        private int min_x;
        private int min_y;

        public String getCurrent_x() {
            return String.valueOf(current_x);
        }

        public void setCurrent_x(int current_x) {
            this.current_x = current_x;
        }

        public String getCurrent_y() {
            return String.valueOf(current_y);
        }

        public void setCurrent_y(int current_y) {
            this.current_y = current_y;
        }

        public String getIdx() {
            return String.valueOf(idx);
        }

        public void setIdx(int idx) {
            this.idx = idx;
        }

        public int getMax_x() {
            return max_x;
        }

        public void setMax_x(int max_x) {
            this.max_x = max_x;
        }

        public int getMax_y() {
            return max_y;
        }

        public void setMax_y(int max_y) {
            this.max_y = max_y;
        }

        public int getMin_x() {
            return min_x;
        }

        public void setMin_x(int min_x) {
            this.min_x = min_x;
        }

        public int getMin_y() {
            return min_y;
        }

        public void setMin_y(int min_y) {
            this.min_y = min_y;
        }
    }
}
