package yahier.exst.item.redpacket;

/**
 * Created by Administrator on 2016/12/26.
 * 红包配置
 */

public class RedpacketSetting {
    int receintervaltime;//抢红包时间间隔（单位：秒）
    int recelimitcount;//抢红包次数限制（单位：次/天）
    int recefreecount;//每天抢红包的免费次数
    float receavgmin;//红包随机金额最小运算值
    float receavgmax;//红包随即金额最大运算值
    int senddailycount;//每日发起红包数量(少于0 等于不设置)
    String sendavgmin;//红包金额人均最小限制(小于等于 0 表示不设置)
    String sendavgmax;//红包金额人均最大限制(小于等于 0 表示不设置)
    int sendqtymax;//红包份数最大限制(小于等于 0 表示不设置)
    int sendqtymin;//红包份数最小限制(小于等于 0 表示不设置)
    int moneymax; //红包金额最大限制(小于等于 0 表示不设置)

    public int getReceintervaltime() {
        return receintervaltime;
    }

    public void setReceintervaltime(int receintervaltime) {
        this.receintervaltime = receintervaltime;
    }

    public int getMoneymax() {
        return moneymax;
    }

    public void setMoneymax(int moneymax) {
        this.moneymax = moneymax;
    }

    public int getSendqtymin() {
        return sendqtymin;
    }

    public void setSendqtymin(int sendqtymin) {
        this.sendqtymin = sendqtymin;
    }

    public int getSendqtymax() {
        return sendqtymax;
    }

    public void setSendqtymax(int sendqtymax) {
        this.sendqtymax = sendqtymax;
    }

    public String getSendavgmax() {
        return sendavgmax;
    }

    public void setSendavgmax(String sendavgmax) {
        this.sendavgmax = sendavgmax;
    }

    public int getSenddailycount() {
        return senddailycount;
    }

    public void setSenddailycount(int senddailycount) {
        this.senddailycount = senddailycount;
    }

    public String getSendavgmin() {
        return sendavgmin;
    }

    public void setSendavgmin(String sendavgmin) {
        this.sendavgmin = sendavgmin;
    }

    public float getReceavgmax() {
        return receavgmax;
    }

    public void setReceavgmax(float receavgmax) {
        this.receavgmax = receavgmax;
    }

    public float getReceavgmin() {
        return receavgmin;
    }

    public void setReceavgmin(float receavgmin) {
        this.receavgmin = receavgmin;
    }

    public int getRecefreecount() {
        return recefreecount;
    }

    public void setRecefreecount(int recefreecount) {
        this.recefreecount = recefreecount;
    }

    public int getRecelimitcount() {
        return recelimitcount;
    }

    public void setRecelimitcount(int recelimitcount) {
        this.recelimitcount = recelimitcount;
    }

    public double getSingleMaxAmount() {
        return Double.parseDouble(sendavgmax);
    }

    public double getSingleMinAmount() {
        return Double.parseDouble(sendavgmin);
    }
}

