package yahier.exst.act.dongtai.tribe;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;

import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.Gift;
import com.stbl.stbl.item.LinkBean;
import com.stbl.stbl.item.MineGiftItem;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.XListView;
import com.stbl.stbl.widget.XListView.OnXListViewListener;

public class TribeLinkAct extends ThemeActivity implements FinalHttpCallback, OnXListViewListener {
	XListView listView;
	UserItem userItem;
	Adapter adapter;
	int pageIndex =1;
	int loadType = 0;// 加载模式
	final int loadTypeBottom = 0;// 底部加载
	final int loadTypeTop = 1;// 顶部加载。清除以前数据
	final int requestCount = 15;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tribe_link_act);
		userItem = (UserItem) getIntent().getSerializableExtra("userItem");
		if (userItem == null){
			finish();
			return;
		}
		setLabel(userItem.getAlias() == null || userItem.getAlias().equals("") ? userItem.getNickname() : userItem.getAlias()+"的精彩链接");
//		setLabel(userItem.getNickname()+"的精彩链接");
		listView = (XListView) findViewById(R.id.list);
		listView.setOnXListViewListener(this);
		adapter = new Adapter(this);
		listView.setAdapter(adapter);
		getData();
	}

	// 获取已上传链接
	private void getData() {
		Params params = new Params();
		params.put("objuserid", userItem.getUserid());
		params.put("page", pageIndex);
		params.put("count", requestCount);
		new HttpEntity(this).commonPostData(Method.mine_links_show, params, this);
	}

	public class Adapter extends CommonAdapter {
		Context mContext;
		List<LinkBean> list;
		int itemWidthHeight;

		public Adapter(Context mContext) {
			this.mContext = mContext;
			list = new ArrayList<LinkBean>();
			itemWidthHeight = (int) mContext.getResources().getDimension(R.dimen.list_head_img_width_height);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		public void setData(List<LinkBean> list) {
			this.list = list;
			notifyDataSetChanged();
		}

		public void addData(List<LinkBean> list) {
			this.list.addAll(list);
			notifyDataSetChanged();
		}

		class Holder {
			ImageView img;
			TextView tvTitle;
		}

		@Override
		public View getView(final int i, View con, ViewGroup parent) {
			Holder ho = null;
			if (con == null) {
				ho = new Holder();
				con = LayoutInflater.from(mContext).inflate(R.layout.tribe_link_item, null);
				ho.img = (ImageView) con.findViewById(R.id.img);
				ho.tvTitle = (TextView) con.findViewById(R.id.tvTitle);
				con.setTag(ho);
			} else
				ho = (Holder) con.getTag();
			final LinkBean item = list.get(i);

			ho.tvTitle.setText(item.getLinktitle());
			PicassoUtil.loadCropCenter(mContext, item.getPicminurl(), itemWidthHeight, itemWidthHeight, ho.img);
			con.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(mContext,CommonWeb.class);
					intent.putExtra("url", item.getLinkurl());
					startActivity(intent);
					
				}
			});
			return con;
		}

	}

	@Override
	public void parse(String methodName, String result) {
		BaseItem item = JSONHelper.getObject(result, BaseItem.class);
		if (item.getIssuccess() != BaseItem.successTag) {
			ToastUtil.showToast(this, item.getErr().getMsg());
			return;
		}
		switch (methodName) {
		case Method.mine_links_show:
			String con = JSONHelper.getStringFromObject(item.getResult());
			List<LinkBean> list = JSONHelper.getList(con, LinkBean.class);
			listView.onRefreshComplete();
			listView.onLoadMoreComplete();
			if (list != null && list.size() > 0) {
				if (loadType == loadTypeBottom) {
					adapter.addData(list);
					if (list.size() < Statuses.requestCount) {
						listView.EndLoad();
					}
				} else {
					adapter.setData(list);
				}
			} else {
				listView.EndLoad();
			}
			break;
		}
	}

	@Override
	public void onRefresh(XListView v) {
		listView.setPullLoadEnable(true);
		pageIndex = 1;
		loadType = loadTypeTop;
		getData();
	}

	@Override
	public void onLoadMore(XListView v) {
		pageIndex++;
		loadType = loadTypeBottom;
		getData();
	}
}
