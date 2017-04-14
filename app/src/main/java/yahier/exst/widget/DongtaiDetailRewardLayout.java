package yahier.exst.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.item.StatusesReward;
import com.stbl.stbl.util.DimenUtils;
import com.stbl.stbl.util.ImageUtils;

import java.util.List;

/**
 * Created by tnitf on 2016/6/15.
 */
public class DongtaiDetailRewardLayout extends LinearLayout {

    private LinearLayout mInnerLayout;

    public DongtaiDetailRewardLayout(Context context) {
        this(context, null);
    }

    public DongtaiDetailRewardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DongtaiDetailRewardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.layout_dongtai_detail_reward, this);
        init();
    }

    private void init() {
        mInnerLayout = (LinearLayout) findViewById(R.id.layout_inner);
        mInnerLayout.setShowDividers(SHOW_DIVIDER_MIDDLE);
        mInnerLayout.setDividerDrawable(getResources().getDrawable(R.drawable.shape_dongtai_detail_reward_layout));
    }

    public void setData(final List<StatusesReward> list) {
        post(new Runnable() {
            @Override
            public void run() {
                addViewList(list);
            }
        });
    }

    public void setDataForShoppingCircle(final List<StatusesReward> list) {
        post(new Runnable() {
            @Override
            public void run() {
                addViewListForShoppingCircle(list);
            }
        });
    }

    private void addViewList(List<StatusesReward> list) {
        mInnerLayout.removeAllViews();
        int innerWidth = 0;
        int size = Math.min(list.size(), 9);
        int width = getWidth();
        for (int i = 0; i < size; i++) {
            if (innerWidth + DimenUtils.dp2px(30) + DimenUtils.dp2px(6) > width) {
                break;
            }
            final StatusesReward reward = list.get(i);
            RoundImageView imageView = new RoundImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LayoutParams layoutParams = new LayoutParams(DimenUtils.dp2px(30), DimenUtils.dp2px(30));
            imageView.setLayoutParams(layoutParams);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), TribeMainAct.class);
                    intent.putExtra("userId", reward.getUser().getUserid());
                    getContext().startActivity(intent);
                }
            });
            mInnerLayout.addView(imageView);
            ImageUtils.loadHead(reward.getUser().getImgurl(), imageView);
            innerWidth += DimenUtils.dp2px(30) + DimenUtils.dp2px(6);
        }
    }

    private void addViewListForShoppingCircle(List<StatusesReward> list) {
        mInnerLayout.removeAllViews();
        int innerWidth = 0;
        int size = Math.min(list.size(), 9);
        int width = getWidth();
        for (int i = 0; i < size; i++) {
            if (innerWidth + DimenUtils.dp2px(30) + DimenUtils.dp2px(6) > width) {
                break;
            }
            final StatusesReward reward = list.get(i);
            RoundImageView imageView = new RoundImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LayoutParams layoutParams = new LayoutParams(DimenUtils.dp2px(30), DimenUtils.dp2px(30));
            imageView.setLayoutParams(layoutParams);
            mInnerLayout.addView(imageView);
            ImageUtils.loadHead(reward.getUser().getImgurl(), imageView);
            innerWidth += DimenUtils.dp2px(30) + DimenUtils.dp2px(6);
        }
    }
}
