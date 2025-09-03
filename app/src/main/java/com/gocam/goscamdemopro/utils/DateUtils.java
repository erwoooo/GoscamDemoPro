package com.gocam.goscamdemopro.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.gocam.goscamdemopro.cloud.data.entity.DayTime;
import com.gos.platform.device.domain.RecFileInfo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Administrator on 2017/6/24 0024.
 */

public class DateUtils {
    public static final String Pattern_yyyyMMdd = "yyyyMMdd";
    public static final String Pattern_yyyy_MM_dd = "yyyy-MM-dd";
    public static final String Pattern_yyyy_MM_dd_baby = "yyyy/MM/dd";
    public static final String Pattern_yyyy_MM_dd_1 = "yyyy.MM.dd";
    public static final String Pattern_yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";
    public static final String Pattern_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public static final String Pattern_yyyy_MM_dd_HH_mm_ss2 = "yyyy/MM/dd HH:mm:ss";
    public static final String Pattern_HH_mm_ss = "HH:mm:ss";
    public static final String Pattern_HH_mm = "HH:mm";
    public static final String Pattern_yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public static final String Pattern_mm_dd = "MM-dd";
    public static final String Pattern_yyyy_MM = "yyyy.MM";


    public static long stringToTime(String pattern, String source) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(source).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public static long stringToTime0TimeZone(String pattern, String source) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        TimeZone timeZone = TimeZone.getTimeZone("GMT+0");
        sdf.setTimeZone(timeZone);
        try {
            return sdf.parse(source).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static long stringToTimeWithTimeZone(String pattern, String source, String timeZone){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            TimeZone zone = TimeZone.getTimeZone(timeZone);
            sdf.setTimeZone(zone);
            return sdf.parse(source).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getDayDate() {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        res = simpleDateFormat.format(date);
        return "< " + res + " >";
    }

    /**
     * 字符串转换成日期
     *
     * @param str
     * @return date
     */
    public static Date strToDate(String str, SimpleDateFormat format) {

//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static int getHH(int seconds){
        return seconds / 3600;
    }

    public static int getMM(int seconds){
        return seconds % 3600 / 60;
    }

    public static int getSS(int seconds){
        return seconds % 3600 % 60;
    }

    public static String getPlayTime(int seconds) {
        int temp;
        StringBuffer sb = new StringBuffer();
        temp = seconds / 3600;
        sb.append((temp < 10) ? "0" + temp + ":" : "" + temp + ":");

        temp = seconds % 3600 / 60;
        sb.append((temp < 10) ? "0" + temp + ":" : "" + temp + ":");

        temp = seconds % 3600 % 60;
        sb.append((temp < 10) ? "0" + temp : "" + temp);
        return sb.toString();
    }

    public static String getPlayTimeMin(int seconds) {
        int temp;
        StringBuffer sb = new StringBuffer();
        temp = seconds / 3600;
        if(temp>0)
            sb.append((temp < 10) ? "0" + temp + ":" : "" + temp + ":");

        temp = seconds % 3600 / 60;
        sb.append((temp < 10) ? "0" + temp + ":" : "" + temp + ":");

        temp = seconds % 3600 % 60;
        sb.append((temp < 10) ? "0" + temp : "" + temp);
        return sb.toString();
    }

    public static String getPlayTimeHour(int seconds) {
        int temp;
        StringBuffer sb = new StringBuffer();
        temp = seconds / 3600;
        sb.append((temp < 10) ? "0" + temp + ":" : "" + temp + ":");

        temp = seconds % 3600 / 60;
        sb.append((temp < 10) ? "0" + temp + ":" : "" + temp + ":");

        temp = seconds % 3600 % 60;
        sb.append((temp < 10) ? "0" + temp : "" + temp);
        return sb.toString();
    }

    public static String getTimeHHMM(int seconds){
        int temp;
        StringBuffer sb = new StringBuffer();
        temp = seconds / 3600;
        sb.append((temp < 10) ? "0" + temp + ":" : "" + temp + ":");

        temp = seconds % 3600 / 60;
        sb.append((temp < 10) ? "0" + temp : "" + temp);
        return sb.toString();
    }

    /**
     * add by zzkong
     * 时间戳转换为时间
     */
    public static String stampToDate(long s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(s);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * add by zzkong
     * 时间戳转换为时间
     */
    public static String stampToDate(long s,String pattern) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = new Date(s);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * add by zzkong
     * 时间戳转换为时间
     */
    public static String stampToDateBy0TimeZone(long s,String pattern) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        TimeZone timeZone = TimeZone.getTimeZone("GMT+0");
        simpleDateFormat.setTimeZone(timeZone);
        Date date = new Date(s);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 日期格式转换
     *
     * @param data
     * @param pattern1
     * @param pattern2
     * @return
     */
    public static String stampToDate(String data, String pattern1, String pattern2) {
        // 定义输入和输出的日期格式
        SimpleDateFormat inputFormat = new SimpleDateFormat(pattern1);
        SimpleDateFormat outputFormat = new SimpleDateFormat(pattern2);
        try {
            // 解析输入字符串为 Date 对象
            Date date = inputFormat.parse(data);

            // 格式化 Date 对象为输出字符串
            String formattedDate = outputFormat.format(date);
            return formattedDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 日期转换成字符串
     *
     * @param date
     * @return str
     */
    public static String dateToStr(Date date, SimpleDateFormat format) {

//        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String str = format.format(date);
        return str;
    }

    public static String getRecTime(RecFileInfo.TimeInfo timeInfo) {
        return addZero(timeInfo.m_hour) + ":" + addZero(timeInfo.m_minute) + ":" + addZero(timeInfo.m_second);
        // return timeInfo.m_year + "-" + timeInfo.m_month + "-" + timeInfo.m_day + " " + timeInfo.m_hour + ":" + timeInfo.m_minute + " "+timeInfo.m_second;
    }

    private static String addZero(int time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return time + "";
        }
    }


    public static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        return createGmtOffsetString(true, true, tz.getRawOffset());
    }

    public static String createGmtOffsetString(boolean includeGmt,
                                               boolean includeMinuteSeparator, int offsetMillis) {
        int offsetMinutes = offsetMillis / 60000;
        char sign = '+';
        if (offsetMinutes < 0) {
            sign = '-';
            offsetMinutes = -offsetMinutes;
        }
        StringBuilder builder = new StringBuilder(9);
        if (includeGmt) {
            builder.append("GMT");
        }
        builder.append(sign);
        appendNumber(builder, 2, offsetMinutes / 60);
        if (includeMinuteSeparator) {
            builder.append(':');
        }
        appendNumber(builder, 2, offsetMinutes % 60);
        return builder.toString();
    }

    private static void appendNumber(StringBuilder builder, int count, int value) {
        String string = Integer.toString(value);
        for (int i = 0; i < count - string.length(); i++) {
            builder.append('0');
        }
        builder.append(string);
    }



    private static final String TAG = "DataUtils";
    /**
     * 日历
     */
    public static List<DateBean> getCalendar(int currentYear, int currentMonth) {
        //实例化集合
        List<DateBean> list = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.clear();
        /**
         * 处理上个月月末
         */
        if (currentMonth - 1 == 0) {
            c.set(Calendar.YEAR, currentYear - 1);
            c.set(Calendar.MONTH, 11);
        } else {
            c.set(Calendar.YEAR, currentYear);
            c.set(Calendar.MONTH, (currentMonth - 2));
        }
        int last_sumDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DATE, last_sumDays);
        //得到一号星期几
        int weekDay = c.get(Calendar.DAY_OF_WEEK);
        if (weekDay < 7) {
            for (int i = weekDay - 1; i >= 0; i--) {
                Integer date = new Integer(last_sumDays - i);
                list.add(new DateBean(date, true, 0,
                        (currentMonth-1)<1?currentYear-1:currentYear,
                        (currentMonth-1)<1?12:(currentMonth-1)));
            }
        }
        /**
         * 处理当前月
         */
        c.clear();
        c.set(Calendar.YEAR, currentYear);
        c.set(Calendar.MONTH, currentMonth - 1);
        int currentsumDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= currentsumDays; i++) {
            Integer date = new Integer(i);
            list.add(new DateBean(date, true, 1, currentYear, currentMonth));
        }
        /**
         * 处理下个月月初
         */
        c.clear();
        if (currentMonth == 12) {
            c.set(Calendar.YEAR, currentYear + 1);
            c.set(Calendar.MONTH, 0);
        } else {
            c.set(Calendar.YEAR, currentYear);
            c.set(Calendar.MONTH, currentMonth);
        }
        c.set(Calendar.DATE, 1);
        int currentWeekDay = c.get(Calendar.DAY_OF_WEEK);
        if (currentWeekDay > 2 && currentWeekDay < 8) {
            for (int i = 1; i <= 8 - currentWeekDay; i++) {
                list.add(new DateBean(i, true, 2,
                        (currentMonth+1)>12?currentYear+1:currentYear,
                        (currentMonth+1)>12?1:(currentMonth+1)));
            }
        }
        return list;
    }

    /**
     * 获取上个月大小
     */
    public static Integer getLastMonth(int currentYear, int currentMonth) {
        //实例化集合
        List<Integer> list = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.clear();
        /**
         * 处理上个月月末
         */
        if (currentMonth - 1 == 0) {
            c.set(Calendar.YEAR, currentYear - 1);
            c.set(Calendar.MONTH, 11);
        } else {
            c.set(Calendar.YEAR, currentYear);
            c.set(Calendar.MONTH, (currentMonth - 2));
        }
        int last_sumDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DATE, last_sumDays);
        //得到一号星期几
        int weekDay = c.get(Calendar.DAY_OF_WEEK);
        if (weekDay < 7) {
            for (int i = weekDay - 1; i >= 0; i--) {
                list.add(i);
            }
        }
        Log.e(TAG, "getLastMonth: " + Integer.parseInt(timeInMillsTrasToDate(0)));
        return list.size() + Integer.parseInt(timeInMillsTrasToDate(0)) - 1;
    }

