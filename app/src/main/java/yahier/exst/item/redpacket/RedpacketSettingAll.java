package yahier.exst.item.redpacket;

import java.io.Serializable;

/**
 * Created by yahier on 17/1/3.
 */

public class RedpacketSettingAll implements Serializable {
    RedpacketCommon config;//通用配置
    RedpacketSetting adconfig;//广告主配置
    RedpacketSetting unadconfig;//非广告主配置


    public RedpacketSetting getAdconfig() {
        return adconfig;
    }

    public void setAdconfig(RedpacketSetting adconfig) {
        this.adconfig = adconfig;
    }

    public RedpacketSetting getUnadconfig() {
        return unadconfig;
    }

    public void setUnadconfig(RedpacketSetting unadconfig) {
        this.unadconfig = unadconfig;
    }

    public RedpacketCommon getConfig() {
        return config;
    }

    public void setConfig(RedpacketCommon config) {
        this.config = config;
    }
}
