package yahier.exst.adapter.dongtai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.util.ImageItem;
import com.stbl.stbl.util.ImageUtils;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/7/24.
 */
public class PublishShortStatusImageAdapter extends BaseAdapter {

    private ArrayList<ImageItem> mList;
    private LayoutInflater mInflater;

    private int mItemHeight;
    private AbsListView.LayoutParams mItemLayoutParams;

    private IAdapter mInterface;

    public PublishShortStatusImageAdapter(ArrayList<ImageItem> list) {
        mList = list;
        mInflater = LayoutInflater.from(MyApplication.getContext());
        mItemLayoutParams = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public int getCount() {
        int size = mList.size();
        if (size > 0 && size < 9) {
            return size + 1;
        }
        return size;
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
            convertView = mInflater.inflate(R.layout.item_publish_short_status_image, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (convertView.getLayoutParams().height != mItemHeight) {
            convertView.setLayoutParams(mItemLayoutParams);
        }
        holder.setData(position);
        return convertView;
    }

    private class ViewHolder {
        ImageView mPhotoIv;
        ImageView mDeleteIv;

        public ViewHolder(View v) {
            mPhotoIv = (ImageView) v.findViewById(R.id.iv_photo);
            mDeleteIv = (ImageView) v.findViewById(R.id.iv_delete);
        }

        public void setData(final int position) {
            if (position >= mList.size()) {
                mPhotoIv.setScaleType(ImageView.ScaleType.FIT_XY);
                mPhotoIv.setImageResource(R.drawable.dongtai_pulish_img_icon);
                mPhotoIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mInterface != null) {
                            mInterface.onAdd();
                        }
                    }
                });
                mDeleteIv.setVisibility(View.INVISIBLE);
                mDeleteIv.setOnClickListener(null);
            } else {
                mPhotoIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ImageUtils.loadFile(mList.get(position).file, mPhotoIv, mItemHeight);
                mPhotoIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mInterface != null) {
                            mInterface.onPreview(position);
                        }
                    }
                });
                mDeleteIv.setVisibility(View.VISIBLE);
                mDeleteIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mInterface != null) {
                            mInterface.onDelete(position);
                        }
                    }
                });
            }
        }
    }

    public void setItemHeight(int height) {
        if (mItemHeight == height) {
            return;
        }
        mItemHeight = height;
        mItemLayoutParams = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, mItemHeight);
        notifyDataSetChanged();
    }

    public void setInterface(IAdapter i) {
        mInterface = i;
    }

    public interface IAdapter {
        void onAdd();

        void onDelete(int position);

        void onPreview(int position);
    }
}
