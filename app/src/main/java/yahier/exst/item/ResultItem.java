package yahier.exst.item;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/8.
 */

public class ResultItem implements Serializable{
    int result;
    int issuccess;

    public int getIssuccess() {
        return issuccess;
    }

    public void setIssuccess(int issuccess) {
        this.issuccess = issuccess;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
