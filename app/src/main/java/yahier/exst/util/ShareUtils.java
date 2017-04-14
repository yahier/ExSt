package yahier.exst.util;

import android.text.TextUtils;

import com.stbl.stbl.R;
import com.stbl.stbl.item.LinkInStatuses;
import com.stbl.stbl.item.LinkStatuses;
import com.stbl.stbl.item.ShareItem;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.StatusesPic;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.ad.AdUserItem2;
import com.stbl.stbl.item.ad.ShoppingCircleDetail;
import com.stbl.stbl.model.Goods;

/**
 * Created by tnitf on 2016/3/20.
 */
public class ShareUtils {

    public static ShareItem createDongtaiShare(Statuses statuses) {
        ShareItem shareItem = new ShareItem();
        if (statuses.getLinks() != null) {
            LinkStatuses link = statuses.getLinks();
            shareItem.nickname = statuses.getUser().getNickname();
            switch (link.getLinktype()) {
                case LinkStatuses.linkTypeCrad: {
                    UserItem user = link.getUserinfo();
//                    shareItem.setTitle(statuses.getUser().getNickname() + "的动态");
//                    shareItem.setContent("分享" + user.getNickname() + "的名片");
                    shareItem.setTitle(user.getNickname() + "的主页");
                    String content = TextUtils.isEmpty(user.getSignature()) ? "来师徒部落，打造您的强人脉商业帝国" : user.getSignature();
                    shareItem.setContent(content);
                    shareItem.setImgUrl(user.getImgurl());
                }
                break;
                case LinkStatuses.linkTypeProduct: {
                    Goods goods = link.getProductinfo();
//                    shareItem.setTitle(statuses.getUser().getNickname() + "的动态");
//                    shareItem.setContent(goods.getGoodsname());
                    shareItem.setTitle(goods.getGoodsname());
                    shareItem.setContent("我在师徒部落发现了一个很赞的商品，赶快来看看吧。");
                    shareItem.setImgUrl(goods.getImgurl());
                }
                break;
                case LinkStatuses.linkTypeStateses: {
                    Statuses originStatus = link.getStatusesinfo();
                    shareItem.setTitle(originStatus.getUser().getNickname() + "的动态");
//                    shareItem.setTitle(statuses.getUser().getNickname() + "的动态");
                    String content = "";
//                    if (statuses.getIsforward() == Statuses.isforwardYes) {
//                        content = "转发" + originStatus.getUser().getNickname() + "的动态";
//                    } else {
//                        content = "分享的图片";
//                    }
                    if (!TextUtils.isEmpty(originStatus.getTitle())) {
                        content = originStatus.getTitle();
                    } else if (!TextUtils.isEmpty(originStatus.getContent())) {
                        content = originStatus.getContent();
                    }
                    shareItem.setContent(content);
                    //取第一张图片
                    String imgUrl = originStatus.getUser().getImgurl();
                    if (originStatus.getStatusestype() == Statuses.type_long) {
                        StatusesPic pic = originStatus.getStatusespic();
                        if (!TextUtils.isEmpty(pic.getDefaultpic())) {
                            imgUrl = pic.getMiddlepic() + pic.getDefaultpic();
                        } else if (pic.getPics() != null && pic.getPics().size() > 0) {
                            imgUrl = pic.getMiddlepic() + pic.getPics().get(0);
                        }
                    }
                    shareItem.setImgUrl(imgUrl);
                }
                break;
                case LinkStatuses.linkTypeVideo: {
                    String title = statuses.getUser().getNickname() + "的动态";
                    shareItem.setTitle(title);
                    String content = link.getLinktitle();
                    shareItem.setContent(content);
                    String imgUrl = link.getLinkex();
                    if (TextUtils.isEmpty(imgUrl)) {
                        imgUrl = statuses.getUser().getImgurl();
                    }
                    shareItem.setImgUrl(imgUrl);
                }
                break;
                case LinkStatuses.linkTypeNiceLink: {
                    LinkInStatuses nickLink = link.getUserlinksinfo();
                    shareItem.setTitle(nickLink.getTitle());
                    shareItem.setContent("师徒部落，人脉商务社交神器");
//                    shareItem.setTitle(statuses.getUser().getNickname() + "的动态");
//                    shareItem.setContent(nickLink.getTitle());
                    shareItem.setImgUrl(nickLink.getImgurl());
                }
                break;
            }
        } else { //纯图文动态
            String content = "分享的图片";
            if (!TextUtils.isEmpty(statuses.getTitle())) {
                content = statuses.getTitle();
            } else if (!TextUtils.isEmpty(statuses.getContent())) {
                content = statuses.getContent();
            }
            shareItem.nickname = statuses.getUser().getNickname();
            shareItem.setTitle("分享"+statuses.getUser().getNickname() + "的动态");
            shareItem.setContent(content);
            //取第一张图片
            String imgUrl = statuses.getUser().getImgurl();
            if (statuses.getStatusestype() == Statuses.type_long) {
                StatusesPic pic = statuses.getStatusespic();
                if (!TextUtils.isEmpty(pic.getDefaultpic())) {
                    imgUrl = pic.getMiddlepic() + pic.getDefaultpic();
                } else if (pic.getPics() != null && pic.getPics().size() > 0) {
                    imgUrl = pic.getMiddlepic() + pic.getPics().get(0);
                }
            }
            shareItem.setImgUrl(imgUrl);
        }
        LogUtil.logE("imgUrl = " + shareItem.getImgUrl());
        return shareItem;
    }

    public static ShareItem createShoppingCircle(ShoppingCircleDetail detail) {
        ShareItem shareItem = new ShareItem();
        shareItem.setTitle(Res.getString(R.string.me_share_shopping_title));
        AdUserItem2 user = detail.getUserinfo();
        if (user != null) {
            shareItem.setContent(String.format(Res.getString(R.string.me_share_shopping_desc), user.getAdnickname()));
            shareItem.setImgUrl(user.getAdimgurl());
        }
        return shareItem;
    }

}
