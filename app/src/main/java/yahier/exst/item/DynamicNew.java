package yahier.exst.item;

import java.io.Serializable;

/**
 * 主页最新动态 or 购物圈
 * Created by Alan Chueng on 2017/2/9 0009.
 */

public class DynamicNew implements Serializable {
    private String imgurl;
    private String content;
    private int type; //类型：1-动态，2-商圈
    private int count;
    private int createtime;

    public static final int TYPE_DYNAMIC = 1;//动态
    public static final int TYPE_SHOPPING = 2;//商圈


    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCreatetime() {
        return createtime;
    }

    public void setCreatetime(int createtime) {
        this.createtime = createtime;
    }
}
