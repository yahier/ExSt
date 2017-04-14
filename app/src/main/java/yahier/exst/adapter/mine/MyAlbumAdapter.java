package yahier.exst.adapter.mine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.Photo;
import com.stbl.stbl.util.ImageUtils;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/4/8.
 */
public class MyAlbumAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Photo> mList;
    private ArrayList<Photo> mSelectedList;

    private int mItemHeight;
    private AbsListView.LayoutParams mImageViewLayoutParams;

    private boolean mIsEditMode;

    public MyAlbumAdapter(ArrayList<Photo> list) {
        mList = list;
        mSelectedList = new ArrayList<>();
        mInflater = LayoutInflater.from(MyApplication.getContext());
        mImageViewLayoutParams = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
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
            convertView = mInflater.inflate(R.layout.item_my_album,
                    parent, false);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (convertView.getLayoutParams().height != mItemHeight) {
            convertView.setLayoutParams(mImageViewLayoutParams);
        }
        Photo item = mList.get(position);
        holder.mSelectIv.setVisibility(mIsEditMode ? View.VISIBLE : View.GONE);
        holder.mSelectIv.setSelected(isSelected(item));
        holder.mCoverView.setVisibility(isSelected(item) ? View.VISIBLE : View.GONE);
        ImageUtils.loadSquareImage(item.getMiddleurl(), holder.mPhotoIv, mItemHeight);
        return convertView;
    }

    private static class ViewHolder {
        ImageView mSelectIv;
        ImageView mPhotoIv;
        View mCoverView;

        public ViewHolder(View v) {
            mSelectIv = (ImageView) v.findViewById(R.id.iv_select);
            mPhotoIv = (ImageView) v.findViewById(R.id.iv_photo);
            mCoverView = v.findViewById(R.id.v_cover);
        }
    }

    private boolean isSelected(Photo photo) {
        return mSelectedList.contains(photo);
    }

    public void toggleChecked(Photo photo) {
        if (isSelected(photo)) {
            mSelectedList.remove(photo);
        } else {
            mSelectedList.add(photo);
        }
        notifyDataSetChanged();
    }

    public ArrayList<Photo> getSelectedList() {
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
                ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
        notifyDataSetChanged();
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
