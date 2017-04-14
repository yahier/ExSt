package yahier.exst.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.DynamicDrawableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.stbl.stbl.util.LogUtil;

/**
 * Created by Administrator on 2016/8/19.
 */

public class EmojiTextView extends TextView {
    public int mEmojiconSize;
    public int mEmojiconAlignment;
    public int mEmojiconTextSize;
    public int mTextStart = 0;
    public int mTextLength = -1;
    public boolean mUseSystemDefault = false;

    public EmojiTextView(Context context) {
        super(context);
        init(null);
    }

    public EmojiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EmojiTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mEmojiconTextSize = (int) getTextSize();
        if (attrs == null) {
            mEmojiconSize = (int) getTextSize();
        } else {
            TypedArray a = getContext().obtainStyledAttributes(attrs, com.rockerhieu.emojicon.R.styleable.Emojicon);
            mEmojiconSize = (int) a.getDimension(com.rockerhieu.emojicon.R.styleable.Emojicon_emojiconSize, getTextSize());
            mEmojiconAlignment = a.getInt(com.rockerhieu.emojicon.R.styleable.Emojicon_emojiconAlignment, DynamicDrawableSpan.ALIGN_BASELINE);
            mTextStart = a.getInteger(com.rockerhieu.emojicon.R.styleable.Emojicon_emojiconTextStart, 0);
            mTextLength = a.getInteger(com.rockerhieu.emojicon.R.styleable.Emojicon_emojiconTextLength, -1);
            mUseSystemDefault = a.getBoolean(com.rockerhieu.emojicon.R.styleable.Emojicon_emojiconUseSystemDefault, false);
            a.recycle();
        }
    }

    /**
     * Set the size of emojicon in pixels.
     */
    public void setEmojiconSize(int pixels) {
        mEmojiconSize = pixels;
        super.setText(getText());
    }

    /**
     * Set whether to use system default emojicon
     */
    public void setUseSystemDefault(boolean useSystemDefault) {
        mUseSystemDefault = useSystemDefault;
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        SpannableString span = new SpannableString(getText());
        ClickableSpan[] links = span.getSpans(getSelectionStart(),
                getSelectionEnd(), ClickableSpan.class);
        if (links.length != 0) {
            return true;
        }
        LogUtil.logE("onTouchEvent","false");
        return false;

    }
}
