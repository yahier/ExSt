/*
 * Copyright 2014 Hieu Rocker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package yahier.exst.widget;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.rockerhieu.emojicon.emoji.Emojicon;
import com.stbl.stbl.R;
import com.stbl.stbl.adapter.ViewPagerAdapter;

import java.util.ArrayList;

/**
 * @author Hieu Rocker (rockerhieu@gmail.com).
 */
public class EmojiKeyboard extends RelativeLayout {

    private OnEmojiconBackspaceClickedListener mOnEmojiconBackspaceClickedListener;
    private OnEmojiconClickedListener mOnEmojiconClickedListener;

    public EmojiKeyboard(Context context) {
        this(context, null);
    }

    public EmojiKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmojiKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.emoji_keyboard, this);
        initView();
    }

    private void initView() {
        ViewPager emojisPager = (ViewPager) findViewById(R.id.vp);
        CirclePageIndicator pagerIndicator = (CirclePageIndicator) findViewById(R.id.vpi);
        ArrayList<View> viewList = new ArrayList<>();
        viewList.add(new EmojiGridLayout(getContext(), Emoji.DATA1));
        viewList.add(new EmojiGridLayout(getContext(), Emoji.DATA2));
        viewList.add(new EmojiGridLayout(getContext(), Emoji.DATA3));
        viewList.add(new EmojiGridLayout(getContext(), Emoji.DATA4));
        viewList.add(new EmojiGridLayout(getContext(), Emoji.DATA5));
        viewList.add(new EmojiGridLayout(getContext(), Emoji.DATA6));
        viewList.add(new EmojiGridLayout(getContext(), Emoji.DATA7));
        viewList.add(new EmojiGridLayout(getContext(), Emoji.DATA8));
        ViewPagerAdapter emojisAdapter = new ViewPagerAdapter(viewList);
        emojisPager.setAdapter(emojisAdapter);
        pagerIndicator.setViewPager(emojisPager);

        findViewById(R.id.iv_delete).setOnTouchListener(new RepeatListener(1000, 50, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnEmojiconBackspaceClickedListener != null) {
                    mOnEmojiconBackspaceClickedListener.onEmojiconBackspaceClicked(v);
                }
            }
        }));
    }

    public static void input(EditText editText, Emojicon emojicon) {
        if (editText == null || emojicon == null) {
            return;
        }

        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        if (start < 0) {
            editText.append(emojicon.getEmoji());
        } else {
            editText.getText().replace(Math.min(start, end), Math.max(start, end), emojicon.getEmoji(), 0, emojicon.getEmoji().length());
        }
    }

    public static void backspace(EditText editText) {
        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
        editText.dispatchKeyEvent(event);
    }

    /**
     * A class, that can be used as a TouchListener on any view (e.g. a Button).
     * It cyclically runs a clickListener, emulating keyboard-like behaviour. First
     * click is fired immediately, next before initialInterval, and subsequent before
     * normalInterval.
     * <p/>
     * <p>Interval is scheduled before the onClick completes, so it has to run fast.
     * If it runs slow, it does not generate skipped onClicks.
     */
    public static class RepeatListener implements OnTouchListener {

        private Handler handler = new Handler();

        private int initialInterval;
        private final int normalInterval;
        private final OnClickListener clickListener;

        private Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                if (downView == null) {
                    return;
                }
                handler.removeCallbacksAndMessages(downView);
                handler.postAtTime(this, downView, SystemClock.uptimeMillis() + normalInterval);
                clickListener.onClick(downView);
            }
        };

        private View downView;

        /**
         * @param initialInterval The interval before first click event
         * @param normalInterval  The interval before second and subsequent click
         *                        events
         * @param clickListener   The OnClickListener, that will be called
         *                        periodically
         */
        public RepeatListener(int initialInterval, int normalInterval, OnClickListener clickListener) {
            if (clickListener == null)
                throw new IllegalArgumentException("null runnable");
            if (initialInterval < 0 || normalInterval < 0)
                throw new IllegalArgumentException("negative interval");

            this.initialInterval = initialInterval;
            this.normalInterval = normalInterval;
            this.clickListener = clickListener;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downView = view;
                    handler.removeCallbacks(handlerRunnable);
                    handler.postAtTime(handlerRunnable, downView, SystemClock.uptimeMillis() + initialInterval);
                    clickListener.onClick(view);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    handler.removeCallbacksAndMessages(downView);
                    downView = null;
                    return true;
            }
            return false;
        }
    }

    public interface OnEmojiconBackspaceClickedListener {
        void onEmojiconBackspaceClicked(View v);
    }

    public interface OnEmojiconClickedListener {
        void onEmojiconClicked(Emojicon emojicon);
    }

    public void setOnEmojiconBackspaceClickedListener(OnEmojiconBackspaceClickedListener listener) {
        mOnEmojiconBackspaceClickedListener = listener;
    }

    public void setOnEmojiconClickedListener(OnEmojiconClickedListener listener) {
        mOnEmojiconClickedListener = listener;
    }

    private class EmojiGridLayout extends RelativeLayout implements AdapterView.OnItemClickListener {

        private Emojicon[] mData;

        public EmojiGridLayout(Context context, Emojicon[] data) {
            super(context);
            mData = data;
            LayoutInflater.from(context).inflate(R.layout.layout_emoji_grid, this);
            GridView gridView = (GridView) findViewById(R.id.gv);
            gridView.setAdapter(new EmojiAdapter(getContext(), mData, false));
            gridView.setOnItemClickListener(this);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mOnEmojiconClickedListener != null) {
                mOnEmojiconClickedListener.onEmojiconClicked((Emojicon) parent.getItemAtPosition(position));
            }
        }
    }
}
