package yahier.exst.item;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/7/15.
 */
public class CommonDictModuleSwitch implements Serializable{
    int showhongbao;
    int stblskin;
    int showoldwallet;
    public final static int stblskin_default = 0;
    public final static int stblskin_newYear = 1;

    public final static int showoldwallet_yes = 1;
    public final static int showoldwallet_no = 0;

    public int getShowhongbao() {
        return showhongbao;
    }

    public void setShowhongbao(int showhongbao) {
        this.showhongbao = showhongbao;
    }

    public int getStblskin() {
        return stblskin;
    }

    public void setStblskin(int stblskin) {
        this.stblskin = stblskin;
    }

    public int getShowoldwallet() {
        return showoldwallet;
    }

    public void setShowoldwallet(int showoldwallet) {
        this.showoldwallet = showoldwallet;
    }
}
