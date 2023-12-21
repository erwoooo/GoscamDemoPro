//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.broadcom.cooee;

public class Cooee {
    public Cooee() {
    }

    public static int send(String ssid, String password) {
        return send(ssid, password, 0, "");
    }

    public static int send(String ssid, String password, int ip) {
        return send(ssid, password, ip, "");
    }

    public static int send(String ssid, String password, int ip, String encryptionKey) {
        return nativeSend(ssid.getBytes(), password.getBytes(), encryptionKey.getBytes(), ip);
    }

    public static native int nativeSend(byte[] var0, byte[] var1, byte[] var2, int var3);

    public static native int SetPacketInterval(int var0);

    static {
        System.loadLibrary("cooee");
    }
}
