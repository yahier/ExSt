package yahier.exst.util;

public class Method {

    // 首页
    public final static String homePageHeanMen = "home/headmen"; // 大宗师
    public final static String homePage = "/home/show"; // 首页
    public final static String homeBanner = "home/banner"; // 首页banner
    public final static String homeMayLike = "home/like"; // 猜你喜欢


    public final static String commitAdApply = "adsys/apply/commit";//提交广告
    public final static String uploadAdImg = "adsys/upload";//上传广告图
    public final static String setAdBrand = "adsys/aduser/set";//设置品牌信息
    public final static String getAdBrand = "adsys/userinfo/get";
    public final static String getAdBrandOnlySettingPage = "adsys/aduser/set/info";//获取用户品牌信息
    public final static String adApplyBusinessCooperate = "adsys/business/add";//申请商务合作
    public final static String adBusinessApplyLeftTimes = "adsys/business/check";//剩下的商务合作申请数量


    //红包
    public final static String checkPacketReceive = "redpacket/v1/square/receive/check";//抢红包前的验证
    public final static String redpacketPick = "redpacket/square/receive";//领取红包
    public final static String getRedpacketSetting = "redpacket/square/settings";//获取红包配置
    public final static String getRedPacketDetail = "redpacket/square/redpacket/get";//获取红包详情
    public final static String getRedpacketPickersV1 = "redpacket/v1/square/redpacket/logs ";//获取红包领取列表
    public final static String getRedpacketPickers = "redpacket/square/redpacket/logs";//获取红包领取列表
    public final static String redpacketCreate = "redpacket/square/create";//创建红包
    public final static String userGetReceiveRedpackets = "redpacket/user/receive/logs";//个人领取红包列表
    public final static String userGetSentRedpackets = "redpacket/user/send/logs";//个人发出红包列表
    public final static String userGetRedpacketSummary = "redpacket/user/summary";//个人领取红包统计（发出与收到）
    public final static String redpacketShareCallback = "redpacket/square/share";//分享购物圈回调
    public final static String redpacketGetPermission = "redpacket/auth/get";//获取红包系统权限
    public final static String redpacketWithdrawStatusCheck = "user/withdraw/account/status/check";//查询用户提现账户状态


    //新注册登录
    public final static String getRecommendMasters = "recommend/master/show"; //推荐师傅
    public final static String getContriesPhoneCode = "common/country/query"; //获取国家电话码列表
    public final static String getUserInfoInRegister = "user/tiny/able/get"; //拜师时 获取的用户信息
    public final static String searchUserV1 = "im/user/search_v1"; //带邀请码结果的搜索
    public final static String checkPhoneNo = "user/check/phone"; //检测手机号码
    public final static String getInfoByInviteCode = "user/getwithinvitecode"; //根据邀请码获取userinfo
    public final static String loginTemp = "/auth/temp/login";//游客授权登录
    public final static String getloginAd = "/common/ad/default";//获取登录广告

    // 动态相关
    public final static String statusesNewMessage = "statuses/remind/show";// 获取动态新消息
    public final static String statusesSquareCount = "statuses/square/count";// 获取动态广场数
    public final static String weiboGetRelatedList = "statuses/my_show";// 获取动态主页列表数据
    public final static String weiboSquare = "statuses/square/show";// 获取微博广场
    public final static String weiboSearch = "statuses/check";// 获取动态搜索
    public final static String weiboPush = "statuses/create";// 发布微博
    public final static String weiboUploadImg = "statuses/upload";// 上传图片
    public final static String weiboRemark = "statuses/comments/create";//  评论微博
    public final static String weiboDeleteRemark = "statuses/comments/destroy";// 删除微博评论
    public final static String weiboForward = "statuses/forward";// 转发微博
    public final static String weiboPraise = "statuses/praise/create";// 微博点赞
    public final static String weiboCollect = "statuses/collect/create";// 微博收藏
    public final static String weiboCancelCollect = "statuses/collect/destroy";// 取消收藏
    public final static String videoList = "statuses/square/video/show";//动态视频列表
    public static final String shoppingCircieCreate = "adsys/square/add"; //创建购物圈
    public static final String getShoppingCircieList = "adsys/square/query"; //购物圈列表显示
    public static final String getV2ShoppingCircieList = "adsys/v2/square/query"; //购物圈列表显示
    public static final String getShoppingCircieDetail = "adsys/square/detail/get"; //查看购物圈明细
    public static final String getV2ShoppingCircieDetail = "adsys/v2/square/detail/get"; //v2查看购物圈明细
    public static final String openHongbaoPreCheck = "adsys/redpacket/receive/check"; //开红包之前的验证
    public static final String openHongbaoCallback = "adsys/redpacket/receive/callback"; //开红包之前的回调
    public static final String getShoppingCircleCommentList = "adsys/square/comment/query"; //查询购物圈评论
    public static final String addShoppingCircleComment = "adsys/square/comment/add"; //增加 购物圈评论
    public static final String deleteShoppingCircleComment = "adsys/square/comment/delete"; //删除购物圈的评论
    public static final String getMyShoppingCircleList = "adsys/square/my/query"; //查询我的购物圈
    public static final String getV2MyShoppingCircleList = "adsys/v2/square/my/query"; //查询我的购物圈
    public static final String deleteShoppingCircle = "adsys/square/delete"; //删除购物圈
    public static final String userCardQuery = "user/card/query";
    public static final String commonUpload = "common/upload";
    public static final String adsysV2SquareAdd = "adsys/v2/square/add"; //发布购物圈红包
    public static final String getDynamicDFcontent = "mission/dfcontent/get"; //获取动态默认文案（做任务需要）

