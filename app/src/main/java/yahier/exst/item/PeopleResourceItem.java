package yahier.exst.item;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yahier on 16/11/11.
 */

public class PeopleResourceItem  implements Serializable {
    UserCount countview;
    List<UserItem> tudiview;

    public UserCount getCountview() {
        return countview;
    }

    public void setCountview(UserCount countview) {
        this.countview = countview;
    }

    public List<UserItem> getTudiview() {
        return tudiview;
    }

    public void setTudiview(List<UserItem> tudiview) {
        this.tudiview = tudiview;
    }
}
