package yahier.exst.act.mine;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.stbl.stbl.R;
import com.stbl.stbl.adapter.PhotoPagerAdapter;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.Photo;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.task.mine.AlbumTask;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.FileUtils;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.DialogFactory;
import com.stbl.stbl.widget.PhotoView;
import com.stbl.stbl.widget.PhotoViewAttacher;
import com.stbl.stbl.widget.ViewPagerFixed;

import java.util.ArrayList;
import java.util.List;

//我的相片点击放大图

public class PhotoImagePagerAct extends BaseActivity implements OnClickListener {
    private static final int COUNT = 15;

    ViewPagerFixed pager;
    PhotoPagerAdapter adapter;
    TextView itemIndex;
    int index;
    int length = 0;
    List<Photo> listPhoto;
    //是否显示进入相册按钮
    boolean isShowGridIcon = false;
    UserItem userItem;

    //页码
    private int mPage = 1;
    //是否请求完成
    private boolean isGetComplete = true;
    //相片总数
    private int mTotalCount = 0;
    //用于优化到了最后一页滑动时不再去请求服务端
    private boolean isLastPage = false;
    //当前页面position
    private int currentPosition = 0;
    //请求失败后最大的重新请求次数，防止停留当前页面无休止重复请求
    private int mErrorRepeatLoad = 200;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    private CirclePageIndicator mPagerIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_pager_act);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        pager = (ViewPagerFixed) findViewById(R.id.item_pager);
//        mPagerIndicator = (CirclePageIndicator) findViewById(R.id.vpi);
        itemIndex = (TextView) findViewById(R.id.item_index);
        findViewById(R.id.findpeople_tview_left).setOnClickListener(this);

        isShowGridIcon = getIntent().getBooleanExtra("isShowGridIcon", false);
        userItem = (UserItem) getIntent().getSerializableExtra("userItem");
        mTotalCount = getIntent().getIntExtra("totalcount", 0);
        if (isShowGridIcon) {
            findViewById(R.id.findpeople_iview_right).setVisibility(View.VISIBLE);
            findViewById(R.id.findpeople_iview_right).setOnClickListener(this);
//            mPagerIndicator.setVisibility(View.VISIBLE);
        }
        // 取intent的值
        index = getIntent().getIntExtra("index", 0);
        listPhoto = (List<Photo>) getIntent().getSerializableExtra("photo");
        if (listPhoto == null) {
            finish();
            return;
        }
        length = mTotalCount == 0 ? listPhoto.size() : mTotalCount;

        adapter = new PhotoPagerAdapter(getSupportFragmentManager(), listPhoto);
        pager.setAdapter(adapter);
//        mPagerIndicator.setViewPager(pager);
        pager.setOffscreenPageLimit(1);
        pager.setCurrentItem(index);
        itemIndex.setText((index + 1) + "/" + length);

