package yahier.exst.act.home.mall;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.stbl.stbl.R;
import com.stbl.stbl.common.RCommonAdapter;
import com.stbl.stbl.common.RViewHolder;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventType;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.model.OrderProduct;
import com.stbl.stbl.ui.BaseClass.STBLBaseActivity;
import com.stbl.stbl.ui.ItemAdapter.base.STBLBaseGroupAdapter;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.HttpUtil;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LoadingView;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OnFinalHttpCallback;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.ToastUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import io.rong.eventbus.EventBus;

/**
 * 添加评论商品
 *
 * @author ruilin
 */
public class GoodsRemarkAddAct extends STBLBaseActivity implements OnClickListener {
    public final static String KEY_ORDER_ID = "KEY_ORDER_ID";
    public final static String KEY_GOODS_LIST = "KEY_GOODS_LIST";
    public final static String KEY_SHOP_NAME = "SHOP_NAME";

    long orderId;
    ArrayList<OrderProduct> mData;
    ArrayList<GoodsRemarkInfo> mRemarkData;
    RemarkAdapter mAdapter;
    RatingBar ratingBar;
    private LinearLayout llPage;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mall_add_comment);
        navigationView.setTitleBar(getString(R.string.mall_remark_goods));
        navigationView.setClickLeftListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent it = getIntent();
        orderId = it.getLongExtra(KEY_ORDER_ID, 0);
        if (0 == orderId) {
            LogUtil.logE("订单id为空");
            finish();
            return;
        }
        mData = (ArrayList<OrderProduct>) it.getSerializableExtra(KEY_GOODS_LIST);
        if (null == mData) {
            LogUtil.logE("商品列表为空");
            finish();
            return;
        }
        mRemarkData = new ArrayList<>(mData.size());
        for (int i = 0; i < mData.size(); i++) {
            GoodsRemarkInfo item = new GoodsRemarkInfo();
            OrderProduct goods = mData.get(i);//转换出错  SellerSimpleGoods
            item.orderdetailid = goods.getOrderdetailid();
            item.goodsid = String.valueOf(goods.getGoodsid());
            item.goodsname = goods.getGoodsname();
            item.skuname = goods.getSkuname();
            item.skuid = goods.getSkuid();
            item.star = 5;
            mRemarkData.add(item);
        }

        String shopName = it.getStringExtra(KEY_SHOP_NAME);
        mAdapter = new RemarkAdapter(this, mData);
        llPage = (LinearLayout) findViewById(R.id.ll_page);
        mAdapter.setDividerHeight(0.5f);
        mAdapter.setDividerColor(R.color.gray_line);
        mAdapter.setAdapter(llPage);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar1);
        TextView tv_perfect = (TextView) findViewById(R.id.textView3);
        ratingBar.setTag(tv_perfect);
        ratingBar.setOnRatingBarChangeListener(ratingBarListener);
        ratingBar.setRating(0);

        findViewById(R.id.btn_commit).setOnClickListener(this);
        TextView shopText = (TextView) findViewById(R.id.textView1);
        shopText.setText(shopName);
