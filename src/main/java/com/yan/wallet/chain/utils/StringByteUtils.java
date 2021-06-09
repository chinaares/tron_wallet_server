package com.yan.wallet.chain.utils;

import java.io.UnsupportedEncodingException;

public class StringByteUtils {
    /**
     * 字节转10进制
     */
    public static int byte2Int(byte b){
        return  (int) b;
    }

    /**
     * 10进制转字节
     */
    public static byte int2Byte(int i){
        return (byte) i;
    }

    /**
     * 字节数组转16进制字符串
     */
    public static String bytes2HexString(byte[] b) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }

        return sb.toString();
    }

    /**
     * 字符转换为字节
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 16进制字符串转字节数组
     */
    public static byte[] hexString2Bytes(String hex) {

        if (hex == null || hex.trim().equals("")){
            return null;
        }
        else if (hex.length()%2 != 0){
            return null;
        }
        else{
            hex = hex.toUpperCase();
            int len = hex.length()/2;
            byte[] b = new byte[len];
            char[] hc = hex.toCharArray();
            for (int i=0; i<len; i++){
                int p=2*i;
                b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p+1]));
            }
            return b;
        }

    }

    /**
     * 字节数组转字符串
     */
    public static String bytes2String(byte[] b) {
        try {
            return new String(b,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /**
     * 字符串转字节数组
     */
    public static byte[] string2Bytes(String s){
        return s.getBytes();
    }

    /**
     * 16进制字符串转字符串
     */
    public static String hex2String(String hex) {
        return bytes2String(hexString2Bytes(hex));
    }

    /**
     * 字符串转16进制字符串
     */
    public static String string2HexString(String s) {
        return bytes2HexString(string2Bytes(s));
    }


}