    public final static String weiboRemarkList = "statuses/comments/show";// 获取微博评论列表。评论详细
    public final static String weiboCommentPraise = "statuses/comments/praise";// 评论点赞
    public final static String weiboSetTop = "statuses/top/create";// 置顶微博
    public final static String weiboUnSetTop = "statuses/top/destroy";// 取消置顶微博
    public final static String weiboDel = "statuses/destroy";// 删除微博
    public final static String weiboReport = "statuses/complaints/create";// 举报动态
    public final static String weiboRewardList = "statuses/reward/show";//获取动态打赏列表
    public static final String redpacketSquareCreate = "redpacket/square/create";
    public static final String redpacketPayVertify = "redpacket/pay/vertify";
    public final static String weiboGetPraiseList = "statuses/praise/show";// 点赞列表
    public final static String weiboGetOneDetail = "statuses/detail_get";// 获取一个动态详细
    public final static String getRankData = "rankings/query";// 排行榜数据
    public final static String getRankDataV1 = "v1/rankings/query";// 排行榜数据
    // 话题 /topic/list

    public final static String userAttentionInfo = "user/attention/info";
    public final static String topicHome = "/topic/home";// 获取社区主页的banner，关注列表，推荐列表
    public final static String topicList = "/topic/list";// 话题列表
    public final static String topicAddThread = "/topic/comment/create";// 跟帖
    public final static String topicThreadtList = "/topic/comment/list";// 跟帖列表
    public final static String uploadThreadImg = "/topic/comment/img/upload";// 上传跟帖图片
    public final static String sendThreadRemark = "/topic/reply/create";// 发布跟帖回复
    public final static String threadReplyList = "/topic/reply/list";// 跟帖回复列表
    public final static String hotTopicList = "/topic/hot/list";// 热门话题列表
    public final static String topicMine = "/topic/myself/list";// 热门话题列表
    public final static String threadDelete = "/topic/comment/destroy";// 删除跟帖
    public final static String replyDelete = "/topic/reply/destroy";// 删除回复
    public final static String myThreadReplys = "/topic/reply/mylist";// 我跟帖的回复列表
    public final static String myThreadJoinCount = "topic/join/count";// 我参与话题的数量
    public final static String topicDetail = "/topic/info";// 话题详细
    public final static String threadDetail = "/topic/comment/info";// 跟帖详细
    public final static String getTopicBanner = "topic/banner";// 获取社区banner
    public final static String getAttenedTopic = "topic/attention/query";//获取关注的话题列表
    public final static String topicAddAttention = "topic/attention/add";//关注话题
    public final static String topicCancelAttention = "topic/attention/destroy";//取消关注话题

