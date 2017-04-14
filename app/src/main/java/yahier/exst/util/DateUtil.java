package yahier.exst.util;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static String[] WEEK = new String[]{"天", "一", "二", "三", "四", "五", "六"};

    private static final long ONE_SECOND = 1000;
    private static final long ONE_MINUTE = ONE_SECOND * 60;
    private static final long ONE_HOUR = ONE_MINUTE * 60;
    private static final long ONE_DAY = ONE_HOUR * 24;

    public static String getTime(String milliseconds, String pattern) {
        Calendar ca = Calendar.getInstance();
        // BigDecimal bd = new BigDecimal(milliseconds);
        //milliseconds = bd.toPlainString();
        if (milliseconds.length() == 10) {
            ca.setTimeInMillis(Long.valueOf(milliseconds) * 1000);
        } else {
            ca.setTimeInMillis(Long.valueOf(milliseconds));
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(ca.getTime());
    }

    public static String getMdHm(String milliseconds) {
        Calendar ca = Calendar.getInstance();
        if (milliseconds.length() == 10) {
            ca.setTimeInMillis(Long.valueOf(milliseconds) * 1000);
        } else {
            ca.setTimeInMillis(Long.valueOf(milliseconds));
        }
        String pattern = "MM-dd HH:mm";

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(ca.getTime());
    }

    /**
     * 获取日期yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getTimeValue(long milliseconds) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String strMi = String.valueOf(milliseconds);
        if (strMi.length() == 10) {
            return sdf.format(milliseconds * 1000);
        } else {
            return sdf.format(milliseconds);
        }

    }


//    public static int getFirstDayWeekOfMonth() {
//        Calendar cal = Calendar.getInstance();
//        cal.setFirstDayOfWeek(Calendar.SUNDAY);
//        int value = getDayOfMonth();//当前的日期数
//        cal.add(Calendar.DAY_OF_MONTH, -value + 1);//回到了11月1号
//        String time = cal.toString();
//        LogUtil.logE("date time", time);
//        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;//返回的值分别是1,2,3,4,5,6,7不是索引值
//        if (w < 0)
//            w = 0;
//        return w;
//    }

    //获取当月第一天的星期数索引
    public static int getFirstDayWeekOfMonth(long millions) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millions);
        cal.setFirstDayOfWeek(Calendar.SUNDAY);
        int value = getDayOfMonth(millions);//当前的日期数
        cal.add(Calendar.DAY_OF_MONTH, -value + 1);//回到了11月1号
        String time = cal.toString();
        LogUtil.logE("date time", time);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;//返回的值分别是1,2,3,4,5,6,7不是索引值
        if (w < 0)
            w = 0;
        return w;
    }


    public static int getDayOfMonth(long millions) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millions);
        int w = cal.get(Calendar.DAY_OF_MONTH);
        return w;

    }


    public static String getTimeValue(String milliseconds) {
        return getTimeValue(Long.valueOf(milliseconds));
    }

    /**
     * 获取HH:mm或者MM-dd HH:mm
     *
     * @param milliseconds
     * @return
     */
    public static String getHmOrMdHm(String milliseconds) {
        Calendar ca = Calendar.getInstance();
        if (milliseconds.length() == 10) {
            ca.setTimeInMillis(Long.valueOf(milliseconds) * 1000);
        } else {
            ca.setTimeInMillis(Long.valueOf(milliseconds));
        }
        Calendar today = Calendar.getInstance();
        if (today.get(Calendar.YEAR) == ca.get(Calendar.YEAR)
                && today.get(Calendar.MONTH) == ca.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) == ca
                .get(Calendar.DAY_OF_MONTH)) {
            String pattern = "HH:mm";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            //return MyApplication.getContext().getString(R.string.today) + " " + sdf.format(ca.getTime());
            return sdf.format(ca.getTime());
        } else {
            String pattern = "MM-dd HH:mm";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(ca.getTime());

        }


    }

    /**
     * 获取HH:mm或者yyyy-MM-dd
     *
     * @param milliseconds
     * @return
     */
    public static String getDateToFormatHmd(String milliseconds) {
        Calendar ca = Calendar.getInstance();
        if (milliseconds.length() == 10) {
            ca.setTimeInMillis(Long.valueOf(milliseconds) * 1000);
        } else {
            ca.setTimeInMillis(Long.valueOf(milliseconds));
        }
        Calendar today = Calendar.getInstance();
        if (today.get(Calendar.YEAR) == ca.get(Calendar.YEAR)
                && today.get(Calendar.MONTH) == ca.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) == ca
                .get(Calendar.DAY_OF_MONTH)) {
            String pattern = "HH:mm";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return MyApplication.getContext().getString(R.string.today) + " " + sdf.format(ca.getTime());
        } else {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(ca.getTime());

        }

    }

    /**
     * 获取HH:mm或者HH-MM-dd HH:mm
     *
     * @param milliseconds
     * @return
     */
    public static String getDateToFormat(String milliseconds) {
        Calendar ca = Calendar.getInstance();
        if (milliseconds.length() == 10) {
            ca.setTimeInMillis(Long.valueOf(milliseconds) * 1000);
        } else {
            ca.setTimeInMillis(Long.valueOf(milliseconds));
        }
        Calendar today = Calendar.getInstance();
        if (today.get(Calendar.YEAR) == ca.get(Calendar.YEAR)
                && today.get(Calendar.MONTH) == ca.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) == ca
                .get(Calendar.DAY_OF_MONTH)) {
            String pattern = "HH:mm";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return MyApplication.getContext().getString(R.string.today) + sdf.format(ca.getTime());
        } else {
            String pattern = "yyyy-MM-dd HH:mm";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(ca.getTime());

        }

    }

    //
    public static String getDateYMDHM(long milliseconds) {
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(milliseconds * 1000);
    }

    public static String getDateYMDHM() {
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(Calendar.getInstance().getTimeInMillis());
    }

    private static String getDateFormat(String milliseconds, String toDayPattern, String otherPattern) {
        Calendar ca = Calendar.getInstance();
        if (milliseconds.length() == 10) {
            ca.setTimeInMillis(Long.valueOf(milliseconds) * 1000);
        } else {
            ca.setTimeInMillis(Long.valueOf(milliseconds));
        }

        String pattern;
        Calendar today = Calendar.getInstance();
        // 如果是今天
        if (today.get(Calendar.YEAR) == ca.get(Calendar.YEAR)
                && today.get(Calendar.MONTH) == ca.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) == ca
                .get(Calendar.DAY_OF_MONTH)) {
            pattern = toDayPattern;
        } else {
            pattern = otherPattern;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(ca.getTime());
    }


    /**
     * 计算时间差，返回数据类似于:刚刚，3分钟前，1小时前，4天前
     *
     * @param milliseconds
     * @return
     */
    public static String getTimeOff(long milliseconds) {
        Calendar ca = Calendar.getInstance();
        long currentMills = ca.getTimeInMillis() / 1000;
        //时间差
        long millOff = currentMills - milliseconds;
        ca.setTimeInMillis(milliseconds * 1000);
        Calendar today = Calendar.getInstance();
        if (millOff < 60) {
            return MyApplication.getContext().getString(R.string.just_now);
        } else if (millOff < 3600) {
            return millOff / 60 + MyApplication.getContext().getString(R.string.minutes_ago);
        } else if (millOff < 86400 && today.get(Calendar.DAY_OF_YEAR) == ca.get(Calendar.DAY_OF_YEAR)) { //同一天
            return millOff / 3600 + MyApplication.getContext().getString(R.string.hours_ago);
        } else if (millOff < 86400 * 2 && today.get(Calendar.DAY_OF_YEAR) - ca.get(Calendar.DAY_OF_YEAR) == 1) { //昨天  如：282-281=1
            return MyApplication.getContext().getString(R.string.yesterdy);
        } else {
            return getYMD(String.valueOf(milliseconds));
        }


    }

    public static String getHm(String milliseconds) {
        Calendar ca = Calendar.getInstance();
        if (milliseconds.length() == 10) {
            ca.setTimeInMillis(Long.valueOf(milliseconds) * 1000);
        } else {
            ca.setTimeInMillis(Long.valueOf(milliseconds));
        }
        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(ca.getTime());
    }

    public static String getYMD(String milliseconds) {
        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(Long.valueOf(milliseconds) * 1000);
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(ca.getTime());
    }

    public static String getMD(long milliseconds) {
        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(milliseconds * 1000);
        String pattern = "MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(ca.getTime());
    }


    public static String getYM(long milliseconds) {
        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(milliseconds * 1000);
        String pattern = "yyyy年MM月";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(ca.getTime());
    }

    // 将年月日 换 成秒数
    public static long getSeconds(String ymd) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(ymd);
            return date.getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
            LogUtil.logE("解析错误");
            return new Date().getTime() / 1000;
        }

    }

    /**
     * 获得
     *
     * @param
     * @return
     */
    public static String getNextDay() {
        Calendar calendar = Calendar.getInstance();
        // calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date date = calendar.getTime();
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static String getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static String getHelpTime(long second) {
        String str = new SimpleDateFormat("MM-dd HH:mm").format(new Date(
                second * 1000));
        return str;
    }

    public static boolean isAfterOneHour(long time) {
        boolean after = false;
        long now = System.currentTimeMillis();
        if ((now - time) > 1 * 60 * 60 * 1000) {
            after = true;
        }
        return after;
    }

    public static int getAge(long second) {
        Calendar oldCalendar = Calendar.getInstance();
        oldCalendar.setTimeInMillis(second * 1000);
        Calendar currCalendar = Calendar.getInstance();

        int oldYear = oldCalendar.get(Calendar.YEAR);
        int currYear = currCalendar.get(Calendar.YEAR);
        return currYear - oldYear;
    }

    /**
     * 获取目标时间和当前时间之间的差距
     *
     * @param milliseconds
     * @return
     */
    public static String getTimeDifference(long milliseconds) {
        long now = new Date().getTime();
        long splitTime = now - milliseconds;
        if (splitTime < (30 * ONE_DAY)) {
            if (splitTime < ONE_MINUTE) {
                return MyApplication.getContext().getString(R.string.common_current_time);
            }
            if (splitTime < ONE_HOUR) {
                return String.format(MyApplication.getContext().getString(R.string.common_minute_before), splitTime / ONE_MINUTE);
            }

            if (splitTime < ONE_DAY) {
                return String.format(MyApplication.getContext().getString(R.string.common_hour_before), splitTime / ONE_HOUR);
            }

            return String.format(MyApplication.getContext().getString(R.string.common_day_before), splitTime / ONE_DAY);
        }
        String result;
        result = MyApplication.getContext().getString(R.string.common_month_day_hhmm);
        return (new SimpleDateFormat(result, Locale.CHINA)).format(new Date(milliseconds));
    }

    public static String getTimeDifferenceOfSecond(long second) {
        return getTimeDifference(second * 1000);
    }

    public static boolean isToday(long time) {
        Calendar oldCalendar = Calendar.getInstance();
        oldCalendar.setTimeInMillis(time);
        Calendar currCalendar = Calendar.getInstance();

        int oldYear = oldCalendar.get(Calendar.YEAR);
        int currYear = currCalendar.get(Calendar.YEAR);

        int oldMonth = oldCalendar.get(Calendar.MONTH);
        int currMonth = currCalendar.get(Calendar.MONTH);

        int oldDay = oldCalendar.get(Calendar.DAY_OF_MONTH);
        int currDay = currCalendar.get(Calendar.DAY_OF_MONTH);

        if (oldYear == currYear && oldMonth == currMonth && oldDay == currDay) {
            return true;
        }
        return false;
    }


}
