package yahier.exst.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.stbl.stbl.act.mine.PhotoImagePagerAct.AlbumDetailFragment;
import com.stbl.stbl.item.Photo;

/**
 * 展示动态网络图片的PagerAdapter 带进度和缓存
 */
public class PhotoPagerAdapter extends FragmentStatePagerAdapter {

	private List<Photo> listPhoto;

	public PhotoPagerAdapter(FragmentManager fm, List<Photo> listPhoto) {
		super(fm);
		this.listPhoto = listPhoto;
	}

	@Override
	public int getCount() {
		return listPhoto.size();
	}

	@Override
	public Fragment getItem(int position) {
		return AlbumDetailFragment.newInstance(listPhoto.get(position)
				.getMiddleurl());
	}
}
