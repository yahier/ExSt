package yahier.exst.act.im;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.item.Relation;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.task.AddFriendUtil;
import com.stbl.stbl.util.HttpUtil;
import com.stbl.stbl.util.InputMethodUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OnFinalHttpCallback;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.XListView;

import java.util.ArrayList;
import java.util.List;

public class SearchUserAct extends BaseActivity implements OnFinalHttpCallback, XListView.OnXListViewListener {
    XListView listView;
    EditText inputWord;
    TextView btnSeach;
    int mPage = 1;
    final int requestCount = 15;
    Adapter adapter;

    boolean isNoToken = false;
    String url = Method.imSearchUser;

    LoadingDialog loading;
    View emptyView;
    public final static int typeEntryAdd = 0;
    public final static int typeEntrySelect = 1;
    int typeEntry = typeEntryAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_search_user_act);
        initView();
        Intent it = getIntent();
        isNoToken = it.getBooleanExtra("isNoToken", false);
        typeEntry = it.getIntExtra("typeEntry", typeEntryAdd);
         url = isNoToken ? Method.imSearchUserNoToken : Method.imSearchUser;
        if (isNoToken) {
            getData();
        }
    }

    void initView() {
        loading = new LoadingDialog(this);
        listView = (XListView) findViewById(R.id.list);
        listView.setOnXListViewListener(this);
        adapter = new Adapter(this);
        listView.setAdapter(adapter);
        emptyView = findViewById(R.id.empty);

        inputWord = (EditText) findViewById(R.id.input);
        showInputSoft(inputWord);

        final ImageView clearIv = (ImageView) findViewById(R.id.iv_clear);
        inputWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString().trim();
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
                inputWord.setText("");
                InputMethodUtils.showInputMethod(inputWord);
            }
        });

        btnSeach = (TextView) findViewById(R.id.search_btn);
        btnSeach.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                getData();
                // showAddWindow("");
            }
        });

        findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodUtils.hideInputMethod(inputWord);
                finish();
            }
        });

        if (typeEntry == typeEntryAdd) {
            if (adapter.getCount() == 0) return;
            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    position = position - listView.getHeaderViewsCount();
                    UserItem item = adapter.getData().get(position);
                    Intent intent = new Intent(SearchUserAct.this, TribeMainAct.class);
                    intent.putExtra("userId", item.getUserid());
                    startActivity(intent);
                }
            });
        }

    }

    void getData() {
        String word = inputWord.getText().toString().trim();
        if (TextUtils.isEmpty(word)) {
            ToastUtil.showToast(R.string.please_input_key_word);
            return;
        }
        InputMethodUtils.hideInputMethod(inputWord);
        JSONObject params = new JSONObject();
        params.put("keyword", word);
        params.put("page", mPage);
        params.put("count", requestCount);
        new HttpUtil(this, null, loading).postJson(url, params.toJSONString(), this);
    }


    private void showInputSoft(final EditText inputContent) {
        inputContent.postDelayed(new Runnable() {

            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(inputContent, InputMethodManager.SHOW_FORCED);// SHOW_FORCED

            }
        }, 100);

    }


    class Adapter extends CommonAdapter {
        Context mContext;
        List<UserItem> list;

        public Adapter(Context mContext) {
            this.mContext = mContext;
            list = new ArrayList<UserItem>();
        }

        class Holder {
            ImageView user_img;
            TextView name;
            TextView tvSent, tvAdd;
            TextView tvSelect;

            public ImageView ivGender;//性别
            public TextView mAgeTv;
            public TextView mLocationTv;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        public void setData(List<UserItem> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public void addData(List<UserItem> list) {
            this.list.addAll(list);
            notifyDataSetChanged();
        }

        public void clearData() {
            this.list.clear();
            notifyDataSetChanged();
        }

        public List<UserItem> getData() {
            return list;
        }

        @Override
        public View getView(final int i, View con, ViewGroup arg2) {

            Holder ho = null;
            if (con == null) {
                ho = new Holder();
                con = LayoutInflater.from(mContext).inflate(R.layout.im_search_user_list_item, null);
                ho.user_img = (ImageView) con.findViewById(R.id.user_img);
                ho.name = (TextView) con.findViewById(R.id.name);

                ho.tvSent = (TextView) con.findViewById(R.id.tvSent);
                ho.tvAdd = (TextView) con.findViewById(R.id.tvAdd);
                ho.tvSelect = (TextView) con.findViewById(R.id.tvSelect);

                ho.mAgeTv = (TextView) con.findViewById(R.id.tv_age);
                ho.mLocationTv = (TextView) con
                        .findViewById(R.id.tv_location);
                ho.ivGender = (ImageView) con.findViewById(R.id.iv_gender);

                if (isNoToken) {
                    ho.tvAdd.setText(R.string.baishi);
                }
                con.setTag(ho);
            } else
                ho = (Holder) con.getTag();

            final UserItem user = list.get(i);
            PicassoUtil.load(mContext, user.getImgurl(), ho.user_img);//遭遇过oom
            ho.name.setText(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() : user.getAlias());
//            ho.name.setText(user.getNickname());



            if (user.getAge() <= 0) {
                ho.mAgeTv.setVisibility(View.GONE);
            } else {
                ho.mAgeTv.setVisibility(View.VISIBLE);
                ho.mAgeTv.setText(String.format(MyApplication.getContext().getString(R.string.im_age),user.getAge()));
            }
            ho.ivGender.setVisibility(View.VISIBLE);
            if (user.getGender() == UserItem.gender_boy) {
//            holder.mAgeTv.setCompoundDrawablesWithIntrinsicBounds(
//                    R.drawable.dongtai_gender_boy, 0, 0, 0);
//            holder.mAgeTv.setBackgroundResource(R.drawable.shape_blue_corner32);
                ho.ivGender.setImageResource(R.drawable.icon_male);
            } else if (user.getGender() == UserItem.gender_girl) {
//            holder.mAgeTv.setCompoundDrawablesWithIntrinsicBounds(
//                    R.drawable.dongtai_gender_girl, 0, 0, 0);
//            holder.mAgeTv.setBackgroundResource(R.drawable.shape_red_corner32);
                ho.ivGender.setImageResource(R.drawable.icon_female);
            } else {
//            holder.mAgeTv.setCompoundDrawablesWithIntrinsicBounds(
//                    0, 0, 0, 0);
//            holder.mAgeTv.setBackgroundResource(R.drawable.shape_unknow_sex_bg);
                ho.ivGender.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(user.getCityname())){
                ho.mLocationTv.setVisibility(View.GONE);
            }else {
                ho.mLocationTv.setVisibility(View.VISIBLE);
                ho.mLocationTv.setText(user.getCityname());
            }

            // 添加好友

            switch (typeEntry) {
                case typeEntryAdd:
                    ho.tvSelect.setVisibility(View.GONE);
                    ho.tvAdd.setVisibility(View.VISIBLE);
                    int relationFlag = user.getRelationflag();
                    if (Relation.isFriend(relationFlag)) {
                        ho.tvAdd.setText(R.string.been_friends);
                        ho.tvAdd.setTextColor(0xff969696);
                        ho.tvAdd.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        ho.tvAdd.setBackgroundDrawable(null);
                        ho.tvAdd.setEnabled(false);
                    } else if (Relation.isSelf(relationFlag)) {
                        ho.tvAdd.setText(R.string.self);
                        ho.tvAdd.setTextColor(0xff969696);
                        ho.tvAdd.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        ho.tvAdd.setBackgroundDrawable(null);
                        ho.tvAdd.setEnabled(false);
                    } else {
                        ho.tvAdd.setText(R.string.plus_friend);
                        ho.tvAdd.setTextColor(0xffff6c6c);
                        ho.tvAdd.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_add, 0, 0, 0);
                        ho.tvAdd.setBackgroundResource(R.drawable.red_line_bg);
                        ho.tvAdd.setEnabled(true);
                    }
                    break;
                case typeEntrySelect:
                    ho.tvSelect.setVisibility(View.GONE);
                    ho.tvAdd.setVisibility(View.GONE);
                    con.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            Intent intent = new Intent();
                            intent.putExtra("user", user);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }


                    });
                    break;

            }
            ho.tvAdd.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (isNoToken) {
                        returnUser(user);
                        return;
                    } else {
                        new AddFriendUtil(SearchUserAct.this, null).addFriendDirect(user);
                    }
                }
            });

