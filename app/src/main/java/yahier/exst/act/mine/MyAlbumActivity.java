package yahier.exst.act.mine;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.mine.MyAlbumAdapter;
import com.stbl.stbl.common.AlbumActivity;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.AlertDialog;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.item.Photo;
import com.stbl.stbl.task.mine.AlbumTask;
import com.stbl.stbl.util.Bimp;
import com.stbl.stbl.util.FileUtils;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.ImageItem;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.DialogFactory;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.RefreshGridView;

import java.io.File;
import java.util.ArrayList;

public class MyAlbumActivity extends ThemeActivity {

    private static final int COUNT = 15;

    private static final int TAKE_PICTURE = 0x000001;
    private static final int TAKE_ALBUM = 0x000002;

    private RefreshGridView mRefreshGridView;
    private ArrayList<Photo> mDataList;
    private MyAlbumAdapter mAdapter;

    private EmptyView mEmptyView;
    private Button mAddImgBtn;

    private long mUserId;

    private int mPage = 1;

    private boolean mIsEditMode;

    private Dialog mActionSheet;

    private File mPhotoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_album);
        setLabel(getString(R.string.me_my_album));

        mUserId = Long.parseLong(SharedToken.getUserId(this));

        initView();

        getPhotoList();
    }

    private void initView() {
        mRefreshGridView = (RefreshGridView) findViewById(R.id.refresh_grid_view);
        mRefreshGridView.setNumColumns(3);
        mRefreshGridView.setHorizontalSpacing(3);
        mRefreshGridView.setVerticalSpacing(3);
        mDataList = new ArrayList<>();
        mAdapter = new MyAlbumAdapter(mDataList);
        mRefreshGridView.setAdapter(mAdapter);

        mRefreshGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Photo photo = mDataList.get(position);
                if (mIsEditMode) {
                    mAdapter.toggleChecked(photo);
                    mAddImgBtn.setText(String.format(getString(R.string.me_delete_d_photo), mAdapter.getSelectedSize()));
                } else {
                    Intent intent = new Intent(MyAlbumActivity.this, PhotoImagePagerAct.class);
                    intent.putExtra("index", position);
                    intent.putParcelableArrayListExtra("photo", mDataList);
                    startActivity(intent);
                    overridePendingTransition(R.anim.ani_zoom_in, 0);
                }
            }
        });

        mRefreshGridView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                getPhotoList();
            }
        });
        mRefreshGridView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mPage++;
                getPhotoList();
            }
        });

        mRefreshGridView.getViewTreeObserver().addOnGlobalLayoutListener(mLayoutListener);

        setRightText(getString(R.string.me_edit), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.getCount() == 0) {
                    ToastUtil.showToast(R.string.me_no_photo);
                    return;
                }
                toggleEditMode();
            }
        });

        mAddImgBtn = (Button) findViewById(R.id.btn_add_img);
        mAddImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsEditMode) {
                    beforeDelete();
                } else {
                    mActionSheet.show();
                }
            }
        });

        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mEmptyView.setRetryText(getString(R.string.me_pull_to_retry));

        ArrayList<String> actionList = new ArrayList<>();
        actionList.add(getString(R.string.me_choose_from_phone));
        actionList.add(getString(R.string.me_photograph));
        mActionSheet = DialogFactory.createActionSheet(this, actionList, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mActionSheet.dismiss();
                Bimp.tempSelectBitmap.clear();
                switch (position) {
                    case 0: {
                        Intent intent = new Intent(MyAlbumActivity.this, AlbumActivity.class);
                        startActivityForResult(intent, TAKE_ALBUM);
                    }
                    break;
                    case 1: {
                        if (Bimp.tempSelectBitmap.size() >= 9) {
                            ToastUtil.showToast(R.string.me_most_add_9_photo);
                            return;
                        }
                        mPhotoFile = new File(FileUtils.getAppDir(), System.currentTimeMillis() + ".jpg");
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                        startActivityForResult(intent, TAKE_PICTURE);
                    }
                    break;
                }
            }
        });
    }

    private void toggleEditMode() {
        mIsEditMode = !mIsEditMode;
        mAdapter.setIsEditMode(mIsEditMode);
        mRefreshGridView.setRefreshEnabled(!mIsEditMode);
        mRefreshGridView.setLoadMoreEnabled(!mIsEditMode);
        if (mIsEditMode) {
            setRightText(getString(R.string.me_cancel_edit));
            mAddImgBtn.setText(String.format(getString(R.string.me_delete_d_photo), 0));
        } else {
            setRightText(getString(R.string.me_edit));
            mAddImgBtn.setText(R.string.me_add_image);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.logE("onActivityResult");
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    FileUtils.scanFile(mPhotoFile);
                    ImageItem takePhoto = new ImageItem();
                    takePhoto.file = mPhotoFile;
                    takePhoto.setImagePath(mPhotoFile.getAbsolutePath());
                    Bimp.tempSelectBitmap.clear();
                    Bimp.tempSelectBitmap.add(takePhoto);
                    upload();
                }
                break;
            case TAKE_ALBUM:
                if (resultCode == RESULT_OK && Bimp.tempSelectBitmap.size() > 0) {
                    upload();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bimp.tempSelectBitmap.clear();
    }

    private void getPhotoList() {
        mTaskManager.start(AlbumTask.getPhotoList(mUserId, mPage, COUNT)
                .setCallback(new HttpTaskCallback<ArrayList<Photo>>(mActivity) {

                    @Override
                    public void onFinish() {
                        mRefreshGridView.reset();
                    }

                    @Override
                    public void onError(com.stbl.base.library.task.TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                        if (mPage == 1 && mAdapter.getCount() == 0) {
                            mEmptyView.showRetry();
                        }
                    }

                    @Override
                    public void onSuccess(ArrayList<Photo> result) {
                        if (mPage == 1) {
                            if (result.size() == 0) {
                                mEmptyView.showEmpty();
                            } else {
                                mEmptyView.hide();
                            }
                            mDataList.clear();
                        }
                        mDataList.addAll(result);
                        mAdapter.notifyDataSetChanged();
                    }
                }));
    }

    private void beforeDelete() {
        ArrayList<Photo> selectedList = mAdapter.getSelectedList();
        if (selectedList.size() == 0) {
            ToastUtil.showToast(R.string.me_not_yet_choose_photo);
            return;
        }
        showConfirmDeleteDialog();
    }

    private void showConfirmDeleteDialog() {
        AlertDialog dialog = AlertDialog.create(mActivity, getString(R.string.me_is_confirm_delete),
                new AlertDialog.AlertDialogInterface() {
                    @Override
                    public void onNegative() {

                    }

                    @Override
                    public void onPositive() {
                        delete();
                    }
                });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void delete() {
        ArrayList<Photo> selectedList = mAdapter.getSelectedList();
        StringBuilder builder = new StringBuilder();
        for (Photo photo : selectedList) {
            builder.append(photo.getPhotoid());
            builder.append("#");
        }
        String photoids = builder.toString().substring(0, builder.length() - 1);

        final LoadingDialog dialog = new LoadingDialog(mActivity);
        dialog.setMessage(getString(R.string.me_deleting));
        dialog.show();
        mTaskManager.start(AlbumTask.delete(photoids)
                .setCallback(new HttpTaskCallback<Integer>(mActivity) {

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(com.stbl.base.library.task.TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(Integer result) {
                        ToastUtil.showToast(R.string.me_delete_success);
                        mAdapter.deleteSelectedItem();
                        if (mAdapter.getCount() == 0) {
                            toggleEditMode();
                            mEmptyView.showLoading();
                            mPage = 1;
                            getPhotoList();
                        }
                    }
                }));
    }

    private void upload() {
        final LoadingDialog dialog = new LoadingDialog(mActivity);
        dialog.setMessage(getString(R.string.me_uploading));
        dialog.show();
        mTaskManager.start(AlbumTask.upload()
                .setCallback(new HttpTaskCallback<Integer>(mActivity) {

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(com.stbl.base.library.task.TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onMessage(int arg1, int arg2, Object obj) {
                        dialog.setMessage(obj.toString());
                    }

                    @Override
                    public void onSuccess(Integer result) {
                        ToastUtil.showToast(R.string.me_upload_success);
                        mRefreshGridView.getTargetView().smoothScrollToPositionFromTop(0, 0);
                        mPage = 1;
                        getPhotoList();
                    }
                }));
    }

    private ViewTreeObserver.OnGlobalLayoutListener mLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int imageSpacing = getResources().getDimensionPixelSize(
                    R.dimen.dp_3);
            int columnWidth = (mRefreshGridView.getWidth() - imageSpacing * 2) / 3;
            mAdapter.setItemHeight(columnWidth);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mRefreshGridView.getViewTreeObserver().removeOnGlobalLayoutListener(
                        this);
            } else {
                mRefreshGridView.getViewTreeObserver().removeGlobalOnLayoutListener(
                        this);
            }
        }
    };

}
