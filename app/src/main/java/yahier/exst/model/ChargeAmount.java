package yahier.exst.model;

import java.io.Serializable;

/**
 * Created by tnitf on 2016/4/13.
 */
public class ChargeAmount implements Serializable {

    private int ex1; //RMB
    private int ex2; //金豆数量

    public int getEx1() {
        return ex1;
    }

    public void setEx1(int ex1) {
        this.ex1 = ex1;
    }

    public int getEx2() {
        return ex2;
    }

    public void setEx2(int ex2) {
        this.ex2 = ex2;
    }
}
