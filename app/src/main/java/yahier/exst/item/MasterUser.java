package yahier.exst.item;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lenovo on 2016/3/14.
 */
public class MasterUser implements Serializable {
    int sortid;
    UserItem userview;
    UserCount countview;
    List<Photo> photoview;


    public UserCount getCountview() {
        return countview;
    }

    public void setCountview(UserCount countview) {
        this.countview = countview;
    }

    public List<Photo> getPhotoview() {
        return photoview;
    }

    public void setPhotoview(List<Photo> photoview) {
        this.photoview = photoview;
    }

    public int getSortid() {
        return sortid;
    }

    public void setSortid(int sortid) {
        this.sortid = sortid;
    }

    public UserItem getUserview() {
        return userview;
    }

    public void setUserview(UserItem userview) {
        this.userview = userview;
    }
}
