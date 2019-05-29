package com.nix.jingxun.addp.web.common.util;


import com.jcraft.jsch.jce.MD5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author keray
 * @date 2019/05/29 13:59
 */
public final class MD5Util {
    public static String md5(String source) {
        if (source == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] inputByteArray = source.getBytes();
            messageDigest.update(inputByteArray);
            return byteArrayToHex( messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
    private static String byteArrayToHex(byte[] byteArray) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);
    }
}
