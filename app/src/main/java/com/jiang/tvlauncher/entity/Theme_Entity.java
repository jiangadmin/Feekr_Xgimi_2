package com.jiang.tvlauncher.entity;

/**
 * @author: jiangadmin
 * @date: 2018/10/14
 * @Email: www.fangmu@qq.com
 * @Phone: 186 6120 1018
 * TODO: 主题
 */
public class Theme_Entity extends BaseEntity {

    /**
     * errorcode : 1000
     * result : {"bgImg":"http://pa5am0bdt.bkt.clouddn.com/086E87BFB8780B07B24C1E1E6E131A0C.png","createAuthor":"系统管理员","createTime":"2018-10-13 20:20:56","id":2,"isDefault":1,"isDelete":0,"micLogoColor":"#091df0","remark":"","startLgeekFlag":1,"status":1,"themeName":"默认","tipContents":"尝试说：哪里有好玩的#试试说出你想看的电影吧#想去哪里玩，尝试说哪里好玩","tipFontColor":"#0317f5","tipShowFlag":1,"tipSwitchRate":7,"updateAuthor":"系统管理员","updateTime":"2018-10-13 20:20:56"}
     */

    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * bgImg : http://pa5am0bdt.bkt.clouddn.com/086E87BFB8780B07B24C1E1E6E131A0C.png
         * createAuthor : 系统管理员
         * createTime : 2018-10-13 20:20:56
         * id : 2
         * isDefault : 1
         * isDelete : 0
         * micLogoColor : #091df0
         * remark :
         * startLgeekFlag : 1
         * status : 1
         * themeName : 默认
         * tipContents : 尝试说：哪里有好玩的#试试说出你想看的电影吧#想去哪里玩，尝试说哪里好玩
         * tipFontColor : #0317f5
         * tipShowFlag : 1
         * tipSwitchRate : 7
         * updateAuthor : 系统管理员
         * updateTime : 2018-10-13 20:20:56
         */

        private String bgImg;
        private String createAuthor;
        private String createTime;
        private int id;
        private int isDefault;
        private int isDelete;
        private String micLogoColor;
        private String remark;
        private int startLgeekFlag;
        private int status;
        private String themeName;
        private String tipContents;
        private String tipFontColor;
        private int tipShowFlag;
        private int tipSwitchRate;
        private String updateAuthor;
        private String updateTime;

        public String getBgImg() {
            return bgImg;
        }

        public void setBgImg(String bgImg) {
            this.bgImg = bgImg;
        }

        public String getCreateAuthor() {
            return createAuthor;
        }

        public void setCreateAuthor(String createAuthor) {
            this.createAuthor = createAuthor;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getIsDefault() {
            return isDefault;
        }

        public void setIsDefault(int isDefault) {
            this.isDefault = isDefault;
        }

        public int getIsDelete() {
            return isDelete;
        }

        public void setIsDelete(int isDelete) {
            this.isDelete = isDelete;
        }

        public String getMicLogoColor() {
            return micLogoColor;
        }

        public void setMicLogoColor(String micLogoColor) {
            this.micLogoColor = micLogoColor;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getStartLgeekFlag() {
            return startLgeekFlag;
        }

        public void setStartLgeekFlag(int startLgeekFlag) {
            this.startLgeekFlag = startLgeekFlag;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getThemeName() {
            return themeName;
        }

        public void setThemeName(String themeName) {
            this.themeName = themeName;
        }

        public String getTipContents() {
            return tipContents;
        }

        public void setTipContents(String tipContents) {
            this.tipContents = tipContents;
        }

        public String getTipFontColor() {
            return tipFontColor;
        }

        public void setTipFontColor(String tipFontColor) {
            this.tipFontColor = tipFontColor;
        }

        public int getTipShowFlag() {
            return tipShowFlag;
        }

        public void setTipShowFlag(int tipShowFlag) {
            this.tipShowFlag = tipShowFlag;
        }

        public int getTipSwitchRate() {
            return tipSwitchRate;
        }

        public void setTipSwitchRate(int tipSwitchRate) {
            this.tipSwitchRate = tipSwitchRate;
        }

        public String getUpdateAuthor() {
            return updateAuthor;
        }

        public void setUpdateAuthor(String updateAuthor) {
            this.updateAuthor = updateAuthor;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }
}
