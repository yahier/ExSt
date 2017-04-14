package yahier.exst.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.util.image.GlideCircleTransform;
import com.stbl.stbl.util.image.GlideRoundTransform;

import java.io.File;
import java.io.InputStream;

public class ImageUtils {

    public static void fastBlur(String url, final ImageView imageView) {
        Glide.with(MyApplication.getContext()).load(url).asBitmap()
                .into(new SimpleTarget<Bitmap>(180, 100) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (bitmap == null) {
                            return;
                        }
                        try {
                            Bitmap bmp = bitmap.copy(Bitmap.Config.RGB_565, true);
                            Bitmap blurBitmap = FastBlurUtil.doBlur(bmp, 8, true);
                            imageView.setImageBitmap(blurBitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 加载图片,同时避免内存溢出
     */
    public static Bitmap decodeBitmapSafety(Resources res, int resId, InputStream inputStream) {
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        InputStream is = inputStream == null ? res.openRawResource(resId) : inputStream;
        int size = 1;
        while (true) {
            try {
                opts.inSampleSize = size;
                bitmap = BitmapFactory.decodeStream(is, null, opts);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                break;
            } catch (OutOfMemoryError error) {
                size *= 2;
                if (size > 32) {
                    break;
                }
            }
        }
        return bitmap;
    }

    public static void loadImage(String url, ImageView view) {
        Glide.with(MyApplication.getContext())
                .load(url)
                .into(view);
    }

    public static void loadSquareImage(String url, ImageView view) {
        Glide.with(MyApplication.getContext())
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.default_square_image)
                .error(R.drawable.default_square_image)
                .into(view);
    }

    public static void loadSquareImage(String url, ImageView view, int size) {
        Glide.with(MyApplication.getContext())
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.default_square_image)
                .error(R.drawable.default_square_image)
                .override(size, size)
                .into(view);
    }

    public static void clearMemoryCache() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Glide.get(MyApplication.getContext()).clearMemory();
        }
    }

    /**
     * 加载头像
     *
     * @param url
     * @param view
     */
    @Deprecated
    public static void loadHead(String url, ImageView view) {
        loadCircleHead(url, view);
    }

    /**
     * 加载正方形图片
     *
     * @param url
     * @param view
     */
    public static void loadIcon(String url, ImageView view) {
        loadSquareImage(url, view);
    }

    public static void loadBanner(String url, ImageView view) {
        Glide.with(MyApplication.getContext())
                .load(url)
                .placeholder(R.drawable.img_banner_default)
                .error(R.drawable.img_banner_default)
                .into(view);
    }

    public static void loadCircleHead(String url, ImageView view) {
        Glide.with(MyApplication.getContext())
                .load(url)
                .centerCrop()
                .transform(new GlideCircleTransform(MyApplication.getContext()))
                .placeholder(R.drawable.def_head)
                .error(R.drawable.def_head)
                .into(view);
    }

    public static void loadCircleHead(String url, ImageView view, int size) {
        Glide.with(MyApplication.getContext())
                .load(url)
                .centerCrop()
                .transform(new GlideCircleTransform(MyApplication.getContext()))
                .placeholder(R.drawable.def_head)
                .error(R.drawable.def_head)
                .override(size, size)
                .into(view);
    }

    public static void loadRoundHead(String url, ImageView view) {
        Glide.with(MyApplication.getContext())
                .load(url)
                .centerCrop()
                .transform(new GlideRoundTransform(MyApplication.getContext(), 4))
                .placeholder(R.drawable.default_round_corner_head)
                .error(R.drawable.default_round_corner_head)
                .into(view);
    }

    public static void loadRoundHead(String url, ImageView view, int size) {
        Glide.with(MyApplication.getContext())
                .load(url)
                .centerCrop()
                .transform(new GlideRoundTransform(MyApplication.getContext(), 4))
                .placeholder(R.drawable.default_round_corner_head)
                .error(R.drawable.default_round_corner_head)
                .override(size, size)
                .into(view);
    }
    public static void loadRoundHead(String url, ImageView view, int size,int radius) {
        Glide.with(MyApplication.getContext())
                .load(url)
                .centerCrop()
                .transform(new GlideRoundTransform(MyApplication.getContext(), radius))
                .placeholder(R.drawable.default_round_corner_head)
                .error(R.drawable.default_round_corner_head)
                .override(size, size)
                .into(view);
    }

    public static void loadBitmap(String url, ImageView view, RequestListener<String, Bitmap> listener) {
        Glide.with(MyApplication.getContext())
                .load(url)
                .asBitmap()
                .listener(listener).into(view);
    }

    public static void loadBitmap(String url, SimpleTarget<Bitmap> target) {
        Glide.with(MyApplication.getContext())
                .load(url)
                .asBitmap()
                .into(target);
    }

    public static void loadCircleHead(File file, ImageView view) {
        Glide.with(MyApplication.getContext())
                .load(file)
                .centerCrop()
                .transform(new GlideCircleTransform(MyApplication.getContext()))
                .placeholder(R.drawable.def_head)
                .error(R.drawable.def_head)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(view);
    }

    public static void loadImage(int resId, ImageView view) {
        Glide.with(MyApplication.getContext())
                .load(resId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(view);
    }

    public static void loadFile(String path, ImageView view) {
        File file = new File(path);
        if (file.exists() && file.length() > 0) {
            Glide.with(MyApplication.getContext())
                    .load(file)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(view);
        }
    }

    public static void loadFile(File file, ImageView view) {
        Glide.with(MyApplication.getContext())
                .load(file)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(view);
    }

    public static void loadFile(int resId, File file, ImageView view) {
        Glide.with(MyApplication.getContext())
                .load(file)
                .asBitmap()
                .placeholder(resId)
                .error(resId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(view);
    }

    public static void loadFile(File file, ImageView view, int size) {
        Glide.with(MyApplication.getContext())
                .load(file)
                .asBitmap()
                .override(size, size)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(view);
    }

}
