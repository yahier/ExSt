package yahier.exst.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.stbl.R;

/**
 * Created by tnitf on 2016/3/24.
 */
public class EmptyView extends RelativeLayout {

    private LinearLayout mEmptyLayout;
    private ImageView mEmptyIv;
    private TextView mEmptyTv;

    private LinearLayout mLoadingLayout;

    private TextView mRetryTv;

    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.view_empty, this);
        mEmptyLayout = (LinearLayout) findViewById(R.id.layout_empty);
        mEmptyIv = (ImageView) findViewById(R.id.iv_empty);
        mEmptyTv = (TextView) findViewById(R.id.tv_empty);

        mLoadingLayout = (LinearLayout) findViewById(R.id.layout_loading);

        mRetryTv = (TextView) findViewById(R.id.tv_retry);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EmptyView, defStyleAttr, 0);
        if (a.hasValue(R.styleable.EmptyView_emptyImage)) {
            Drawable background = a.getDrawable(R.styleable.EmptyView_emptyImage);
            if (background != null) {
                mEmptyIv.setVisibility(View.VISIBLE);
                mEmptyIv.setBackgroundDrawable(background);
            }
        }
        if (a.hasValue(R.styleable.EmptyView_emptyText)) {
            mEmptyTv.setText(a.getString(R.styleable.EmptyView_emptyText));
        }
        if (a.hasValue(R.styleable.EmptyView_emptyTextColor)) {
            mEmptyTv.setTextColor(a.getColor(R.styleable.EmptyView_emptyTextColor, 0xff969696));
        }
        if (a.hasValue(R.styleable.EmptyView_emptyTextSize)) {
            mEmptyTv.setTextSize(a.getDimension(R.styleable.EmptyView_emptyTextSize, 30));
        }
        a.recycle();
    }

    public void showLoading() {
        setVisibility(View.VISIBLE);
        mEmptyLayout.setVisibility(View.GONE);
        mRetryTv.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.VISIBLE);
    }

    public void showEmpty() {
        setVisibility(View.VISIBLE);
        mLoadingLayout.setVisibility(View.GONE);
        mRetryTv.setVisibility(View.GONE);
        mEmptyLayout.setVisibility(View.VISIBLE);
    }

    public void showRetry() {
        setVisibility(View.VISIBLE);
        mEmptyLayout.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.GONE);
        mRetryTv.setVisibility(View.VISIBLE);
    }

    public void hide() {
        setVisibility(View.GONE);
        mEmptyLayout.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.GONE);
        mRetryTv.setVisibility(View.GONE);
    }

    public void setEmptyImage(int resId) {
        mEmptyIv.setVisibility(View.VISIBLE);
        mEmptyIv.setImageResource(resId);
    }

    public void setEmptyText(String text) {
        mEmptyTv.setText(text);
    }

    public void setOnRetryListener(final OnClickListener listener) {
        mRetryTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoading();
                listener.onClick(v);
            }
        });
    }

    public void setRetryText(String text) {
        mRetryTv.setText(text);
    }
}
