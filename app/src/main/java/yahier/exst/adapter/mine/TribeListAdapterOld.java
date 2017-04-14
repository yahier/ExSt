package yahier.exst.adapter.mine;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.model.Ad;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.DimenUtils;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.widget.TribeHeaderView;
import com.stbl.stbl.widget.TribeHeaderViewOld;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/27.
 */

public class TribeListAdapterOld extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE_HEADER = 0;
    private static final int ITEM_TYPE_ONE = 1;
    private static final int ITEM_TYPE_MULTI = 2;
    private static final int ITEM_TYPE_LOAD_MORE = 3;

    private LayoutInflater mInflater;
    private ArrayList<Ad> mList;

    private AdapterInterface mInterface;

    private TribeHeaderViewOld mHeaderView;

    private int mOneHeight = 0;
    private ViewGroup.LayoutParams mOneLayoutParams;

    private int mMultiHeight = 0;
    private ViewGroup.LayoutParams mMultiLayoutParams;

    private int mLoadMoreMode = MODE_HIDE;
    private static final int MODE_HIDE = 0;
    private static final int MODE_LOADING = 1;
    private static final int MODE_HAS_MORE = 2;
    private static final int MODE_LOAD_MORE = 3; //下拉
    private static final int MODE_TO_ALL = 4; //去查看全部
    private static final int MODE_FINISH = 5; //加载完了全部

    public TribeListAdapterOld(ArrayList<Ad> list, TribeHeaderViewOld headerView) {
        mList = list;
        mHeaderView = headerView;
        mInflater = LayoutInflater.from(MyApplication.getContext());
        mOneLayoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mMultiLayoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                return new HeadViewHolder(mHeaderView);
            case ITEM_TYPE_ONE:
                return new AdOneViewHolder(mInflater.inflate(R.layout.item_tribe_ad_one, parent, false));
            case ITEM_TYPE_MULTI:
                return new AdMultiViewHolder(mInflater.inflate(R.layout.item_tribe_ad_multi, parent, false));
            case ITEM_TYPE_LOAD_MORE:
                return new LoadMoreViewHolder(mInflater.inflate(R.layout.item_load_more, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeadViewHolder) {
            HeadViewHolder h = (HeadViewHolder) holder;
        } else if (holder instanceof AdOneViewHolder) {
            AdOneViewHolder h = (AdOneViewHolder) holder;
            h.bind(mList.get(position - 1));
        } else if (holder instanceof AdMultiViewHolder) {
            AdMultiViewHolder h = (AdMultiViewHolder) holder;
            h.bind(mList.get(position - 1));
        } else if (holder instanceof LoadMoreViewHolder) {
            LoadMoreViewHolder h = (LoadMoreViewHolder) holder;
            h.bind();
        }
    }

    @Override
    public int getItemCount() {
//        return 1 + mList.size() +  1;
        return 1 + mList.size() + (mLoadMoreMode == MODE_HIDE ? 0 : 1);
    }

    private class HeadViewHolder extends RecyclerView.ViewHolder {

        public HeadViewHolder(View v) {
            super(v);
        }
    }

    private class AdOneViewHolder extends RecyclerView.ViewHolder {

        ImageView mCoverIv;
        TextView mTitleTv, tvAdBusinessType;

        public void bind(final Ad ad) {
            if (itemView.getLayoutParams().height != mOneHeight) {
                itemView.setLayoutParams(mOneLayoutParams);
            }
            ImageUtils.loadImage(ad.adimgminurl, mCoverIv);
            mTitleTv.setText(ad.adtitle);

//            if(ad.businessclass==Ad.businessclassNone){
//                tvAdBusinessType.setVisibility(View.GONE);
//            }else{
//                tvAdBusinessType.setVisibility(View.VISIBLE);
//                tvAdBusinessType.setText(ad.businessclassname);
//            }
            mCoverIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mInterface != null) {
                        mInterface.onItemClick(ad);
                    }
                }
            });
            tvAdBusinessType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mInterface != null) {
                        mInterface.onAdBusinessClick(ad);
                    }
                }
            });
        }

        public AdOneViewHolder(View v) {
            super(v);
            mCoverIv = (ImageView) v.findViewById(R.id.iv_cover);
            mTitleTv = (TextView) v.findViewById(R.id.tv_title);
            tvAdBusinessType = (TextView) v.findViewById(R.id.tvAdBusinessType);
        }
    }

    private class AdMultiViewHolder extends RecyclerView.ViewHolder {

        ImageView mCoverIv;
        TextView mTitleTv, tvAdBusinessType;

        public void bind(final Ad ad) {
            if (itemView.getLayoutParams().height != mMultiHeight) {
                itemView.setLayoutParams(mMultiLayoutParams);
            }
            ImageUtils.loadImage(ad.adimgminurl, mCoverIv);
            mTitleTv.setText(ad.adtitle);
//            if(ad.businessclass==Ad.businessclassNone){
//                tvAdBusinessType.setVisibility(View.GONE);
//            }else{
//                tvAdBusinessType.setVisibility(View.VISIBLE);
//                tvAdBusinessType.setText(ad.businessclassname);
//            }
            mCoverIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mInterface != null) {
                        mInterface.onItemClick(ad);
                    }
                }
            });
            tvAdBusinessType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mInterface != null) {
                        mInterface.onAdBusinessClick(ad);
                    }
                }
            });
        }

        public AdMultiViewHolder(View v) {
            super(v);
            mCoverIv = (ImageView) v.findViewById(R.id.iv_cover);
            mTitleTv = (TextView) v.findViewById(R.id.tv_title);
            tvAdBusinessType = (TextView) v.findViewById(R.id.tvAdBusinessType);
        }
    }

    private class LoadMoreViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout mLayout;
        TextView mTv;
        ProgressBar mPb;

        public void bind() {
            mTv.setVisibility(View.VISIBLE);
            mTv.setVisibility(mLoadMoreMode != MODE_LOADING ? View.VISIBLE : View.GONE);
            mPb.setVisibility(mLoadMoreMode == MODE_LOADING ? View.VISIBLE : View.GONE);
            if (mLoadMoreMode == MODE_FINISH){
                mTv.setText("全部加载完了");
                mLayout.setEnabled(false);
            }else if(mLoadMoreMode == MODE_LOAD_MORE){
                mTv.setText("上滑查看更多");
            }else if(mLoadMoreMode == MODE_TO_ALL){
                mTv.setText("查看全部品牌");
            }
            mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mLoadMoreMode == MODE_HIDE || mLoadMoreMode == MODE_FINISH
                            || mLoadMoreMode == MODE_LOADING) return;
                    if (mLoadMoreMode == MODE_TO_ALL){
                        mInterface.toAll();
                        return;
                    }
                    if (mInterface != null) {
                        setLoadingMore();
                        mInterface.onLoadMore();
                    }
                }
            });
        }

        public LoadMoreViewHolder(View v) {
            super(v);
            mLayout = (RelativeLayout) v.findViewById(R.id.layout);
            mTv = (TextView) v.findViewById(R.id.text_view);
            mPb = (ProgressBar) v.findViewById(R.id.progress_bar);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE_HEADER;
        } else if (position == 1 && mList.size() == 1) {
            return ITEM_TYPE_ONE;
        } else if (position == mList.size() + 1) {
            return ITEM_TYPE_LOAD_MORE;
        } else {
            return ITEM_TYPE_MULTI;
        }
    }

    public void setLoadingMore() {
        mLoadMoreMode = MODE_LOADING;
        notifyItemChanged(mList.size() + 1);
    }

    public void hideLoadMore() {
        mLoadMoreMode = MODE_HIDE;
        notifyDataSetChanged();
//        notifyItemChanged(mList.size() + 1);
    }
    public void allLoadFinish() {
        mLoadMoreMode = MODE_FINISH;
        notifyItemChanged(mList.size() + 1);
    }

    public void setHasMore() {
        mLoadMoreMode = MODE_HAS_MORE;
        notifyItemChanged(mList.size() + 1);
    }
    public void setLoadMore() {
        mLoadMoreMode = MODE_LOAD_MORE;
        notifyItemChanged(mList.size() + 1);
    }
    public void setToAll() {
        mLoadMoreMode = MODE_TO_ALL;
        notifyItemChanged(mList.size() + 1);
    }

    public void setItemHeight() {
        mOneHeight = (Device.getWidth() - DimenUtils.dp2px(24)) / 2;
        mOneLayoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, mOneHeight);
        mMultiHeight = (Device.getWidth() - DimenUtils.dp2px(34)) / 4;
        mMultiLayoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, mMultiHeight);
        notifyDataSetChanged();
    }

    public void setInterface(AdapterInterface listener) {
        mInterface = listener;
    }

    public interface AdapterInterface {
        void onItemClick(Ad ad);

        void onLoadMore();

        void onAdBusinessClick(Ad ad);

        void toAll();
    }

}
