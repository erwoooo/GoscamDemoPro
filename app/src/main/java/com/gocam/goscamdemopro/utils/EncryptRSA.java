package com.gocam.goscamdemopro.utils;

import android.text.TextUtils;

import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.EncryptUtils;

import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import kotlin.text.Charsets;

public class EncryptRSA {
    private static String RSA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDXIvie7Vmy1ohwjvy3Lj+BHEri\n" +
            "yUR0gODsnJM5VTHboVieKuvZZH3YyG3aiAGPQpb12yULgMTRWMFodwGDjqU4jldY\n" +
            "yx471646Q+zvaEd9aWfVu2MnT+1doMyBabVu5lPsLav9CKGfcDNnGEBBkQGk1m8z\n" +
            "4TPLoco8uwp2FTi0bwIDAQAB";

    /**
     * MD5加密
     *
     * @param encryptStr 需要加密的数据
     * @return 加密后的数据
     */
    public static String encrypt32(String encryptStr) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(encryptStr.getBytes());
            StringBuilder hexValue = new StringBuilder();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16)
                    hexValue.append("0");
                hexValue.append(Integer.toHexString(val));
            }
            encryptStr = hexValue.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return encryptStr;
    }

    //得到32位的uuid
    public static String getUUID32() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    private static String getDeviceSN() {
        String serialNumber = android.os.Build.SERIAL;
        return serialNumber;
    }

    public static String encrypt32UUID(String encryptStr) {
        try {
            return encrypt32(encryptStr);
        } catch (Exception e) {
            e.printStackTrace();
            return encryptStr;
        }
    }

    /**
     * RSA加密
     *
     * @param encryptData 需要加密的数据
     * @return 加密后的数据
     */
    public static String encryptRsa(String encryptData) {
        byte[] data = encryptData.getBytes(Charsets.UTF_8);
        byte[] publicKey = EncodeUtils.base64Decode(RSA_PUBLIC_KEY);
        byte[] encryptBytes = EncryptUtils.encryptRSA(data, publicKey, 1024, "RSA/None/PKCS1Padding");
        return EncodeUtils.base64Encode2String(encryptBytes);
    }

    public static String paramSign(TreeMap<String, String> params, String appendKey) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (TextUtils.isEmpty(entry.getValue().toString())) {
                continue;
            }
            stringBuilder.append(entry.getKey()).append(entry.getValue());
        }
        stringBuilder.append(appendKey);
        return EncryptUtils.encryptMD5ToString(stringBuilder.toString());
    }
}
