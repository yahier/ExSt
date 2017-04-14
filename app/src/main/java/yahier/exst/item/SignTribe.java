package yahier.exst.item;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yahier on 17/1/16.
 */

public class SignTribe implements Serializable {
    ArrayList<UserItem> proxysigninview;

    public ArrayList<UserItem> getProxysigninview() {
        return proxysigninview;
    }

    public void setProxysigninview(ArrayList<UserItem> proxysigninview) {
        this.proxysigninview = proxysigninview;
    }
}
