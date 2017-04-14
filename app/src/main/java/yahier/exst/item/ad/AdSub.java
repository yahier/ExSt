package yahier.exst.item.ad;

import com.stbl.stbl.model.Ad;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/8.
 */

public class AdSub implements Serializable{
    int sortno;
    ArrayList<Ad> adview;
    int count;//总条数

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSortno() {
        return sortno;
    }

    public void setSortno(int sortno) {
        this.sortno = sortno;
    }

    public ArrayList<Ad> getAdview() {
        return adview;
    }

    public void setAdview(ArrayList<Ad> adview) {
        this.adview = adview;
    }
}