    // 用户相关
    public final static String userUploadHeadImg = "/head/upload";// 上传图像
    public final static String getInviteApplyMsg = "/user/appinvite/smstext/get";//获取邀请短信文案。
    // public final static String userAddTag = "tags/add_usertags";// 添加用户标签
    public final static String userFollow = "user/attention/create";// 关注用户
    public final static String userCancelFollow = "user/attention/destroy";// 取消关注
    public final static String userIgnore = "user/shield/create";// 屏蔽用户
    public final static String userCancelgnore = "user/shield/destroy";// 取消屏蔽用户
    public final static String userGetInfo = "user/info/get";// 读取用户基本信息(超级名片)，该接口已停用
    public final static String userLoginInfo = "user/logininfo/get"; //读取用户基本信息
    public final static String userGetCount = "user/attention/count";// 查看统计 徒弟
    public final static String userGetPeopleResource = "user/contact/get";// 查看统计 徒弟
    public final static String statusesCollectList = "statuses/collect/show";// 动态收藏列表
    public final static String getElders = "user/info/relation";// 获取师傅 长老 酋长
    public final static String statusesNotSee = "user/conceal/create";//不让ta看我的动态
    public final static String statusesYesSee = "user/conceal/destroy";//让ta看我的动态
    public final static String getUserQrcode = "user/qrcode/get";//获取用户二维码图片
    public final static String getUserQrInfocode = "user/qrinfo/get";//获取用户二维码信息
    public final static String getUserVoice = "user/phonetic/get";//读取用户语音

    public final static String userUpdate = "user/info/update";// 更新用户资料
    public final static String userUpdateImg = "user/imgurl/update";// 更新头像
    public final static String userRegisterByPhone = "auth/register/create";// 更新手机注册
    public final static String userChangePhone = "user/security/banding_phone";// 更新手机绑定
    //public final static String userLoginPhone = "user/user_phone_login";// 用户手机登录
    public final static String authLogin = "/auth/login";// 用户手机登录
    public final static String smsLogin = "/auth/smslogin/login";// 动态短信登录
    public final static String wxBindOrUnBind = "user/security/banding_openid"; //微信绑定/微信解绑

    public final static String refreshToken = "/auth/refresh";
    public final static String userGetLevel = "user/level/get";// 用户获取等级
    public final static String userGetAttendList = "user/attention/byme/get";// 我的关注
    public final static String userGetTudis = "user/tudi/show";// 我的徒弟
    public final static String userGetFans = "user/attention/me/get";// 我的fans
    public final static String userGetGiftList = "user/gift/show";// 获取礼物列表
    public final static String userReward = "user/gift";// 打赏
    public final static String userGetMineGift = "user/gift/log";// 获取礼盒
    public final static String userBecomeSeller = "seller/add";// 成为卖家
    public final static String userGetTribeInfo = "user/tribe/get";// 获取部落信息
    public static final String userTinyTribeInfo = "user/tribe/tiny/get";//获取过渡页信息
    // 登录、注册
    public final static String userRegisterByPhoneNew = "v1/auth/user/register"; // 手机注册新
    public final static String phoneVerify = "/auth/phonecode/vertify"; // 验证手机
    public final static String wxLogin = "/auth/otherauth/login"; // 微信登录
    public final static String getMasterList = "/recommend/master/query"; // 大师列表
    public final static String changePwd = "/user/security/find_pwd"; // 找回密码/修改密码
    public final static String modifyPwd = "user/security/modify_pwd"; //修改密码
    public final static String baishi = "/im/relation/baishi"; // 拜师
    public final static String getSmsCode = "/auth/phonecode/regisget"; //获取短信。仅注册使用
    public final static String getImgVerify = "/auth/vertifycode/get"; // 获取图片验证码
    public final static String getSmsVerify = "/auth/phonecode/get"; // 获取短信验证码和图片图形
    public final static String getJustmsCode = "auth/phonecode/smsget"; //获取短信。不用图片验证码的地方使用
    public final static String checkPhoneNick = "user/check/phoneandnick"; //检查手机号码和昵称
    // 消息相关

    public final static String updateAlias = "user/remark/update";//更新昵称
    public final static String getUserSimpleInfo = "user/simpleinfo/query";//读取用户simple信息
    public final static String setTargetTop = "im/setting/update";//聊天设置置顶
    public final static String getTargetTopStatus = "im/setting/status";//获取聊天设置置顶
    public final static String imSettingQuery = "im/setting/query";//获取推送置顶列表


    public final static String uploadMsgError = "common/appmsg/add";// 上传消息发送错误信息
    public final static String getBothGroups = "im/home/groups/show";// 查看师傅和我的帮会
    public final static String getFriendTypeSimple = "im/contacts/catepage/show";// 查看师傅user和徒弟好友数量


