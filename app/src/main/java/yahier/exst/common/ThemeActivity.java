package yahier.exst.common;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.im.rong.BusinessCardProvider;
import com.stbl.stbl.act.im.rong.CollectProvider;
import com.stbl.stbl.act.im.rong.PhotoCollectionsProvider;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.dialog.STProgressDialog;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

/**
 * 主题控制Act
 *
 * @author lenovo
 */
public class ThemeActivity extends BaseActivity {
    protected RelativeLayout topBanner;
    TextView topLeft;
    TextView topMiddle;
    TextView mRightTv;
    ImageView topRight;
    public MyApplication app;
    public String tag;
    FrameLayout mainLayout;
    private ImageView ivCartImg; //购物车图标
    private TextView tvCartNum; //购物车商品数量
    protected STProgressDialog stblDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (MyApplication) getApplication();
        stblDialog = new STProgressDialog(this);
        //initSystemBar(this);
    }


    //测试沉静状态栏
//    public static void initSystemBar(Activity activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            setTranslucentStatus(activity, true);
//        }
//        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
//        tintManager.setStatusBarTintEnabled(true);
//        // 使用颜色资源
//        tintManager.setStatusBarTintResource(R.color.theme_yellow);
//
//    }

    @TargetApi(19)
//    private static void setTranslucentStatus(Activity activity, boolean on) {
//        Window win = activity.getWindow();
//        WindowManager.LayoutParams winParams = win.getAttributes();
//        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//        if (on) {
//            winParams.flags |= bits;
//        } else {
//            winParams.flags &= ~bits;
//        }
//        win.setAttributes(winParams);
//
//    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    protected UserItem getUserItem(){
        return MyApplication.getInstance().getUserItem();
    }

    protected void setEmptyView() {
        View view = LayoutInflater.from(this).inflate(R.layout.common_empty_view, null);
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        mainLayout.removeAllViews();
        mainLayout.addView(view);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.theme_parent);
        tag = getClass().getName();
        topBanner = (RelativeLayout) findViewById(R.id.theme_top_banner);
        topLeft = (TextView) findViewById(R.id.theme_top_banner_left);
        topMiddle = (TextView) findViewById(R.id.theme_top_banner_middle);
        mRightTv = (TextView) findViewById(R.id.theme_top_tv_right);
        topRight = (ImageView) findViewById(R.id.theme_top_banner_right);
        topLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ivCartImg = (ImageView) findViewById(R.id.iv_top_goods_cart);
        tvCartNum = (TextView) findViewById(R.id.tv_top_cart_num);

        mainLayout = (FrameLayout) findViewById(R.id.mainLayout);
        View mainView = this.getLayoutInflater().inflate(layoutResID, null);
        mainLayout.addView(mainView);

//		topMiddle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.bangyibang_font_large));
    }

    /***
     * 判断是否是卖家
     */
    public static boolean isMerchant(String userId) {
        if (userId.length() > Config.userMerchantIdLength) {
            setImMerchantInputs(true);
            return true;
        }
        setImMerchantInputs(false);
        return false;
    }

    public static boolean isMerchantDoNothing(String userId) {
        if (userId.length() > Config.userMerchantIdLength) {
            return true;
        }
        return false;
    }

    public static boolean isMerchant(long userId) {
        return isMerchant(String.valueOf(userId));
    }


    //设置私聊信息输入信息类型
    public static void setImMerchantInputs(boolean isMerchant) {
//        if (isMerchant) {
//            InputProvider.ExtendProvider[] providerPrivate = {new PhotoCollectionsProvider(RongContext.getInstance()),
//                    new CameraInputProvider(RongContext.getInstance())
//            };
//            RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, providerPrivate);
//        } else {
//            InputProvider.ExtendProvider[] providerPrivate = {new PhotoCollectionsProvider(RongContext.getInstance()),// 图片
//                    new CameraInputProvider(RongContext.getInstance()),// 相机
//                    //new RedPacketProvider(RongContext.getInstance()),// 红包
//                    new BusinessCardProvider(RongContext.getInstance()),// 名片
//                    new CollectProvider(RongContext.getInstance()),// 收藏
//            };
//            RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, providerPrivate);
//        }

    }

    protected void setLabel(Object... objs) {
        Object obj = objs[0];
        try {
            int StringId = Integer.parseInt(obj.toString());
            topMiddle.setText(StringId);
            return;
        } catch (Exception e) {
        }
        topMiddle.setText(obj.toString());
    }

    //设置购物车数量
    public void setTopCartNum(int num) {
        ivCartImg.setVisibility(View.VISIBLE);
        tvCartNum.setVisibility(View.VISIBLE);
        tvCartNum.setText(String.valueOf(num));
    }

    //点击购物车事件
    public void setTopCarOnClickListener(OnClickListener listener) {
        ivCartImg.setOnClickListener(listener);
    }

    protected void setRightText(CharSequence text, OnClickListener listener) {
        mRightTv.setVisibility(View.VISIBLE);
        mRightTv.setText(text);
        mRightTv.setOnClickListener(listener);
    }

    protected void setRightText(int text, OnClickListener listener) {
        setRightText(getString(text),listener);
    }



    protected void setRightText(int text) {
        setRightText(getString(text));
    }

    protected void setRightText(CharSequence text) {
        mRightTv.setVisibility(View.VISIBLE);
        mRightTv.setText(text);
    }

    protected void setActionText(int text) {
        mRightTv.setText(text);
    }

    protected void setActionText(CharSequence text) {
        mRightTv.setText(text);
    }

    protected void setRightTextListener(OnClickListener listener) {
        mRightTv.setOnClickListener(listener);
    }

    public void setRightGone() {
        topRight.setVisibility(View.GONE);
    }

    public void setTopGone() {
        topBanner.setVisibility(View.GONE);
    }

    public void setRightImage(int resId, OnClickListener clickListener) {
        topRight.setVisibility(View.VISIBLE);
        topRight.setImageResource(resId);
        topRight.setOnClickListener(clickListener);
    }

    public void hideRightImage() {
        topRight.setVisibility(View.GONE);
    }

    public void setRightTextVisibility(int visibility) {
        mRightTv.setVisibility(visibility);
    }

    public void showToast(String text) {
        ToastUtil.showToast(this, text);
    }

    public void callPhone(String phoneNo) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNo));
        startActivity(intent);
    }

    public void enterAct(Class<?> mClass) {
        Intent intent = new Intent(this, mClass);
        startActivity(intent);
    }

    public void enterActBundle(Class<?> mClass, Bundle mBundle) {
        Intent intent = new Intent(this, mClass);
        intent.putExtras(mBundle);
        startActivity(intent);
    }

    public void showDialog(String msg) {
        if (stblDialog == null) {
            stblDialog = new STProgressDialog(this);
        }
        stblDialog.setMsgText(msg);
        stblDialog.show();
    }

    public void dissmissDialog() {
        if (stblDialog != null) {
            stblDialog.dismiss();
        }
    }

}
