package com.yan.wallet.chain.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class YanDateUtils {
    public static final String TIME_STRING_1 = "yyyy-MM-dd";
    public static final String TIME_STRING_2 = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_STRING_3 = "yyyyMMddHHmmss";
    public static final String TIME_STRING_4 = "yyyyMMddHHmmssSSS";

    public static Date parseDate(String dateString, String formatString) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatString);
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 格式化时间
     * @param date
     * @param formatString
     * @return
     */
    public static String getDateString(Date date, String formatString) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatString);
        return sdf.format(date);
    }

    /**
     * 格式化当前时间
     * @param formatString
     * @return
     */
    public static String getDateString(String formatString) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatString);
        return sdf.format(new Date());
    }
}
