package yahier.exst.model;

import java.io.Serializable;

/**
 * 上传商品时，图片描述实体
 * Created by Administrator on 2016/3/25 0025.
 */
public class ImageDescriptionInfo implements Serializable{
    private String imgId; //图片上传后返回的id
    private String imgurl; //图片地址
    private String description; //图片描述

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ImageDescriptionInfo{" +
                "imgurl='" + imgurl + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
