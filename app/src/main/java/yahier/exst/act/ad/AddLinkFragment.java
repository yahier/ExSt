package yahier.exst.act.ad;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.common.AlbumActivity;
import com.stbl.stbl.common.BaseFragment;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.item.LinkBean;
import com.stbl.stbl.task.mine.LinkTask;
import com.stbl.stbl.util.Bimp;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.FileUtils;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.ImageItem;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.DialogFactory;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/27.
 */

public class AddLinkFragment extends BaseFragment implements View.OnClickListener {

    private ImageView ivAdd;
    private EditText etTitle;
    private EditText etLink;
    private TextView mDoneTv;

    private static final int TAKE_PICTURE = 0x000001;

    Dialog pop;

    private File mPhotoFile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_link, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ivAdd = (ImageView) getView().findViewById(R.id.iv_add);
        etTitle = (EditText) getView().findViewById(R.id.et_title);
        etLink = (EditText) getView().findViewById(R.id.et_link);
        mDoneTv = (TextView) getView().findViewById(R.id.tv_done);

        etTitle.setText((String) (SharedPrefUtils.getFromUserFile(KEY.INPUT_LINK_TITLE, "")));
        etLink.setText((String) (SharedPrefUtils.getFromUserFile(KEY.INPUT_LINK_URL, "")));
        String path = (String) (SharedPrefUtils.getFromUserFile(KEY.ADD_LINK_IMAGE, ""));
        File file = new File(path);
        if (file.exists()) {
            mPhotoFile = file;
            ImageUtils.loadFile(mPhotoFile.getAbsolutePath(), ivAdd);
        }

        getView().findViewById(R.id.tv_cancel).setOnClickListener(this);

        ivAdd.setOnClickListener(this);
        mDoneTv.setOnClickListener(this);
        Bimp.tempSelectBitmap.clear();

        ArrayList<String> actionList = new ArrayList<>();
        actionList.add(getString(R.string.me_choose_from_phone));
        actionList.add(getString(R.string.me_photograph));
        pop = DialogFactory.createActionSheet(mActivity, actionList, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                switch (position) {
                    case 0:
                        Bimp.tempSelectBitmap.clear();
                        Intent intent = new Intent(mActivity, AlbumActivity.class);
                        intent.putExtra("MAX_NUM", 1);
                        startActivity(intent);
                        break;
                    case 1:
                        photo();
                        break;
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Bimp.tempSelectBitmap.size() > 0) {
            mPhotoFile = Bimp.tempSelectBitmap.get(0).file;
            ImageUtils.loadFile(mPhotoFile, ivAdd);
        }
    }

    private void addLink(String linktitle, String linkurl) {
        final LoadingDialog dialog = new LoadingDialog(mActivity);
        dialog.setMessage("添加链接中");
        dialog.show();
        mTaskManager.start(LinkTask.addLink(linktitle, linkurl, mPhotoFile)
                .setCallback(new HttpTaskCallback<LinkBean>(mActivity) {

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(LinkBean result) {
                        Intent intent = new Intent();
                        intent.putExtra(KEY.TYPE, PublishShoppingLinkActivity.TYPE_ADD_LINK);
                        intent.putExtra(KEY.LINK_BEAN, result);
                        mActivity.setResult(Activity.RESULT_OK, intent);
                        mActivity.onBackPressed();
                        SharedPrefUtils.putToUserFile(KEY.INPUT_LINK_TITLE, "");
                        SharedPrefUtils.putToUserFile(KEY.INPUT_LINK_URL, "");
                        SharedPrefUtils.putToUserFile(KEY.ADD_LINK_IMAGE, "");
                    }
                }));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.logE("onActivityResult");
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == mActivity.RESULT_OK) {
                    FileUtils.scanFile(mPhotoFile);
                    ImageItem imageItem = new ImageItem();
                    imageItem.file = mPhotoFile;
                    imageItem.imagePath = mPhotoFile.getAbsolutePath();
                    Bimp.tempSelectBitmap.clear();
                    Bimp.tempSelectBitmap.add(imageItem);
                }
                break;
        }
    }

    @Override
    public void onClick(final View arg0) {
        arg0.setEnabled(false);
        arg0.postDelayed(new Runnable() {
            @Override
            public void run() {
                arg0.setEnabled(true);
            }
        }, Config.interClickTime);
        hideInputSoft();
        switch (arg0.getId()) {
            case R.id.iv_add:
                pop.show();
                break;
            case R.id.tv_done:
                String linktitle = etTitle.getText().toString().trim();
                String linkurl = etLink.getText().toString().trim();
                if (mPhotoFile == null) {
                    ToastUtil.showToast(getString(R.string.me_please_add_picture));
                    return;
                }
                if (TextUtils.isEmpty(linktitle)) {
                    ToastUtil.showToast(getString(R.string.me_input_title));
                    return;
                }
                if (TextUtils.isEmpty(linkurl)) {
                    ToastUtil.showToast(getString(R.string.me_input_link));
                    return;
                }
                addLink(linktitle, linkurl);
                break;
            case R.id.tv_cancel:
                mActivity.onBackPressed();
                break;
            default:
                break;
        }

    }

    private void hideInputSoft() {
        ((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void photo() {
        mPhotoFile = new File(FileUtils.getAppDir(), System.currentTimeMillis() + ".jpg");
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    public void saveInputData() {
        Bimp.tempSelectBitmap.clear();
        String linktitle = etTitle.getText().toString().trim();
        String linkurl = etLink.getText().toString().trim();
        SharedPrefUtils.putToUserFile(KEY.INPUT_LINK_TITLE, linktitle);
        SharedPrefUtils.putToUserFile(KEY.INPUT_LINK_URL, linkurl);
        if (mPhotoFile != null && mPhotoFile.exists()) {
            SharedPrefUtils.putToUserFile(KEY.ADD_LINK_IMAGE, mPhotoFile.getAbsolutePath());
        }
    }

}
