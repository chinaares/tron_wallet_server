//package com.yan.wallet.chain.tron.test;
//
//import com.yan.wallet.chain.utils.NumUtils;
//import com.yan.wallet.chain.utils.StringByteUtils;
//import com.yan.wallet.chain.utils.YanStrUtils;
//import org.tron.common.utils.ByteArray;
//import org.tron.common.utils.ByteUtil;
//
//import java.math.BigInteger;
//
//public class Test {
//    public static void main(String[] args) {
//        for (long i = 1; i < 20000000000L; i++) {
//            byte[] value1 = longTo32Bytes(i);
//            byte[] value2 = bigInterToByte(BigInteger.valueOf(i));
//            String v1 = StringByteUtils.bytes2HexString(value1);
//            String v2 = StringByteUtils.bytes2HexString(value2);
//            if (!v1.equals(v2)) {
//                System.out.println("value1 -> " + v1);
//                System.out.println("value2 -> " + v2);
//            } else {
//                if (i%100 == 0) {
//                    System.out.println(i);
//                }
//            }
//        }
//    }
//
//    private static byte[] longTo32Bytes(long value) {
//        byte[] longBytes = ByteArray.fromLong(value);
//        byte[] zeroBytes = new byte[24];
//        return ByteUtil.merge(zeroBytes, longBytes);
//    }
//
//    public static byte[] bigInterToByte(BigInteger value) {
//        String value1 = NumUtils.bigInterToHexString(value);
//        if (value1.startsWith("0x") || value1.startsWith("0X")) {
//            value1 = value1.substring(2);
//        }
//        value1 = YanStrUtils.lPend(value1, "0", 64);
//        return StringByteUtils.hexString2Bytes(value1);
//    }
//
//}