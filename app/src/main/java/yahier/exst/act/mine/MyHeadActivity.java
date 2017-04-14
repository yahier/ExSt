package yahier.exst.act.mine;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.stbl.stbl.R;
import com.stbl.stbl.act.im.RongDB;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.ImgUrl;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.IMAccount;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.mine.UserTask;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.CropPhotoUtils;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.FileUtils;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.DialogFactory;
import com.stbl.stbl.widget.PhotoView;

import java.io.File;
import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by tnitf on 2016/4/28.
 */
public class MyHeadActivity extends ThemeActivity {

    private PhotoView mPhotoView;
    private ProgressBar mProgressBar;
    private Dialog mActionSheet;

    private String mHeadUrl;

    private Dialog mLoadingDialog;

    private boolean mIsDestroy;

    private Dialog mSaveDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_head);

        setLabel("个人头像");

        mHeadUrl = getIntent().getStringExtra(EXTRA.HEAD_URL);

        initView();

        LocalBroadcastHelper.getInstance().register(mReceiver, ACTION.LOAD_HEAD);
    }

    private void initView() {
        mPhotoView = (PhotoView) findViewById(R.id.photo_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        ArrayList<String> actionList = new ArrayList<>();
        actionList.add("从手机选择");
        actionList.add("拍照");
        mActionSheet = DialogFactory.createActionSheet(this, actionList, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mActionSheet.dismiss();
                CropPhotoUtils.select(MyHeadActivity.this, position);
            }
        });

        mLoadingDialog = DialogFactory.createLoadingDialog(this, "图片正在上传");

        ArrayList<String> actionList2 = new ArrayList<>();
        actionList2.add("保存到手机");
        mSaveDialog = DialogFactory.createActionSheet(this, actionList2, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSaveDialog.dismiss();
                if (!TextUtils.isEmpty(mHeadUrl)) {
                    FileUtils.saveImage(mHeadUrl, "my_head");
                }
            }
        });


        findViewById(R.id.btn_modify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionSheet.show();
            }
        });

        mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!mSaveDialog.isShowing() && !TextUtils.isEmpty(mHeadUrl)) {
                    mSaveDialog.show();
                }
                return true;
            }
        });

        if (!TextUtils.isEmpty(mHeadUrl)) {
            loadHead();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
        LocalBroadcastHelper.getInstance().unregister(mReceiver);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION.LOAD_HEAD:
                    mHeadUrl = intent.getStringExtra(EXTRA.HEAD_URL);
                    if (!TextUtils.isEmpty(mHeadUrl)) {
                        loadHead();
                    }
                    break;
            }
        }
    };

    private void loadHead() {
        ImageUtils.loadImage(mHeadUrl, mPhotoView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        File file = CropPhotoUtils.onActivityResult(this, requestCode, resultCode, data);
        if (file != null) {
            uploadHead(file.getAbsolutePath());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadHead(String path) {
        mLoadingDialog.show();
        UserTask.uploadHead(path).setCallback(this, mUploadHeadCallback).start();
    }

    private SimpleTask.Callback<ImgUrl> mUploadHeadCallback = new SimpleTask.Callback<ImgUrl>() {
        @Override
        public void onError(TaskError e) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(ImgUrl result) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast("头像更新成功");
            mHeadUrl = result.getLarge();
            mPhotoView.setImageDrawable(null);
            mProgressBar.setVisibility(View.VISIBLE);
            loadHead();
            afterUploadSuccess(result);
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private void afterUploadSuccess(ImgUrl result) {
        UserItem userItem = app.getUserItem();
        userItem.setImgurl(result.getSmall());
        userItem.setImgminurl(result.getSmall());
        userItem.setImgmiddleurl(result.getLarge());
        userItem.setImgurloriginal(result.getOri());
        SharedUser.putUserValue(userItem);
        //更新im头像
        RongDB rongdb = new RongDB(this);
        rongdb.update(new IMAccount(RongDB.typeUser, SharedToken.getUserId(this), userItem.getNickname(), result.getSmall(), userItem.getCertification(), ""));
        //再来刷新
        IMAccount account = rongdb.get(RongDB.typeUser, SharedToken.getUserId(this));
        if (account != null) {
            Uri uri = Uri.parse(account.getImgurl());
            UserInfo user = new UserInfo(account.getUserid(), account.getNickname(), uri);
            RongIM.getInstance().refreshUserInfoCache(user);
        }
    }
}
