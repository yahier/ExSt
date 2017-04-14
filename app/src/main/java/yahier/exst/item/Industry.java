package yahier.exst.item;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/12/14.
 */

public class Industry implements Serializable{
    String title;
    int value;
    List<Industry> nodes;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Industry> getNodes() {
        return nodes;
    }

    public void setNodes(List<Industry> nodes) {
        this.nodes = nodes;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
