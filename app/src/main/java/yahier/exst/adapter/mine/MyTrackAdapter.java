package yahier.exst.adapter.mine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.model.Footprint;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.widget.RoundImageView;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/4/6.
 */
public class MyTrackAdapter extends BaseAdapter {

//    private static final int ITEM_TYPE_TRIBE = 0;
//    private static final int ITEM_TYPE_GOODS = 1;
//    private static final int ITEM_TYPE_DONGTAI = 2;

    private LayoutInflater mInflater;
    private ArrayList<Footprint> mList;

    public MyTrackAdapter(ArrayList<Footprint> list) {
        mList = list;
        mInflater = LayoutInflater.from(MyApplication.getContext());
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

    /*
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (type == ITEM_TYPE_DONGTAI) {
            return getDongtaiView(position, convertView, parent);
        } else if (type == ITEM_TYPE_GOODS) {
            return getGoodsView(position, convertView, parent);
        } else {
            return getCommonView(position, convertView, parent);
        }
    }
    */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_my_visitor, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Footprint item = mList.get(position);
        ImageUtils.loadHead(item.getBusinessurl(), holder.mHeadIv);
        holder.mContentTv.setText(item.getRemark());
        holder.mTimeTv.setText(DateUtil.getTimeOff(item.getCreatetime()));
        holder.mHeadIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return convertView;
    }

    private View getCommonView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_my_visitor, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Footprint item = mList.get(position);
        ImageUtils.loadHead(item.getBusinessurl(), holder.mHeadIv);
        holder.mContentTv.setText(item.getRemark());
        holder.mTimeTv.setText(DateUtil.getTimeOff(item.getCreatetime()));
        return convertView;
    }

    private View getTribeView(int position, View convertView, ViewGroup parent) {
        TribeViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_my_track_tribe, parent, false);
            holder = new TribeViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (TribeViewHolder) convertView.getTag();
        }
        Footprint item = mList.get(position);

        return convertView;
    }

    private View getGoodsView(int position, View convertView, ViewGroup parent) {
        GoodsViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_my_track_goods, parent, false);
            holder = new GoodsViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (GoodsViewHolder) convertView.getTag();
        }

        return convertView;
    }

    private View getDongtaiView(int position, View convertView, ViewGroup parent) {
        DongtaiViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_my_track_dongtai, parent, false);
            holder = new DongtaiViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (DongtaiViewHolder) convertView.getTag();
        }

        return convertView;
    }

    private class ViewHolder {
        RoundImageView mHeadIv;
        TextView mContentTv;
        TextView mTimeTv;

        public ViewHolder(View v) {
            mHeadIv = (RoundImageView) v.findViewById(R.id.iv_head);
            mContentTv = (TextView) v.findViewById(R.id.tv_content);
            mTimeTv = (TextView) v.findViewById(R.id.tv_time);
        }
    }

    private class TribeViewHolder {
        RoundImageView mHeadIv;
        TextView mNickTv;
        TextView mRelationTv;
        TextView mAgeTv;
        TextView mCityTv;
        TextView mTimeTv;

        public TribeViewHolder(View v) {
            mHeadIv = (RoundImageView) v.findViewById(R.id.iv_head);
            mNickTv = (TextView) v.findViewById(R.id.tv_nick);
            mRelationTv = (TextView) v.findViewById(R.id.tv_relation);
            mAgeTv = (TextView) v.findViewById(R.id.tv_age);
            mCityTv = (TextView) v.findViewById(R.id.tv_city);
            mTimeTv = (TextView) v.findViewById(R.id.tv_time);
        }
    }

    private class GoodsViewHolder {
        ImageView mIconIv;
        TextView mContentTv;
        TextView mPriceTv;
        TextView mSaleTv;
        TextView mTimeTv;

        public GoodsViewHolder(View v) {
            mIconIv = (ImageView) v.findViewById(R.id.iv_icon);
            mContentTv = (TextView) v.findViewById(R.id.tv_content);
            mPriceTv = (TextView) v.findViewById(R.id.tv_price);
            mSaleTv = (TextView) v.findViewById(R.id.tv_sale);
            mTimeTv = (TextView) v.findViewById(R.id.tv_time);
        }
    }

    private class DongtaiViewHolder {
        ImageView mIconIv;
        TextView mContentTv;
        TextView mNickTv;
        TextView mTimeTv;

        public DongtaiViewHolder(View v) {
            mIconIv = (ImageView) v.findViewById(R.id.iv_icon);
            mContentTv = (TextView) v.findViewById(R.id.tv_content);
            mNickTv = (TextView) v.findViewById(R.id.tv_nick);
            mTimeTv = (TextView) v.findViewById(R.id.tv_time);
        }
    }

    /*
    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        int type = ITEM_TYPE_TRIBE;
        Footprint footprint = mList.get(position);
        int moduletype = footprint.getModuletype();
        if (moduletype == 2) {
            type = ITEM_TYPE_DONGTAI;
        } else if (moduletype == 5) {
            type = ITEM_TYPE_GOODS;
        }
        return type;
    }
    */
}
