package yahier.exst.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.rockerhieu.emojicon.emoji.Emojicon;
import com.stbl.stbl.R;
import com.stbl.stbl.util.InputMethodUtils;
import com.stbl.stbl.widget.EmojiKeyboard;

/**
 * Created by tnitf on 2016/6/13.
 */
public class InputDialog extends Dialog {

    private EmojiKeyboard mEmojiKeyboard;
    private EditText mContentEt;
    private ImageView mEmojiIv;
    private ImageView mSendIv;

    public InputDialog(Context context) {
        super(context, R.style.InputDialogTheme);
        setContentView(R.layout.dialog_input);

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_animation);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        initView();
    }

    private void initView() {
        mEmojiKeyboard = (EmojiKeyboard) findViewById(R.id.emoji_keyboard);
        mContentEt = (EditText) findViewById(R.id.et_content);
        mEmojiIv = (ImageView) findViewById(R.id.iv_emoji);
        mSendIv = (ImageView) findViewById(R.id.iv_send);

        mContentEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mEmojiKeyboard.getVisibility() == View.VISIBLE) {
                    mEmojiKeyboard.setVisibility(View.GONE);
                }
                return false;
            }
        });

        mEmojiIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmojiKeyboard.getVisibility() == View.GONE) {
                    InputMethodUtils.hideInputMethod(mContentEt);
                    mEmojiIv.setImageResource(R.drawable.input_dialog_keyboard);
                    mEmojiKeyboard.setVisibility(View.VISIBLE);
                } else {
                    mEmojiKeyboard.setVisibility(View.GONE);
                    mEmojiIv.setImageResource(R.drawable.icon_emoji);
                    InputMethodUtils.showInputMethod(mContentEt);
                }
            }
        });

        mEmojiKeyboard = (EmojiKeyboard) findViewById(R.id.emoji_keyboard);
        mEmojiKeyboard.setOnEmojiconBackspaceClickedListener(new EmojiKeyboard.OnEmojiconBackspaceClickedListener() {
            @Override
            public void onEmojiconBackspaceClicked(View v) {
                EmojiKeyboard.backspace(mContentEt);
            }
        });

        mEmojiKeyboard.setOnEmojiconClickedListener(new EmojiKeyboard.OnEmojiconClickedListener() {
            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                EmojiKeyboard.input(mContentEt, emojicon);
            }
        });

    }

    @Override
    public void show() {
        super.show();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (mEmojiKeyboard.getVisibility() == View.VISIBLE) {
            mEmojiKeyboard.setVisibility(View.GONE);
        }
    }

    public void setOnSendListener(View.OnClickListener listener) {
        mSendIv.setOnClickListener(listener);
    }

    public String getContent() {
        return mContentEt.getText().toString();
    }

    public void setSendButtonEnable(boolean enable) {
        mSendIv.setEnabled(enable);
    }

    public void hideFaceView() {
        if (mEmojiKeyboard.getVisibility() == View.VISIBLE) {
            mEmojiKeyboard.setVisibility(View.GONE);
        }
        dismiss();
    }

    public void clearContent() {
        mContentEt.setText("");
    }

    public void setHint(CharSequence hint) {
        mContentEt.setHint(hint);
    }
}
