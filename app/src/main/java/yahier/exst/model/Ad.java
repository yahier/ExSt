package yahier.exst.model;

import com.stbl.stbl.item.UserItem;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/22.
 */

public class Ad implements Serializable {

    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_GOODS = 1;
    public static final int TYPE_SHOP = 2;
    public static final int TYPE_MODULE = 3;
    public static final int TYPE_TOPIC = 4;
    public static final int TYPE_WEBSITE = 99;

    public static final int isAderYes = 1;
    public static final int isAderNo = 0;
    public String adid;
    public String adimgminurl;
    public String adimglarurl;
    public String adurl;
    public int type;
    public String adtitle;
    public int issys;

    public static final int issysYes = 1;
    public static final int issysNo = 0;


    public UserItem user;
    public String tradeclassname;
    //分别是广告合作的类别和对应的名称

    public final static int businessclassNone = 0;
    public int businessclass;
    public String businessclassname;

    public UserCard usercard;

    public boolean expand;


}
