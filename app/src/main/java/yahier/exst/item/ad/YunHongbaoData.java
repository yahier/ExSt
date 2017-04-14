package yahier.exst.item.ad;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/28.
 */

public class YunHongbaoData implements Serializable {
    int Status;
    YunHongbaoInfo Info;
    YunHongbaoPickerInfo RecipientsGroup;


    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public YunHongbaoInfo getInfo() {
        return Info;
    }

    public void setInfo(YunHongbaoInfo info) {
        Info = info;
    }

    public YunHongbaoPickerInfo getRecipientsGroup() {
        return RecipientsGroup;
    }

    public void setRecipientsGroup(YunHongbaoPickerInfo recipientsGroup) {
        RecipientsGroup = recipientsGroup;
    }
}