    public final static String getPhoneNumbers = "im/appcontacts/show";// 查看手机通讯录好友
    public final static String uploadPhoneNumbers = "im/appcontacts/sync";// 上传手机通讯录
    public final static String getAppContacts = "im/contacts/show";// 查看app通讯录好友
    public final static String createDiscussionTeam = "im/discussion/create";// 创建讨论组
    public final static String imGetReplayCount = "im/relation/apply/count";// 获取被申请的数量
    public final static String imSearchUser = "im/user/search";// 搜素用户
    public final static String imSearchUserNoToken = "im/user/search_unlogin";// 无token搜素用户

    public final static String imAddRelation = "im/relation/apply/join";// 建立联系加好友或者收徒
    public final static String imAddFriendDirect = "im/relation/friend/create";//直接添加好友
    public final static String imDeleteRelation = "im/relation/destroy";// 删除联系
    public final static String applyList = "im/relation/apply/show";// 获取收到的申请列表
    public final static String applyListV1 = "v1/im/relation/apply/show";// 获取收到的申请列表(分页版)

    public final static String imDeleteapply = "im/relation/apply/destroy";// 删除申请

    public final static String handleApply = "im/relation/apply/handle";// 处理申请
    public final static String showGroupInfo = "im/group/show";// 查看群信息
    public final static String imEditGroup = "im/group/update";// 编辑群信息
    public final static String imShowMembers = "im/group/member/show";// 查看群成员
    public final static String imShowMembersTiny = "im/group/member/tinyinfo";// 查看群成员简单信息

    public final static String imUploadGroupIcon = "im/group/upload_icon";// 上传群图标
    public final static String imGetSimpleUserInfo = "user/data/query";// 获取用户图像和昵称     user/tiny/get
    public final static String imShutup = "im/group/member/shutup";// 禁言
    public final static String imReportUser = "im/relation/complaint/create";// 举报用户
    public final static String imInvitePhoneContact = "im/appcontacts/invite";// 邀请手机通讯录好友加入
    public final static String imSendGift = "im/gift/send";// 送礼物
    public final static String imIsFriend = "im/relation/isfriend";//是否是好友

    public final static String imCreateRedPacket = "hongbao/create";// 创建红包
    public final static String imSendMessageMass = "im/message/mass";// 群发消息
    public final static String imPickRedPacket = "hongbao/pick";// 抢红包
    public final static String imOpenRedPacket = "hongbao/open";// 开红包
    public final static String imGetRedPacket = "hongbao/pickrecord/get";// 获取红包信息


    public final static String imShowDiscussionInvite = "im/discussion/invite/show";//查看讨论组邀请
    public final static String imDealDiscussionInvite = "im/discussion/invite/handle";//处理讨论组邀请
    public final static String imShowDiscussion = "im/discussion/show";//查看讨论组
    public final static String imShowBaseDiscussion = "im/discussion/base/show";//查看讨论组.即使被删除也有数据返回
    public final static String imShowDiscussionMembers = "im/discussion/member/show";//查看讨论组成员
    public final static String imDeleteDiscussions = "im/discussion/destroy";//查看讨论组成员
    public final static String imRemoveDiscussionMembers = "im/discussion/member/destroy";//移除讨论组成员
    public final static String imInviteMembers = "im/discussion/invite/member";//邀请成员到讨论组
    public final static String imUpdateInviteInfo = "im/discussion/update";//编辑讨论组信息
    public final static String imDiscussionQuit = "/im/discussion/member/quit";//退出讨论组
    public final static String imDiscussionCollectionAdd = "im/discussion/collection/add";//添加到通讯录
    public final static String imDiscussionCollectionRemove = "im/discussion/collection/remove";//从通讯录移除
    public final static String imDiscussionCollectionList = "im/discussion/collection/show";//获取收藏的讨论组列表

