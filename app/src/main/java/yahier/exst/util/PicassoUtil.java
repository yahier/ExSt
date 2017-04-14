package yahier.exst.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.util.image.GlideCircleTransform;
import com.stbl.stbl.util.image.GlideRoundTransform;
import com.stbl.stbl.widget.RoundImageView;

import java.io.File;

public class PicassoUtil {

    public static void load(Context mContext, String url, ImageView img) {
        if (mContext == null) return;
        load(mContext, url, img, R.drawable.default_square_image);
    }

    public static void load(Context mContext, String url, ImageView img, int imageResId) {
        if (img == null) {
            return;
        }
        if (img instanceof RoundImageView) {
            Glide.with(MyApplication.getContext())
                    .load(url)
                    .centerCrop()
                    .transform(new GlideCircleTransform(MyApplication.getContext()))
                    .placeholder(R.drawable.def_head)
                    .error(R.drawable.def_head)
                    .into(img);
        } else {
            Glide.with(MyApplication.getContext())
                    .load(url)
                    .placeholder(imageResId)
                    .error(imageResId)
                    .into(img);
        }
    }

    public static void loadGroupLogo(Context mContext, String imgUrl, String defaultUrl, ImageView img) {
        if (mContext == null) return;
        if (TextUtils.isEmpty(imgUrl)) {
            Glide.with(mContext)
                    .load(defaultUrl)
                    .centerCrop()
                    .transform(new GlideRoundTransform(MyApplication.getContext(), 6))
                    .placeholder(R.drawable.default_group)
                    .error(R.drawable.default_group)
                    .into(img);
        } else {
            Glide.with(mContext)
                    .load(imgUrl)
                    .centerCrop()
                    .transform(new GlideRoundTransform(MyApplication.getContext(), 6))
                    .placeholder(R.drawable.default_group)
                    .error(R.drawable.default_group)
                    .into(img);
        }
    }

    public static void loadGroupLogo(Context mContext, String imgUrl, int width, int Height, ImageView img) {
        if (mContext == null) return;
        Glide.with(mContext)
                .load(imgUrl)
                .centerCrop()
                .transform(new GlideRoundTransform(MyApplication.getContext(), 6))
                .override(width, Height)
                .placeholder(R.drawable.default_group)
                .error(R.drawable.default_group)
                .into(img);
    }

    /**
     * 正中间剪裁图片
     *
     * @param mContext
     * @param imgUrl
     * @param img
     */
    public static void loadCropCenter(Context mContext, String imgUrl, int width, int Height, ImageView img) {
        Glide.with(MyApplication.getContext())
                .load(imgUrl)
                .centerCrop()
                .override(width, Height)
                .placeholder(R.drawable.default_square_image)
                .error(R.drawable.default_square_image)
                .into(img);
    }

    public static void load(Context mContext, int resid, ImageView img) {
        if (mContext == null) return;
        Glide.with(MyApplication.getContext()).load(resid).into(img);
    }

    public static void load(Context context, File file, ImageView img) {
        Glide.with(MyApplication.getContext())
                .load(file)
                .placeholder(R.drawable.default_square_image)
                .error(R.drawable.default_square_image)
                .into(img);
    }

    public static void loadGoods(Context mContext, String url, ImageView img, int width, int height) {
        if (mContext == null) return;
        Glide.with(MyApplication.getContext())
                .load(url)
                .centerCrop()
                .override(width, height)
                .placeholder(R.drawable.img_goods_default)
                .error(R.drawable.img_goods_default)
                .into(img);


    }

    //加载动态图
    public static void loadStatusesCropCenter(Context mContext, String imgUrl, int width, int Height, ImageView img) {
        Glide.with(MyApplication.getContext())
                .load(imgUrl)
                .centerCrop()
                .placeholder(R.drawable.default_square_image)
                .error(R.drawable.default_square_image)
                .override(width, Height)
                .into(img);
    }

    public static void loadStatuses(Context mContext, String url, ImageView img) {
        Glide.with(MyApplication.getContext())
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.default_square_image)
                .error(R.drawable.default_square_image)
                .into(img);
    }

    public static void loadBanner(Context mContext, String url, ImageView img) {
        Glide.with(MyApplication.getContext())
                .load(url)
                .placeholder(R.drawable.img_banner_default)
                .error(R.drawable.img_banner_default)
                .into(img);
    }

}
