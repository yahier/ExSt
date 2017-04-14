package yahier.exst.util;

import android.content.Context;
import android.text.ClipboardManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONException;
import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.Relation;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * 将e计数法 改成普通计数
     *
     * @param
     * @return
     */

    public static void main(String[] s) {
        System.out.println("main");
        System.out.println(get2ScaleString(11.1f));
        System.out.println(get2ScaleString(0.01f));
        System.out.println(get2ScaleString(1.11f));
        System.out.println(get2ScaleString(10.112222f));
        System.out.println(get2ScaleString(2221.11f));
        System.out.println(get2ScaleString(1111111.11f));

    }

    public static String get(double d) {
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        return nf.format(new Double(d));
    }

    public static String gets(long value) {
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        return nf.format(new Long(value));
    }


    /**
     * 获取小数点后两位数的值。四舍五入。小数点前至少一位，小数点后固定两位
     *
     * @param value
     * @return
     */
    public static String get2ScaleString(float value) {
        try {
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(value);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.logE("LogUtil", "格式化浮点型发生错误");
            return getFloat2ScaleString(value);
        }
    }

    public static String get2ScaleString(double value) {
        try {
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(value);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.logE("LogUtil", "格式化浮点型发生错误");
            return getFloat2ScaleString(value);
        }
    }

    public static String get2ScaleStringFloor(double value) {
        try {
            DecimalFormat df = new DecimalFormat("0.00");
            df.setRoundingMode(RoundingMode.FLOOR);
            return df.format(value);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.logE("LogUtil", "格式化浮点型发生错误");
            return getFloat2ScaleStringFloor(value);
        }
    }

    /**
     * 获取小数点后两位数的值。四舍五入。小数点前至少一位，小数点后固定两位
     *
     * @param value
     * @return
     */
    public static String getFloat2ScaleString(float value) {
        try {
            BigDecimal bd = new BigDecimal(value);
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            return bd.toString();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.logE("LogUtil", "格式化浮点型发生错误");
            return "";
        }
    }

    public static String getFloat2ScaleString(double value) {
        try {
            BigDecimal bd = new BigDecimal(value);
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            return bd.toString();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.logE("LogUtil", "格式化浮点型发生错误");
            return "";
        }
    }

    public static String getFloat2ScaleStringFloor(double value) {
        try {
            BigDecimal bd = new BigDecimal(value);
            bd = bd.setScale(2, BigDecimal.ROUND_FLOOR);
            return bd.toString();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.logE("LogUtil", "格式化浮点型发生错误");
            return "";
        }
    }

    /**
     * 判断是否6位数字
     *
     * @param value
     * @return
     */
    public static boolean isSixNum(String value) {
        String regExp = "[0-9]{6}";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(value);
        return m.find();
    }

    /**
     * 昵称是否合法——数字、英文、中文组成
     *
     * @return
     */
    public static boolean isLegalNick(String string) {
        return string.matches("[0-9]*[a-zA-Z]*[\u4e00-\u9fa5]*");
    }

    public static boolean is_number(String number) {
        if (number == null)
            return false;
        return number.matches("[+-]?[1-9]+[0-9]*(\\.[0-9]+)?");
    }

    public static boolean is_alpha(String alpha) {
        if (alpha == null)
            return false;
        return alpha.matches("[a-zA-Z]+");
    }

    public static boolean is_chinese(String chineseContent) {
        if (chineseContent == null)
            return false;
        return chineseContent.matches("[\u4e00-\u9fa5]");
    }

    public static String formatMobile(String str) {
        if (str == null) {
            return null;
        }
        str = str.replaceAll(" ", "");
        str = str.replaceAll("-", "");
        return str;
    }

    public static String getRelationString(int relationCode) {
        if (Relation.isMaster(relationCode)) {
            return "师傅";
        }

        if (Relation.isStu(relationCode)) {
            return "徒弟";
        }
        return "";
    }

    /*
     * UTF-8编码，一个英文占一个字节，一个汉字占3个字节， GBK编码（简繁体），一个英文占一个字节，一个汉字占2个字节
     */
    public static int getGBKByteLength(String uft8Str) {
        try {
            byte[] temp = uft8Str.getBytes("utf-8");// 这里写原编码方式
            byte[] newtemp = new String(temp, "utf-8").getBytes("gbk");// 转成GBK编码

            uft8Str = new String(newtemp, "gbk");// 当前uft8Str是GBK编码

            int currentByteLength = newtemp.length;

            return currentByteLength;

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // 如果转码失败，返回字符串的字符长度
            return uft8Str.length();
        }
    }

    public static String getWidth(int length) {
        StringBuilder builder = new StringBuilder();
        builder.delete(0, builder.length());
        for (int i = 0; i < length; i++) {
            builder.append("    ");
        }
        return builder.append("  ").toString();
    }

    public static void copyToClipboard(String text) {
        ClipboardManager clipboardManager = (ClipboardManager)
                MyApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(text);
        ToastUtil.showToast(MyApplication.getContext().getString(R.string.common_copy_success));
    }

    /**
     * A hashing method that changes a string (like a URL) into a hash suitable for using as a
     * disk filename.
     */
    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static String getExceptionMsg(Throwable e) {
        if (e == null || TextUtils.isEmpty(e.getMessage())) {
            return "";
        }
        String text = e.getMessage();
        if (e instanceof JSONException || e instanceof org.json.JSONException) {
            text = "JSON解析错误";
        } else if (e instanceof ArrayIndexOutOfBoundsException || e instanceof IndexOutOfBoundsException) {
            text = "索引越界错误";
        } else if (e instanceof SocketTimeoutException) {
            text = "连接服务器超时";
        } else if (e instanceof ConnectException || e instanceof SocketException || e instanceof UnknownHostException) {
            text = "连接服务器失败";
        } else if (e instanceof NullPointerException) {
            text = "空指针错误";
        }
        return text;
    }

    public static String getUserTipExceptionMsg(Throwable e) {
        String text = "";
        if (e == null) {
            return text;
        }
        if (e instanceof SocketTimeoutException) {
            text = "连接服务器超时";
        } else if (e instanceof ConnectException || e instanceof SocketException || e instanceof UnknownHostException) {
            text = "连接服务器失败";
        } else {
            LogUtil.logE(e.getMessage());
        }
        return text;
    }

    public static String replaceAllLineBreak(String text) {
        return text.replaceAll("\r|\n", " ");//将内容区域的回车换行去除
    }

    public static String replaceAllChar(CharSequence text) {
        return text.toString().replaceAll("[.]", " ");//将所有字符替换成空格
    }

    public static String createSpace(CharSequence text) {
        int length = text.toString().length();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append("\u3000");
        }
        return builder.toString();
    }

}
