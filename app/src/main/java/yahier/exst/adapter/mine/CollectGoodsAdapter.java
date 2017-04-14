package yahier.exst.adapter.mine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.stbl.stbl.R;
import com.stbl.stbl.act.mine.MyCollectionActivity;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.util.ImageUtils;

import java.util.ArrayList;

/**
 * Created by vienan on 2015/9/17.
 */
public class CollectGoodsAdapter extends BaseSwipeAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Goods> mList;

    private ICollectGoodsAdapter mInterface;

    private int mMode;

    public CollectGoodsAdapter(ArrayList<Goods> list) {
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
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_layout;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View convertView = mInflater.inflate(R.layout.item_collect_goods, null);
        ViewHolder holder = new ViewHolder(convertView);
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public void fillValues(int position, View convertView) {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.swipeLayout.close();
        final Goods item = mList.get(position);
        ImageUtils.loadIcon(item.getImgurl(), holder.mIconIv);
        holder.mTitleTv.setText(item.getGoodsname());
        if (item.getMinprice() == item.getMaxprice()) {
            holder.mPriceTv.setText("¥" + item.getMinprice());
        } else {
            holder.mPriceTv.setText("¥" + item.getMinprice() + "~" + item.getMaxprice());
        }
        holder.mPayNumberTv.setText(item.getSalecount() + "人付款");


        switch(mMode){
            case MyCollectionActivity.mode_look:
                holder.mAddTv.setVisibility(View.GONE);
                break;
            case MyCollectionActivity.mode_im_choose:
                holder.mAddTv.setVisibility(View.VISIBLE);
                break;
            case MyCollectionActivity.mode_statuses_choose:
                holder.mAddTv.setVisibility(View.VISIBLE);
                break;
        }

        holder.mDeleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInterface != null) {
                    mInterface.onDelete(item);
                }
            }
        });

        holder.mItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterface != null) {
                    mInterface.onItemClick(item);
                }
            }
        });

        holder.mAddTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterface != null) {
                    mInterface.onAdd(item);
                }
            }
        });
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
        }
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    public void setInterface(ICollectGoodsAdapter i) {
        mInterface = i;
    }

    public interface ICollectGoodsAdapter {
        void onItemClick(Goods item);

        void onDelete(Goods item);

        void onAdd(Goods item);
    }

}
