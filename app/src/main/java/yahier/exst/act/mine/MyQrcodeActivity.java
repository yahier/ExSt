package yahier.exst.act.mine;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.stbl.stbl.R;
import com.stbl.stbl.act.im.AddFriendFromPhoneAct;
import com.stbl.stbl.barcoe.CaptureActivity;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.ShareDialog;
import com.stbl.stbl.item.ImgUrl;
import com.stbl.stbl.item.QRCodeInfo;
import com.stbl.stbl.item.ShareItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.mine.UserTask;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.ConfigControl;
import com.stbl.stbl.util.FileUtils;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.DialogFactory;
import com.stbl.stbl.widget.EmptyView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyQrcodeActivity extends ThemeActivity {

    private EmptyView mEmptyView;
    private ImageView imgQRCode;
    private Dialog mActionSheet;

    private boolean mIsDestroy;

    private TextView mNickTv;
    private ImageView mHeadIv;
    private TextView mInviteCodeTv;
    private View linBg;
    String iconPathTemp = Config.localFilePath + "QRCode";
    String iconPath = Config.localFilePath + SharedUser.getUserNick() + "_QRCode.jpg";
    boolean isFromMine = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qrcode);
        initView();
        getQrcodeUrl();
        isFromMine = getIntent().getBooleanExtra("isFromMine", true);
        if (isFromMine) {
            setLabel(getString(R.string.me_my_qrcode));
        } else {
            setLabel(getString(R.string.me_i_want_to_accept_student));
        }

    }

    private void initView() {
        setLabel(getString(R.string.me_my_qrcode));
        setRightImage(R.drawable.tribe_tiny_info_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionSheet.show();
            }
        });
//        setRightText(getString(R.string.me_more), new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mActionSheet.show();
//            }
//        });

        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mEmptyView.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQrcodeUrl();
            }
        });

        imgQRCode = (ImageView) findViewById(R.id.imgQRCode);

        mHeadIv = (ImageView) findViewById(R.id.iv_head);
        mNickTv = (TextView) findViewById(R.id.tv_nick);
        mInviteCodeTv = (TextView) findViewById(R.id.tv_invite_code);


        ArrayList<String> actionList = new ArrayList<>();
        //actionList.add(getString(R.string.me_share));
        actionList.add("保存为图片");
        mActionSheet = DialogFactory.createActionSheet(this, actionList, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mActionSheet.dismiss();
                switch (position) {
                    //share
                    case 0:
                        //Intent intent = new Intent(MyQrcodeActivity.this, CaptureActivity.class);
                        //startActivity(intent);
                        saveImage();
                        break;
                    case 1:
                        break;
                }
            }
        });
        linBg = findViewById(R.id.linBg);
    }


    private void showQRCode(String content) {
        try {
            Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.shifu_in_qrcode);
            createQRImage(content, 600, 600, mBitmap, iconPathTemp);
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Bitmap mQRCodeBitmap = BitmapFactory.decodeFile(iconPathTemp);
                    imgQRCode.setImageBitmap(mQRCodeBitmap);
                }
            }, 200);


        } catch (Exception e) {

        }

    }


    public Bitmap createViewBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
    }

    QRCodeInfo data;

    private void saveImage() {
        if (data != null) {
            try {
                FileUtils.saveBitmap(new File(iconPath), createViewBitmap(linBg));
                //通知media扫描
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(new File(Config.localFilePath));
                intent.setData(uri);
                sendBroadcast(intent);
                ToastUtil.showToast(R.string.me_save_success);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ToastUtil.showToast(R.string.me_not_yet_load_qrcode);
        }
    }

    private void share(){
        if(data==null){
            ToastUtil.showToast(R.string.me_not_yet_load_qrcode);
            return;
        }
        ShareItem shareItem = new ShareItem();
        String content = getString(R.string.me_i_wait_for_you_at_stbl);
        String title = getString(R.string.me_invite_you_join_in_stbl);
        if (!TextUtils.isEmpty(data.getUserinfo().getNickname())){
//                    content = getString(R.string.me_hi_i_am) + userItem.getNickname() + "，" + content;
            title = String.format(title,data.getUserinfo().getNickname());
        }
        shareItem.setTitle(title);
        shareItem.setContent(content);
        shareItem.setImgUrl(data.getUserinfo().getImgmiddleurl());
        new ShareDialog(MyQrcodeActivity.this).shareInvite(Long.parseLong(SharedToken.getUserId(MyQrcodeActivity.this)), shareItem);
    }

    private void getQrcodeUrl() {
        UserTask.getQrcodeInfoUrl(0).setCallback(this, mGetQrcodeUrlCallback).start();
    }

    private SimpleTask.Callback<String> mGetQrcodeUrlCallback = new SimpleTask.Callback<String>() {
        @Override
        public void onError(TaskError e) {
            mEmptyView.showRetry();
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(String result) {
            data = JSONObject.parseObject(result, QRCodeInfo.class);
            imgQRCode.setVisibility(View.VISIBLE);
            showQRCode(data.getQrurl());
            //显示用户信息
            UserItem userItem = data.getUserinfo();
            ImageUtils.loadCircleHead(userItem.getImgurl(), mHeadIv);
            mNickTv.setText(userItem.getNickname());
            if (!TextUtils.isEmpty(userItem.getInvitecode())) {
                mInviteCodeTv.setText(Html.fromHtml(String.format(getString(R.string.me_invite_code_s), userItem.getInvitecode())));
                mInviteCodeTv.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };


    /**
     * 生成二维码Bitmap
     *
     * @param content   内容
     * @param widthPix  图片宽度
     * @param heightPix 图片高度
     * @param logoBm    二维码中心的Logo图标（可以为null）
     * @param filePath  用于存储二维码图片的文件路径
     * @return 生成二维码及保存文件是否成功
     */
    public static boolean createQRImage(String content, int widthPix, int heightPix, Bitmap logoBm, String filePath) {
        try {
            if (content == null || "".equals(content)) {
                return false;
            }

            //配置参数
            Map hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            //设置空白边距的宽度
//            hints.put(EncodeHintType.MARGIN, 2); //default is 4

            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix, heightPix, hints);
            int[] pixels = new int[widthPix * heightPix];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthPix + x] = 0xff000000;
                    } else {
                        pixels[y * widthPix + x] = 0xffffffff;
                    }
                }
            }

            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);

            if (logoBm != null) {
                bitmap = addLogo(bitmap, logoBm);
            }

            //必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
            return bitmap != null && bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(filePath));
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 在二维码中间添加Logo图案
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }

        if (logo == null) {
            return src;
        }

        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        //logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }

}
