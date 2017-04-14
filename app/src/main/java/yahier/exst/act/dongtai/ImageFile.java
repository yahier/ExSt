package yahier.exst.act.dongtai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;

import com.stbl.base.library.task.TaskCallback;
import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.adapter.FolderAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.task.mine.AlbumTask;
import com.stbl.stbl.util.ImageBucket;
import com.stbl.stbl.util.PublicWay;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;

import java.util.ArrayList;

import io.rong.imlib.model.Conversation;

/**
 * 这个类主要是用来进行显示包含图片的文件夹
 */
public class ImageFile extends ThemeActivity {

    private EmptyView mEmptyView;

    private ArrayList<ImageBucket> mList;
    private FolderAdapter folderAdapter;
    GridView grid;
    private Conversation conversation;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugin_camera_image_file);
        setLabel(getString(R.string.common_photo_album));
        Intent it = getIntent();
        conversation = it.getParcelableExtra("conversation");
        initVews();
        PublicWay.activityList.add(this);
        int maxNum = it.getIntExtra("maxNum", -1);
        if (-1 != maxNum) {
            folderAdapter.setMaxNum(maxNum);
        }

        getImageBucketList();
    }

    void initVews() {
        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mEmptyView.setOnRetryListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageBucketList();
            }
        });

        grid = (GridView) findViewById(R.id.fileGridView);
        mList = new ArrayList<>();
        folderAdapter = new FolderAdapter(this, mList, conversation);
        grid.setAdapter(folderAdapter);
        findViewById(R.id.theme_top_banner_left).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                //Bimp.tempSelectBitmap.clear();
                finish();
            }
        });

    }

    private void getImageBucketList() {
        mTaskManager.start(AlbumTask.getImageBucketList()
                .setCallback(new TaskCallback<ArrayList<ImageBucket>>() {
                    @Override
                    public void onError(TaskError e) {
                        ToastUtil.showToast(e.msg);
                        if (folderAdapter.getCount() == 0) {
                            mEmptyView.showRetry();
                        }
                    }

                    @Override
                    public void onSuccess(ArrayList<ImageBucket> result) {
                        if (result.size() == 0) {
                            mEmptyView.showEmpty();
                            mEmptyView.setEmptyText(getString(R.string.no_photo));
                        } else {
                            mEmptyView.hide();
                        }
                        mList.clear();
                        mList.addAll(result);
                        folderAdapter.notifyDataSetChanged();
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PublicWay.activityList.remove(this);
    }
}
