package yahier.exst.item.ad;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/9/28.
 */

public class YunHongbaoPickerInfo implements Serializable {
    int Offset;
    int Length;
    List<YunHongbaoPicker> List;


    public int getOffset() {
        return Offset;
    }

    public void setOffset(int offset) {
        Offset = offset;
    }

    public int getLength() {
        return Length;
    }

    public void setLength(int length) {
        Length = length;
    }

    public java.util.List<YunHongbaoPicker> getList() {
        return List;
    }

    public void setList(java.util.List<YunHongbaoPicker> list) {
        List = list;
    }
}
