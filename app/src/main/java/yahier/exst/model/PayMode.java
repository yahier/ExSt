package yahier.exst.model;

import java.io.Serializable;

/**
 * Created by tnitf on 2016/4/13.
 * 支付方式
 */
public class PayMode implements Serializable {

    private String name; //名称
    private int type; //支付type

    public PayMode(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
