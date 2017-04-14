package yahier.exst.adapter.mine;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.OneTypeAdapter;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.util.ImageUtils;

import java.util.ArrayList;

/**
 * Created by vienan on 2015/9/17.
 */
public class AddGoodsAdapter extends OneTypeAdapter<Goods> {

    private ICollectGoodsAdapter mInterface;

    public AddGoodsAdapter(ArrayList<Goods> list) {
        super(list);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_add_goods;
    }

    @Override
    protected OneTypeAdapter.ViewHolder getViewHolder() {
        return new OneTypeAdapter.ViewHolder() {

            ImageView mIconIv;
            TextView mTitleTv;
            TextView mPriceTv;
            TextView mAddTv;

            @Override
            public void init(View v) {
                mIconIv = (ImageView) v.findViewById(R.id.iv_icon);
                mTitleTv = (TextView) v.findViewById(R.id.tv_title);
                mPriceTv = (TextView) v.findViewById(R.id.tv_price);
                mAddTv = (TextView) v.findViewById(R.id.tv_add);
            }

            @Override
            public void bind(View v, int position) {
                final Goods item = mList.get(position);
                ImageUtils.loadIcon(item.getImgurl(), mIconIv);
                mTitleTv.setText(item.getGoodsname());
                if (item.getMinprice() == item.getMaxprice()) {
                    mPriceTv.setText("¥" + item.getMinprice());
                } else {
                    mPriceTv.setText("¥" + item.getMinprice() + "~" + item.getMaxprice());
                }

                mAddTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mInterface != null) {
                            mInterface.onAdd(item);
                        }
                    }
                });
            }
        };
    }

    public void setInterface(ICollectGoodsAdapter i) {
        mInterface = i;
    }

    public interface ICollectGoodsAdapter {

        void onAdd(Goods item);
    }

}
