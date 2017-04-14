package yahier.exst.item.ad;

import com.stbl.stbl.item.CommonDictWebItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/10/14.
 */

public class CommonDictH5 implements Serializable {
    List<CommonDictWebItem> dic;
    long updatetime;


    public List<CommonDictWebItem> getDic() {
        return dic;
    }

    public void setDic(List<CommonDictWebItem> dic) {
        this.dic = dic;
    }

    public long getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(long updatetime) {
        this.updatetime = updatetime;
    }
}
