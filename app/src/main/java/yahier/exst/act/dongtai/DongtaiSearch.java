package yahier.exst.act.dongtai;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.DongtaiMainAdapter;
import com.stbl.stbl.adapter.DongtaiSquareAdapter;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.Collect;
import com.stbl.stbl.item.LinkStatuses;
import com.stbl.stbl.item.Message;
import com.stbl.stbl.item.PraiseResult;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.util.DongtaiSearchRecordDB;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.InputMethodUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.TextListener;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.XListView;
import com.stbl.stbl.widget.XListView.OnXListViewListener;

import java.util.List;

/**
 * 查找动态。大量代码从广场复制，也仍然使用广场的adapter
 *
 * @author lenovo
 */
public class DongtaiSearch extends BaseActivity implements OnClickListener, FinalHttpCallback, OnXListViewListener, DongtaiMainAdapter.OnStatesesListener {
    EditText inputSearch;
    XListView listView;
    View emptyView;
    DongtaiMainAdapter adapter;
    long lastStatusesId;
    Button btnClear;
    int loadType = 0;// 加载模式
    final int loadTypeBottom = 0;// 底部加载
    final int loadTypeTop = 1;// 顶部加载。清除以前数据
    int position;
    SimpleCursorAdapter adapter_history;
    DongtaiSearchRecordDB db = new DongtaiSearchRecordDB(this);
    final int requestDetailDongtai = 100;
    final int requestForward = 101;// 转发
    final int resultUpdateCode = 102;// 更新数据
    final int resultDeleteCode = 104;
    MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dongtai_search);
        // setLabel("查找动态");
        initViews();
        app = (MyApplication) getApplication();
        findViewById(R.id.btn_back).setOnClickListener(this);
        InputMethodUtils.showInputMethodDelay(inputSearch);
    }

    void initViews() {
        emptyView = findViewById(R.id.empty);
        btnClear = (Button) findViewById(R.id.btn_clear_history);
        btnClear.setOnClickListener(this);
        inputSearch = (EditText) findViewById(R.id.input);
        final ImageView clearIv = (ImageView) findViewById(R.id.iv_clear);
        inputSearch.addTextChangedListener(new TextListener() {

            @Override
            public void afterTextChanged(Editable edit) {
                if (edit.toString().trim().equals("")) {
                    btnClear.setVisibility(View.VISIBLE);
                    showCursorData();
                }
                String content = edit.toString().trim();
                if (TextUtils.isEmpty(content)) {
                    clearIv.setVisibility(View.GONE);
                } else {
                    clearIv.setVisibility(View.VISIBLE);
                }
            }
        });
        clearIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSearch.setText("");
                InputMethodUtils.showInputMethod(inputSearch);
            }
        });

        findViewById(R.id.search_btn).setOnClickListener(this);
        listView = (XListView) findViewById(R.id.list);
        //listView.setEmptyView(findViewById(R.id.empty));
        listView.setOnXListViewListener(this);
        adapter = new DongtaiMainAdapter(this,DongtaiMainAdapter.typeStatusesSearch);
        adapter.setOnStatesesListener(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
                if (btnClear.getVisibility() == View.VISIBLE) {
                    Cursor cursor = adapter_history.getCursor();
                    int nameColumnIndex = cursor.getColumnIndex(DongtaiSearchRecordDB.word);
                    String name = cursor.getString(nameColumnIndex);
                    inputSearch.setText(name);
                    inputSearch.setSelection(name.length());
                    loadType = loadTypeTop;
                    lastStatusesId = 0;
                    listView.setAdapter(adapter);
                    getMainList(name);
                }
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //如果是在
                if (btnClear.getVisibility() == View.GONE) {
                adapter.checkIsPauseVideo(firstVisibleItem-1, visibleItemCount+firstVisibleItem-2);}
            }
        });


        showCursorData();
    }

    @SuppressWarnings("deprecation")
    void showCursorData() {
        Cursor c = db.query();
        if(c.getCount()==0){
            btnClear.setVisibility(View.GONE);
        }else{
            btnClear.setVisibility(View.VISIBLE);
        }
        listView.setPullLoadEnable(false);
        listView.setPullRefreshEnable(false);
        adapter_history = new SimpleCursorAdapter(this, R.layout.single_list_item, c, new String[]{DongtaiSearchRecordDB.word}, new int[]{R.id.name});
        listView.setAdapter(adapter_history);
    }




    public void onDestroy() {
        super.onDestroy();
        adapter.stopVideo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 如果从动态详细中返回
        if (requestCode == requestDetailDongtai) {
            adapter.resetVideo();
            switch (resultCode) {
                case resultDeleteCode:
                    long statusesId = data.getLongExtra("statusesId", 0);
                    adapter.deleteStatusesId(statusesId);
                    break;
                case resultUpdateCode:
                    //adapter.hideInput();
                    Statuses statuses = (Statuses) data.getSerializableExtra("item");
                    LogUtil.logE("onActivityResult " + this.position);
                    adapter.updateItem(statuses, position);
                    break;
            }
        }  else if (requestCode == DongtaiSquareAdapter.requestTribe) {
            if (resultCode == DongtaiSquareAdapter.requestTribe) {
                boolean isFollowed = data.getBooleanExtra("isFollowed", false);
                //adapter.setFollowStateChanged(statuses.getUser().getUserid(), isFollowed);
            }
        }else if(requestCode==DongtaiMainAdapter.requestFullVideo){
            adapter.testContinueVideo();

        }

    }

    /**
     * 获取主列表数据
     */
    void getMainList(String... name) {
        listView.setPullLoadEnable(true);
        listView.setPullRefreshEnable(true);
        btnClear.setVisibility(View.GONE);
        String word = inputSearch.getText().toString();
        if (name != null && name.length != 0) {
            word = name[0];
        }
        if (!word.trim().equals("")) {
            db.insert(word);
        }
        Params params = new Params();
        params.put("lastid", lastStatusesId);
        params.put("count", Statuses.requestCount);
        params.put("word", word);
        new HttpEntity(this).commonPostData(Method.weiboSearch, params, this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_btn:
                if (TextUtils.isEmpty(inputSearch.getText().toString().trim())) {
                    ToastUtil.showToast(R.string.please_input_key_word);
                    return;
                }
                loadType = loadTypeTop;
                lastStatusesId = 0;
                listView.setAdapter(adapter);
                getMainList();
                break;
            case R.id.btn_clear_history:
                db.deleteAllData();
                showCursorData();
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh(XListView v) {
        listView.setPullLoadEnable(true);
        loadType = loadTypeTop;
        lastStatusesId = 0;
        getMainList();
    }

    @Override
    public void onLoadMore(XListView v) {
        loadType = loadTypeBottom;
        listView.setPullLoadEnable(false);
        getMainList();

    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }
            switch (methodName) {
                case Method.weiboSearch:
                    listView.onLoadMoreComplete();
                    listView.onRefreshComplete();
                    break;
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.weiboSearch:
                listView.onLoadMoreComplete();
                listView.onRefreshComplete();
                listView.setPullLoadEnable(true);
                List<Statuses> list = JSONHelper.getList(obj, Statuses.class);
                LogUtil.logE(methodName, list.size() + "");
                if (list.size() > 0) {
                    lastStatusesId = list.get(list.size() - 1).getStatusesid();
                    InputMethodUtils.hideInputMethod(inputSearch);
                }
                if (list.size() < Statuses.requestCount) {
                    listView.EndLoad();
                }
                if (loadType == loadTypeBottom) {
                    adapter.addData(list);
                } else {
                    adapter.setData(list);
                }
                listView.setEmptyView(emptyView);
                break;
            case Method.weiboRemark:
                ToastUtil.showToast(R.string.remark_success);
                // 然后进入详细 怪怪的哈
                Intent intent = new Intent(this, DongtaiDetailActivity.class);
                intent.putExtra(DongtaiMainAdapter.key, statuses);
                intent.putExtra(DongtaiDetailActivity.keyType, statuses.getStatusestype());
                startActivityForResult(intent, requestDetailDongtai);
                break;
            case Method.weiboCollect:
                Collect mCollect = JSONHelper.getObject(obj, Collect.class);
                collect(mCollect.getCollectcount());
                break;
            case Method.weiboCancelCollect:
                Collect mCollect2 = JSONHelper.getObject(obj, Collect.class);
                cancelCollect(mCollect2.getCollectcount());
                break;
            case Method.userFollow:
                ToastUtil.showToast(R.string.follow_success);
                break;
            case Method.userIgnore:
                ToastUtil.showToast(R.string.shield_success);
                break;
            case Method.weiboPraise:
                PraiseResult praiseItem = JSONHelper.getObject(obj, PraiseResult.class);
               // adapter.updatePraiseText(listView, position, praiseItem.getCount(), praiseItem.getType());
                showPraiseAnim(praiseItem);
                break;
        }

    }




    //收藏成功后
    void collect(int count) {
        int headerCount = listView.getHeaderViewsCount();
        int visibilePosition = listView.getFirstVisiblePosition();
        LogUtil.logE("position",position+":"+visibilePosition+":"+headerCount);
        position = position - visibilePosition + headerCount;
        View viewItem = listView.getChildAt(position);
        if(viewItem==null)return;
        TextView tvFovor = (TextView) viewItem.findViewById(R.id.item_text4);
        ImageView img = (ImageView) viewItem.findViewById(R.id.imgItem4);
        StatusesAnimUtil.showAnimCollect(this,tvFovor,img,count);
        //更新
        statuses.setFavorcount(count);
        statuses.setIsfavorited(Statuses.isfavoritedYes);
        adapter.update(position, statuses);
    }


    //取消收藏
    void cancelCollect(int count) {
        float scaleSmall = 0.5f;
        final int duration = 200;
        int headerCount = listView.getHeaderViewsCount();
        int visibilePosition = listView.getFirstVisiblePosition();
        position = position - visibilePosition + headerCount;
        View viewItem = listView.getChildAt(position);
        if(viewItem==null)return;
        TextView tvFovor = (TextView) viewItem.findViewById(R.id.item_text4);
        final ImageView img = (ImageView) viewItem.findViewById(R.id.imgItem4);


        StatusesAnimUtil.showAnimCancelCollect(this,tvFovor,img,count);
        //更新
        statuses.setFavorcount(count);
        statuses.setIsfavorited(Statuses.isfavoritedNo);
        adapter.update(position, statuses);
    }


    //点赞与取消点赞
    void showPraiseAnim(PraiseResult praiseItem) {
        float scaleValue = 1.5f;
        final int duration = 200;
        int headerCount = listView.getHeaderViewsCount();
        int visibilePosition = listView.getFirstVisiblePosition();
        position = position - visibilePosition + headerCount;
        //更新
        statuses.setPraisecount(praiseItem.getCount());
        View viewItem = listView.getChildAt(position);
        if(viewItem==null)return;
        TextView tvPraise = (TextView) viewItem.findViewById(R.id.item_text3);
        final ImageView img = (ImageView) viewItem.findViewById(R.id.imgItem3);
        StatusesAnimUtil.showAnimPraiseAnim(this,tvPraise,img,praiseItem,statuses);
        adapter.update(position, statuses);
    }

    Statuses statuses;
    Message message;

    @Override
    public void doForward(Statuses statuses, int position) {
        this.position = position;
        Intent intent = new Intent(this, DongtaiRepost.class);
        intent.putExtra("data", statuses);
        startActivityForResult(intent, requestForward);

    }

    @Override
    public void doRemark(int position, Statuses statuses, String content) {
        message = new Message();
        message.setContent(content);
        message.setName(SharedUser.getUserNick());
        // 以上构造评论item
        this.position = position;
        this.statuses = statuses;

        Params params = new Params();
        params.put("statusesid", String.valueOf(statuses.getStatusesid()));
        params.put("content", content);
        new HttpEntity(this).commonPostData(Method.weiboRemark, params, this);

    }


    @Override
    public void doCollect(Statuses mStatuses, int position, int isCollected) {
        this.position = position;
        this.statuses = mStatuses;
        Params params = new Params();
        params.put("userid", app.getUserId());
        params.put("statusesid", statuses.getStatusesid());
        if (isCollected == Statuses.isfavoritedYes) {
            new HttpEntity(this).commonPostData(Method.weiboCancelCollect, params, this);
        } else {
            new HttpEntity(this).commonPostData(Method.weiboCollect, params, this);
        }

    }

    @Override
    public void enterDongtaiDetail(int position, Statuses statuses, boolean scrollToPosition) {
        this.position = position;
        LogUtil.logE("enterDongtaiDetail " + this.position);
        Intent intent = new Intent(this, DongtaiDetailActivity.class);
        intent.putExtra("statusesId", statuses.getStatusesid());
        intent.putExtra(DongtaiDetailActivity.keyType, statuses.getStatusestype());
        intent.putExtra(KEY.SCROLL_TO_POSITION, scrollToPosition);
        startActivityForResult(intent, requestDetailDongtai);

    }

    boolean isPauseVideo = true;
    @Override
    public void doFullClick(int position) {
        LogUtil.logE("dofull");
        isPauseVideo = false;
        Intent intent = new Intent(this, VideoPlayAct.class);
        startActivityForResult(intent, DongtaiMainAdapter.requestFullVideo);
    }

    @Override
    public void doPraise(Statuses mStatuses, int position) {
        this.position = position;
        this.statuses = mStatuses;
        Params params = new Params();
        params.put("userid", app.getUserId());
        params.put("statusesid", statuses.getStatusesid());
        new HttpEntity(this).commonPostData(Method.weiboPraise, params, this);
    }

    public void onPause(){
        super.onPause();
        if(isPauseVideo){
            adapter.pauseInVisibleVideo();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isPauseVideo = true;
    }

//    @Override
//    public void enterTribe(Statuses statuses) {
//        this.statuses = statuses;
//        Intent intent = new Intent(this, TribeMainAct.class);
//        intent.putExtra("userId", statuses.getUser().getUserid());
//        intent.putExtra("typeEntry", TribeMainAct.typeEntryOther);
//        startActivityForResult(intent, DongtaiSquareAdapter.requestTribe);
//
//    }
}
