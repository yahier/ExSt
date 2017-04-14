package yahier.exst.act.ad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.adapter.BrandPlusAdapter;
import com.stbl.stbl.adapter.OneTypeAdapter;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.ad.AdSub;
import com.stbl.stbl.model.Ad;
import com.stbl.stbl.model.NameValue;
import com.stbl.stbl.task.AdTask;
import com.stbl.stbl.util.DimenUtils;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.AdHelper;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.RefreshListView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/22.
 */

public class BrandPlusActivity extends ThemeActivity {

    private static final String TYPE_ALL = "0";
    private static final int COUNT = 10;

    private EmptyView mEmptyView;

    private ArrayList<Ad> mList;
    private RefreshListView mRefreshListView;
    private BrandPlusAdapter mAdapter;

    private TextView mSelectTypeTv;
    private String mSelectType;

    private ArrayList<NameValue> mTypeList;
    private ListPopupWindow mPopupWindow;
    private TypeAdapter mTypeAdapter;

    private int mPage = 1;
    private int sortno = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_plus);

        initView();
        initTypeList();
        getAdInfoList();
    }

    private void initView() {
        setLabel(getString(R.string.me_brand_plus));

        mSelectTypeTv = (TextView) findViewById(R.id.tv_select_type);
        mSelectTypeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.show();
            }
        });

        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mEmptyView.setRetryText(getString(R.string.me_pull_to_retry));

        mList = new ArrayList<>();
        mRefreshListView = (RefreshListView) findViewById(R.id.refresh_list_view);
        mAdapter = new BrandPlusAdapter(mActivity, mList);
        mRefreshListView.setAdapter(mAdapter);

        mRefreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshListView.setLoadMoreEnabled(true);
                mPage = 1;
                getAdInfoList();
            }
        });
        mRefreshListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getAdInfoList();
            }
        });

        mAdapter.setInterface(new BrandPlusAdapter.AdapterInterface() {
            @Override
            public void onItemClick(Ad ad) {
                Intent intent = new Intent(mActivity, CommonWeb.class);
                intent.putExtra("ad", ad);
                intent.putExtra("title", getString(R.string.me_ad_detail));
                intent.putExtra("type", CommonWeb.typeAd);
                startActivity(intent);
            }

            @Override
            public void toAdCooperate(Ad ad) {
                AdHelper.toApplyAdCooperateAct(ad, mActivity);
            }

        });
    }

    private void initTypeList() {
        String brandplusoption = (String) SharedPrefUtils.getFromPublicFile(KEY.brandplusoption, "[]");
        try {
            mTypeList = (ArrayList<NameValue>) JSON.parseArray(brandplusoption, NameValue.class);
            if (mTypeList == null) {
                mTypeList = new ArrayList<>();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mTypeAdapter = new TypeAdapter(mTypeList);
        mPopupWindow = new ListPopupWindow(this);
        mPopupWindow.setAdapter(mTypeAdapter);
        mPopupWindow.setAnchorView(mSelectTypeTv);
        mPopupWindow.setWidth(DimenUtils.dp2px(130));
        mPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
        mPopupWindow.setModal(true);
        mPopupWindow.setVerticalOffset(-DimenUtils.dp2px(8));
        mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_statuses_main_window));
        mPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPopupWindow.dismiss();
                NameValue item = mTypeList.get(position);
                mSelectType = item.value;
                mSelectTypeTv.setText(item.name);
                mList.clear();
                mAdapter.notifyDataSetChanged();
                mEmptyView.showLoading();
                mPage = 1;
                getAdInfoList();
            }
        });
        String areaCode = SharedUser.getUserItem().getAreacode();
        for (NameValue item : mTypeList) {
            if (item.value.equals(areaCode)) {
                mSelectType = areaCode;
                mSelectTypeTv.setText(item.name);
                break;
            }
        }
        if (mSelectType == null) {
            mSelectType = TYPE_ALL;
            mSelectTypeTv.setText(getString(R.string.me_all));
        }
    }

    private void getAdInfoList() {
        mTaskManager.start(AdTask.getAdList(mSelectType, mPage, COUNT, sortno)
                .setCallback(new HttpTaskCallback<AdSub>(mActivity) {

                    @Override
                    public void onFinish() {
                        mRefreshListView.reset();
                    }

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                        if (mPage == 1 && mAdapter.getCount() == 0) {
                            mEmptyView.showRetry();
                        }
                    }

                    @Override
                    public void onSuccess(AdSub sub) {
                        if (mPage == 1) {
                            if (sub.getAdview().size() == 0) {
                                mEmptyView.showEmpty();
                            } else {
                                mEmptyView.hide();
                            }
                            mList.clear();
                        }
                        sortno = sub.getSortno();
                        mList.addAll(sub.getAdview());
                        mAdapter.notifyDataSetChanged();
                        if (sub.getAdview().size() < COUNT) {
                            mRefreshListView.setLoadMoreEnabled(false);
                        } else {
                            mPage++;
                        }
                    }
                }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.closeFloatLayout();
    }

    private class TypeAdapter extends OneTypeAdapter<NameValue> {

        public TypeAdapter(ArrayList<NameValue> list) {
            super(list);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_brand_plus_type;
        }

        @Override
        protected ViewHolder getViewHolder() {
            return new ViewHolder() {

                TextView mTv;

                @Override
                public void init(View v) {
                    mTv = (TextView) v;
                }

                @Override
                public void bind(View v, int position) {
                    mTv.setText(getList().get(position).name);
                }
            };
        }
    }

}
