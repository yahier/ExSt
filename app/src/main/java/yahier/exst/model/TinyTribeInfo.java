package yahier.exst.model;

import com.stbl.stbl.item.Photo;
import com.stbl.stbl.item.Relation;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.UserItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/8/30.
 */

public class TinyTribeInfo implements Serializable {

    public UserItem user;
    public Relation relation;
    public List<Photo> userphotoview;
    public int userphotocount;
    public Statuses lateststatusesview;

}
