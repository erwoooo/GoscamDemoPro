package com.gocam.goscamdemopro.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

public class Util {
    /**
     * 获取手机设备唯一表示
     */
    @SuppressLint("MissingPermission")
    public static String getPhoneUUID(Context ct) {
        //wifi mac地址
        String wifiMac = getWifiMacAddress();
        dbg.D("Utils", "mac=" + wifiMac);
        //IMEI（imei）
        TelephonyManager tm = (TelephonyManager) ct.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = "";
        try {
            imei = tm.getDeviceId();
        } catch (Exception e) {
            dbg.D("Utils", "getDevice ex:" + e.getMessage());
        }
        dbg.D("Utils", "imei=" + imei);
        String szAndroidID = Settings.Secure.getString(ct.getContentResolver(), Settings.Secure.ANDROID_ID);
        dbg.D("Utils", "devId=" + szAndroidID);
        String szLongID = wifiMac + ":" + imei + ":" + szAndroidID + ":" + ct.getPackageName();
        dbg.D("Utils", "old_szLongID=" + szLongID);
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(szLongID.getBytes(), 0, szLongID.length());
        byte p_md5Data[] = m.digest();
        String m_szUniqueID = new String();
        for (int i = 0; i < p_md5Data.length; i++) {
            int b = (0xFF & p_md5Data[i]);
            if (b <= 0xF)
                m_szUniqueID += "0";
            m_szUniqueID += Integer.toHexString(b);
        }
        szLongID = m_szUniqueID.toUpperCase();
        dbg.D("Utils", "new_szLongID=" + szLongID);
        return szLongID;
    }

    public static String getWifiMacAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.getName().equals("wlan0")) {
                    byte[] addr = networkInterface.getHardwareAddress();
                    if (addr != null && addr.length > 0) {
                        StringBuilder buf = new StringBuilder();
                        for (byte b : addr) {
                            buf.append(String.format("%02X:", b));
                        }
                        if (buf.length() > 0) {
                            buf.deleteCharAt(buf.length() - 1);
                        }
                        String mac = buf.toString().trim();
                        dbg.D("Utils", "interfaceName=" + networkInterface.getName() + " >>> mac=" + mac);
                        return mac;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "00:00:00:00:00:00";
    }


    public static int analysisDataHeader(byte[] data) {
        int nFrameNo = byteArrayToInt_Little(data, 0);
        int nFrameType = byteArrayToInt_Little(data, 4);
        int nCodeType = byteArrayToInt_Little(data, 8);
        int nFrameRate = byteArrayToInt_Little(data, 12);
        int nTimestamp = byteArrayToInt_Little(data, 16);
        int sWidth = byteArrayToShort_Little(data, 20);
        int sHeight = byteArrayToShort_Little(data, 22);
        int nReserved = byteArrayToInt_Little(data, 24);
        int nDataSize = byteArrayToInt_Little(data, 28);
        //if (nCodeType == 12) {
        sWidth = byteArrayToShort_Little(data, 20);
        sHeight = byteArrayToShort_Little(data, 22);
        //}

        //if (nFrameType == 4 || nFrameType == 12) {
        nReserved = byteArrayToInt_Little(data, 24);
        //}
        Log.d("Header", "nFrameNo=" + nFrameNo
                + ",nFrameType=" + nFrameType
                + ",nCodeType=" + nCodeType
                + ",nFrameRate=" + nFrameRate
                + ",nTimestamp=" + nTimestamp
                + ",sWidth=" + sWidth
                + ",sHeight=" + sHeight
                + ",nReserved=" + nReserved
                + ",nDataSize=" + nDataSize);
        return nDataSize;
    }

    public static final short byteArrayToShort_Little(byte byt[], int nBeginPos) {
        return (short) ((0xff & byt[nBeginPos]) | ((0xff & byt[nBeginPos + 1]) << 8));
    }

    public static final int byteArrayToInt_Little(byte byt[], int nBeginPos) {
        return (0xff & byt[nBeginPos]) | (0xff & byt[nBeginPos + 1]) << 8
                | (0xff & byt[nBeginPos + 2]) << 16
                | (0xff & byt[nBeginPos + 3]) << 24;
    }
}
