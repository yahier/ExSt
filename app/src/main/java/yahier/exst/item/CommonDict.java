package yahier.exst.item;

import com.stbl.stbl.item.ad.AdBusinessType;
import com.stbl.stbl.item.ad.RedPacketDict;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lenovo on 2016/7/15.
 */
public class CommonDict implements Serializable{
    CommonDictDefaultImg defaultimg;
    CommonDictModuleSwitch moduleswitch;
    CommonDictCurrentExchangeRate currencyexchangerate;
    CommonDictSysIntrod sysintrod;
    UserItem assistant;
    CommonDictAdsys adsys;
    RedPacketDict redpacket;


    public CommonDictCurrentExchangeRate getCurrencyexchangerate() {
        return currencyexchangerate;
    }

    public void setCurrencyexchangerate(CommonDictCurrentExchangeRate currencyexchangerate) {
        this.currencyexchangerate = currencyexchangerate;
    }

    public CommonDictSysIntrod getSysintrod() {
        return sysintrod;
    }

    public void setSysintrod(CommonDictSysIntrod sysintrod) {
        this.sysintrod = sysintrod;
    }

    public CommonDictModuleSwitch getModuleswitch() {
        return moduleswitch;
    }

    public void setModuleswitch(CommonDictModuleSwitch moduleswitch) {
        this.moduleswitch = moduleswitch;
    }


    public CommonDictDefaultImg getDefaultimg() {
        return defaultimg;
    }

    public void setDefaultimg(CommonDictDefaultImg defaultimg) {
        this.defaultimg = defaultimg;
    }

    public UserItem getAssistant() {
        return assistant;
    }

    public void setAssistant(UserItem assistant) {
        this.assistant = assistant;
    }


    public CommonDictAdsys getAdsys() {
        return adsys;
    }

    public void setAdsys(CommonDictAdsys adsys) {
        this.adsys = adsys;
    }

    public RedPacketDict getRedpacket() {
        return redpacket;
    }

    public void setRedpacket(RedPacketDict redpacket) {
        this.redpacket = redpacket;
    }
}
