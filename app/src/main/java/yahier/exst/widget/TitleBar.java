package yahier.exst.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.stbl.R;

/**
 * Created by tnitf on 2016/3/16.
 */
public class TitleBar extends RelativeLayout {

    private ImageView mBackIv;
    private TextView mTitleTv;
    private TextView mActionTv;
    private ImageView mActionIv;
    private RelativeLayout rl_root;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.bar_title, this);

        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
        mBackIv = (ImageView) findViewById(R.id.iv_back);
        mTitleTv = (TextView) findViewById(R.id.tv_title);
        mActionTv = (TextView) findViewById(R.id.tv_action);
        mActionIv = (ImageView) findViewById(R.id.iv_action);
    }

    public void setOnBackListener(OnClickListener listener) {
        mBackIv.setOnClickListener(listener);
    }

    public void setTitle(CharSequence text) {
        mTitleTv.setText(text);
    }
    public void setCenterTitle(CharSequence text) {
        mTitleTv.setText(text);
        LayoutParams rlp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rlp.addRule(CENTER_IN_PARENT);
        mTitleTv.setLayoutParams(rlp);
    }

    public void setCenterTitle(int text) {
        mTitleTv.setText(text);
        LayoutParams rlp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rlp.addRule(CENTER_IN_PARENT);
        mTitleTv.setLayoutParams(rlp);
    }
    public void setTitle(int text) {
        mTitleTv.setText(text);
    }

    public void setActionText(CharSequence text, OnClickListener listener) {
        mActionTv.setVisibility(View.VISIBLE);
        mActionTv.setText(text);
        mActionTv.setOnClickListener(listener);
    }

    public void setActionIcon(int resId, OnClickListener listener) {
        mActionIv.setVisibility(View.VISIBLE);
        mActionIv.setImageResource(resId);
        mActionIv.setOnClickListener(listener);
    }

}
