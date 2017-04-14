package yahier.exst.util;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.LruCache;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rockerhieu.emojicon.EmojiconHandler;
import com.stbl.stbl.R;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.widget.EmojiTextView;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/8/19.
 */

public class EmojiParseThread {

    private static volatile EmojiParseThread sInstance;

    public static EmojiParseThread getInstance() {
        if (sInstance == null) {
            synchronized (EmojiParseThread.class) {
                if (sInstance == null) {
                    sInstance = new EmojiParseThread();
                }
            }
        }
        return sInstance;
    }

    private ExecutorService mPool;
    private Handler mHandler;
    private LruCache<String, SpannableStringBuilder> mCache;

    private EmojiParseThread() {
        mPool = Executors.newFixedThreadPool(2);
        mHandler = new Handler(Looper.getMainLooper());
        mCache = new LruCache<>(300);//缓存300个SpannableStringBuilder对象
    }

    public void parse(final CharSequence text, final EmojiTextView view) {
        if (view != null) {
            if (!TextUtils.isEmpty(text)) {
                final String tag = String.valueOf(text.toString().hashCode());
                view.setTag(tag);
                SpannableStringBuilder cacheString = getStringFromCache(tag);
                if (cacheString != null) {
                    view.setText(cacheString);
                    checkAutoLink(view, cacheString);
                    return;
                }
                view.setText(StringUtil.createSpace(text));
                final WeakReference<EmojiTextView> viewRef = new WeakReference<>(view);
                final int emojiSize = view.mEmojiconSize;
                final int emojiAlignment = view.mEmojiconAlignment;
                final int textSize = view.mEmojiconTextSize;
                final int index = view.mTextStart;
                final int length = view.mTextLength;
                final boolean useSystemDefault = view.mUseSystemDefault;
                mPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        final SpannableStringBuilder builder = new SpannableStringBuilder(text);
                        EmojiconHandler.addEmojis(MyApplication.getContext(), builder, emojiSize, emojiAlignment, textSize, index, length, useSystemDefault);
                        putStringToCache(tag, builder);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                EmojiTextView v = viewRef.get();
                                if (v != null && v.getTag().toString().equals(tag)) {
                                    v.setText(builder);
                                }
                                checkAutoLink(view, builder);
                            }
                        });
                    }
                });


            } else {
                view.setText("");
            }
        }

    }


    void checkAutoLink(TextView view, SpannableStringBuilder style) {
        CharSequence text2 = view.getText();
        if (text2 instanceof Spannable) {
            //LogUtil.logE("yahier", "新加设置");
            int end = text2.length();
            Spannable sp = (Spannable) text2;
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            //SpannableStringBuilder style = new SpannableStringBuilder(text2);
            //style.clearSpans();// should clear old spans
            for (URLSpan url : urls) {
                MyURLSpan myURLSpan = new MyURLSpan(url.getURL(), view);
                style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            view.setText(style);
        }
    }

    private class MyURLSpan extends ClickableSpan {

        private String mUrl;
        private View mView;

        MyURLSpan(String url, View mView) {
            mUrl = url;
            this.mView = mView;
        }

        @Override
        public void onClick(View widget) {
            Intent intent = new Intent(mView.getContext(), CommonWeb.class);
            intent.putExtra("url", mUrl);
            mView.getContext().startActivity(intent);
        }
    }

    public void parseIm(final CharSequence text, final EmojiTextView view) {
        if (view != null) {
            if (!TextUtils.isEmpty(text)) {
                final String tag = String.valueOf(text.toString().hashCode());
                view.setTag(tag);
                SpannableStringBuilder cacheString = getStringFromCache(tag);
                if (cacheString != null) {
                    view.setText(cacheString);
                    return;
                }
                //view.setText(StringUtil.createSpace(text));
                final WeakReference<EmojiTextView> viewRef = new WeakReference<>(view);
                final int emojiSize = view.mEmojiconSize;
                final int emojiAlignment = view.mEmojiconAlignment;
                final int textSize = view.mEmojiconTextSize;
                final int index = view.mTextStart;
                final int length = view.mTextLength;
                final boolean useSystemDefault = view.mUseSystemDefault;
                mPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        final SpannableStringBuilder builder = new SpannableStringBuilder(text);
                        EmojiconHandler.addEmojis(MyApplication.getContext(), builder, emojiSize, emojiAlignment, textSize, index, length, useSystemDefault);
                        putStringToCache(tag, builder);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                EmojiTextView v = viewRef.get();
                                if (v != null && v.getTag().toString().equals(tag)) {
                                    v.setText(builder);
                                }
                            }
                        });
                    }
                });
            } else {
                view.setText("");
            }
        }

    }

    public void putStringToCache(String key, SpannableStringBuilder string) {
        if (TextUtils.isEmpty(key) || string == null) {
            return;
        }
        mCache.put(key, string);
    }

    public SpannableStringBuilder getStringFromCache(String key) {
        if (!TextUtils.isEmpty(key)) {
            return mCache.get(key);
        }
        return null;
    }

}
