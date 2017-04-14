package yahier.exst.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.stbl.stbl.R;
import com.stbl.stbl.act.ad.PublishShoppingActivity;
import com.stbl.stbl.act.ad.PublishShoppingLinkActivity;
import com.stbl.stbl.util.KEY;

/**
 * Created by Administrator on 2016/10/14.
 */

public class PublishShoppingLinkPanel extends LinearLayout implements View.OnClickListener {

    private PublishShoppingActivity mActivity;

    public PublishShoppingLinkPanel(Context context) {
        this(context, null);
    }

    public PublishShoppingLinkPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PublishShoppingLinkPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mActivity = (PublishShoppingActivity) context;
        LayoutInflater.from(context).inflate(R.layout.publish_shopping_link_panel, this);
        initView();
    }

    private void initView() {
        findViewById(R.id.iv_add_link).setOnClickListener(this);
        findViewById(R.id.iv_nice_link).setOnClickListener(this);
        findViewById(R.id.iv_card).setOnClickListener(this);
        findViewById(R.id.iv_goods).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add_link:
                jumpToLink(PublishShoppingLinkActivity.TYPE_ADD_LINK);
                break;
            case R.id.iv_nice_link:
                jumpToLink(PublishShoppingLinkActivity.TYPE_NICE_LINK);
                break;
            case R.id.iv_card:
                jumpToLink(PublishShoppingLinkActivity.TYPE_CARD);
                break;
            case R.id.iv_goods:
                jumpToLink(PublishShoppingLinkActivity.TYPE_GOODS);
                break;
        }
    }

    private void jumpToLink(int type) {
        Intent intent = new Intent(mActivity, PublishShoppingLinkActivity.class);
        intent.putExtra(KEY.TYPE, type);
        mActivity.startActivityForResult(intent, PublishShoppingActivity.REQUEST_CODE_ADD_LINK);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

}
