package yahier.exst.adapter;

import java.util.ArrayList;
import java.util.List;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.PicassoUtil;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PraiseGridAdapter extends CommonAdapter {
	Context mContext;
	List<UserItem> listUser;
	Resources resources;
	final int praiseSize = 6; 

	public PraiseGridAdapter(Context mContext) {
		this.mContext = mContext;
		listUser = new ArrayList<UserItem>();
		resources = mContext.getResources();
	}

	public PraiseGridAdapter(Context mContext, List<UserItem> listUser) {
		this.mContext = mContext;
		this.listUser = listUser;
		resources = mContext.getResources();
	}

	@Override
	public int getCount() {
		return listUser.size() > praiseSize ? praiseSize : listUser.size();
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
		final UserItem user = listUser.get(i);
		PicassoUtil.load(mContext, user.getImgurl(), ho.img);
		//PicassoUtil.load(mContext, user.getImgurl(), ho.img,resources.getDimensionPixelSize(R.dimen.list_praise_head_img_width_height), resources.getDimensionPixelSize(R.dimen.list_praise_head_img_width_height));
		ho.img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent2 = new Intent(mContext, TribeMainAct.class);
				intent2.putExtra("userId", user.getUserid());
				mContext.startActivity(intent2);

			}
		});
		return con;
	}

}
