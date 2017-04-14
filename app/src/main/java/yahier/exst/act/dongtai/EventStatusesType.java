package yahier.exst.act.dongtai;

import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.StatusesCollect;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.model.Goods;

public class EventStatusesType {

	public final static int typeCard = 1;
	public final static int typeWish = 2;
	public final static int typeStatuses = 3;
	public final static int typeGoods = 4;

	Goods goods;
	UserItem user;
	StatusesCollect statuses;
	int type;

	public EventStatusesType(Goods goods) {
		this.goods = goods;
		this.type = typeGoods;
	}

	public EventStatusesType(UserItem user) {
		this.user = user;
		this.type = typeCard;
	}

	public EventStatusesType(StatusesCollect statuses) {
		this.statuses = statuses;
		this.type = typeStatuses;
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public UserItem getUser() {
		return user;
	}

	public void setUser(UserItem user) {
		this.user = user;
	}

	public StatusesCollect getStatuses() {
		return statuses;
	}

	public void setStatuses(StatusesCollect statuses) {
		this.statuses = statuses;
	}

}
