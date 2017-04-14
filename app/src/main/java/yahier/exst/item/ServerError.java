package yahier.exst.item;

import android.app.Activity;
import android.content.Context;

import com.stbl.stbl.R;
import com.stbl.stbl.act.login.LoginActivity;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.TipsDialog.OnTipsListener;
import com.stbl.stbl.util.database.DataCacheDB;
import com.stbl.stbl.widget.jpush.JPushManager;

import java.io.Serializable;

public class ServerError implements Serializable {

    // HttpMethod错误 -100001
    // 反序列化数据失败 -100003
    // RSA解密失败 -100004
    // 请求参数不正确 -200000
    // 认证失败 -201000
    // Token非法 -201001
    // Token缓存异常 -201002
    // AccessToken过期 -201003
    // RrefreshToken过期 -201004
    // AccessToken获取缓存失败 -201005
    // 登录信息验证错误 -201101
    // 该账户已在其他地方登录 -201103
    // 授权登录没有记录 -201104
    // 输入的账号密码错误 -201105
    // 该手机号码已被注册 -201201
    // 验证码不正确 -201202
    // 昵称重复 -201203
    // 昵称长度超出范围 -201204
    // 个性签名超出范围 -201205
    // 支付钱包不存在 -201301
    // 支付密码不能为空 -201302
    public final static int errCodeWrongPpayPd = -201303;// 支付密码不正确
    // 卖家钱包余额不足 -201311
    // 指定的支付方式不存在 -201312
    // 货币兑换异常 -201313
    // 没有收支明细数据 -201314
    // 余额不足 -201315
    // 绿豆不足 -201316
    // 金豆不足 -201317
    // User_用户不存在或已被禁用 -211000
    // User_用户财富记录不存在 -211001
    // User_用户统计记录不存在 -211002
    // User_用户相册记录不存在 -211003
    // User_用户等级配置不存在 -211004
    // User_用户签到配置不存在 -211005
    // User_用户头像上传失败 -211011
    // User_用户语音上传失败 -211012
    // User_用户头像数据保存失败 -211013
    // UserSign_用户今天已签到过 -211101
    // UserSign_用户今天已经帮过该用户签到 -211103
    // UserTag_该用户标签已经存在 -211201
    // UserTag_该用户标签不存在 -211202
    // UserAlbum_用户图册信息不存在 -211301
    // Relation_该用户已被关注 -212101
    // Relation_该用户没有被关注 -212102
    // Relation_该用户不能取消当前的关注类型 -212103
    // Relation_该用户已被屏蔽 -212104
    // Relation_该用户没有被屏蔽 -212105
    // Relation_该用户不能关注自己 -212106
    // Relation_该用户不能屏蔽自己 -212107
    // Relation_该用户不能取消关注自己 -212108
    // Relation_已拥有师傅并超过重新拜师的时间限制 -212202
    // Relation_已经重新拜过师不再允许再次拜师 -212203
    // Relation_重新拜师的师傅不能是之前的师傅 -212204
    // Relation_找不到拜师的记录信息 -212205
   public final static int codeStatusesNotExist = -213001; // Statuses_动态信息不存在 -213001
    // Statuses_动态评论记录不存在 -213002
    // Statuses_动态统计记录不存在 -213003
    // Statuses_动态置顶数量大于3 -213101
    // Statuses_动态重复置顶 -213102
    // Statuses_动态未置顶不能取消置顶 -213103
    // Statuses_动态转发图片接口保存失败 -213104
    // Statuses_不允许转发自己的动态 -213105
    // Statuses_动态已被收藏 -213106
    // Statuses_动态已被点赞 -213107
    // Statuses_动态没有被收藏 -213108
    // Statuses_动态评论已点过赞 -213201
    // Statuses_动态评论只允许自己删除 -213202
    // Mission_任务信息记录不存在 -216001
    // Mission_任务还没开放 -216101
    // Mission_任务已经结束 -216102
    // Mission_任务不满足领取条件 -216103
    // Mission_只允许放弃进行中的任务 -216104
    // Mission_只允许领取进行中的任务奖励 -216105
    // Mall_卖家钱包记录不存在 -217001
    // Mall_店铺信息记录不存在 -217002
    // Mall_商品信息不存在 -217011
    // Mall_商品统计表不存在 -217012
    // Mall_商品型号信息不存在 -217013
    // Mall_商品图片信息不存在 -217014
    // Mall_商品收藏信息不存在 -217015
    // Mall_商品Banner信息不存在 -217016
    // Mall_订单信息不存在 -217021
    // Mall_订单物流单信息不存在 -217022
    // Mall_订单退货单信息不存在 -217023
    // MallGoods_商品必须下架后才能删除 -217101
    // MallGoods_不支持该排序方式 -217102
    // MallGoods_库存异常 -217103
    // MallOrder_订单不符合操作要求 -217201
    // MallOrder_订单状态异常 -217202
    // MallOrder_当前订单状态不允许关闭订单 -217203
    // MallOrder_订单更新失败 -217204
    // MallOrder_下单失败 -217205
    // MallOrder_支付类型不正确 -217206
    // MallOrder_支付验证失败 -217207
    // MallTicket_已经领取了该优惠卷 -217301
    // MallTicket_该优惠卷已过期 -217302
    // MallTicket_订单优惠验证失败 -217303
    // BankCard_未绑定该银行卡 -218001
    // BankCard_最多绑定5张银行卡 -218002
    // BankCard_不能绑定同张银行卡 -218003
    // BankCard_银行绑定保存失败 -218004
    // BankCard_银行卡实名认证失败 -218005
    // BankCard_用户没有绑定银行卡 -218006
    // Image_图像上传失败 -221000
    // Image_图片格式错误 -221101
    // Image_拷贝文件夹不存在 -221102
    // Image_拷贝文件夹不存在 -221102
    // IM_GroupId错误 -222001
    // IM_群名称不能超过50字 -222002
    // IM_群描述不能超过300字 -222003
    // IM_指定用户接收方式需要提供用户信息 -222004
    // SMS_回调地址不能为空 -223101
    // SMS_回复指令不能为空 -223102
    // SMS_目标手机号码不能为空 -223201
    // SMS_通知内容不能为空 -223202
    // SMS_上行短信数据包为空 -223203
    // SMS_上行短信数据包格式错误 -223204
    // SMS_解析上行短信失败 -223205
    // SMS_没找到短信内容模板 -223206
    // SMS_短信发送失败 -223207
    // SMS_目标邮箱不能为空 -223301
    // Withdraw_当前提现单找不到 -231001
    // Withdraw_当前提现已删除 -231002
    // Withdraw_当前提现状态不符合操作要求 -231003
    // 未知错误 -999999

