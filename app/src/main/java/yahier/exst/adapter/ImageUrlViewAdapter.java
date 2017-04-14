package yahier.exst.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.stbl.stbl.R;
import com.stbl.stbl.item.StatusesPic;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.widget.PhotoView;
import com.stbl.stbl.widget.PhotoViewAttacher;

/**
 * 展示动态网络图片的PagerAdapter 带进度和缓存
 */
public class ImageUrlViewAdapter extends PagerAdapter {

	private LayoutInflater inflater;
	private StatusesPic pics;

	public ImageUrlViewAdapter(StatusesPic pics, Context mContext) {
		inflater = LayoutInflater.from(mContext);
		this.pics = pics;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getCount() {
		if (pics == null)
			return 0;
		else
			return pics.getPics().size();
	}

	@Override
	public Object instantiateItem(ViewGroup view, final int position) {
		View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
		assert imageLayout != null;
		PhotoView imageView = (PhotoView) imageLayout.findViewById(R.id.image);
		final ProgressBar progressBar = (ProgressBar) imageLayout.findViewById(R.id.loading);

		progressBar.setVisibility(View.VISIBLE);
		ImageUtils.loadBitmap(pics.getOriginalpic() + pics.getPics().get(position), imageView, new RequestListener<String, Bitmap>() {
			@Override
			public boolean onException(Exception e, String s, Target<Bitmap> target, boolean b) {
				progressBar.setVisibility(View.GONE);
				return false;
			}

			@Override
			public boolean onResourceReady(Bitmap bitmap, String s, Target<Bitmap> target, boolean b, boolean b1) {
				progressBar.setVisibility(View.GONE);
				if (onPagerItemClick != null) {
					onPagerItemClick.decodeQr(bitmap, position);
				}
				return false;
			}
		});

		imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
			@Override
			public void onPhotoTap(View view, float x, float y) {
				if (onPagerItemClick != null) {
					onPagerItemClick.onPagerItemClick();
				}
			}
		});
		imageView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (onPagerItemClick != null) {
					onPagerItemClick.onLongClick();
				}
				return true;
			}
		});
		view.addView(imageLayout, 0);
		return imageLayout;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	OnPagerItemClickListener onPagerItemClick;

	public void setPagerItemOnClickListener(OnPagerItemClickListener onPagerItemClick) {
		this.onPagerItemClick = onPagerItemClick;
	}

	public interface OnPagerItemClickListener {
		void onPagerItemClick();

		void onLongClick();

		void decodeQr(Bitmap bm, int position);
	}
}