    // 用户 相册
    public final static String userGetAlbums = "user/albums/get";// 获取相册
    public final static String userAddAlbums = "user/album/create";// 添加相册
    public final static String userUploadAlbumPhoto = "photo/upload";// 添加相片
    public final static String userGetAlbumPhotos = "user/photos/get";// 获取相片
    public final static String userDeleteAlbumPhotos = "user/photos/destroy";// 删除相片
    // 用户钱包
    public final static String userCurrencyBillList = "user/currency/show";// 查看收支明细接口
    public final static String userCurrencyCharge = "user/excharge";// 现金充值
    public final static String userGetWealth = "user/wealth/get";// 获取钱包余额信息
    public final static String userPasswordCheck = "user/wallet/pwd/check";// 用户是否有支付密码
    public final static String userPayPassword = "/user/wallet/paypassword/update";// 用户修改支付密码
    public final static String userGetPeaList = "user/pea/show";// 获取金豆和绿豆收支
    public final static String userTransferPea = "user/wallet/bean/transfer";// 转让师徒豆
    public final static String buyGoldPea = "user/wallet/gold/buy";// 购买金豆
    public final static String buyGreenPea = "user/wallet/green/buy";// 购买绿豆
    public final static String userRecharge = "/user/wallet/recharge";// 余额充值

    // 签到
    public final static String tribeSignList = "user/sign/tribe/show";// 部落签到列表签到
    public final static String userSignin = "user/sign";// 本人签到
    public final static String userSigninProxy = "user/sign/proxy";// 代理签到
    public final static String userSignList = "user/sign/show";// 获取签到列表
    public final static String getSignDataV1 = "v1/user/sign/show";//新的 获取签到数据
    public final static String userSignInV1 = "v1/user/sign";//新的 签到


    public final static String userSignOpenBox = "user/sign/chest";// 开启宝箱
    public final static String userSignUserList = "user/sign/proxy/show";// 获取签到列表
    public final static String userSignRule = "web/wx/sign.html"; //签到规则(h5)

    // 标签
    public final static String getAllTags = "user/tags/show";// 获取所有标签
    public final static String getTagProffessional = "user/tags/professions/show";// 获取所有标签
    public final static String getMyTags = "user/tags/get";// 获取我的标签
    public final static String tagUpdate = "user/tags/update";// 修改标签

    // other
    public final static String uploadSuperCardVoice = "/phonetic/upload";// 上传超级名片的语音
    public final static String getProvinceCityData = "common/citytree/show";// 获取省份和城市
    public final static String getAreaData = "common/district/show";// 获取区
    public final static String searchCardTag = "user/tags/query";// 查询名片列表

    // 物流信息
    public final static String expressInfo = "/buyer/order/express/info";
    public final static String getExpressCompanys = "common/express/query";
    // 获取短信验证码
    public final static String getPhoneVerify = "/auth/register/code_get";

    // 商品－卖家
    public final static String sellerWithdraLog = "/seller/withdraw/log/query"; //提现记录
    public final static String sellerWalletUnsettledLog = "/seller/wallet/unsettledlog/query";//未结算记录
    public final static String sellerBindBankCard = "/seller/card/binding"; //绑定/解绑银行卡
    public final static String sellerWalletInfo = "/seller/wallet/get"; // 钱包信息
    public final static String sellerAccountInfo = "/seller/wallet/log_show"; // 收入流水
    public final static String sellerBankList = "/seller/card/query"; // 可选银行卡列表
    public final static String sellerBankCard = "/seller/card/get"; //获取绑定的银行卡信息
    public final static String sellerAddCard = "/seller/card/add"; // 添加银行卡
    public final static String sellerUnbindCard = "/seller/card/del"; // 解除银行卡绑定
    public final static String sellerWithdrawl = "/seller/withdraw/apply"; // 提现申请
    public final static String uploadGoodsPhoto = "/goods/upload"; // 添加商品-上传图片
    public final static String uploadGoodsDescribe = "/goods/add"; // 添加商品-上传文本属性
    public final static String uploadGoodsEdit = "/goods/update"; // 编辑商品-上传文本属性
    public final static String sellerGoodsInfo = "/goods/get"; // 单个商品信息
    public final static String sellerGoodsUp = "/goods/up"; // 上架
    public final static String sellerGoodsDown = "/goods/down"; // 下架
    public final static String sellerGoodsDelete = "/goods/del"; // 删除
    public final static String sellerGoods = "/seller/goods/query"; // 卖家商品分页列表
    public final static String sellerOrderList = "/seller/order/show"; // 订单列表
    public final static String sellerOrderSend = "/seller/order/deliver"; // 发货
    public final static String goodsClass = "/buyer/home/class"; // 商品类别
    public final static String orderInfo = "/seller/order/info"; // 订单详细
    public final static String orderAgreeReturn = "/seller/order/refund/agree"; // 订单同意退货
    public final static String orderDisagreeReturn = "/seller/order/refund/unagree"; // 订单不同意退货
    public final static String orderAgreeReturnAmount = "/seller/order/refund/amount_agree"; // 订单同意退款
    public final static String orderDisagreeReturnAmount = "/seller/order/refund/amount_unagree"; // 订单不同意退款
    public final static String sellerGotReturnGoods = "/seller/order/refund/confirm"; // 卖家确认收到退货
    public final static String sellerOrderStatistics = "/seller/order/count"; // 卖家订单统计数

