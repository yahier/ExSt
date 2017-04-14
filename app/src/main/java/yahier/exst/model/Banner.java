package yahier.exst.model;

import java.io.Serializable;

public class Banner implements Serializable{

	String imgurl;
	String title;
	int type;
	long createtime;
	String parame;
	String extparame;

//	public final static int typeNoAction = 0;
//	public final static int typeGoods = 1;
//	public final static int typeShop = 2;
//	public final static int typeModule = 3;
//	public final static int typeTopic = 4;
//	public final static int typeWeb = 9;
	
	public final static int typeNoAction = 0;//APP首页
	public final static int typeGoods = 1;//商品明细
	public final static int typeOrder = 2;//订单明细
	public final static int typeStatuses = 3;//动态明细
	public final static int typeTopic = 4;// 话题明细
	public final static int typeCard = 5;//部落
	public final static int typeBangyibang = 6;// 帮一帮明细
	public final static int typeLiveShow = 8;//直播间
	public final static int typeExchangeMall = 9;//兑换商城明细
	public final static int typePurchaseAd = 11;//广告购买页面
	public final static int typePulishDynamic = 301;//跳转至短动态发布页面
	public final static int typePulishLongDynamic = 302;// 长动态发布
	public final static int typeWonderfulVideo = 311;// 精彩视频
	public final static int typeExchangeDetail = 900;// 兑换商品列表
	public final static int typeMine = 1200;// 我的模块
	public final static int typeWallet = 1201;// 我的钱包
	public final static int typeMyContacts = 1202;// 我的人脉
	public final static int typeShouTu = 1203;//我要收徒
	public final static int typeAccountUpgrade = 1204;//账号升级
	public final static int typeRichScan = 1205;//扫一扫
	public final static int typeUserInfoSetting = 1211;//跳转至资料设置页面
	public final static int typeShoppingCircle = 1300;//商圈列表
	public final static int typeShoppingCirclePulish = 1301;//广告购买页面(同11)
	public final static int typePulishRedPacket = 1302;// 商圈发布
	public final static int getTypeShoppingCircleDetail = 1303;// 商圈详情
	public final static int typeAppSetting = 1400;// App设置
	public final static int typeModel = 98;// 模块跳转
	public final static int typeWeb = 99;



	// 模块类型,typeModel = 98 的时候的子模块类型
	public final static int typeModelHome = 1;
	public final static int typeModelStatuses = 2;
	public final static int typeModelMessage = 3;
	public final static int typeModelCommunity = 4;
	public final static int typeModelMall = 5;
	public final static int typeModelMaotai = 6;
	

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getParame() {
		return parame;
	}

	public void setParame(String parame) {
		this.parame = parame;
	}

	public String getExtparame() {
		return extparame;
	}

	public void setExtparame(String extparame) {
		this.extparame = extparame;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

}
