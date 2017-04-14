package yahier.exst.item.ad;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/12/26.
 */

public class RPImgInfo implements Serializable{
    ArrayList<String> middlepic;
    ArrayList<String> originalpic;


    public ArrayList<String> getMiddlepic() {
        return middlepic;
    }

    public void setMiddlepic(ArrayList<String> middlepic) {
        this.middlepic = middlepic;
    }

    public ArrayList<String> getOriginalpic() {
        return originalpic;
    }

    public void setOriginalpic(ArrayList<String> originalpic) {
        this.originalpic = originalpic;
    }
}
