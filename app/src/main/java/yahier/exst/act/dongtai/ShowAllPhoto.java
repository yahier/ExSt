package yahier.exst.act.dongtai;

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

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.AlbumGridViewAdapter;
import com.stbl.stbl.common.AlbumActivity;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.util.Bimp;
import com.stbl.stbl.util.ImageItem;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.PublicWay;
import com.stbl.stbl.util.ToastUtil;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.message.ImageMessage;

//import com.master.util.Res;

/**
 * 这个是显示一个文件夹里面的所有图片时的界面
 *
 * @author king
 * @version 2014年10月18日 下午11:49:10
 * @QQ:595163260
 */
public class ShowAllPhoto extends ThemeActivity implements OnClickListener {
    private GridView gridView;

    private AlbumGridViewAdapter gridImageAdapter;
    // 完成按钮
    private Button okButton;
    private Intent intent;
    private Context mContext;
    public static ArrayList<ImageItem> dataList = new ArrayList<ImageItem>();
    private int maxNum;
    private Conversation conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugin_camera_show_all_photo);
        setLabel(R.string.choose_picture);
        findViewById(R.id.theme_top_banner_left).setOnClickListener(this);
        mContext = this;
        okButton = (Button) findViewById(R.id.showallphoto_ok_button);
        this.intent = getIntent();
        String folderName = intent.getStringExtra("folderName");
        if (folderName.length() > 8) {
            folderName = folderName.substring(0, 9) + "...";
        }
        setLabel(folderName);
        maxNum = intent.getIntExtra("maxNum", PublicWay.MAX_NUM);
        conversation = intent.getParcelableExtra("conversation");
        init();
        initListener();
        isShowOkBt();

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            gridImageAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void init() {
        IntentFilter filter = new IntentFilter("data.broadcast.action");
        registerReceiver(broadcastReceiver, filter);

        gridView = (GridView) findViewById(R.id.showallphoto_myGrid);
        gridImageAdapter = new AlbumGridViewAdapter(this, dataList, Bimp.tempSelectBitmap);
        gridView.setAdapter(gridImageAdapter);
        okButton = (Button) findViewById(R.id.showallphoto_ok_button);
    }

    private void initListener() {
        gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {
            public void onItemClick(final ToggleButton toggleButton, int position, boolean isChecked, View button) {
                if (Bimp.tempSelectBitmap.size() >= maxNum && isChecked) {
                    button.setVisibility(View.GONE);
                    toggleButton.setChecked(false);
                    ToastUtil.showToast(getString(R.string.common_more_can_choise) + maxNum + getString(R.string.common_zhang));
                    return;
                }

                if (isChecked) {
                    button.setVisibility(View.VISIBLE);
                    Bimp.tempSelectBitmap.add(dataList.get(position));
                    okButton.setText(getString(R.string.mall_done) + "(" + Bimp.tempSelectBitmap.size() + "/" + maxNum + ")");
                } else {
                    button.setVisibility(View.GONE);
                    //Bimp.tempSelectBitmap.remove(dataList.get(position));
                    removePhoto(dataList.get(position));
                    okButton.setText(getString(R.string.mall_done) + "(" + Bimp.tempSelectBitmap.size() + "/" + maxNum + ")");
                }
                isShowOkBt();
            }
        });

        okButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                okButton.setClickable(false);
                if (conversation != null) {
                    Message message = Message.obtain();
                    handler.sendMessage(message);
                }
                //intent.setClass(mContext, DongtaiPulish.class);
                //startActivity(intent);

                for (int i = 0; i < PublicWay.activityList.size(); i++) {
                    LogUtil.logE("finish." + i);
                    Activity activity = PublicWay.activityList.get(i);
                    if (activity instanceof AlbumActivity) {
                        activity.setResult(RESULT_OK);
                    }
                    activity.finish();
                }
                PublicWay.activityList.clear();

                finish();
            }
        });

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

    //新加
    int intevalTime = 300;
    int indexSending = 0;
    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message message) {
            LogUtil.logE("handler " + indexSending);
            if (indexSending == Bimp.tempSelectBitmap.size()) {
                // WaitingDialog.dismiss();
                Bimp.tempSelectBitmap.clear();//add new
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


    public void isShowOkBt() {
        if (Bimp.tempSelectBitmap.size() > 0) {
            okButton.setText(getString(R.string.completed) + "(" + Bimp.tempSelectBitmap.size() + "/" + maxNum + ")");
            okButton.setPressed(true);
            okButton.setClickable(true);
            okButton.setTextColor(Color.WHITE);
        } else {
            okButton.setText(getString(R.string.completed) + "(" + Bimp.tempSelectBitmap.size() + "/" + maxNum + ")");
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
            case R.id.theme_top_banner_left:
//			if (Bimp.tempSelectBitmap.size() > 0) {
//				DialogFactory.createConfirmDialog(this, "提示", "确定放弃吗？", new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						Bimp.tempSelectBitmap.clear();
//						finish();
//					}
//				}).show();
//			}else {
//				finish();
//			}
                finish();
                break;
        }
    }

//	@Override
//	public void onBackPressed() {
//		if (Bimp.tempSelectBitmap.size() > 0) {
//			DialogFactory.createConfirmDialog(this, "提示", "确定放弃吗？", new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Bimp.tempSelectBitmap.clear();
//					finish();
//				}
//			}).show();
//		}else {
//			super.onBackPressed();
//		}
//	}

}