//        ToastUtil.showToast(this, "请划动星星评分");
    }

    ProgressDialog dialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_commit:
                sendData();
                if (null == dialog) {
                    dialog = LoadingView.createDefLoading(this);
                    dialog.setMessage(getString(R.string.mall_put_data));
                }
                dialog.show();
                break;
        }
    }

    public void sendData() {
        JSONObject json = new JSONObject();
        try {
            json.put("orderid", orderId);
            json.put("shopstar", ratingBar.getRating());
            JSONArray jarray = new JSONArray();
            for (int i = 0; i < mRemarkData.size(); i++) {
                JSONObject item = new JSONObject();
                GoodsRemarkInfo info = mRemarkData.get(i);
                item.put("orderdetailid", info.orderdetailid);
                item.put("goodsid", info.goodsid);
                item.put("goodsname", info.goodsname);
                item.put("skuid", info.skuid);
                item.put("skuname", info.skuname);
                Log.e("GoodsRemarkAddAct", " -------------------- Score : "+info.star+" --------------------- ");
                item.put("star", info.star);
                item.put("content", info.content);
                jarray.put(item);
            }
            json.put("productstarlist", jarray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new HttpEntity(this).commonPostJson(Method.addGoodsComment, json.toString(), this);
    }

    OnRatingBarChangeListener ratingBarListener = new OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            TextView tv_perfect = (TextView) ratingBar.getTag();
            if (rating == 5f) {
//				tv_perfect.setVisibility(View.VISIBLE);
                tv_perfect.setText(getString(R.string.mall_prefect));
            } else if (rating == 1f || rating == 2f || rating == 0f) {
//				tv_perfect.setVisibility(View.GONE);
                tv_perfect.setText(getString(R.string.mall_bad));
            } else if (rating == 3f || rating == 4f) {
//				tv_perfect.setVisibility(View.GONE);
                tv_perfect.setText(getString(R.string.mall_common_remark));
            }
        }
    };

    class RemarkAdapter extends STBLBaseGroupAdapter<OrderProduct> {
        private int mMaxLength = 60;
        public RemarkAdapter(Activity act, List<OrderProduct> mDatas) {
            super(act, mDatas);
//            , R.layout.mall_add_comment_item
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parentView) {
            convertView = getInflaterView(R.layout.mall_add_comment_item);

            OrderProduct info = getItem(position);

            ImageView ivDiZhi = (ImageView) convertView.findViewById(R.id.icon_dizhi);
            PicassoUtil.load(getContext(), info.getImgurl(), ivDiZhi);

            TextView tvGoodsName = (TextView)convertView.findViewById(R.id.tv_goodsname);
            TextView tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
            TextView tvNum = (TextView) convertView.findViewById(R.id.tv_num);
            TextView tvXingHao = (TextView) convertView.findViewById(R.id.tv_xinghao);
            final TextView tv_perfect = (TextView) convertView.findViewById(R.id.textView3);

            tvGoodsName.setText(info.getGoodsname());
            tvPrice.setText("¥" + info.getPrice());
            tvNum.setText("x" + info.getCount());
            tvXingHao.setText(info.getSkuname());

            final TextView textCount = (TextView) convertView.findViewById(R.id.tv_text_count);
            RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar1);
            ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if (rating == 5f) {
//					tv_perfect.setVisibility(View.VISIBLE);
                        tv_perfect.setText(R.string.mall_prefect);
                    } else if (rating == 1f || rating == 2f || rating == 0f) {
//					tv_perfect.setVisibility(View.GONE);
                        tv_perfect.setText(R.string.mall_bad);
                    } else if (rating == 3f || rating == 4f) {
//					tv_perfect.setVisibility(View.GONE);
                        tv_perfect.setText(R.string.mall_common_remark);
                    }
                    mRemarkData.get(position).star = (int) rating;
                }
            });
            ratingBar.setRating(0);
            final EditText et_content = (EditText) convertView.findViewById(R.id.editText1);
            et_content.addTextChangedListener(new TextWatcher() {
                private CharSequence temp;
                private int editStart ;
                private int editEnd ;
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    temp = s;
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    editStart = et_content.getSelectionStart();
                    editEnd = et_content.getSelectionEnd();
                    if (temp.length() > mMaxLength) {
                        ToastUtil.showToast(getString(R.string.mall_remark_text_tips)+mMaxLength+getString(R.string.mall_remark_text_tips2));
                        s.delete(editStart-1, editEnd);
                        int tempSelection = editStart;
                        et_content.setText(s);
                        et_content.setSelection(tempSelection);
                    }
                    textCount.setText(s.toString().length()+"/"+mMaxLength);
                    mRemarkData.get(position).content = et_content.getText().toString();
                }
            });

            return convertView;
        }

    }

    class GoodsRemarkInfo {
        public long orderdetailid;
        public String goodsid;
        public String goodsname;
        public long skuid;
        public String skuname;
        public int star;
        public String content;
    }

    @Override
    public void httpParseError(String methodName, BaseItem baseItme) {
        dialog.dismiss();
    }

    @Override
    public void httpParseResult(String methodName, String result, String valueObj) {
        dialog.dismiss();
        switch (methodName) {
            case Method.addGoodsComment:
                //通知我的模块，订单数量更新
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));
                EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 0));
                finish();
                break;
        }
    }
}
