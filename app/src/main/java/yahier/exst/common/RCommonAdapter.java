package yahier.exst.common;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class RCommonAdapter<T> extends BaseAdapter {
	protected LayoutInflater mInflater;
	protected Activity mActivity;
	protected List<T> mDatas;
	protected final int mItemLayoutId;

	public RCommonAdapter(Activity act, List<T> mDatas, int itemLayoutId) {
		this.mActivity = act;
		this.mInflater = LayoutInflater.from(mActivity);
		this.mDatas = mDatas;
		this.mItemLayoutId = itemLayoutId;
	}

	@Override
	public int getCount() {
		return mDatas == null ? 0 : mDatas.size();
	}

	public void addData(List<T> mDatas) {
		this.mDatas.addAll(mDatas);
		notifyDataSetChanged();
	}

	public void setData(List<T> mDatas) {
		this.mDatas = mDatas;
		notifyDataSetChanged();
	}

	public void deleteItem(int index) {
		this.mDatas.remove(index);
		notifyDataSetChanged();
	}

	@Override
	public T getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final RViewHolder viewHolder = getViewHolder(position, convertView, parent);
		convert(viewHolder, getItem(position));
		convert(viewHolder, getItem(position), position);
		return viewHolder.getConvertView();

	}

	public abstract void convert(RViewHolder helper, T item);

	public void convert(RViewHolder helper, T item, int position) {
	};

	private RViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
		return RViewHolder.get(mActivity, convertView, parent, mItemLayoutId, position);
	}

}
