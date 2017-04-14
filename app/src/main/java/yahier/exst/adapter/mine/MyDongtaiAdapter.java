package yahier.exst.adapter.mine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.model.Goods;

import java.util.ArrayList;

/**
 * Created by vienan on 2015/9/17.
 */
public class MyDongtaiAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Statuses> mList;
    private SwipeLayout mCurrentSwipeLayout;

    private IMyDongtaiAdapter mInterface;

    private int mMode;

    public MyDongtaiAdapter(ArrayList<Statuses> list) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_collect_goods, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        final Statuses item = mList.get(position);
//        Picasso.with(MyApplication.getContext()).load(item.getImgurl()).fit().placeholder(R.drawable.dongtai_default).error(R.drawable.dongtai_default).into(holder.mIconIv);
//        holder.mTitleTv.setText(item.getGoodsname());
//        if (item.getMinprice() == item.getMaxprice()) {
//            holder.mPriceTv.setText("¥" + item.getMinprice());
//        } else {
//            holder.mPriceTv.setText("¥" + item.getMinprice() + "~" + item.getMaxprice());
//        }
//        holder.mPayNumberTv.setText(item.getSalecount() + "人付款");
//
//        if (mMode == MyCollectionActivity.mode_choose) {
//            holder.mAddTv.setVisibility(View.VISIBLE);
//        } else {
//            holder.mAddTv.setVisibility(View.GONE);
//        }

//        holder.mDeleteTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mInterface != null) {
//                    mInterface.onDelete(item);
//                }
//            }
//        });
//
//        holder.mItemLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mInterface != null) {
//                    mInterface.onItemClick(item);
//                }
//            }
//        });
//
//        holder.mAddTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mInterface != null) {
//                    mInterface.onAdd(item);
//                }
//            }
//        });
        return convertView;
    }

    private class ViewHolder {
        RelativeLayout mItemLayout;
        SwipeLayout swipeLayout;
        ImageView mIconIv;
        TextView mTitleTv;
        TextView mPriceTv;
        TextView mPayNumberTv;
        TextView mDeleteTv;
        TextView mAddTv;

        public ViewHolder(View v) {
            mItemLayout = (RelativeLayout) v.findViewById(R.id.item_surface);
            swipeLayout = (SwipeLayout) v.findViewById(R.id.swipe_layout);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewWithTag("Bottom2"));
            mIconIv = (ImageView) v.findViewById(R.id.iv_icon);
            mTitleTv = (TextView) v.findViewById(R.id.tv_title);
            mPriceTv = (TextView) v.findViewById(R.id.tv_price);
            mPayNumberTv = (TextView) v.findViewById(R.id.tv_pay_number);
            mDeleteTv = (TextView) v.findViewById(R.id.tv_delete);
            mAddTv = (TextView) v.findViewById(R.id.tv_add);

            swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onStartOpen(SwipeLayout layout) {
                    if (mCurrentSwipeLayout != null && mCurrentSwipeLayout != layout)
                        mCurrentSwipeLayout.close(true);
                }

                @Override
                public void onOpen(SwipeLayout layout) {
                    mCurrentSwipeLayout = layout;
                }

                @Override
                public void onStartClose(SwipeLayout layout) {

                }

                @Override
                public void onClose(SwipeLayout layout) {
                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

                }
            });
        }
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    public void setInterface(IMyDongtaiAdapter i) {
        mInterface = i;
    }

    public interface IMyDongtaiAdapter {
        void onItemClick(Goods item);

        void onDelete(Goods item);

        void onAdd(Goods item);
    }

}
