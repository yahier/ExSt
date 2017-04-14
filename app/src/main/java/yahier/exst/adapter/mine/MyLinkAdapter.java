package yahier.exst.adapter.mine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.LinkBean;
import com.stbl.stbl.util.ImageUtils;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/4/8.
 */
public class MyLinkAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<LinkBean> mList;
    private ArrayList<LinkBean> mSelectedList;

    private int mItemWidth;
    private int mItemHeight;
    private AbsListView.LayoutParams mImageViewLayoutParams;

    private boolean mIsEditMode;

    public MyLinkAdapter(ArrayList<LinkBean> list) {
        mList = list;
        mSelectedList = new ArrayList<>();
        mInflater = LayoutInflater.from(MyApplication.getContext());
        mImageViewLayoutParams = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.MATCH_PARENT);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_my_link,
                    parent, false);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (convertView.getLayoutParams().height != mItemHeight) {
            convertView.setLayoutParams(mImageViewLayoutParams);
        }
        LinkBean item = mList.get(position);
        holder.mSelectIv.setVisibility(mIsEditMode ? View.VISIBLE : View.GONE);
        holder.mSelectIv.setSelected(isSelected(item));
        holder.mCoverView.setVisibility(isSelected(item) ? View.VISIBLE : View.GONE);
        ImageUtils.loadSquareImage(item.getPiclarurl(), holder.mPictureIv, mItemHeight);
        holder.mUrlTv.setText(item.getLinkurl());
        holder.mTitleTv.setText(item.getLinktitle());
        return convertView;
    }

    private static class ViewHolder {
        ImageView mSelectIv;
        ImageView mPictureIv;
        TextView mUrlTv;
        TextView mTitleTv;
        View mCoverView;

        public ViewHolder(View v) {
            mSelectIv = (ImageView) v.findViewById(R.id.iv_select);
            mPictureIv = (ImageView) v.findViewById(R.id.iv_picture);
            mUrlTv = (TextView) v.findViewById(R.id.tv_url);
            mTitleTv = (TextView) v.findViewById(R.id.tv_title);
            mCoverView = v.findViewById(R.id.v_cover);
        }
    }

    private boolean isSelected(LinkBean item) {
        return mSelectedList.contains(item);
    }

    public void toggleChecked(LinkBean item) {
        if (isSelected(item)) {
            mSelectedList.remove(item);
        } else {
            mSelectedList.add(item);
        }
        notifyDataSetChanged();
    }

    public ArrayList<LinkBean> getSelectedList() {
        return mSelectedList;
    }

    public int getSelectedSize() {
        return mSelectedList.size();
    }

    public void selectAll() {
        mSelectedList.clear();
        mSelectedList.addAll(mList);
        notifyDataSetChanged();
    }

    public void selectNone() {
        mSelectedList.clear();
        notifyDataSetChanged();
    }

    public void setItemHeight(int height) {
        if (height == mItemHeight) {
            return;
        }
        mItemHeight = height;
        mImageViewLayoutParams = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, mItemHeight);
        notifyDataSetChanged();
    }

    public void setItemWidth(int width) {
        if (width == mItemWidth) {
            return;
        }
        mItemWidth = width;
    }

    public void setIsEditMode(boolean isEdit) {
        mIsEditMode = isEdit;
        mSelectedList.clear();
        notifyDataSetChanged();
    }

    public void deleteSelectedItem() {
        mList.removeAll(mSelectedList);
        mSelectedList.clear();
        notifyDataSetChanged();
    }
}
