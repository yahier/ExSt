package yahier.exst.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.ImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 小原图的adapter。现在用在了打赏栏。
 * 
 * @author lenovo
 * 
 */
public class SignGridAdapter extends CommonAdapter {
	Context mContext;
	List<UserItem> listUser;

	public SignGridAdapter(Context mContext) {
		this.mContext = mContext;
		listUser = new ArrayList<UserItem>();
	}

	public SignGridAdapter(Context mContext, List<UserItem> listUser) {
		this.mContext = mContext;
		this.listUser = listUser;
		notifyDataSetChanged();
	}

	public void setData(List<UserItem> listUser) {
		this.listUser = listUser;
		notifyDataSetChanged();
	}

	//最大显示6条
	@Override
	public int getCount() {
		return listUser.size() < 5 ? listUser.size() : 5;
	}

	class Holder {
		ImageView img;
	}

	@Override
	public View getView(final int i, View con, ViewGroup parent) {
		Holder ho = null;
		if (con == null) {
			ho = new Holder();
			con = LayoutInflater.from(mContext).inflate(R.layout.item_round_image, null);
			ho.img = (ImageView) con.findViewById(R.id.item_iv);
			con.setTag(ho);
		} else
			ho = (Holder) con.getTag();
		ImageUtils.loadCircleHead(listUser.get(i).getImgurl(), ho.img);
		return con;
	}

}