//            ho.tvSelect.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View arg0) {
//                    Intent intent = new Intent();
//                    intent.putExtra("user", user);
//                    setResult(Activity.RESULT_OK, intent);
//                    finish();
//                }
//
//
//            });
            //拜师选择就不能点击进入
            if (typeEntry == typeEntryAdd) {
                ho.user_img.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, TribeMainAct.class);
                        intent.putExtra("userId", user.getUserid());
                        mContext.startActivity(intent);
                    }
                });

            }
            return con;

        }

    }


    void returnUser(UserItem user) {
        Intent it = new Intent();
        it.putExtra("user", user);
        setResult(RESULT_OK, it);
        finish();
    }


    @Override
    public void onHttpResponse(String methodName, String json, Object handle) {
        switch (methodName) {
            case Method.imSearchUser:
            case Method.imSearchUserNoToken:
                listView.onLoadMoreComplete();
                listView.onRefreshComplete();
                List<UserItem> list = JSONHelper.getList(json, UserItem.class);
                LogUtil.logE("size:" + list.size());
                if (list == null || list.size() == 0) {
                    if (mPage == 1)
                        adapter.clearData();
                    listView.EndLoad();
                } else {
                    if (list.size() < requestCount) {
                        listView.EndLoad();
                    }
                    InputMethodUtils.hideInputMethod(inputWord);
                    if (mPage == 1) {
                        adapter.setData(list);
                    } else {
                        adapter.addData(list);
                    }
                }
                listView.setEmptyView(emptyView);
                break;
        }

    }


    @Override
    public void onHttpError(String methodName, String msg, Object handle) {
        listView.onLoadMoreComplete();
        listView.onRefreshComplete();
    }

    @Override
    public void onRefresh(XListView v) {
        listView.setPullLoadEnable(true);
        mPage = 1;
        getData();
    }

    @Override
    public void onLoadMore(XListView v) {
        mPage++;
        getData();
    }
}