//        mPagerIndicator.setOnPageChangeListener(new OnPageChangeListener() {
        pager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                itemIndex.setText((position + 1) + "/" + length);
                //有进入相册
                //张数最后10张
                //上次请求已完成
                if (isShowGridIcon && listPhoto != null && (position + 10) >= listPhoto.size() && isGetComplete) {
                    getPhotoList();
                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    @Override
    protected void setStatusBar() {

    }

    public static class AlbumDetailFragment extends Fragment {
        private static final String IMAGE_DATA_EXTRA = "extra_image_data";
        private String mImageUrl;
        private ProgressBar mProgressBar;
        //这个view是用来解决在progressbar显示的时候不能滑动的
        private View mTouchView;
        private PhotoView mImageView;
        private TextView tvReplay;

        private Dialog mActionSheet;
        private Activity mActivity;

        /**
         * Factory method to generate a new instance of the fragment given an
         * image number.
         *
         * @param imageUrl The image url to load
         * @return A new instance of ImageDetailFragment with imageNum extras
         */
        public static AlbumDetailFragment newInstance(String imageUrl) {
            final AlbumDetailFragment f = new AlbumDetailFragment();

            final Bundle args = new Bundle();
            args.putString(IMAGE_DATA_EXTRA, imageUrl);
            f.setArguments(args);

            return f;
        }

        /**
         * Empty constructor as per the Fragment documentation
         */
        public AlbumDetailFragment() {
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mActivity = activity;
        }

        /**
         * Populate image using a url from extras, use the convenience factory
         * method {@link AlbumDetailFragment#newInstance(String)} to create this
         * fragment.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mImageUrl = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA) : null;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate and locate the main ImageView
            return inflater.inflate(R.layout.item_pager_image, container, false);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            mProgressBar = (ProgressBar) getView().findViewById(R.id.loading);
            mTouchView = getView().findViewById(R.id.v_touch_view);
            mImageView = (PhotoView) getView().findViewById(R.id.image);
            tvReplay = (TextView) getView().findViewById(R.id.tv_replay);

            tvReplay.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvReplay.setVisibility(View.GONE);
                    loadImg();
                }
            });
            ArrayList<String> actionList = new ArrayList<>();
            actionList.add("保存到手机");
            mActionSheet = DialogFactory.createActionSheet(mActivity, actionList, new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mActionSheet.dismiss();
                    FileUtils.saveImage(mImageUrl, System.currentTimeMillis() + "");
                }
            });

            loadImg();
            // Pass clicks on the ImageView to the parent activity to handle
            if (OnClickListener.class.isInstance(getActivity())) {
                mImageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (!mActionSheet.isShowing()) {
                            mActionSheet.show();
                        }
                        return true;
                    }
                });
            }
            /**这个监听时间可以实现单击结束当前activity*/
            mImageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    if (mActivity != null && !mActivity.isFinishing()) {
                        mActivity.finish();
                    }
                }
            });
        }

        public void loadImg() {
            // Use the parent activity to load the image asynchronously into the
            // ImageView (so a single
            // cache can be used over all pages in the ViewPager
            if (PhotoImagePagerAct.class.isInstance(getActivity())) {
                mProgressBar.setVisibility(View.VISIBLE);
                mTouchView.setVisibility(View.VISIBLE);
                mTouchView.setClickable(true);
                mTouchView.setFocusable(true);
                mTouchView.setFocusableInTouchMode(true);
                ImageUtils.loadBitmap(mImageUrl, mImageView, new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String s, Target<Bitmap> target, boolean b) {
                        mProgressBar.setVisibility(View.GONE);
                        mTouchView.setVisibility(View.GONE);
                        tvReplay.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap bitmap, String s, Target<Bitmap> target, boolean b, boolean b1) {
                        mProgressBar.setVisibility(View.GONE);
                        mTouchView.setVisibility(View.GONE);
                        return false;
                    }
                });
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            if (mImageView != null) {
                mImageView.setScaleType(ScaleType.FIT_CENTER);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (mImageView != null) {
                mImageView.setImageDrawable(null);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.findpeople_tview_left:
                finish();
                break;
            case R.id.findpeople_iview_right: {
                Intent intent = null;
                if (SharedToken.getUserId(MyApplication.getContext()).equals(userItem.getUserid() + "")) {
                    intent = new Intent(this, MyAlbumActivity.class);
                } else {
                    intent = new Intent(this, OtherAlbumActivity.class);
                    intent.putExtra(EXTRA.USER_ITEM, userItem);
                }
                startActivity(intent);
            }
            break;
        }
    }

    private void getPhotoList() {
        if (userItem == null || isLastPage) return;
        isGetComplete = false;
        mPage++;
        long userid = userItem.getUserid();
        mTaskManager.start(AlbumTask.getPhotoList(userid, mPage, COUNT)
                .setCallback(new HttpTaskCallback<ArrayList<Photo>>(mActivity) {

                    @Override
                    public void onError(com.stbl.base.library.task.TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                        isGetComplete = true;
                        mPage--;
                        //停留在最后一张，自动重新请求
                        if ((currentPosition + 1) == listPhoto.size()) {
                            if (mErrorRepeatLoad > 0)
                                getPhotoList();
                            mErrorRepeatLoad--;
                        }
                    }

                    @Override
                    public void onSuccess(ArrayList<Photo> result) {
                        if (result.size() == 0) {
                            isLastPage = true;
                        } else {
                            listPhoto.addAll(result);
                            adapter.notifyDataSetChanged();
                        }
                        isGetComplete = true;
                    }
                }));
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.ani_zoom_out);
    }

}
