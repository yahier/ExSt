package yahier.exst.item;

import com.stbl.stbl.item.ad.AdBusinessType;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/10/9.
 */

public class CommonDictAdsys implements Serializable{
    List<AdBusinessType> adbusinessestype;
    List<AdBusinessType> adreporttype;
    float withdrawlimit;


    public List<AdBusinessType> getAdbusinessestype() {
        return adbusinessestype;
    }

    public void setAdbusinessestype(List<AdBusinessType> adbusinessestype) {
        this.adbusinessestype = adbusinessestype;
    }

    public float getWithdrawlimit() {
        return withdrawlimit;
    }

    public void setWithdrawlimit(float withdrawlimit) {
        this.withdrawlimit = withdrawlimit;
    }

    public List<AdBusinessType> getAdreporttype() {
        return adreporttype;
    }

    public void setAdreporttype(List<AdBusinessType> adreporttype) {
        this.adreporttype = adreporttype;
    }
}
