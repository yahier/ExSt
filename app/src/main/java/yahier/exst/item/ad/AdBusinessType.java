package yahier.exst.item.ad;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/1.
 */

public class AdBusinessType implements Serializable{
    String name;
    String value;
    public final static String typeValueProtocal = "1";
    public final static String typeValueJoin = "2";
    public final static String typeProtocal = "诚招代理";
    public final static String typeJoin = "诚招加盟";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
