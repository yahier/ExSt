package yahier.exst.widget.avsdk;

import com.tencent.av.sdk.AVError;

/**
 * Created by meteorshower on 16/3/23.
 */
public class QavsdkError extends AVError {

    public static String getErrorValue(int value){
        String valueResultError;
        switch(value){
            case AV_OK:
                valueResultError = "成功";
                break;
            case AV_ERR_FAILED:
                valueResultError = "请求失败!!";
                break;
            case AV_ERR_REPEATED_OPERATION:
                valueResultError = "已经执行此项操作,请勿重复操作!!";
                break;
            case AV_ERR_EXCLUSIVE_OPERATION:
                valueResultError = "不可执行此项操作,存在正在执行互斥操作!!";
                break;
            case AV_ERR_HAS_IN_THE_STATE:
                valueResultError = "已成功执行此项操作,无需再次执行!!";
                break;
            case AV_ERR_INVALID_ARGUMENT:
                valueResultError = "请求参数错误!!";
                break;
            case AV_ERR_TIMEOUT:
                valueResultError = "请求超时!!";
                break;
            case AV_ERR_NOT_IMPLEMENTED:
                valueResultError = "暂不支持该项操作!!";
                break;
            case AV_ERR_NOT_IN_MAIN_THREAD:
                valueResultError = "请在主进程中使用此项服务!!";
                break;
            case AV_ERR_RESOURCE_IS_OCCUPIED:
                valueResultError = "1008";
                break;
            case AV_ERR_CONTEXT_NOT_EXIST:
                valueResultError = "AVContext对象未处于 CONTEXT_STA TE_STARTED 状态!!";
                break;
            case AV_ERR_CONTEXT_NOT_STOPPED:
                valueResultError = "AVContext对象未处于 CONTEXT_STA TE_STOPPED 状态!!";
                break;
            case AV_ERR_ROOM_NOT_EXIST:
                valueResultError = "AVRoom对象未处于 ROOM_STATE_ ENTERED 状态!!";
                break;
            case AV_ERR_ROOM_NOT_EXITED:
                valueResultError = "AVRoom对象未处于 ROOM_STATE_ EXITED 状态!!";
                break;
            case AV_ERR_DEVICE_NOT_EXIST:
                valueResultError = "设备不存在!!";
                break;
            case AV_ERR_ENDPOINT_NOT_EXIST:
                valueResultError = "AVEndpoint对象不存在!!";
                break;
            case AV_ERR_ENDPOINT_HAS_NOT_VIDEO:
                valueResultError = "成员没有发视频!!";
                break;
            case AV_ERR_TINYID_TO_OPENID_FAILED:
                valueResultError = "tinyid 转 identifier 失败!!";
                break;
            case AV_ERR_OPENID_TO_TINYID_FAILED:
                valueResultError = "identifier 转 tinyid 失败!!";
                break;
            case AV_ERR_INVITE_FAILED:
                valueResultError = "发送邀请失败!!";
                break;
            case AV_ERR_ACCEPT_FAILED:
                valueResultError = "接受邀请失败!!";
                break;
            case AV_ERR_REFUSE_FAILED:
                valueResultError = "拒绝邀请失败!!";
                break;
            case AV_ERR_SERVER_FAILED:
                valueResultError = "请求失败(SDK后台错误)!!";
                break;
            case AV_ERR_SERVER_INVALID_ARGUMENT:
                valueResultError = "参数错误(SDK后台错误)!!";
                break;
            case AV_ERR_SERVER_NO_PERMISSION:
                valueResultError = "没有权限(SDK后台错误)!!";
                break;
            case AV_ERR_SERVER_TIMEOUT:
                valueResultError = "请求超时(SDK后台错误)!!";
                break;
            case AV_ERR_SERVER_ALLOC_RESOURCE_FAILED:
                valueResultError = "资源不够(SDK后台错误)!!";
                break;
            case AV_ERR_SERVER_ID_NOT_IN_ROOM:
                valueResultError = "不在房间内(SDK后台错误)!!";
                break;
            case AV_ERR_SERVER_NOT_IMPLEMENT:
                valueResultError = "未实现(SDK后台错误)!!";
                break;
            case AV_ERR_SERVER_REPEATED_OPERATION:
                valueResultError = "重复操作(SDK后台错误)!!";
                break;
            case AV_ERR_SERVER_ROOM_NOT_EXIST:
                valueResultError = "房间不存在(SDK后台错误)!!";
                break;
            case AV_ERR_SERVER_ENDPOINT_NOT_EXIST:
                valueResultError = "成员不存在(SDK后台错误)!!";
                break;
            case AV_ERR_SERVER_INVALID_ABILITY:
                valueResultError = "错误能力(SDK后台错误)!!";
                break;
            default:
                valueResultError = "未知错误!!";
                break;
        }
        return valueResultError;
    }
}
