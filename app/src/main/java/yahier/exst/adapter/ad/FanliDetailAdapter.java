package yahier.exst.adapter.ad;

import android.view.View;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.OneTypeAdapter;
import com.stbl.stbl.model.MoneyFlowItem;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.Res;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/3/16.
 */
public class FanliDetailAdapter extends OneTypeAdapter<MoneyFlowItem> {

    private boolean mIsShowNoMore = false;

    public FanliDetailAdapter(ArrayList<MoneyFlowItem> list) {
        super(list);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_fanli_detail;
    }

    @Override
    protected ViewHolder getViewHolder() {
        return new ViewHolder() {

            TextView mTitleTv;
            TextView mTimeTv;
            TextView mAmountTv;
            TextView mNoMoreTv;

            @Override
            public void init(View v) {
                mTitleTv = (TextView) v.findViewById(R.id.tv_title);
                mTimeTv = (TextView) v.findViewById(R.id.tv_time);
                mAmountTv = (TextView) v.findViewById(R.id.tv_amount);
                mNoMoreTv = (TextView) v.findViewById(R.id.tv_no_more);
            }

            @Override
            public void bind(View v, int position) {
                MoneyFlowItem item = mList.get(position);
                mTitleTv.setText(item.remark);
                mTimeTv.setText(DateUtil.getTimeDifference(item.createtime * 1000));
                if (item.optype == 0) {
                    mAmountTv.setText("+" + item.amount);
                    mAmountTv.setTextColor(Res.getColor(R.color.f_red));
                } else {
                    mAmountTv.setText("-" + item.amount);
                    mAmountTv.setTextColor(Res.getColor(R.color.f_black));
                }
                if (position == mList.size() - 1 && mIsShowNoMore) {
                    mNoMoreTv.setVisibility(View.VISIBLE);
                } else {
                    mNoMoreTv.setVisibility(View.GONE);
                }
            }
        };
    }

    public void showNoMore(boolean isShow) {
        mIsShowNoMore = isShow;
        notifyDataSetChanged();
    }
}
