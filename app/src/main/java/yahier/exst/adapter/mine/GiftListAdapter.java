package yahier.exst.adapter.mine;

import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.Gift;
import com.stbl.stbl.item.MineGiftItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.widget.RoundImageView;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/4/12.
 */
public class GiftListAdapter extends BaseAdapter implements View.OnClickListener {

    private LayoutInflater mInflater;
    private ArrayList<MineGiftItem> mList;

    private IGiftListAdapter mInterface;

    public GiftListAdapter(ArrayList<MineGiftItem> list) {
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
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_gift, parent, false);
            holder.mHeadIv = (RoundImageView) convertView.findViewById(R.id.iv_head);
            holder.mGiftIv = (ImageView) convertView.findViewById(R.id.iv_gift);
            holder.mNickTv = (TextView) convertView.findViewById(R.id.tv_nick);
            holder.mTimeTv = (TextView) convertView.findViewById(R.id.tv_time);
            holder.mAmountTv = (TextView) convertView.findViewById(R.id.tv_amount);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MineGiftItem item = mList.get(position);
        UserItem user = item.getObjuser();

        Gift gift = item.getGiftinfo();
        String typeValue = "";
        switch (gift.getCurrencytype()) {
            case Gift.type_renminbi:
                typeValue = "人民币";
                break;
            case Gift.type_jindou:
                typeValue = "金豆";
                break;
            case Gift.type_lvdou:
                typeValue = "绿豆";
                break;
        }
        String newMessageInfo = "<font color='#F4B10B'><b>" + gift.getValue() + "</b></font>" + typeValue;
        holder.mTimeTv.setText(DateUtil.getTimeOff(item.getCreatetime()));
        holder.mAmountTv.setText(Html.fromHtml(newMessageInfo));
        //PicassoUtil.load(gift.getGiftimg(), holder.mGiftIv);
        ImageUtils.loadSquareImage(gift.getGiftimg(), holder.mGiftIv);
        //PicassoUtil.load(user.getImgurl(), holder.mHeadIv);
        ImageUtils.loadCircleHead(user.getImgurl(), holder.mHeadIv);
        holder.mNickTv.setText(TextUtils.isEmpty(user.getAlias()) ? user.getNickname() : user.getAlias());
//        holder.mNickTv.setText(user.getNickname());

        holder.mHeadIv.setTag(position);
        holder.mHeadIv.setOnClickListener(this);

        return convertView;
    }

    private static class ViewHolder {
        RoundImageView mHeadIv;
        TextView mNickTv;
        ImageView mGiftIv;
        TextView mTimeTv;
        TextView mAmountTv;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_head:
                int position = (int) v.getTag();
                if (mInterface != null) {
                    mInterface.onClickHead(position);
                }
                break;
        }
    }

    public void setInterface(IGiftListAdapter i) {
        mInterface = i;
    }

    public interface IGiftListAdapter {
        void onClickHead(int position);
    }
}
