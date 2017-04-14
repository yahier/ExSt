package yahier.exst.item.im;

import com.stbl.stbl.item.UserItem;

import java.io.Serializable;

/**
 * 收到的申请item
 * 
 * @author lenovo
 * 
 */
public class ApplyGotItem implements Serializable {
	/**申请id*/
	long applyid;
	/**申请类型 1-收徒，2-好友*/
	int applytype;
	/**申请说明*/
	String applymsg;
	/**申请状态 0-申请中，1-接受，2-拒绝*/
	int applystate;
	/**申请时间*/
	long createtime;
	FromUser fromuser;
	public final static int applystateDefault = 0;
	public final static int applystateReceived = 1;
	public final static int applystateRejected = 2;

	/**申请用户信息*/
	public class FromUser{
		/**用户id*/
		private long userid;
		/**用户昵称*/
		private String nickname;
		/**用户头像*/
		private String imgmiddleurl;
		/**徒弟数*/
		private int tudicount;
		/**家族数*/
		private int familycount;

		/**该用户大酋长的酋长信息*/
		private BigchiefinfoBean bigchiefinfo;

		/**该用户酋长信息（如果用户是大酋长的话）*/
		private BigchiefinfoBean cbigchiefinfo;

		public long getUserid() {
			return userid;
		}

		public void setUserid(long userid) {
			this.userid = userid;
		}

		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}

		public String getImgmiddleurl() {
			return imgmiddleurl;
		}

		public void setImgmiddleurl(String imgmiddleurl) {
			this.imgmiddleurl = imgmiddleurl;
		}

		public int getTudicount() {
			return tudicount;
		}

		public void setTudicount(int tudicount) {
			this.tudicount = tudicount;
		}

		public int getFamilycount() {
			return familycount;
		}

		public void setFamilycount(int familycount) {
			this.familycount = familycount;
		}

		public BigchiefinfoBean getBigchiefinfo() {
			return bigchiefinfo;
		}

		public void setBigchiefinfo(BigchiefinfoBean bigchiefinfo) {
			this.bigchiefinfo = bigchiefinfo;
		}

		public BigchiefinfoBean getCbigchiefinfo() {
			return cbigchiefinfo;
		}

		public void setCbigchiefinfo(BigchiefinfoBean cbigchiefinfo) {
			this.cbigchiefinfo = cbigchiefinfo;
		}

		//大酋长信息
		public class BigchiefinfoBean {
			private long bigchiefuserid;
			/**酋长等级*/
			private int zlevel;
			/**酋长图标*/
			private String imgurl;
			/**酋长昵称*/
			private String nickname;

			public long getBigchiefuserid() {
				return bigchiefuserid;
			}

			public void setBigchiefuserid(long bigchiefuserid) {
				this.bigchiefuserid = bigchiefuserid;
			}

			public int getZlevel() {
				return zlevel;
			}

			public void setZlevel(int zlevel) {
				this.zlevel = zlevel;
			}

			public String getImgurl() {
				return imgurl;
			}

			public void setImgurl(String imgurl) {
				this.imgurl = imgurl;
			}

			public String getNickname() {
				return nickname;
			}

			public void setNickname(String nickname) {
				this.nickname = nickname;
			}
		}
	}

	public long getApplyid() {
		return applyid;
	}

	public void setApplyid(long applyid) {
		this.applyid = applyid;
	}

	public int getApplytype() {
		return applytype;
	}

	public void setApplytype(int applytype) {
		this.applytype = applytype;
	}

	public String getApplymsg() {
		return applymsg;
	}

	public void setApplymsg(String applymsg) {
		this.applymsg = applymsg;
	}

	public int getApplystate() {
		return applystate;
	}

	public void setApplystate(int applystate) {
		this.applystate = applystate;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public FromUser getFromuser() {
		return fromuser;
	}

	public void setFromuser(FromUser fromuser) {
		this.fromuser = fromuser;
	}
}
