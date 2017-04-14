package yahier.exst.model.mgs;

import com.stbl.stbl.item.UserItem;

import java.io.Serializable;

public class SortModel implements Serializable {

    public String name;//主键，昵称或者备注（别名）
    public String sortKey;

    public String number = "";

    public String sortLetters; // 显示数据拼音的首字母

    public SortToken sortToken = new SortToken();

    public UserItem user;

    //默认保存昵称的索引信息
    public String nick;
    public String nickSortKey;
    public String nickSortLetters;
    public SortToken nickSortToken = new SortToken();

//    @Override
//    public int hashCode() {
//        final int prime = 31;
//        int result = 1;
//        result = prime * result + ((name == null) ? 0 : name.hashCode());
//        result = prime * result + ((sortKey == null) ? 0 : sortKey.hashCode());
//        return result;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj)
//            return true;
//        if (obj == null)
//            return false;
//        if (getClass() != obj.getClass())
//            return false;
//        SortModel other = (SortModel) obj;
//        if (name == null) {
//            if (other.name != null)
//                return false;
//        } else if (!name.equals(other.name))
//            return false;
//
//        if (sortKey == null) {
//            if (other.sortKey != null)
//                return false;
//        } else if (!sortKey.equals(other.sortKey))
//            return false;
//        return true;
//    }

}
