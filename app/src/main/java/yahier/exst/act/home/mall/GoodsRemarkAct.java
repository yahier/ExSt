package yahier.exst.act.home.mall;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.RCommonAdapter;
import com.stbl.stbl.common.RViewHolder;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.model.Comment;
import com.stbl.stbl.model.CommentList;
import com.stbl.stbl.ui.BaseClass.STBLBaseTableActivity;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.HttpUtil;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OnFinalHttpCallback;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.XListView;
import com.stbl.stbl.widget.XListView.OnXListViewListener;

/**
 * 商品评论列表
 *
 * @author ruilin
 */
public class GoodsRemarkAct extends STBLBaseTableActivity<Comment> {

    public final static int PAGE_LINE_COUNT = 15;

    private long mGoodsId;
    private TextView emptyView;
    private long lastId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.include_refresh_navigation_list);

        navigationView.setTitleBar(getString(R.string.mall_goods_remark));
        navigationView.setClickLeftListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent it = getIntent();
        mGoodsId = it.getLongExtra("goodsid", -1);
        if (-1 == mGoodsId) {
            ToastUtil.showToast(this, getString(R.string.mall_data_err));
        }

        bindRefreshList(R.id.lv_content, new RemarkAdapter(this, arrayList));
        emptyView = (TextView) findViewById(R.id.empty_tv);
        emptyView.setText(R.string.mall_this_goods_no_remark);

        startRefresh();
    }

    @Override
    public void onReload() {
        requireData(-1);
    }

    @Override
    public void loadMore() {
        if (arrayList.size() > 0)
            requireData(arrayList.get(arrayList.size() - 1).id);
        else
            stopRefresh();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void httpParseError(String methodName, BaseItem baseItme) {
        stopRefresh();
    }

    @Override
    public void httpParseResult(String methodName, String result, String valueObj) {
        stopRefresh();
        Log.e("GoodsRemarkAct", String.valueOf(valueObj));
        switch (methodName) {
            case Method.getGoodsCommentList:
                CommentList comment = JSONHelper.getObject(valueObj, CommentList.class);
                if (comment == null) return;
                ArrayList<Comment> list = JSONHelper.getList(comment.commentlist, Comment.class);

                if (list != null) {
                    if (list.size() < PAGE_LINE_COUNT) {
                        getRefreshView().EndLoad();
                    }

                    if (lastId == -1) {
                        arrayList.clear();
                    }

                    arrayList.addAll(list);
                    tableAdapter.notifyDataSetChanged();
                }
                emptyView.setVisibility(arrayList.size() == 0 ? View.VISIBLE : View.GONE);
                break;
        }
    }

    private void requireData(long lastId) {
        this.lastId = lastId;
        JSONObject json = new JSONObject();
        try {
            json.put("goodsid", mGoodsId);
            if (lastId > -1)
                json.put("lastid", lastId); // 最后一条id
            json.put("count", PAGE_LINE_COUNT);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new HttpEntity(this).commonPostJson(Method.getGoodsCommentList, json.toString(), this);
    }

    private class RemarkAdapter extends RCommonAdapter<Comment> {

        public RemarkAdapter(Activity act, List<Comment> mDatas) {
            super(act, mDatas, R.layout.mall_comment_item);
        }

        @Override
        public void convert(RViewHolder helper, Comment item) {
            if (item.user != null) {
                helper.setText(R.id.name, item.user.nickname);
            }
            helper.setText(R.id.what, getString(R.string.mall_buy) + item.skuname);
            helper.setText(R.id.tv_time, DateUtil.getTime("" + item.createtime, "yyyy/MM/dd HH:mm"));
            helper.setText(R.id.tv_content, item.content);
            RatingBar starts = helper.getView(R.id.ratingBar1);
            Log.e("GoodsRemarkAct - adapter ", " ------- Score : "+item.score+" ----------- ");
            starts.setRating(item.score);
            helper.setImageByUrl(R.id.iv_img, item.user.imgurl);
        }
    }
}
