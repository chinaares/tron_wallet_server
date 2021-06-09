package com.yan.wallet.chain.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public class YanStrUtils {

    private static String source = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static String NumSource = "0123456789";
    private static String RANDOM_STR = source + NumSource;

    public static boolean isEmpty(String str) {
        if(str != null && str.length() != 0) {
            for(int i = 0; i < str.length(); ++i) {
                char c = str.charAt(i);
                if(c != 32 && c != 9 && c != 13 && c != 10) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789abcdef".indexOf(c);
        return b;
    }


    public static String getYanString(String prefix) {
        StringBuffer sb = new StringBuffer();
        sb.append(prefix);
        sb.append(YanDateUtils.getDateString(YanDateUtils.TIME_STRING_3));
        sb.append(YanStrUtils.getRandomCode(6));
        return sb.toString();
    }

    public static String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0) {
            return null;
        }
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    public static String getRandomStr(int length) {
        String str = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            str += RANDOM_STR.charAt(random.nextInt(RANDOM_STR.length()));
        }

        return str;
    }


    public static String binaryString2hexString(String bString) {
        if (YanObjectUtils.isEmpty(bString) || bString.length() % 8 != 0) {
            return null;
        }
        StringBuffer tmp=new StringBuffer();
        int iTmp = 0;
        for (int i = 0; i < bString.length(); i += 4) {
            iTmp = 0;
            for (int j = 0; j < 4; j++) {
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));
        }
        return tmp.toString();
    }

    public static String getRandomCode(int length) {
        return RandomStringUtils.random(length, source);
    }

    public static String lPend(String value, String append, Integer length) {
        if (value == null) {
            value = "";
        }

        if (append == null) {
            append = "_";
        }
        while (value.length() < length) {
            value = append + value;
        }

        return value;
    }

    public static String rPend(String value, String append, Integer length) {
        if (value == null) {
            value = "";
        }

        if (append == null) {
            append = "_";
        }
        while (value.length() < length) {
            value = value + append;
        }

        return value;
    }

    public static String getRandomNumCode(int length) {
        String str = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            str += NumSource.charAt(random.nextInt(NumSource.length()));
        }

        return str;
    }

    public static String getFixedStr(String str, Integer length) {
        if (null == str || length < 1) {
            return null;
        }
        if (str.length() < length) {
            return str;
        }
        return str.substring(0, length);
    }
}