    // 商品 - 买家
    public final static String getPointsmallOrderSnapShow = "pointsmall/order/snap/show"; // 获取积分商城商品详情
    public final static String getPointsmallProductGet = "pointsmall/product/get"; // 获取积分商城商品详情
    public final static String getPointsmallProductQuery = "pointsmall/product/query"; // 获取积分商城主页商品列表
    public final static String getPointsmallHomeShow = "pointsmall/home/show"; // 获取积分商城主页全部汇总信息
    public final static String getMallAll = "mall/home/show"; // 获取商城主页全部汇总信息
    public final static String getMallBanners = "buyer/home/banner"; //
    public final static String mallHots = "buyer/home/hot"; // 热门商品
    public final static String getTypeGoods = "buyer/goods/show"; // 获取分类下的商品
    public final static String getMallInfo = "buyer/info/show"; // 获取数量
    public final static String mallMayLike = "buyer/home/like"; // 猜你喜欢
    public final static String goodsDetail = "goods/get"; // 商品详细
    public final static String addShoppingCart = "buyer/cart/create"; // 添加进购物车
    public final static String cartDelete = "buyer/cart/destroy"; // 从购物车删除
    public final static String buyerCartUpdate = "buyer/cart/update"; //更改购物车商品数量
    public final static String goodsQueryRemark = "/goods/comment/query"; // 查询物品评论
    public final static String goodsCollectOrCancel = "goods/like/add_cancel"; // 收藏商品与取消收藏
    public final static String getAddressList = "buyer/address/show"; // 获取地址列表
    public final static String getDefaultAddress = "buyer/address/default"; // 获取地址列表
    public final static String addressSetDefault = "buyer/address/set_default"; // 设置成默认地址
    public final static String addressDelete = "buyer/address/destroy"; // 删除地址
    public final static String addressAddOrEdit = "buyer/address/add_edit";// 添加／编辑收货地址
    public final static String searchGoods = "buyer/goods/search";//搜索商品
    public final static String cartShow = "buyer/cart/show"; // 购物车物品列表
    public final static String getDiscountList = "buyer/ticket/show"; // 获取已经领取的优惠券列表
    public final static String getEnableCouponList = "buyer/order/ticket/getused"; // 获取可用的优惠券列表
    public final static String GoodsClass = "/buyer/home/class"; // 商品类别  暂未用到的接口
    public final static String orderPrePay = "buyer/order/pay/pre"; // 预支付
    public final static String payByBalanceOrDou = "buyer/order/pay/wallet"; // 用余额或者金豆支付
    public final static String goodsPayVerify = "buyer/order/pay/verify"; // 支付验证
    public final static String rechargePayVerify = "user/wallet/rechargeverify"; // 充值余额支付验证
    public final static String getCollectedGoods = "goods/like/show"; // 获取收藏的物品
    public final static String getRecommendGoods = "goods/recommend/query"; //获取推荐商品
    public final static String buyBeanPayVerify = "user/wallet/bean/payverify"; // 购买金豆绿豆支付验证

    //306
    public static final String shoppingCircleUpload = "adsys/square/upload";
    public static final String getFanliDetail = "user/withdraw/walletlogs/query";
    public static final String getWithdrawDetail = "user/withdraw/orderlogs/query";
    public static final String getWithdrawAccountList = "user/withdraw/account/query";
    public static final String bindWithdrawAccount = "user/withdraw/account/bind";
    public static final String unbindWithdrawAccount = "user/withdraw/account/unbind";
    public static final String applyWithdraw = "user/withdraw/order/create";
    // 314

