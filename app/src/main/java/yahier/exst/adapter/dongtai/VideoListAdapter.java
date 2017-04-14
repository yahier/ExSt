package yahier.exst.adapter.dongtai;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.adapter.DongtaiMainAdapter;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.dialog.InputDialog;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class VideoListAdapter extends CommonAdapter implements OnClickListener,FinalHttpCallback {
    Context mContext;
    List<Statuses> listData;
    Resources resources;
    List<Boolean> isPraised;
    final int praiseSize = 6;

    public VideoListAdapter(Context mContext) {
        this.mContext = mContext;
        listData = new ArrayList<Statuses>();
        isPraised = new ArrayList<Boolean>();
        resources = mContext.getResources();
    }


    public void setData(List<Statuses> list) {
        this.listData = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return 6;//listUser.size();
    }
    Statuses cStatuses;
    @Override
    public void onClick(final View view) {
        view.setEnabled(false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, Config.interClickTime);
        cStatuses = (Statuses) view.getTag();
        int cPosition;
        Intent intent;
        switch(view.getId()) {
            case R.id.linItem1:
                cPosition = (int) view.getTag(R.id.tag_dongtai_item);
                onStatesesListener.doForward(cStatuses, cPosition);
                //pauseInVisibleVideo();
                break;
            case R.id.linItem2:
                cPosition = (int) view.getTag(R.id.tag_dongtai_item);
                showInputDialog();
                break;
            case R.id.linItem3:
                cPosition = (int) view.getTag(R.id.tag_dongtai_item);
                onStatesesListener.doPraise(cStatuses, cPosition);
                break;
            case R.id.linItem4:
                cPosition = (int) view.getTag(R.id.tag_dongtai_item);
                //LogUtil.logE("cPosition",cPosition);
                onStatesesListener.doCollect(cStatuses, cPosition, cStatuses.getIsfavorited());
                break;
            case R.id.item_iv:
                intent = new Intent(mContext, TribeMainAct.class);
                intent.putExtra("userId", cStatuses.getUser().getUserid());
                //startAct(intent);
                break;
        }
    }


    InputDialog mInputDialog;

    void showInputDialog() {
        ToastUtil.showLong("showInputDialog");
        mInputDialog = new InputDialog(mContext);
        mInputDialog.show();
        mInputDialog.setOnSendListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mInputDialog.getContent();
                if (content.equals("")) {
                    ToastUtil.showToast(R.string.please_input_first);
                    return;
                }

                if (content.length() > Config.remarkContentLength) {
                    String toastTip = String.format(mContext.getString(R.string.statuses_content_exceed_length), String.valueOf(Config.remarkContentLength));
                    ToastUtil.showToast(toastTip);
                    return;
                }
                mInputDialog.setSendButtonEnable(false);
                Params params = new Params();
                params.put("statusesid", cStatuses.getStatusesid());
                params.put("content", content);
                new HttpEntity(mContext).commonPostData(Method.weiboRemark, params, VideoListAdapter.this);
            }
        });
    }
    void setTag(Object tag, View... views) {
        for (View view : views) {
            view.setTag(tag);
        }
    }

    void setPositionTag(Object position, View... views) {
        for (View view : views) {
            view.setTag(R.id.tag_dongtai_item, position);
        }
    }


    DongtaiMainAdapter.OnStatesesListener onStatesesListener;

    public void setOnStatesesListener(DongtaiMainAdapter.OnStatesesListener onStatesesListener) {
        this.onStatesesListener = onStatesesListener;
    }

    @Override
    public void parse(String methodName, String result) {

    }

    public interface OnStatesesListener {
        void doForward(Statuses statuses, int position);// 转发

        void doRemark(int position, Statuses statuses, String content);// 评论

        void doPraise(Statuses statuses, int position);

        void doCollect(Statuses statuses, int position, int isCollected);// 收藏

        void enterDongtaiDetail(int position, Statuses statuses, boolean scrollToPosition);

        void doFullClick(int position);
    }
    class Holder {
        ImageView imgUser, imgAuthorized, imgRelation, imgVideoStart, imgVideoCover;
        TextView tvName, tvTime, tvShortTitle, linVideoTitle;
        ViewGroup layout_video;

        TextView tvitem1, tvitem2, tvitem3, tvitem4;
        ImageView imgItem1, imgItem2, imgItem3, imgItem4;
        View linItem1, linItem2, linItem3, linItem4;
    }

    @Override
    public View getView(final int i, View con, ViewGroup parent) {
        Holder ho = null;
        if (con == null) {
            ho = new Holder();
            con = LayoutInflater.from(mContext).inflate(R.layout.dongtai_video_item, null);
            ho.layout_video = (ViewGroup) con.findViewById(R.id.layout_video);

            ho.imgUser = (ImageView) con.findViewById(R.id.imgUser);
            ho.imgAuthorized = (ImageView) con.findViewById(R.id.imgAuthorized);
            ho.imgRelation = (ImageView) con.findViewById(R.id.imgRelation);
            ho.imgVideoCover = (ImageView) con.findViewById(R.id.imgVideoCover);
            ho.imgVideoStart = (ImageView) con.findViewById(R.id.linVideoStart);

            ho.tvName = (TextView) con.findViewById(R.id.tvName);
            ho.tvTime = (TextView) con.findViewById(R.id.tvTime);
            ho.tvShortTitle = (TextView) con.findViewById(R.id.tvShortTitle);
            ho.linVideoTitle = (TextView) con.findViewById(R.id.linVideoTitle);

            //
            ho.tvitem1 = (TextView) con.findViewById(R.id.item_text1);
            ho.tvitem2 = (TextView) con.findViewById(R.id.item_text2);
            ho.tvitem3 = (TextView) con.findViewById(R.id.item_text3);
            ho.tvitem4 = (TextView) con.findViewById(R.id.item_text4);

            ho.linItem1 = con.findViewById(R.id.linItem1);
            ho.linItem2 = con.findViewById(R.id.linItem2);
            ho.linItem3 = con.findViewById(R.id.linItem3);
            ho.linItem4 = con.findViewById(R.id.linItem4);

            ho.imgItem1 = (ImageView) con.findViewById(R.id.imgItem1);
            ho.imgItem2 = (ImageView) con.findViewById(R.id.imgItem2);
            ho.imgItem3 = (ImageView) con.findViewById(R.id.imgItem3);
            ho.imgItem4 = (ImageView) con.findViewById(R.id.imgItem4);

            con.setTag(ho);
        } else
            ho = (Holder) con.getTag();


        if(listData.size()==0) return con;
        Statuses statuses = listData.get(i);
        ho.tvitem1.setText(statuses.getForwardcount() + "");
        ho.tvitem2.setText(statuses.getCommentcount() + "");
        ho.tvitem3.setText(statuses.getPraisecount() + "");
        ho.tvitem4.setText(statuses.getFavorcount() + "");
        if (statuses.getIspraised() == Statuses.ispraisedYes) {
            isPraised.set(i, true);
            ho.imgItem3.setImageResource(R.drawable.dongtai_praise_presed);
            ho.tvitem3.setTextColor(mContext.getResources().getColor(R.color.theme_brown));
        } else {
            ho.imgItem3.setImageResource(R.drawable.dongtai_item3);
            ho.tvitem3.setTextColor(mContext.getResources().getColor(R.color.black7));
        }

        if (statuses.getIsfavorited() == Statuses.isfavoritedYes) {
            ho.imgItem4.setImageResource(R.drawable.dongtai_collect_pressed);
            ho.tvitem4.setTextColor(mContext.getResources().getColor(R.color.theme_brown));
        } else {
            ho.imgItem4.setImageResource(R.drawable.dongtai_item4);
            ho.tvitem4.setTextColor(mContext.getResources().getColor(R.color.black7));
        }
        ho.linItem1.setOnClickListener(this);
        ho.linItem2.setOnClickListener(this);
        ho.linItem3.setOnClickListener(this);
        ho.linItem4.setOnClickListener(this);

        setTag(statuses, ho.tvitem1, ho.tvitem2, ho.linItem1, ho.linItem2, ho.linItem3, ho.linItem4);
        setPositionTag(i, ho.tvitem1, ho.tvitem2, ho.linItem1, ho.linItem2, ho.linItem3, ho.linItem4);

        return con;
    }

}
