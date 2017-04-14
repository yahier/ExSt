package yahier.exst.widget;

import android.content.Context;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.rockerhieu.emojicon.EmojiconTextView;

/**
 * Created by lenovo on 2016/7/31.
 */
public class TextViewFixTouchConsume extends EmojiTextView {
    boolean dontConsumeNonUrlClicks = true;
    boolean linkHit;
    public TextViewFixTouchConsume(Context context) {
        super(context);
        init();
    }
    public TextViewFixTouchConsume(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public TextViewFixTouchConsume(Context context, AttributeSet attrs,
                                   int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean ret = false;
                CharSequence text = ((TextView) v).getText();
                Spannable stext = Spannable.Factory.getInstance().newSpannable(text);
                TextView widget = (TextView) v;
                int action = event.getAction();
                if (action == MotionEvent.ACTION_UP ||
                        action == MotionEvent.ACTION_DOWN) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    x -= widget.getTotalPaddingLeft();
                    y -= widget.getTotalPaddingTop();
                    x += widget.getScrollX();
                    y += widget.getScrollY();
                    Layout layout = widget.getLayout();
                    int line = layout.getLineForVertical(y);
                    int off = layout.getOffsetForHorizontal(line, x);
                    ClickableSpan[] link = stext.getSpans(off, off, ClickableSpan.class);
                    if (link.length != 0) {
                        if (action == MotionEvent.ACTION_UP) {
                            link[0].onClick(widget);
                        }
                        ret = true;
                    }
                }
                return ret;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getMovementMethod() == null) {
            boolean result = super.onTouchEvent(event);
            return result;
        }
        MovementMethod m = getMovementMethod();
        setMovementMethod(null);
        boolean mt = m.onTouchEvent(this, (Spannable) getText(), event);
        if (mt && event.getAction() == MotionEvent.ACTION_DOWN) {
            event.setAction(MotionEvent.ACTION_UP);
            mt = m.onTouchEvent(this, (Spannable) getText(), event);
            event.setAction(MotionEvent.ACTION_DOWN);
        }
        boolean st = super.onTouchEvent(event);
        setMovementMethod(m);
        setFocusable(false);
        return mt || st;
    }
    @Override
    public boolean hasFocusable() {
        return false;
    }

}
