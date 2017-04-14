package yahier.exst.common;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ToggleButton;

import com.stbl.base.library.task.TaskCallback;
import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.ImageFile;
import com.stbl.stbl.adapter.AlbumGridViewAdapter;
import com.stbl.stbl.task.mine.AlbumTask;
import com.stbl.stbl.util.Bimp;
import com.stbl.stbl.util.ImageItem;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.PublicWay;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.message.ImageMessage;

/**
 *
 */
public class AlbumActivity extends ThemeActivity implements OnClickListener {

    private EmptyView mEmptyView;

    // 显示手机里的所有图片的列表控件
    private GridView gridView;

    // gridView的adapter
    private AlbumGridViewAdapter gridImageAdapter;
    // 完成按钮
    private Button okButton;

    private ArrayList<ImageItem> dataList;

    public int maxNum;
    private Conversation conversation;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugin_camera_album);
        setLabel(getString(R.string.common_choise_photo));
        setRightText(getString(R.string.common_photo_album), this);
        findViewById(R.id.theme_top_banner_left).setOnClickListener(this);
        // 注册一个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消选中的图片仍处于选中状态
        IntentFilter filter = new IntentFilter("data.broadcast.action");
        registerReceiver(broadcastReceiver, filter);

        init();
        initListener();
        getImageItemList();

        PublicWay.activityList.add(this);
        Intent it = getIntent();
        maxNum = it.getIntExtra("MAX_NUM", PublicWay.MAX_NUM);
        isShowOkBt();
        conversation = it.getParcelableExtra("conversation");
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        PublicWay.activityList.remove(this);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            gridImageAdapter.notifyDataSetChanged();
        }
    };


    // 完成按钮的监听
    private class AlbumSendListener implements OnClickListener {
        public void onClick(View v) {
            if (conversation != null) {
                Message message = Message.obtain();
                handler.sendMessage(message);
            }
            Intent intent = new Intent();
//			intent.putParcelableArrayListExtra("url_list", Bimp.tempSelectBitmap);
            setResult(Activity.RESULT_OK, intent);
            finish();

        }

    }


    int intevalTime = 300;
    int indexSending = 0;
    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message message) {
            LogUtil.logE("handler " + indexSending);
            if (indexSending == Bimp.tempSelectBitmap.size()) {
                // WaitingDialog.dismiss();
                Bimp.tempSelectBitmap.clear();
                return true;
            }
            testSend();
            indexSending++;
            handler.sendEmptyMessageDelayed(1, intevalTime);
            return false;
        }
    });

    /**
     * 测试发送多选图.还需要加上延时
     */
    void testSend() {
        ImageItem item = Bimp.tempSelectBitmap.get(indexSending);
        String path = "file://" + item.getImagePath();
        Uri mUri = Uri.parse(path);
        final ImageMessage content = ImageMessage.obtain(mUri, mUri);
        if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null)
            RongIM.getInstance().getRongIMClient().sendImageMessage(conversation.getConversationType(), conversation.getTargetId(), content, null, null, null);
    }


    // 初始化，给一些对象赋值
    private void init() {
        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mEmptyView.setOnRetryListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageItemList();
            }
        });

        gridView = (GridView) findViewById(R.id.myGrid);
        dataList = new ArrayList<>();
        gridImageAdapter = new AlbumGridViewAdapter(this, dataList, Bimp.tempSelectBitmap);
        gridView.setAdapter(gridImageAdapter);

        okButton = (Button) findViewById(R.id.ok_button);
    }

    private void initListener() {
        gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(final ToggleButton toggleButton, int position, boolean isChecked, View chooseBt) {
                if (Bimp.tempSelectBitmap.size() >= maxNum) {
                    toggleButton.setChecked(false);
                    chooseBt.setVisibility(View.GONE);
                    if (!removeOneData(dataList.get(position))) {
                        ToastUtil.showToast(AlbumActivity.this, getString(R.string.common_more_can_choise) + maxNum + getString(R.string.common_zhang));
                    }
                    return;
                }
                if (isChecked) {
                    chooseBt.setVisibility(View.VISIBLE);
                    Bimp.tempSelectBitmap.add(dataList.get(position));
                    okButton.setText(getString(R.string.finish) + "(" + Bimp.tempSelectBitmap.size() + "/" + maxNum + ")");
                } else {
                    //Bimp.tempSelectBitmap.remove(dataList.get(position));
                    removePhoto(dataList.get(position));
                    chooseBt.setVisibility(View.GONE);
                    okButton.setText(getString(R.string.finish) + "(" + Bimp.tempSelectBitmap.size() + "/" + maxNum + ")");
                }
                isShowOkBt();
            }
        });

        okButton.setOnClickListener(new AlbumSendListener());

    }

    private void getImageItemList() {
        mTaskManager.start(AlbumTask.getImageItemList()
                .setCallback(new TaskCallback<ArrayList<ImageItem>>() {
                    @Override
                    public void onError(TaskError e) {
                        ToastUtil.showToast(e.msg);
                        if (gridImageAdapter.getCount() == 0) {
                            mEmptyView.showRetry();
                        }
                    }

                    @Override
                    public void onSuccess(ArrayList<ImageItem> result) {
                        if (result.size() == 0) {
                            mEmptyView.showEmpty();
                            mEmptyView.setEmptyText(getString(R.string.no_photo));
                        } else {
                            mEmptyView.hide();
                        }
                        dataList.clear();
                        dataList.addAll(result);
                        gridImageAdapter.notifyDataSetChanged();
                    }
                }));
    }

    private void removePhoto(ImageItem item) {
        if (item == null || Bimp.tempSelectBitmap.size() == 0) {
            return;
        }
        ImageItem target = null;
        for (ImageItem i : Bimp.tempSelectBitmap) {
            if (i.getImagePath().equals(item.getImagePath())) {
                target = i;
                break;
            }
        }
        if (target != null) {
            Bimp.tempSelectBitmap.remove(target);
        }
    }

    private boolean removeOneData(ImageItem imageItem) {
        if (Bimp.tempSelectBitmap.contains(imageItem)) {
            Bimp.tempSelectBitmap.remove(imageItem);
            okButton.setText(getString(R.string.finish) + "(" + Bimp.tempSelectBitmap.size() + "/" + maxNum + ")");
            return true;
        } else {
            for (ImageItem item : Bimp.tempSelectBitmap) {
                if (item.imagePath.equals(imageItem.imagePath)) {
                    Bimp.tempSelectBitmap.remove(item);
                    okButton.setText(getString(R.string.finish) + "(" + Bimp.tempSelectBitmap.size() + "/" + maxNum + ")");
                    return true;
                }
            }
        }
        return false;
    }

    public void isShowOkBt() {
        if (Bimp.tempSelectBitmap.size() > 0) {
            okButton.setText(getString(R.string.finish) + "(" + Bimp.tempSelectBitmap.size() + "/" + maxNum + ")");
            okButton.setPressed(true);
            okButton.setClickable(true);
            okButton.setTextColor(Color.WHITE);
        } else {
            okButton.setText(getString(R.string.finish) + "(" + Bimp.tempSelectBitmap.size() + "/" + maxNum + ")");
            okButton.setPressed(false);
            okButton.setClickable(false);
            okButton.setTextColor(Color.parseColor("#E1E0DE"));
        }
    }


    @Override
    protected void onRestart() {
        isShowOkBt();
        super.onRestart();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.theme_top_tv_right:
                Intent intent = new Intent(AlbumActivity.this, ImageFile.class);
                intent.putExtra("maxNum", maxNum);
                intent.putExtra("conversation", conversation);
                startActivity(intent);
                break;
            case R.id.theme_top_banner_left:
                if (conversation != null)
                    Bimp.tempSelectBitmap.clear();
                finish();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (conversation != null)
            Bimp.tempSelectBitmap.clear();
        finish();
    }


}
