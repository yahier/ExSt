package yahier.exst.item;

import com.stbl.stbl.model.Banner;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lenovo on 2016/3/15.
 */
public class TopicAll implements Serializable {
    List<Banner> banners;
    List<Topic> attentions;
    List<Topic> recommends;

    public List<Topic> getAttentions() {
        return attentions;
    }

    public void setAttentions(List<Topic> attentions) {
        this.attentions = attentions;
    }

    public List<Banner> getBanners() {
        return banners;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }

    public List<Topic> getRecommends() {
        return recommends;
    }

    public void setRecommends(List<Topic> recommends) {
        this.recommends = recommends;
    }
}
