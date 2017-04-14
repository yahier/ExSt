package yahier.exst.act.dongtai;


import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.GVImgAdapter;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.LinkStatuses;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.StatusesPic;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.InputMethodUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.TextListener;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.TipsDialog.OnTipsListener;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;

/**
 * @author lenovo
 *         转发页面。
 */
public class DongtaiRepost extends BaseActivity implements OnClickListener, FinalHttpCallback {
    Context mContext;
    private EditText inputContent;
    private TextView btnPulish;
    long statusesid;// 发布的微博id
    // 链接相关
    int linkType;
    Statuses linkStatuses;
    String linkId;
    TextView tvTitle;
    // 发布生成的动态
    Statuses newStatuses;
    TextView tvLeftCount;
    final String tag = getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dongtai_repost_act);
        mContext = DongtaiRepost.this;
        inits();
        getIntentData();

    }

    /**
     * 获取进入activity时 带入的数据
     */
    void getIntentData() {
        Intent intent = getIntent();
        linkStatuses = (Statuses) intent.getSerializableExtra("data");
        tvTitle.setText(R.string.repost_statuses);
        showInputSoft();
        findViewById(R.id.tvLinkDelete1).setVisibility(View.GONE);
        showStatusesLink();
    }

    /**
     * 将整个动态都传进来，而不仅仅是链接的动态
     *
     * @param
     */
    void showStatusesLink() {
        if (linkStatuses.getIsforward() == Statuses.isforwardYes) {
//            String alias = linkStatuses.getUser().getAlias();
//            String nickName = alias == null || alias.equals("") ? linkStatuses.getUser().getNickname() : alias;
            String nickName = linkStatuses.getUser().getNickname();
            String content = linkStatuses.getContent();
            content = content.replace(Config.longWeiboFillMark, "");
            inputContent.setText("//@" + nickName + ":" + content);
            linkStatuses = linkStatuses.getLinks().getStatusesinfo();
        }
        linkType = Statuses.linkTypeStatuses;
        linkId = String.valueOf(linkStatuses.getStatusesid());//
        TextView link3Tv = (TextView) findViewById(R.id.link3_content);

        if (linkStatuses.getTitle() != null && !linkStatuses.getTitle().equals("")) {
            link3Tv.setText(linkStatuses.getTitle());
        } else {
            if (linkStatuses.getContent() != null)
                link3Tv.setText(linkStatuses.getContent().replace(Config.longWeiboFillMark, " "));
        }
        TextView lin3UserName = (TextView) findViewById(R.id.link3_userName);
        String alias = linkStatuses.getUser().getAlias();
        lin3UserName.setText(alias == null || alias.equals("") ? linkStatuses.getUser().getNickname() : alias);
//        lin3UserName.setText(linkStatuses.getUser().getNickname());

        ImageView link3Img = (ImageView) findViewById(R.id.link3_img);
        StatusesPic link3Pics = linkStatuses.getStatusespic();
        if (link3Pics != null) {
            String imgUrl;
            if (link3Pics.getDefaultpic().equals(GVImgAdapter.longCover)) {
                imgUrl = link3Pics.getMiddlepic() + link3Pics.getDefaultpic();
            } else if (link3Pics.getPics().size() > 0) {
                imgUrl = link3Pics.getMiddlepic() + link3Pics.getPics().get(0);
            } else {
                imgUrl = link3Pics.getEx();
            }
            PicassoUtil.loadStatuses(mContext, imgUrl, link3Img);
        } else {
            PicassoUtil.load(mContext, R.drawable.dongtai_default, link3Img);
        }


    }


    /**
     * 转发时，进入则弹出键盘
     */
    private void showInputSoft() {
        inputContent.postDelayed(new Runnable() {

            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(inputContent, InputMethodManager.SHOW_FORCED);// SHOW_FORCED

            }
        }, 100);


    }


    @Override
    public void onClick(final View view) {
        switch(view.getId()) {
            case R.id.theme_top_banner_left:
                showDeleteTipDialog();
                break;
            case R.id.weibo_pulish:
                InputMethodUtils.hideInputMethod(inputContent);
                WaitingDialog.show(mContext, R.string.waiting, false);
                view.setEnabled(false);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setEnabled(true);
                    }
                }, Config.interClickTime);
                forward();
                break;
        }
    }

    private void hideInputSoft() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    void inits() {
        tvLeftCount = (TextView) findViewById(R.id.tvLeftCount);
        tvTitle = (TextView) findViewById(R.id.theme_top_banner_middle);
        btnPulish = (TextView) findViewById(R.id.weibo_pulish);
        btnPulish.setOnClickListener(this);
        inputContent = (EditText) findViewById(R.id.dongtai_content_input);

        final int maxWordSize = Config.remarkContentLength;
        inputContent.addTextChangedListener(new TextListener() {

            @Override
            public void afterTextChanged(Editable edit) {
                String content = edit.toString();
                int size = content.length();
                if (size > maxWordSize) {
                    inputContent.setText(content.substring(0, maxWordSize));
                    inputContent.setSelection(maxWordSize);
                    size = maxWordSize;
                }
                tvLeftCount.setText(size + "/" + maxWordSize);

            }
        });
        findViewById(R.id.theme_top_banner_left).setOnClickListener(this);


    }


    void showDeleteTipDialog() {
        TipsDialog.popup(this, R.string.is_giveup_editing, R.string.cancel, R.string.queding, new OnTipsListener() {

            @Override
            public void onConfirm() {
                InputMethodUtils.hideInputMethod(inputContent);
                finish();
            }

            @Override
            public void onCancel() {
            }
        });
    }


    // 转发
    private void forward() {
        Params params = new Params();
        params.put("content", inputContent.getText().toString());
        params.put("imgcount", 0);
        params.put("statusesid", linkStatuses.getStatusesid());
        new HttpEntity(this).commonPostData(Method.weiboForward, params, this);
    }


    @Override
    public void onBackPressed() {
        showDeleteTipDialog();
    }

    // 发布成功后续
    public void pulishPostResult() {
        WaitingDialog.dismiss();
        ToastUtil.showToast(R.string.post_success);
        sendBroadCast();
        //setResult(resultRorwardCode);
        finish();
    }


    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }
            WaitingDialog.dismiss();
            return;
        }
        hideInputSoft();
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch(methodName) {
            case Method.weiboForward:
                newStatuses = JSONHelper.getObject(obj, Statuses.class);
                statusesid = newStatuses.getStatusesid();
                pulishPostResult();
                break;
        }

    }


    void sendBroadCast() {
        Intent intent = new Intent("getOneNewStatuses");
        intent.putExtra("statuses", newStatuses);
        sendBroadcast(intent);
    }


}
