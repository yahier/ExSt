package yahier.exst.act.mine;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.common.AlbumActivity;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.item.LinkBean;
import com.stbl.stbl.task.mine.LinkTask;
import com.stbl.stbl.util.Bimp;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.FileUtils;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.ImageItem;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.DialogFactory;

import java.io.File;
import java.util.ArrayList;

public class MineAddLinkAct extends ThemeActivity implements OnClickListener {

    private ImageView ivAdd;
    private TextView tvAddTips;
    private EditText etTitle;
    private EditText etLink;
    private Button mDoneBtn;

    private static final int TAKE_PICTURE = 0x000001;

    Dialog pop;

    private File mPhotoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_link);
        setLabel(R.string.me_add_link);

        ivAdd = (ImageView) findViewById(R.id.iv_add);
        tvAddTips = (TextView) findViewById(R.id.tv_add_tips);
        etTitle = (EditText) findViewById(R.id.et_title);
        etLink = (EditText) findViewById(R.id.et_link);
        mDoneBtn = (Button) findViewById(R.id.btn_finish);

        ivAdd.setOnClickListener(this);
        tvAddTips.setOnClickListener(this);
        mDoneBtn.setOnClickListener(this);
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
            tvAddTips.setText(R.string.me_change_picture);
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
                        intent.putExtra("linkbean", result);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.logE("onActivityResult");
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
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
            case R.id.tv_add_tips:
            case R.id.iv_add:
                pop.show();
                break;
            case R.id.btn_finish:
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bimp.tempSelectBitmap.clear();
    }
}
