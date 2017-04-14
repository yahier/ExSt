package yahier.exst.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.stbl.R;

/**
 * Created by tnitf on 2016/6/14.
 */
public class LoadMoreFooterView extends RelativeLayout {

    public static final int MODE_COMPLETE = 0; //没有更多了
    public static final int MODE_LOADING = 1; //加载更多中
    public static final int MODE_FAILED = 2; //加载失败，重新加载

    private ProgressBar mProgressBar;
    private TextView mContentTv;

    private OnLoadMoreListener mListener;

    private int mMode;

    public LoadMoreFooterView(Context context) {
        this(context, null);
    }

    public LoadMoreFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.footer_view_load_more, this);
        init();
    }

    private void init() {
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mContentTv = (TextView) findViewById(R.id.tv_content);
    }

    private void setMode(int mode) {
        mMode = mode;
        switch (mode) {
            case MODE_LOADING:

                break;
            case MODE_COMPLETE:

                break;
            case MODE_FAILED:

                break;
        }
    }

    public void setBeforeLoad(String content) {
        setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mContentTv.setVisibility(VISIBLE);
        mContentTv.setText(content);
        setBackgroundResource(R.drawable.list_selector_white);
        setClickable(true);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    setLoading();
                    mListener.onLoadMore();
                }
            }
        });
    }

    public void setBeforeLoad() {
        setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mContentTv.setVisibility(VISIBLE);
        mContentTv.setText("加载更多");
        setBackgroundResource(R.drawable.list_selector_white);
        setClickable(true);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    setLoading();
                    mListener.onLoadMore();
                }
            }
        });
    }

    private void setLoading() {
        setVisibility(VISIBLE);
        mProgressBar.setVisibility(VISIBLE);
        mContentTv.setText(R.string.loading);
        mContentTv.setVisibility(GONE);
        setBackgroundResource(R.color.bg_white);
        setClickable(false);
        setOnClickListener(null);
    }

    public void setLoadDone() {
        setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mContentTv.setVisibility(VISIBLE);
        mContentTv.setText(R.string.no_more);
        setBackgroundResource(R.color.bg_white);
        setClickable(false);
        setOnClickListener(null);
    }

    public void setLoadFail() {
        setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mContentTv.setVisibility(VISIBLE);
        mContentTv.setText("加载失败，重新加载");
        setBackgroundResource(R.drawable.list_selector_white);
        setClickable(true);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    setLoading();
                    mListener.onLoadMore();
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mListener = listener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
