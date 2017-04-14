package yahier.exst.adapter.home;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.OneTypeAdapter;
import com.stbl.stbl.model.Headmen;
import com.stbl.stbl.model.UserSimple;
import com.stbl.stbl.util.DimenUtils;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.SharedPrefUtils;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/7/24.
 */
public class BigChiefGridAdapter extends OneTypeAdapter<Headmen> {

    private int mItemHeight;
    private AbsListView.LayoutParams mItemLayoutParams;
    private String mDefaultHeadUrl;

    public BigChiefGridAdapter(ArrayList<Headmen> list) {
        super(list);
        mItemLayoutParams = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mDefaultHeadUrl = (String) SharedPrefUtils.getFromPublicFile(KEY.defaultimgUser, "");
        mItemHeight = DimenUtils.dp2px(74);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_big_chief_grid;
    }

    @Override
    protected OneTypeAdapter.ViewHolder getViewHolder() {
        return new OneTypeAdapter.ViewHolder() {

            ImageView mHeadIv;
            ImageView mHeadForegroud;
            TextView mNickTv;

            @Override
            public void init(View v) {
                mHeadIv = (ImageView) v.findViewById(R.id.iv_head);
                mNickTv = (TextView) v.findViewById(R.id.tv_nick);
                mHeadForegroud = (ImageView) v.findViewById(R.id.iv_head_foregroud);
            }

            @Override
            public void bind(View v, int position) {
                if (v.getLayoutParams().height != mItemHeight) {
                    v.setLayoutParams(mItemLayoutParams);
                }
                Headmen men = getList().get(position);
                UserSimple user = men.getUserview();
                if (user != null) {
                    if (SharedPrefUtils.isNewYear()){
                        mHeadForegroud.setVisibility(View.VISIBLE);
                    }
                    String headUrl = user.getImgurl();
                    if (headUrl.equals(mDefaultHeadUrl) || headUrl.contains("default/user")) {
                        headUrl = "";
                    }
                    ImageUtils.loadRoundHead(headUrl, mHeadIv, mItemHeight,2);
                    String alias = user.getAlias();
                    String name = TextUtils.isEmpty(alias) ? user.getNickname() : user.getAlias();
                    mNickTv.setText(name);
                }
            }
        };
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
}
