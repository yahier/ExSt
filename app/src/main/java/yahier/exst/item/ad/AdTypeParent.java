package yahier.exst.item.ad;

import java.util.List;

/**
 * Created by Administrator on 2016/10/1.
 */

public class AdTypeParent {
    int id;
    String title;
    List<AdType> childnode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<AdType> getChildnode() {
        return childnode;
    }

    public void setChildnode(List<AdType> childnode) {
        this.childnode = childnode;
    }
}
