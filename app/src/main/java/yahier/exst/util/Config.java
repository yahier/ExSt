package yahier.exst.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

import java.io.File;

public class Config {
    //开发环境
    public static String hostUploadWeiboImg = "https://dev-img.stbl.cc/";
    public static String hostMain = "https://dev-api.stbl.cc/";
    public static String hostMainYunZhangHu = "https://rpv2.yunzhanghu.com/";
    public static String YunZhangHuSeccussCode = "0000";

    //红包
    public static String hostMainRedPacket;
    public static final String reserveHostIp = "test2-api.stbl.cc";

    public final static int interClickTime = 500;
    public final static int statusesRemarkDBCount = 3;
    //aliyun dns配置相关
    public final static String aliyunHost = "http://203.107.1.1/181759/d";
    public final static String aliyunAccountId = "181759";

    public final static String humanService = "4008633361";
    public final static String longWeiboFillMark = "{img}";
    public final static String goodsInfoImgMark = "{img}";
    public final static int remarkContentLength = 140;
    public final static int userMerchantIdLength = 14;
    //上传的图片最大宽高和高度。任何一项超过此值都需要再压缩
    public final static int maxUploadImgWidth = 960;
    public final static int maxUploadImgHeight = 3000;

    //广告的最大宽度和最小宽度
    public final static int minUploadAdImgWidth = 640;
    public final static int maxUploadAdImgWidth = 1500;
    public final static int maxUploadAdImgHeight = 700;

    public static final int MAX_HEAD_WIDTH = 500;
    public static final int MAX_HEAD_HEIGHT = 500;

    //弹窗距离屏幕左右的距离
    public static final int WINDOW_MARGIN_HORIZONTAL_DP = 50;

    // public final static String publicKey =
    // "BgIAAACkAABSU0ExAAQAAAEAAQBbNliodikFvOVRA8wEfkhzsgxZPbU+qBRu1ns3D5K9KWHEow8aup/DcY8aDI87ogZn1KFwXtk9kVKFEy4VLPKoB4EXUOKJFAJo3U+Fw+id6H9MzaLW5aSJJQtxCWjxg+SwKA+NotbFz5Z5OMZbJIbq9FRdFTBgyI1Ov/9VMOQFmQ==";
    public final static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDLqXFLK3HQ6a1IylueCmyhbBhSrZNWtj6J7SlNlxVAAMynitMrCh9pEmF3VjlLliu3Ur1ULRO9M3yCADDKC5mh2l2NdamlnuuOoGatN5JJkbJla8LkrufCjA5Q69wCq4CHJ96f2/Ar0xlTqhMCr/lkETyjMqBza5TWHkp2Gld/DQIDAQAB";
    public final static String privateKey = "MIICXQIBAAKBgQDLqXFLK3HQ6a1IylueCmyhbBhSrZNWtj6J7SlNlxVAAMynitMrCh9pEmF3VjlLliu3Ur1ULRO9M3yCADDKC5mh2l2NdamlnuuOoGatN5JJkbJla8LkrufCjA5Q69wCq4CHJ96f2/Ar0xlTqhMCr/lkETyjMqBza5TWHkp2Gld/DQIDAQABAoGAQM/TdhCFT+6NbrS3Izw+BcDYnLcQRHAKxunqUv7ZjFy0xDyxpui5xj5LNicCZMONdfGmvrr5pSRVYymcXTAD26SpyzBzzk3OpZbyGm7KYNk55O0dqqSyLRW1ox6vVga6c10g0HXdOSUFpepuomGZtnsDNz/g4aXpwt8P3PnvyiECQQDtXbJo86DkoDMV7/+M3BhUVxWEq34wMkbwcnrwK+0UVasvMdLzrNEg8wqWzfytY3EYy++QO69oBCw5IhNULbw3AkEA26ZmXSWV8QcRoMITrsbj6g1En0cHaT4mxFO/vM++g2yHCTgS1wJDALjblZ6eT57JLb2P82eYO5gE0MwhDt5k2wJBALDP5MANCsKDGsqyldelc3IP1HWZEUxxhypA/Ja/AcDt97AWGx9skfNLV43yLtAxjON9thvoecavpQmB+ofv8Y0CQDCZyAqSN3QmcUMVzk8c2O8/0lA/4ibqRp+oPEOl3+yqhdKHYrvKV1MAlC2t9C9/3dnOGNztriR5hY9Bq4rjsIMCQQDHoJzsTAKECu2HdFbwzRP0FEGmJq1vnr8WtJFNKAe4OmrJXtHvBvK2DoqvrHYoWdGh8cPkYvqsQtmOpkXAEaqP";


    public final static int statusesCoverWidthScale = 5;
    public final static int statusesCoverHeightScale = 3;

    public final static int uploadAdWidthScale = 640;
    public final static int uploadAdHeightScale = 306;

    public static final int AD_DESIGN_WIDTH = 592;
    public static final int AD_DESIGN_HEIGHT = 280;

    /**
     * 本地文件存储路径
     */
    public final static String localFilePath = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/stbl/";

    public final static String localFilePathLog = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/stbl/stbl_log/";

    public final static String localFilePathCrashLog = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/stbl/crash_log/";

    /**
     * 确认生成根目录文件。在MyApplicayion的onCreate方法中被调用
     */
    public static void ensurePath() {
        if (!new File(localFilePath).exists()) {
            new File(localFilePath).mkdir();
        }

        if (ConfigControl.logable) {
            if (!new File(localFilePathLog).exists()) {
                new File(localFilePathLog).mkdirs();
            }
        }

    }

    public static int getCurrentVersion(Context mContext) {
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0);
            return info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
            return -1;
        }
    }
}
