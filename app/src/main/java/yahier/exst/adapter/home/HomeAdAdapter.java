package yahier.exst.adapter.home;

import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.adapter.OneTypeAdapter;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.model.Ad;
import com.stbl.stbl.model.UserCard;
import com.stbl.stbl.task.AdTask;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.DimenUtils;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.RealtimeBlurView;

import java.util.ArrayList;

public class HomeAdAdapter extends OneTypeAdapter<Ad> {

    private AdapterInterface mInterface;
    private RelativeLayout.LayoutParams params;
    private RelativeLayout.LayoutParams mFloatParams;
    private BaseActivity mActivity;

    public HomeAdAdapter(BaseActivity activity, ArrayList<Ad> list) {
        super(list);
        mActivity = activity;
        mInflater = LayoutInflater.from(mActivity);
        int width = Device.getWidth() - DimenUtils.dp2px(24);
        int height = Config.AD_DESIGN_HEIGHT * width / Config.AD_DESIGN_WIDTH;
        params = new RelativeLayout.LayoutParams(width, height);
        mFloatParams = new RelativeLayout.LayoutParams(width, height + DimenUtils.dp2px(48));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_brand_plus;
    }

    @Override
    protected ViewHolder getViewHolder() {
        return new ViewHolder() {

            ImageView mCoverIv;
            TextView mTitleTv;
            ImageView mLogoIv;
            TextView tvAdBusinessType;
            TextView tvAdIcon;//广告标识

            RealtimeBlurView mBlurView;
            RelativeLayout mFloatLayout;
            ImageView mCrownIv;
            ImageView mHeadIv;
            TextView mNickTv;
            ImageView mOwnerLogoIv;
            TextView mFamilyTv;
            TextView mStudentTv;
            TextView mNearestChiefTv;
            ImageView mNearestLogoIv;
            TextView mViewHomepageTv;
            ImageView mCloseIv;

            @Override
            public void init(View v) {
                mCoverIv = (ImageView) v.findViewById(R.id.iv_cover);
                mTitleTv = (TextView) v.findViewById(R.id.tv_title);
                mLogoIv = (ImageView) v.findViewById(R.id.iv_logo);
                tvAdBusinessType = (TextView) v.findViewById(R.id.tvAdBusinessType);
                tvAdIcon = (TextView) v.findViewById(R.id.tv_ad_icon);

                mBlurView = (RealtimeBlurView) v.findViewById(R.id.blur_view);
                mFloatLayout = (RelativeLayout) v.findViewById(R.id.layout_float);
                mCrownIv = (ImageView) v.findViewById(R.id.iv_crown);
                mHeadIv = (ImageView) v.findViewById(R.id.iv_head);
                mNickTv = (TextView) v.findViewById(R.id.tv_nick);
                mOwnerLogoIv = (ImageView) v.findViewById(R.id.iv_ad_chief_logo);
                mFamilyTv = (TextView) v.findViewById(R.id.tv_family_amount);
                mStudentTv = (TextView) v.findViewById(R.id.tv_student_amount);
                mNearestChiefTv = (TextView) v.findViewById(R.id.tv_nearest_bigchief);
                mNearestLogoIv = (ImageView) v.findViewById(R.id.iv_nearest_chief_logo);
                mViewHomepageTv = (TextView) v.findViewById(R.id.tv_view_homepage);
                mCloseIv = (ImageView) v.findViewById(R.id.iv_close);
            }

            @Override
            public void bind(View v, int position) {
                mCoverIv.setLayoutParams(params);
                mFloatLayout.setLayoutParams(mFloatParams);

                final Ad ad = getList().get(position);
                ImageUtils.loadImage(ad.adimglarurl, mCoverIv);
                mTitleTv.setText(ad.adtitle);
                mCoverIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mInterface != null) {
                            mInterface.onItemClick(ad);
                        }
                    }
                });
                ImageUtils.loadCircleHead(ad.user.getImgurl(), mLogoIv);
                if (ad.businessclass == Ad.businessclassNone) {
                    tvAdBusinessType.setVisibility(View.GONE);
                } else {
                    tvAdBusinessType.setVisibility(View.VISIBLE);
                    tvAdBusinessType.setText(ad.businessclassname);
                }
                if (ad.usercard != null) {
                    setView(ad);
                }

                if (ad.expand) {
                    mFloatLayout.setVisibility(View.VISIBLE);
                    mBlurView.forceBlur();
                } else {
                    mFloatLayout.setVisibility(View.GONE);
                }

                if (ad.issys != Ad.issysYes) {
                    tvAdIcon.setVisibility(View.VISIBLE);
                    mLogoIv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ad.expand = true;
                            mFloatLayout.setVisibility(View.VISIBLE);
                            if (ad.usercard == null) {
                                ImageUtils.loadImage("", mHeadIv);
                                mNickTv.setText("");
                                mFamilyTv.setText("");
                                mStudentTv.setText("");
                                mNearestChiefTv.setText("");
                                mCrownIv.setVisibility(View.GONE);
                                mOwnerLogoIv.setVisibility(View.GONE);
                                ImageUtils.loadImage("", mNearestLogoIv);
                                mViewHomepageTv.setOnClickListener(null);
                                getUserCard(ad);
                            }
                            mBlurView.forceBlur();
                        }
                    });
                } else {
                    tvAdIcon.setVisibility(View.GONE);
                    mLogoIv.setOnClickListener(null);
                }

                tvAdBusinessType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mInterface != null) {
                            mInterface.toAdCooperate(ad);
                        }
                    }
                });
                mCloseIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ad.expand = false;
                        mFloatLayout.setVisibility(View.GONE);
                    }
                });
                mFloatLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }

            private void setView(Ad ad) {
                final UserCard.UserBaseInfoView info = ad.usercard.userbaseinfoview;
                ImageUtils.loadCircleHead(info.imgurl, mHeadIv);
                mNickTv.setText(info.nickname);
                mFamilyTv.setText(String.format(mActivity.getString(R.string.me_family_amount_d), ad.usercard.userexview.familycount));
                mStudentTv.setText(String.format(mActivity.getString(R.string.me_student_amount_d), ad.usercard.userexview.tudicount));
                if (ad.usercard.bigchiefinfo == null) {
                    mNearestChiefTv.setText("");
                    ImageUtils.loadImage("", mNearestLogoIv);
                } else {
                    mNearestChiefTv.setText(Html.fromHtml(String.format(mActivity.getString(R.string.me_nearest_bigchief_s), ad.usercard.bigchiefinfo.bigchiefusername)));
                    ImageUtils.loadImage(ad.usercard.bigchiefinfo.imgurl, mNearestLogoIv);
                }
                if ((info.roleflag & 4) == 4) {
                    mCrownIv.setVisibility(View.VISIBLE);
                    mOwnerLogoIv.setVisibility(View.VISIBLE);
                    ImageUtils.loadImage(ad.usercard.cbigchiefinfo.imgurl, mOwnerLogoIv);
                } else {
                    mCrownIv.setVisibility(View.GONE);
                    mOwnerLogoIv.setVisibility(View.GONE);
                }
                mViewHomepageTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivity, TribeMainAct.class);
                        intent.putExtra("userId", info.userid);
                        mActivity.startActivity(intent);
                    }
                });
            }
        };

    }

    private void getUserCard(final Ad ad) {
        mActivity.getTaskManager().start(AdTask.getUserCard(ad.user.getUserid())
                .setCallback(new HttpTaskCallback<UserCard>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(UserCard result) {
                        ad.usercard = result;
                        notifyDataSetChanged();
                    }
                }));
    }

    public void setInterface(AdapterInterface listener) {
        mInterface = listener;
    }

    public interface AdapterInterface {
        void onItemClick(Ad ad);

        void toAdCooperate(Ad ad);
    }

    public void closeFloatLayout() {
        for (Ad ad : mList) {
            ad.expand = false;
        }
        notifyDataSetChanged();
    }
}
