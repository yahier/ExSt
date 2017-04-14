package yahier.exst.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.PicassoUtil;

import java.util.ArrayList;
import java.util.List;

public class DongtaiAddBusinessCardAdapter extends CommonAdapter {
    Context mContext;
    List<UserItem> list;

    public DongtaiAddBusinessCardAdapter(Context mContext) {
        this.mContext = mContext;
        list = new ArrayList<UserItem>();
    }

    public void setData(List<UserItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void addData(List<UserItem> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    class Holder {
        ImageView img;
        TextView name, signature;
        View btnAdd;

        public TextView mAgeTv;
        public TextView mLocationTv;
        public ImageView ivGender;//性别

    }

    @Override
    public View getView(final int i, View con, ViewGroup parent) {
        Holder ho = null;
        if (con == null) {
            ho = new Holder();
            con = LayoutInflater.from(mContext).inflate(R.layout.dongtai_add_business_card_item, null);
            ho.img = (ImageView) con.findViewById(R.id.imgUser);
            ho.name = (TextView) con.findViewById(R.id.name);
           // ho.userGenderAge = (TextView) con.findViewById(R.id.user_gender_age);
           // ho.city = (TextView) con.findViewById(R.id.user_city);
            ho.signature = (TextView) con.findViewById(R.id.user_signature);
            ho.btnAdd = (View) con.findViewById(R.id.btnAdd);

            ho.mAgeTv = (TextView) con.findViewById(R.id.tv_age);
            ho.mLocationTv = (TextView) con
                    .findViewById(R.id.tv_location);
            ho.ivGender = (ImageView) con.findViewById(R.id.iv_gender);
            con.setTag(ho);
        } else
            ho = (Holder) con.getTag();

        final UserItem user = list.get(i);
        //PicassoUtil.load(mContext, user.getImgmiddleurl(), ho.img);
        ImageUtils.loadHead(user.getImgmiddleurl(), ho.img);
//        ho.name.setText(user.getNickname());
        ho.name.setText(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() : user.getAlias());

        if (user.getSignature() == null || user.getSignature().equals("")) {
            ho.signature.setVisibility(View.INVISIBLE);
        } else {
            ho.signature.setVisibility(View.VISIBLE);
            ho.signature.setText(mContext.getString(R.string.signature) + ":" + user.getSignature());
        }

        if (user.getAge() == 0) {
            ho.mAgeTv.setVisibility(View.GONE);
        } else {
            ho.mAgeTv.setVisibility(View.VISIBLE);
            ho.mAgeTv.setText(String.format(MyApplication.getContext().getString(R.string.im_age), user.getAge()));
        }
        ho.ivGender.setVisibility(View.VISIBLE);
        if (user.getGender() == UserItem.gender_boy) {
            ho.ivGender.setImageResource(R.drawable.icon_male);
        } else if (user.getGender() == UserItem.gender_girl) {
            ho.ivGender.setImageResource(R.drawable.icon_female);
        } else {
            ho.ivGender.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(user.getCityname())) {
            ho.mLocationTv.setVisibility(View.GONE);
        } else {
            ho.mLocationTv.setVisibility(View.VISIBLE);
            ho.mLocationTv.setText(user.getCityname());
        }

        ho.btnAdd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                adapterListener.onChoosedFinish(user);

            }
        });

        return con;
    }

    OnAdapterListener adapterListener;

    public void setOnAdapterListener(OnAdapterListener adapterListener) {
        this.adapterListener = adapterListener;
    }

    public interface OnAdapterListener {
        void onChoosedFinish(UserItem user);
    }
}