    public static final int errcode_rp_pickover = -232101;//红包抢完了

    public final static int ERR_NOT_ENOUGH_MONEY = -201315; // 余额不足
    public final static int ERR_NOT_ENOUGH_GREEN_BEAN = -201316; // 绿豆不足
    public final static int ERR_NOT_ENOUGH_GOLD_BEAN = -201317; // 金豆不足

    public final static int ERR_NO_PAY_PWD = -201302; // 支付密码不能为空
    public final static int ERR_LOGIN_WX_NO_RECORD = -201401; // 微信登录无纪录
    public final static int ERR_LOGIN_WX_BIND_FAILD = -201403; //微信绑定失败

    public final static int ERR_TOKEN_NULL = -201000; // 认证失败
    public final static int ERR_TOKEN_INVAILD = -201001; // Token非法
    public final static int ERR_TOKEN_EXCEPTION = -201002; // Token缓存异常
    public final static int ERR_TOKEN_TIME_OUT = -201003; // AccessToken过期
    public final static int ERR_TOKEN_REFRESH_TIME_OUT = -201004; // RrefreshToken过期
    public final static int ERR_TOKEN_FAILD = -201005; // AccessToken获取缓存失败
    public final static int ERR_FEFRESH_TOKEN_INVALID = -201006; // refreshToken错误
    public final static int ERR_TOKEN_LOGIN_FAILD = -201101; // 登录信息验证错误
    public final static int ERR_ACCOUNT_LOGIN_OTHER = -201103; // 该账户已在其他地方登录
    public final static int ERR_ACCOUNT_NOT_EXIST = -211000; // 用户不存在 .修改为这个状态不退出
    public final static int ERR_LOGIN_USER_NO_EXIST = -201108; // 登录用户不存在
    public final static int ERR_DISCUSSION_NO_EXIST = -222206; // 讨论组不存在
    public final static int ERR_DISCUSSION_NO_EXIST1 = -222204; // 讨论组不存在
    public final static int ERR_DISCUSSION_NO_MEMBER = -222205; //不是该讨论组成员


    /**
     * 帐号异常
     */
    public static int[] errorTokenCodeArrays = {ERR_TOKEN_NULL, ERR_TOKEN_INVAILD, ERR_TOKEN_EXCEPTION, ERR_TOKEN_TIME_OUT, ERR_TOKEN_REFRESH_TIME_OUT, ERR_TOKEN_FAILD, ERR_TOKEN_LOGIN_FAILD,
            ERR_ACCOUNT_LOGIN_OTHER, ERR_LOGIN_USER_NO_EXIST, ERR_FEFRESH_TOKEN_INVALID};
    /**
     * 账户余额不足
     */
    public static int[] errorMoneyArrays = {ERR_NOT_ENOUGH_MONEY, ERR_NOT_ENOUGH_GREEN_BEAN, ERR_NOT_ENOUGH_GOLD_BEAN};

    int errcode;
    String msg;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static boolean checkError(Context ctx, int errorCode, String msg) {
        for (int i = 0; i < ServerError.errorTokenCodeArrays.length; i++) {
            if (errorCode == ServerError.errorTokenCodeArrays[i]) {
                logout(ctx, msg);
                return false;
            }
        }
        // 跳转充值界面
        for (int i = 0; i < ServerError.errorMoneyArrays.length; i++) {
            if (errorCode == ServerError.errorMoneyArrays[i]) {
                Payment.gotoWallet(ctx);
                break;
            }
        }
        return true;
    }

    public static String dialogTag = "";

    // 返回登录界面
    public static void logout(final Context mContext, String msg) {
        if (mContext == null) return;
        SharedToken.clearToken();
        JPushManager.getInstance().clearAlias(mContext);
        //帐号被抢登，清除缓存
        new DataCacheDB(mContext).deleteAllData();
        String tempTag = "";
        if ((mContext instanceof Activity)) {
            if (((Activity) mContext).isFinishing()) return;
            tempTag = ((Activity) mContext).getClass().getSimpleName();
        }
        if (dialogTag != null && !dialogTag.equals(tempTag)) {
            dialogTag = tempTag;
            TipsDialog dialog = TipsDialog.popup(mContext, mContext.getString(R.string.common_error), msg, mContext.getString(R.string.enter));
            dialog.setCancelable(false);
            dialog.setOnTipsListener(new OnTipsListener() {
                @Override
                public void onConfirm() {
                    dialogTag = "";
                    if (mContext instanceof LoginActivity) {
                        return;
                    }
                    MyApplication app = (MyApplication) mContext.getApplicationContext();
                    app.restartApplication(null);
                }

                @Override
                public void onCancel() {
                    dialogTag = "";
                }
            });
        }
    }


}
