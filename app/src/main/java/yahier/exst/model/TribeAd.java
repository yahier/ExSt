package yahier.exst.model;

import com.stbl.stbl.item.ad.AdSub;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 部落广告实体
 * Created by Administrator on 2016/9/27.
 */

public class TribeAd implements Serializable {

    public Ad main;
    //public ArrayList<Ad> sub;
    public AdSub sub;
}
