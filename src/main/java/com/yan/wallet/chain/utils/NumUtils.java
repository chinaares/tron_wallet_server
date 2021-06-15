package com.yan.wallet.chain.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumUtils {


    public static String longToHexStr(long value) {
        return "0x" + Long.toHexString(value);
    }

    public static Long hexStrToLong(String value) {
        return Long.valueOf(value.substring(2), 16);
    }

    public static BigInteger amountToBigInteger(BigDecimal amount, int scale) {
        return amount.multiply(BigDecimal.TEN.pow(scale)).toBigInteger();
    }

    public static BigDecimal bigIntegerToAmount(BigInteger value, int scale) {
        return new BigDecimal(value).divide(BigDecimal.TEN.pow(scale));
    }

    public static BigInteger hexToBigInteger(String strHex) {
        if (strHex.length() > 2) {
            if (strHex.charAt(0) == '0' && (strHex.charAt(1) == 'X' || strHex.charAt(1) == 'x')) {
                strHex = strHex.substring(2);
            }
            return new BigInteger(strHex, 16);
        }
        return BigInteger.ZERO;
    }

    public static BigDecimal hexStrToAmount(String hexStr, int scale) {
        return bigIntegerToAmount(hexToBigInteger(hexStr), scale);
    }

    public static String bigInterToHexString(BigInteger value) {
        return "0x" + value.toString(16);
    }

    public static void main(String[] args) {
        System.out.println(hexStrToLong("0x9450c0"));
    }
}
