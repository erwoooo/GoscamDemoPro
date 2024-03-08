package com.gocam.goscamdemopro.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class Aes128 {

    private static final String ENCRYPT_KEY = "ABCDEFGHIJKLMNOP";

    // 加密
    public static String Encrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            return null;
        }
        byte[] raw = sKey.getBytes("UTF-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// "算法/模式/补码方式"
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("UTF-8"));
        return new String(Base64.encode(encrypted, Base64.DEFAULT), "UTF-8");// 此处使用BASE64做转码功能，同时能起到2次加密的作用。
    }

    // 加密
    public static byte[] Encrypt(byte[] sSrc, String sKey) throws Exception {
        if (sKey == null) {
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            return null;
        }
        byte[] raw = sKey.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// "算法/模式/补码方式"
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc);
        return Base64.encode(encrypted, Base64.DEFAULT);// 此处使用BASE64做转码功能，同时能起到2次加密的作用。
    }

    // 解密
    public static String Decrypt(String sSrc, String sKey) throws Exception {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                return null;
            }
            byte[] raw = sKey.getBytes("UTF-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = Base64.decode(sSrc.getBytes("UTF-8"), Base64.DEFAULT);// 先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original, "UTF-8");
                return originalString;
            } catch (Exception e) {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // 解密
    public static byte[] Decrypt(byte[] sSrc, String sKey) throws Exception {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = Base64.decode(sSrc, Base64.DEFAULT);// 先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                return original;
            } catch (Exception e) {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String encrypt(String originalStr) {
        if (!TextUtils.isEmpty(originalStr))
            try {
                return Aes128.Encrypt(originalStr, ENCRYPT_KEY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return "";
    }

    public static String encrypt(String originalStr, String sKey) {
        if (!TextUtils.isEmpty(originalStr) && !TextUtils.isEmpty(sKey))
            try {
                return Aes128.Encrypt(originalStr, sKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return "";
    }

    public static String decrypt(String encryptStr, String sKey) {
        if (!TextUtils.isEmpty(encryptStr) && !TextUtils.isEmpty(sKey))
            try {
                return Aes128.Decrypt(encryptStr, sKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return "";
    }

    public static String getKey(long saveTime) {
        return String.format("%016d", saveTime);
    }


    public static String decryptAndroid(String content, String key) {
        try {
            return new String(decrypt(hexStringToByteArray(content), key.getBytes(StandardCharsets.UTF_8)), "UTF-8");
        } catch (Exception var3) {
            var3.printStackTrace();
        }
        return null;
    }

    public static byte[] decrypt(byte[] content, byte[] key) {
        if (content != null && content.length != 0) {
            try {
                //区别在这里
                SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(2, secretKeySpec);
                byte[] result = cipher.doFinal(content);
                return result;
            } catch (Exception var9) {
                throw new RuntimeException(var9);
            }
        } else {
            return null;
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

}

