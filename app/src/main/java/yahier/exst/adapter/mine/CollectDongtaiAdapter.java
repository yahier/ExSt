package yahier.exst.adapter.mine;

import android.text.Html;
import android.text.TextUtils;
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
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.StatusesCollect;
import com.stbl.stbl.util.ImageUtils;

import java.util.ArrayList;

/**
 * Created by vienan on 2015/9/17.
 */
public class CollectDongtaiAdapter extends BaseSwipeAdapter {

    private LayoutInflater mInflater;
    private ArrayList<StatusesCollect> mList;

    private ICollectDongtaiAdapter mInterface;

    private int mMode;

    public CollectDongtaiAdapter(ArrayList<StatusesCollect> list) {
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
        View convertView = mInflater.inflate(R.layout.item_collect_dongtai, null);
        ViewHolder holder = new ViewHolder(convertView);
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public void fillValues(int position, View convertView) {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.swipeLayout.close();
        final StatusesCollect item = mList.get(position);
        Statuses statuses = item.getStatuses();
        if (TextUtils.isEmpty(statuses.getStatusespic().getDefaultpic())) {
            ImageUtils.loadIcon(statuses.getUser().getImgurl(), holder.mIconIv);
        } else {
            String url = statuses.getStatusespic().getMiddlepic() + statuses.getStatusespic().getDefaultpic();
            ImageUtils.loadIcon(url, holder.mIconIv);
        }
        if (TextUtils.isEmpty(item.getStatuses().getTitle())) {
            holder.mTitleTv.setText(item.getStatuses().getContent());
        } else {
            holder.mTitleTv.setText(item.getStatuses().getTitle());
        }
        String name = TextUtils.isEmpty(item.getStatuses().getUser().getAlias()) ? item.getStatuses().getUser().getNickname() : item.getStatuses().getUser().getAlias();
        String publisher = "<font color='#F4B10B'>" + name + "</font>"+convertView.getResources().getString(R.string.statuses_by_someone);
//        String publisher = "<font color='#F4B10B'>" + item.getStatuses().getUser().getNickname() + "</font>"+convertView.getResources().getString(R.string.statuses_by_someone);
        holder.mPublisherTv.setText(Html.fromHtml(publisher));

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
        TextView mPublisherTv;
        TextView mDeleteTv;
        TextView mAddTv;

        public ViewHolder(View v) {
            mItemLayout = (RelativeLayout) v.findViewById(R.id.item_surface);
            swipeLayout = (SwipeLayout) v.findViewById(R.id.swipe_layout);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewWithTag("Bottom2"));
            mIconIv = (ImageView) v.findViewById(R.id.iv_icon);
            mTitleTv = (TextView) v.findViewById(R.id.tv_title);
            mPublisherTv = (TextView) v.findViewById(R.id.tv_publisher);
            mDeleteTv = (TextView) v.findViewById(R.id.tv_delete);
            mAddTv = (TextView) v.findViewById(R.id.tv_add);
        }
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    public void setInterface(ICollectDongtaiAdapter i) {
        mInterface = i;
    }

    public interface ICollectDongtaiAdapter {
        void onItemClick(StatusesCollect item);

        void onDelete(StatusesCollect item);

        void onAdd(StatusesCollect item);
    }

}