    // 买家 订单
    public final static String orderPointsmallOrderShow = "pointsmall/order/show"; // 积分兑换查询订单列表
    public final static String orderPointsmallOrderInfo = "pointsmall/order/info"; // 积分兑换查询订单明细
    public final static String orderPointsmallOrderCreate = "pointsmall/order/create"; // 积分兑换下单
    public final static String orderHelperCancel = "buyer/order/helper/cancel"; // 取消投诉
    public final static String orderCommit = "buyer/order/create"; // 提交订单
    public final static String showOrderList = "buyer/order/show"; // 查看订单
    public final static String showOrderCount = "buyer/order/count"; // 查看订单总数
    public final static String orderCancel = "buyer/order/cancel"; // 取消订单
    public final static String orderDelete = "buyer/order/delete"; // 删除订单
    public final static String mallOrderDetail = "/buyer/order/info"; // 订单详情
    public final static String orderConfirmGet = "buyer/order/confirm"; // 确认收货
    public final static String orderRemindSend = "buyer/order/remind"; // 提醒发货
    public final static String orderApplyReturn = "buyer/order/refund/create"; // 申请退货
    public final static String orderApplyReturnUploadImg = "mall/refund/img/upload"; // 申请退货的上传图片
    public final static String replyReturnGiveup = "buyer/order/refund/delete"; // 放弃申请退货

    public final static String getGoodsCommentList = "/goods/comment/query"; // 商品评论列表
    public final static String addGoodsComment = "/buyer/order/evaluate"; // 添加商品评论
    public final static String replyRefund = "buyer/order/refund/amount_create";// 申请退款
    public final static String replyRefundGiveup = "buyer/order/refund/amount_delete";// 放弃申请退款
    public final static String replyRefundDeliver = "buyer/order/refund/deliver"; //买家回邮
    public final static String buyerOrderHelper = "buyer/order/helper"; //投诉
    // 帮一帮
    public final static String bangyibang_show = "bang/show"; // 搜索列表
    public final static String bangyibang_create_xuqiu = "bang/create"; // 发布需求
    public final static String bangyibang_close_xuqiu = "bang/close"; // 关闭需求
    public final static String bangyibang_delete_xuqiu = "bang/destroy"; // 删除需求
    public final static String bangyibang_recommend_show = "bang/recommend/show"; // 收到的推荐
    public final static String bangyibang_recommend_create = "bang/recommend/create"; // 推荐好友给需要帮助的人
    public final static String bangyibang_recommend_select = "bang/recommend/select"; // 采纳推荐
    public final static String bangyibang_recommend_hasundo = "bang/hasundo"; // 是否有未处理推荐
    public final static String bangyibang_invited_show = "bang/request/show"; // 收到的邀请列表
    public final static String getIndustrys = "user/tags/trades/show"; // 获取行业列表//新加接口
    public final static String getAllIndustrys = "common/professions/query"; // 获取所有的行业职业


    // public final static String bangyibang_invited_contactto_fabuzhe =
    // "bang/s2p"; // 被推荐人 联系 发布人
    // public final static String bangyibang_fabuzhe_contactto_invited =
    // "bang/p2r"; // 发布人 联系 被推荐人
    public final static String bangyibang_contact = "bang/contact"; // 发布人 联系
    // 被推荐人

    // 我的
    public final static String mine_foot1 = "user/trace/show";    // 我的足迹
    public final static String mine_foot2 = "user/visit/show";    // 我的访客
    public final static String mine_links_show = "user/links/show"; //获取精彩链接
    public final static String mine_links_del = "user/links/destroy";//删除链接
    public final static String mine_link_create = "user/links/create"; //添加链接
    public final static String mine_link_img_upload = "user/links/upload";//上传链接图片
    public final static String mine_get_bind_info = "user/oauth/info"; //获取第三方授权账号信息

