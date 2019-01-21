package com.zhixin.ywymvp.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期处理类
 */
public class DateUtils {

    public static String FORMAT_RAW = "yyyyMMddHHmmss";
    public static String FORMAT_DATE = "yyyy-MM-dd";
    public static String FORMAT_DATE2 = "yyyyMMdd";
    public static String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static String YYYYMMDDHHMM = "yyyyMMddHHmm";
    public static String YYYY = "yyyy";


    private DateUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");

    }

    /**
     * @param format 日期格式
     * @param l      long型日期
     * @return 日期字符串
     */
    public static String getDate(String format, long l) {
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        Date date = new Date(l);
        return df.format(date);
    }

    /**
     * @param format 日期格式
     * @return 日期字符串
     */
    public static String getDate(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        Date date = new Date();
        return df.format(date);
    }

    /**
     * @param format 日期格式
     * @param s      {@link String }型日期
     * @return 日期字符串
     */
    public static long getDate(String format, String s) {
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        Date date = null;
        try {
            date = df.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date != null ? date.getTime() : 0;
    }

    public static String getTime(long time) {
        time /= 1000;
        int time_int = (int) time;
        return getTime(time_int);

    }

    public static String getTime(int time) {
        time = Math.max(0, time);
        String ret = "";
        int second = time % 60;
        time /= 60;
        int minute = time % 60;
        time /= 60;
        int hour = time;
        ret = String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minute, second);
        return ret;
    }


    /**
     * 获取与当前时间的时间差
     *
     * @param time 时间（毫秒）
     * @return 时间差
     */
    public static long getTimeDiff(long time) {
        return Math.max(0, (new Date().getTime() - time));
    }

    /**
     * 友好的日期，如“1分钟前”
     *
     * @param t 时间（毫秒）
     * @return 日期
     */
    public static String getReadableDate(long t) {
        try {
            long cur = System.currentTimeMillis();
            long offset = cur - t;
            if (offset < 60 * 1000 * 2) {// 1分钟内
//                return (int) (offset / 1000) + "秒前";
                return "刚刚";
            } else if (offset < 60 * 60 * 1000) {// 1小时内
                return (int) (offset / 60 / 1000) + "分钟前";
            } else if (offset < 24 * 60 * 60 * 1000) {// 1天内
                return (int) (offset / 60 / 60 / 1000) + "小时前";
            } else if (offset < 7 * 24 * 60 * 60 * 1000) {// 7天内
                return (int) (offset / 24 / 60 / 60 / 1000) + "天前";
            } else {
                return
                        getDate(FORMAT_DATE, t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取用户友好的日期，如“1分钟前”
     *
     * @param s 时间，格式必须是{@link DateUtils#FORMAT_DATETIME}
     * @return 日期
     */
    public static String getReadableDate(String s) {

        long l;
        try {
            l = Long.parseLong(s);
        } catch (NumberFormatException e) {
            SimpleDateFormat df = new SimpleDateFormat(FORMAT_DATETIME, Locale.getDefault());
            Date date;
            try {
                date = df.parse(s);
                l = date.getTime();
            } catch (ParseException e2) {
                df = new SimpleDateFormat(FORMAT_DATETIME, Locale.getDefault());

                try {
                    date = df.parse(s);
                    l = date.getTime();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                    return "";
                }

            }
        }

        return getReadableDate(l);
    }

    /**
     * 获取今年
     *
     * @return 今年
     */
    public static int getThisYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * 获取本月
     *
     * @return 本月
     */
    public static int getThisMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    /**
     * 获取今天（在本月中第几天）
     *
     * @return 今天
     */
    public static int getToday() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 日期选择PV返回时间
     *
     * @param date
     * @return
     */
    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    /**
     * 日期选择PV返回时间
     *
     * @param date
     * @return
     */
    public static String getTimeYM(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        return format.format(date);
    }

    public static String getCurrentTimeFormatStamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        Date curDate = new Date(System.currentTimeMillis());
        long i = curDate.getTime();
        String str = String.valueOf(i);
        String timeStamp = str.substring(0, 10);
        return timeStamp;
    }

    public static String addDay(String s, int n) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Calendar cd = Calendar.getInstance();
            cd.setTime(sdf.parse(s));
            cd.add(Calendar.DATE, n);//增加一天
            //cd.add(Calendar.MONTH, n);//增加一个月

            return sdf.format(cd.getTime());

        } catch (Exception e) {
            return null;
        }

    }

    public static Calendar getCalendar(String str, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);

            Date date = sdf.parse(str);

            Calendar calendar = Calendar.getInstance();

            calendar.setTime(date);
            return calendar;
        } catch (Exception e) {
            return Calendar.getInstance();
        }

    }

    public static String getDate(Calendar calendar, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        String date = df.format(calendar.getTime());
        return date;
    }

    public static String getDayOfWeek(Calendar calendar) {
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.SUNDAY) {
            return "日";
        }
        if (day == Calendar.MONDAY) {
            return "一";
        }
        if (day == Calendar.TUESDAY) {
            return "二";
        }
        if (day == Calendar.WEDNESDAY) {
            return "三";
        }
        if (day == Calendar.THURSDAY) {
            return "四";
        }
        if (day == Calendar.FRIDAY) {
            return "五";
        }
        if (day == Calendar.SATURDAY) {
            return "六";
        }
        return "?";
    }

}
