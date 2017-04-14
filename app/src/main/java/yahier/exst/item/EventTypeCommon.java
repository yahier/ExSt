package yahier.exst.item;

import java.io.Serializable;

public class EventTypeCommon implements Serializable {
    int type;
    UserItem user;
    private long userId; //用户id
    public final static int typeRefreshTags = 1;
    public final static int typeRefreshHomePage = 2;
    public final static int typeRefreshStatuses = 3;
    public final static int typeToStatusesTop = 33;
    public final static int typeRefreshIM = 4;
    public final static int typeRefreshMine = 5;
    public final static int typeHelperAfterRecommend = 6;//帮一帮推荐好友之后
    public final static int typeChangeFansAttendSize = 7;//改变了粉丝，关注数
    public final static int typeChangeRelation = 8;//取消关注 或者删除好友
    public final static int typeSignIn = 9;//签到了
    public final static int typeGetDiscussionMember = 10;//获取新的讨论组成员

    public final static int typeUpdateStatusesAttend = 11;//刷新
    public final static int typeUpdateStatusesSquare = 12;//获取新的讨论组成员
    public final static int typeToTopStatusesAttend = 13;//刷新
    public final static int typeToTopStatusesSquare = 14;//获取新的讨论组成员
    public final static int typeGetRecommendMaster = 15;
    //预防列表没有请求到数据
    public final static int typeGetStatusesAttend = 16;//
    public final static int typeGetStatusesSquare = 17;//

    public final static int typeCloseIntroduceAd = 18; //关闭广告介绍页
    public final static int typeClosePublishAd = 19; //关闭广告投放页

    public final static int typeUpdateStatusesShoppingCircle = 20;
    public final static int typeToTopStatusesShoppingCircle = 21;//刷新
    public final static int typeGetStatusesShoppingCircle = 22;//
    public final static int typeClickedShare = 23;//
    public final static int typeUpdateBrand = 24;//
    public final static int typeCloseHelpOrder = 25;// 关闭代付订单详情页
    public final static int typeShoppingCircleReplyCommentSuccess = 26;// 购物圈回复评论成功

    public final static int typeToDongtaiIndex = 27;// 去某个动态模块，userid为模块值
    public final static int typeRefreshWeb = 28;// 刷新CommonWeb


    public EventTypeCommon(int type) {
        this.type = type;
    }
    public EventTypeCommon(int type,long userId) {
        this.type = type;
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public EventTypeCommon(UserItem user) {
        this.user = user;
    }

    public UserItem getUser() {
        return user;
    }

    public void setUser(UserItem user) {
        this.user = user;
    }
}
