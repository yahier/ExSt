package yahier.exst.model.seller;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/29 0029.
 */
public class BankCardTypeInfo implements Serializable{
    private String bankname; //银行名称
    private int bin; //识别码
    private String imgurl; //银行卡icon

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public int getBin() {
        return bin;
    }

    public void setBin(int bin) {
        this.bin = bin;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
}
