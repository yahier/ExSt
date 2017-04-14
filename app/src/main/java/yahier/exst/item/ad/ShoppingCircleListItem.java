package yahier.exst.item.ad;

import java.util.List;

/**
 * Created by Administrator on 2016/9/30.
 */

public class ShoppingCircleListItem {
    long sortid;
    int isexpire;//1-是，0-否，
    public final static int isexpireYes = 1;
    public final static int isexpireNo = 0;
    List<ShoppingCircleDetail> datalist;

    public long getSortid() {
        return sortid;
    }

    public void setSortid(long sortid) {
        this.sortid = sortid;
    }

    public int getIsexpire() {
        return isexpire;
    }

    public void setIsexpire(int isexpire) {
        this.isexpire = isexpire;
    }


    public List<ShoppingCircleDetail> getDatalist() {
        return datalist;
    }

    public void setDatalist(List<ShoppingCircleDetail> datalist) {
        this.datalist = datalist;
    }
}
