package yahier.exst.adapter.mine;

import android.content.Context;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.item.Gift;
import com.stbl.stbl.item.SignListResult;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.PicassoUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 打赏的礼物列表
 *
 * @author lenovo
 */
public class SignWeekAdapter extends CommonAdapter {
    Context mContext;
    List<Integer> list;
    int offSize = 0;//偏移量
    int dateOfToday;//今天的日期数

    public SignWeekAdapter(Context mContext) {
        this.mContext = mContext;
        list = new ArrayList<>();
    }

    public void setData(List<Integer> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    public void setDateData(int offSize, int dateOfToday) {
        this.offSize = offSize;
        this.dateOfToday = dateOfToday;
        LogUtil.logE("dateValue:" + offSize + ":" + dateOfToday);
    }

    @Override
    public int getCount() {
        return list.size() + offSize;
    }

    @Override
    public Integer getItem(int arg0) {
        return list.get(arg0);
    }

    class Holder {
        ImageView img;
        View imgGou;
        TextView name, value;
        View linName;
    }

    @Override
    public View getView(final int i, View con, ViewGroup parent) {
        Holder ho = null;
        if (con == null) {
            ho = new Holder();
            con = LayoutInflater.from(mContext).inflate(R.layout.sign_gird_item, null);
            ho.img = (ImageView) con.findViewById(R.id.img);
            ho.name = (TextView) con.findViewById(R.id.name);
            ho.imgGou = con.findViewById(R.id.imgGou);
            ho.linName = con.findViewById(R.id.linName);
            con.setTag(ho);
        } else
            ho = (Holder) con.getTag();

        //当前 offSize = 2;
        if (i < offSize) {
            con.setVisibility(View.GONE);
        } else {
            con.setVisibility(View.VISIBLE);
            int dataIndex = i - offSize;
            ho.name.setText("" + (dataIndex + 1));


            int signState = list.get(dataIndex);//越界 30 30
            switch(signState) {
                case SignListResult.stateToSign:
                    ho.imgGou.setVisibility(View.GONE);
                    ho.img.setVisibility(View.GONE);
                    ho.linName.setVisibility(View.VISIBLE);
                    ho.name.setBackgroundResource(R.color.transparent);
                    //ho.name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    //ho.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    ho.name.setTextColor(mContext.getResources().getColor(R.color.f_black));
                    break;
                case SignListResult.stateSigned:
                    ho.imgGou.setVisibility(View.VISIBLE);
                    //ho.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);//R.drawable.icon_qiandao_gou
                    ho.name.setTextColor(mContext.getResources().getColor(R.color.f_black));
                    ho.name.setBackgroundResource(R.color.transparent);
                    ho.linName.setVisibility(View.VISIBLE);
                    //ho.name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    ho.img.setVisibility(View.GONE);
                    break;
                case SignListResult.stateExpire:
                    ho.imgGou.setVisibility(View.GONE);
                    //ho.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    ho.name.setTextColor(mContext.getResources().getColor(R.color.gray_ccc));
                    //ho.name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    ho.linName.setVisibility(View.VISIBLE);
                    ho.name.setBackgroundResource(R.color.transparent);
                    ho.img.setVisibility(View.GONE);
                    break;
                case SignListResult.stateBoxToOpen:
                    ho.imgGou.setVisibility(View.GONE);
                    ho.linName.setVisibility(View.GONE);
                    //ho.name.setTextColor(mContext.getColor(R.color.f_black));
                    ho.img.setImageResource(R.drawable.icon_qiandao_baoxiang_now);
                    ho.img.setVisibility(View.VISIBLE);
                    break;
                case SignListResult.stateBoxOpened:
                    ho.imgGou.setVisibility(View.GONE);
                    ho.linName.setVisibility(View.GONE);
                    //ho.name.setTextColor(mContext.getColor(R.color.f_black));
                    ho.img.setImageResource(R.drawable.icon_qiandao_baoxiang_open);
                    ho.img.setVisibility(View.VISIBLE);
                    break;
                case SignListResult.stateBoxClose:
                    ho.imgGou.setVisibility(View.GONE);
                    ho.img.setVisibility(View.VISIBLE);
                    ho.linName.setVisibility(View.GONE);
                    //ho.name.setTextColor(mContext.getColor(R.color.f_black));
                    ho.img.setImageResource(R.drawable.icon_qiandao_baoxiang_lock);
                    break;
            }


            //当天没有签到时的样式不同
            if (dataIndex == dateOfToday - 1) {
                if (signState == SignListResult.stateToSign) {
                    //ho.name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    ho.name.setTextColor(mContext.getResources().getColor(R.color.theme_red));
                    ho.name.setBackgroundResource(R.drawable.shape_qiandao_today_bg);
                }
            }
        }


        return con;
    }

}
