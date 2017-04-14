package yahier.exst.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.ad.IntroduceAdAct;
import com.stbl.stbl.act.ad.PublishShoppingActivity;
import com.stbl.stbl.util.ImageUtils;

public class ShoppingCircleTipsDialog extends Dialog {

    private ImageView mBgIv;
    private TextView tvToDetail; //去了解品牌服务
    private TextView tvClose; //关闭

    public ShoppingCircleTipsDialog(final Context context) {
        super(context, R.style.Common_Dialog);
        setContentView(R.layout.dialog_shopping_circle_tips);

        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.alpha = 1.0f;
        window.setAttributes(params);

        mBgIv = (ImageView) findViewById(R.id.iv_bg);
        tvToDetail = (TextView) findViewById(R.id.tv_to_detail);
        tvClose = (TextView) findViewById(R.id.tv_close);

        ImageUtils.loadImage(R.drawable.open_shopping_intro, mBgIv);

        tvToDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调到品牌介绍页
                Intent intent = new Intent(context, IntroduceAdAct.class);
                context.startActivity(intent);
                dismiss();
            }
        });
        findViewById(R.id.tv_send_redpacket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                context.startActivity(new Intent(context, PublishShoppingActivity.class));
            }
        });
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    public static void create(Activity activity) {
        ShoppingCircleTipsDialog dialog = new ShoppingCircleTipsDialog(activity);
        dialog.show();
    }
}