    /**
     * list转string字符串
     */
    public static String returnList(List<String> list) {
        if (null == list && list.size() == 0) {
            return "";
        } else {
            //去除空格
            String str = String.valueOf(list).replaceAll(" ", "");
            return str.substring(1, str.length() - 1);
        }
    }

    /**
     * 时间转换
     */
    @TargetApi(Build.VERSION_CODES.N)
    public static String timeInMillsTrasToDate(int formatType) {
        DateFormat formatter = null;
        switch (formatType) {
            case 0:
                formatter = new SimpleDateFormat("dd");
                break;
            case 1:
                formatter = new SimpleDateFormat("yyyy-MM");
                break;
            case 2:
                formatter = new SimpleDateFormat("yyyy");
                break;
            case 3:
                formatter = new SimpleDateFormat("MM");
                break;
        }
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

    public static class DateBean {
        private int year;//2020
        private int mon;//12
        private String dateString;//2019-05-12;
        private long dailyStartTime = 0L; //毫秒值，一天的开始时间
        private long dailyEndTime = 0L;   //毫秒值，一天的结束时间


        private int week;
        private boolean sign;
        //0 上月 1本月 2下月
        private int month;
        private boolean isFlag;

        private boolean isSelected;

        public boolean isSelected(){
            return isSelected;
        }

        public void setSelected(boolean isSelected){
            this.isSelected = isSelected;
        }

        public boolean isFlag() {
            return isFlag;
        }

        public void setFlag(boolean flag) {
            isFlag = flag;
        }

        public int getWeek() {
            return week;
        }

        public void setWeek(int week) {
            this.week = week;
        }

        public boolean isSign() {
            return sign;
        }

        public void setSign(boolean sign) {
            this.sign = sign;
        }

        private DayTime dayTime;
        public DayTime getDayTime(){
            return dayTime;
        }
        public void setDayTime(DayTime dayTime){
            this.dayTime = dayTime;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getYear(){
            return year;
        }

        public int getMon(){
            return mon;
        }

        public String getDateString(){
            return dateString;
        }

        public long getDailyStartTime(){
            return dailyStartTime;
        }

        public long getDailyEndTime(){
            return dailyEndTime;
        }

        public DateBean(int week, boolean sign, int month, int year, int mon) {
            this.week = week;
            this.sign = sign;
            this.month = month;

            this.year = year;
            this.mon = mon;

            //2019-02-12
            dateString = year+"-"+(mon>9?mon:("0"+mon))+"-"+(week>9?week:("0"+week));

            long time = DateUtils.stringToTime(Pattern_yyyy_MM_dd, dateString);
            dailyStartTime = CalendarAdjust.getDailyStartTime(time);
            dailyEndTime = CalendarAdjust.getDailyEndTime(time);
        }
    }

    /**
     * 时间转换  秒转分
     */
    public static String getMinutes(int s) {

        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;

        minute = s / 60;
        if (minute < 60) {
            second = s % 60;
            timeStr = unitFormat(minute) + ":" + unitFormat(second);
        } else {
            hour = minute / 60;
            if (hour > 99)
                return "";
            minute = minute % 60;
            second = s - hour * 3600 - minute * 60;
            timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
        }

        return timeStr;
    }

    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + i;
        else
            retStr = "" + i;
        return retStr;
    }

    public static String getTime(long time){
        String timeStr = "";
        long mTime = time/60;
        long sTime = time%60;

        long hTime = mTime/60;
        mTime = mTime%60;

        if(hTime > 0){
            timeStr = hTime>9 ? hTime+":" : "0"+hTime+":";
        }
        if(mTime > 0){
            timeStr+= mTime > 9 ? mTime+":" : "0"+mTime+":";
        }else {
            timeStr+="00:";
        }
        if(sTime > 0){
            timeStr+= sTime > 9 ? sTime+"" : "0"+sTime+"";
        }else{
            timeStr+="00";
        }
        return timeStr;
    }


    public static int getMonth(long timeMills){
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date(timeMills));
        return instance.get(Calendar.MONTH) + 1;
    }

    public static int getWeek(long timeMills){
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date(timeMills));
        return instance.get(Calendar.WEEK_OF_MONTH);
    }

}