    // 通用
    public final static String common_share_url = "common/share/create";
    public final static String report = "user/tipoff/add";// 举报
    public final static String checkUpdateInfo = "common/version"; //检查更新
    public final static String common_dic = "common/dic/query"; //获取公用字典表
    public final static String getOfficeAccount = "common/stblaccount/query"; //获取官方信息账号 小秘书.
    public final static String commonBannerQuery = "common/banner/query"; //获取banner
    public static final String missionCallback = "mission/callback";//完成任务通知服务器
    //直播
    public final static String imLiveRoomCreate = "im/liveroom/create";//直播房间创建
    public final static String imLiveroomIn = "im/liveroom/in";//进入房间
    public final static String imLiveRoomGet = "im/liveroom/get";//获取房间数据
    public final static String liveRoomGetMember = "im/liveroom/member/show"; //  获取直播间成员列表
    public final static String imLiveroomOut = "im/liveroom/out";//退出房间
    public final static String liveRoomInviteFriend = "im/liveroom/guest/invite"; //直播间邀请朋友
    public final static String liveRoomPopmessageSend = "im/liveroom/popmessage/send"; //发送弹幕
    public final static String imLiveroomPlacePick = "im/liveroom/place/pick";//抢麦
    public final static String imLiveroomPlaceDown = "im/liveroom/place/down";//下麦
    public final static String imLiveRoomMemberClickLike = "im/liveroom/member/clicklike"; //点赞
    public final static String imLiveBulletScreenModify = "im/liveroom/master/popmsgstatus/modify"; //房主开启/禁止广播
    public final static String imLiveOwnerKick = "im/liveroom/master/member/kickout"; //把某个用户踢出房间（房主功能）
    public final static String imLiveRoomMasterMicstatusModify = "im/liveroom/master/micstatus/modify";//禁位
    public final static String imLiveRoomPlaceKickOut = "im/liveroom/master/place/kickout"; //把某个在麦位的用户踢下麦（房主功能）
    public final static String imLiveRoomPlaceChange = "im/liveroom/place/change";//切换麦位

    //推送
    public final static String jpushCommentDeviceId = "jpush/user/sync";

    //web h5
    public final static String wealthLevelIntro = "web/wx/financial.html"; //财富等级说明
    public final static String peopleLevelIntro = "web/wx/contacts.html"; //人脉等级说明
    public final static String FAQ = "web/wx/common%20problem.html";
    public final static String FUNCTION_INTRO = "web/wx/function-introduction.html";

    //返利
    public final static String userGetFanli = "user/withdraw/wallet/get";// 获取返利信息
    public final static String userGetBrandAdList = "adsys/brandplus/query";// 获取品牌+广告列表
    public static final String userGetHomeAdList = "adsys/home/get"; //获取首页广告列表
    public static final String userGetTribeAdList = "adsys/tribe/get";//获取部落广告列表
    public static final String userGetAdInfo = "adsys/user/query";
    public static final String reportAd = "adsys/report/commit";//举报广告
    public static final String userGetHomeAdNew = "guest/adsys/home/get"; //获取首页广告列表(新接口)
    //广告
    /**
     * 获取签名
     */
    public final static String redpacketAuthSignGet = "redpacket/auth/sign/get";
    /**
     * 是否广告主
     */
    public final static String adsysIsAderGet = "adsys/isader/get";
    /**
     * 广告投放状态
     */
    public final static String adsysApplyStatusGet = "adsys/apply/status/get";
    /**
     * 创建广告订单
     */
//    public final static String adsysOrderCreate = "adsys/order/create";
    public final static String adsysV1OrderCreate = "adsys/v1/order/create";
    /**
     * 广告订单支付完跟服务端确认接口
     */
    public final static String adsysOrderPayVerify = "adsys/order/pay/verify";
    /**
     * 查询当前用户订单
     */
    public final static String adsysOrderUnpayGet = "adsys/order/get";
    /**
     * 广告订单预支付
     */
    public final static String adsysOrderPayPre = "adsys/order/pay/pre";
    /**
     * 取消广告订单
     */
    public final static String adsysOrderCancel = "adsys/order/cancel";
    /**
     * 代付信息同步到后端
     */
    public final static String adsysOrderOtherpayRecord = "adsys/order/otherpay/record";
    /**
     * 获取订单详细信息
     */
    public final static String adsysOrderDetailGet = "adsys/order/detail/get";
    /**
     * 补开发票
     */
    public final static String adsysOrderInvoiceCreate = "adsys/order/invoice/create";
    /*** 获取商品信息*/
    public final static String adsysGoodsGet = "adsys/goods/get";
    /**
     * 商务合作申请列表
     */
    public final static String adsysBusinessQuery = "adsys/business/query";

    /**
     * 获取广告商务合作分类
     */
    public final static String adGetBusinessTypes = "adsys/apply/businessclass/query";
    /**
     * 获取广告分类
     */
    public final static String adGetTypes = "adsys/apply/tradeclass/query";

    public final static String getAdDetail = "adsys/ad/query";
    //云账户接口
    public final static String getTokenByYunZhangHu = "token/sign";
    public final static String getHongbaoDetailByYunZhangHu = "api/hongbao/detail";
    public static final String getHongBaoWallet = "/api/hongbao/wallet";
}
