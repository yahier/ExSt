package yahier.exst.item.ad;

/**
 * Created by Administrator on 2016/10/1.
 * 投放广告时候.使用
 */

public class AdType {
    int id;
    String title;
    int parentid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentid() {
        return parentid;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
