package android.goscam.qrcode;

import android.app.Application;
import android.text.TextUtils;

import ulife.goscam.com.loglib.dbg;

public class QRCodec {
    public static final int ACTION_FACTORY = 0;

    public static final int ACTION_SHARING = 1;

    public static final int ACTION_SMART_CFG = 2;

    public static final int ACTION_FACTORY_V1 = 3;

    public static final int ACTION_FACTORY_V3 = 4;

    public static final int QRC_OPTION_QUERY = 2;
    // private static final int QRC_OPTION_ENABLE = 1;
    // private static final int QRC_OPTION_DISABLE = 0;

    // private static final int QRC_TAG_ACTION = 0;
    private static final int QRC_TAG_IPC_ID = 1;// ID
    // private static final int QRC_TAG_IPC_ABI = 2;// 能力级
    private static final int QRC_TAG_IPC_MAC = 3;// MAC
    private static final int QRC_TAG_STREAM_USR = 4;
    private static final int QRC_TAG_STREAM_PWD = 5;
    private static final int QRC_TAG_SMART_SSID = 6;// Smart配置WiFi-SSID
    private static final int QRC_TAG_SMART_PWD = 7;// Smart配置WiFi-密码
    // private static final int QRC_TAG_RESERVE = 8;o
    // private static final int QRC_TAG_ENCRYPTMTH = 9; // 加解密/编解码方法
    // private static final int QRC_TAG_VERSION = 10; // GQRC 版本号

    public static final int QRC_BIT_ENCRYPTED_IC = 0x2; // 是否有加密IC
    public static final int QRC_BIT_PIR = 0x4; // 是否有PIR传感器，0:无，1:有，下同
    public static final int QRC_BIT_PTZ = 0x8; // 是否有云台
    public static final int QRC_BIT_MICROPHONE = 0x10; // 是否有咪头/麦克风
    public static final int QRC_BIT_SPEAKER = 0x20; // 是否有喇叭
    public static final int QRC_BIT_SDCARD_SLOT = 0x40; // 是否有SD卡
    public static final int QRC_BIT_TEMPERATURE = 0x80; // 是否有温感探头
    public static final int QRC_BIT_AUTO_TIMEZONE = 0x100; // 是否支持同步时区
    public static final int QRC_BIT_NIGHT_VISON = 0x200; // 是否支持夜视
    public static final int QRC_BIT_ETHERNET_SLOT = 0x400; // 是否带网口
    public static final int QRC_BIT_SMART_WIFI = 0x800; // 设备端WiFi模块是否支持SmartConfig功能

    public String szDevID;
    public String szDevMAC;
    public String szUser;
    public String szPwd;
    public String szWifiSSID;
    public String szWifiPwd;
    public String reserve;
    public int action; // 0.出厂;1.分享;2.配置WIFI
    public int capability; // 设备能力集 对应 E_AbilityInfo
    public short encryptMth;
    public short version;

    public boolean isCompatV1 = false;

    public int c_device_type; // 设备类型900中性版101彩益100海尔、200NVR 、300VR
    private int un_resolution_0_flag; // 主码流分辨率大小 Width:高16位 Height:低16位
    // Ming@2016.06.14
    private int un_resolution_1_flag; // 子码流
    private int un_resolution_2_flag; // 第3路码流
    private int c_encrypted_ic_flag; // 是否有加密IC, 被硬解绑占用
    private int c_pir_flag; // 是否有PIR传感器，0:无，1:有，下同
    private int c_ptz_flag; // 是否有云台
    private int c_mic_flag; // 是否有咪头
    private int c_speaker_flag; // 是否有喇叭
    private int c_sd_flag; // 是否有SD卡
    private int c_temperature_flag; // 是否有温感探头
    private int c_timezone_flag; // 是否支持同步时区
    private int c_night_vison_flag; // 是否支持夜视
    private int c_ethernet_flag; // 是否带网卡
    /**
     * 是否支持smart扫描
     * 0代表不支持
     * 1代表7601smart
     * 2代表8188smart
     * 3代表6212a smart
     * 9代表不支持二维码扫描
     * 10代表只支持扫描二维码连接wifi
     * 11代表支持扫描二维码连接wifi和7601smart
     * 12代表支持扫描二维码连接wifi和8188smart
     * 13代表支持扫描二维码连接wifi和6212asmart
     * 14AP模式(新增添加方式)
     * 15代表AP添加+8188smart
     * 16代表AP添加+声波添加
     * 17代表声波添加
     * 18代表4G添加方式
     * 19代表二维码扫描和8811smart(支持WIFI 5G)
     */
    public int c_smart_connect_flag;
    private int c_motion_detection_flag; // 是否支持移动侦测
    private int c_record_duration_flag;

    public static QRCodec newRecognizedQRC(String qrtext) {
        if (TextUtils.isEmpty(qrtext)) {
            return null;
        }
        dbg.D("QR","qrtext="+qrtext);
        QRCodec qrc = new QRCodec();
        String[] v0Strings = new String[6];
        int[] v0Flags = new int[4];// 3
        int[] v1Flags = new int[4];// 4
        char[] v1AbiFlags = new char[16];// 13
        int offset = 0;
        recognizeCompat(qrtext, v0Flags, v0Strings, v1Flags, v1AbiFlags);
        offset = 0;
        qrc.action = v0Flags[offset++];
        qrc.capability = v0Flags[offset++];
        qrc.isCompatV1 = v0Flags[offset++] == 1;
        dbg.D("QR","qr_version="+v0Flags[2] + ",offset="+offset);
        offset = 0;
        qrc.szDevID = v0Strings[offset++];
        if (TextUtils.isEmpty(qrc.szDevID)) {
            return null;
        }
        qrc.szDevMAC = v0Strings[offset++];
        qrc.szUser = v0Strings[offset++];
        qrc.szPwd = v0Strings[offset++];
        qrc.szWifiSSID = v0Strings[offset++];
        qrc.szWifiPwd = v0Strings[offset++];
        for (int i = 0; i < 6; i++) {
            dbg.i("v0Strings[" + i + "]=" + v0Strings[i]);
        }
        offset = 0;
        qrc.c_device_type = v1Flags[offset++];
        qrc.un_resolution_0_flag = v1Flags[offset++];
        qrc.un_resolution_1_flag = v1Flags[offset++];
        qrc.un_resolution_2_flag = v1Flags[offset++];
        offset = 0;
        qrc.c_encrypted_ic_flag = v1AbiFlags[offset++];
        qrc.c_pir_flag = v1AbiFlags[offset++];
        qrc.c_ptz_flag = v1AbiFlags[offset++];
        qrc.c_mic_flag = v1AbiFlags[offset++];
        qrc.c_speaker_flag = v1AbiFlags[offset++];
        qrc.c_sd_flag = v1AbiFlags[offset++];
        qrc.c_temperature_flag = v1AbiFlags[offset++];
        qrc.c_timezone_flag = v1AbiFlags[offset++];
        qrc.c_night_vison_flag = v1AbiFlags[offset++];
        qrc.c_ethernet_flag = v1AbiFlags[offset++];
        qrc.c_smart_connect_flag = v1AbiFlags[offset++];
        qrc.c_motion_detection_flag = v1AbiFlags[offset++];
        qrc.c_record_duration_flag = v1AbiFlags[offset++];
        qrc.isCompatV1 = qrc.c_device_type != 0;
        //做第三版能力集兼容时强制置为false
        qrc.isCompatV1 = false;
        dbg.e("qrc=" + qrc);
        return qrc;
    }

    private QRCodec() {
    }

    public boolean isHaier() {
        // Haier
        return c_device_type == 100;
    }

    public boolean isElife() {
        // CaiYi
        return c_device_type == 101;
    }

    //是否支持硬解绑
    public boolean isSupportHardUnbind(){
        return  c_encrypted_ic_flag == 1;
    }

    public boolean isGoscam() {
        return c_device_type == 0 || c_device_type == 900;
    }

    /**
     * IPC扫描二维码完成WiFi配置
     */
    public void rawIDToQRConfig() {
        action = ACTION_FACTORY;
        capability &= ~QRC_BIT_SMART_WIFI;
    }

    /**
     * IPC通过WiFi Samrt功能完成WiFi配置
     */
    public void rawIDToWiFiConfig() {
        action = ACTION_FACTORY;
        capability |= QRC_BIT_SMART_WIFI;
    }

    /**
     * IPC通过网线进行安装
     */
    public void rawIDToLanConfig() {
        action = ACTION_FACTORY;
        capability |= QRC_BIT_ETHERNET_SLOT;
    }

    public boolean isFactoryProduction() {
        return action == ACTION_FACTORY || action == ACTION_FACTORY_V1 || action == ACTION_FACTORY_V3;
    }

    public boolean isIPCSharing() {
        return action == ACTION_SHARING;
    }

    public boolean isSmartConfiguration() {
        return action == ACTION_SMART_CFG;
    }

    // public String getQrText() {
    // if (mCGetQrCode == 0)
    // return null;
    // return buildQRText(mCGetQrCode);
    // }

    // public synchronized void release() {
    // if (mCGetQrCode != 0) {
    // freeCGetQrCode(mCGetQrCode);
    // mCGetQrCode = 0;
    // }
    // }

    public boolean hasEncryptedIC() {
        if (isCompatV1) {
            return c_encrypted_ic_flag == 1;
        }
        return checkAbilityInt(capability, QRC_BIT_ENCRYPTED_IC, QRC_OPTION_QUERY);
    }

    public boolean hasPIR() {
        if (isCompatV1) {
            return c_pir_flag == 1;
        }
        return checkAbilityInt(capability, QRC_BIT_PIR, QRC_OPTION_QUERY);
    }

    public boolean hasPTZ() {
        if (isCompatV1) {
            return c_ptz_flag == 1;
        }
        return checkAbilityInt(capability, QRC_BIT_PTZ, QRC_OPTION_QUERY);
    }

    public boolean hasMIC() {
        if (isCompatV1) {
            return c_mic_flag == 1;
        }
        return checkAbilityInt(capability, QRC_BIT_MICROPHONE, QRC_OPTION_QUERY);
    }

    public boolean hasSpeaker() {
        if (isCompatV1) {
            return c_speaker_flag == 1;
        }
        return checkAbilityInt(capability, QRC_BIT_SPEAKER, QRC_OPTION_QUERY);
    }

    public boolean hasSdcardSlot() {
        if (isCompatV1) {
            return c_sd_flag == 1;
        }
        return checkAbilityInt(capability, QRC_BIT_SDCARD_SLOT, QRC_OPTION_QUERY);
    }

    public boolean hasTemperatureSensor() {
        if (isCompatV1) {
            return c_temperature_flag == 1;
        }
        return checkAbilityInt(capability, QRC_BIT_TEMPERATURE, QRC_OPTION_QUERY);
    }

    public boolean isAutoTimezone() {
        if (isCompatV1) {
            return c_timezone_flag == 1;
        }
        return checkAbilityInt(capability, QRC_BIT_AUTO_TIMEZONE, QRC_OPTION_QUERY);
    }

    public boolean hasNightVison() {
        if (isCompatV1) {
            return c_night_vison_flag == 1;
        }
        return checkAbilityInt(capability, QRC_BIT_NIGHT_VISON, QRC_OPTION_QUERY);
    }

    public boolean hasEthernetSlot() {//使用第三版能力集，qr_version=2
        if (isCompatV1) {
            return c_ethernet_flag == 1;
        }

        if (c_ethernet_flag == 2 || c_ethernet_flag == 1) {
            return true;
        }
        return false;
//        return checkAbilityInt(capability, QRC_BIT_ETHERNET_SLOT, QRC_OPTION_QUERY);
    }

    public boolean hasAp(){//是否支持AP模式
        return c_smart_connect_flag == 14 || c_smart_connect_flag == 15 || c_smart_connect_flag == 16;
    }

    public boolean hasVoice(){
        return c_smart_connect_flag == 16 || c_smart_connect_flag == 17;
    }

    public boolean has4G(){
        return c_smart_connect_flag == 18;
    }

    public boolean hasSmartWifi() {
        if (isCompatV1) {
            return c_smart_connect_flag != 0;
        }
        switch (c_smart_connect_flag) {
            case 1:
            case 2:
            case 3:
            case 11:
            case 12:
            case 13:
            case 15:
                return true;
            default:
                return false;
        }
//        return checkAbilityInt(capability, QRC_BIT_SMART_WIFI, QRC_OPTION_QUERY);
    }

    public int getSmartWifiModule() {
        if (c_smart_connect_flag < 0) {
            c_smart_connect_flag = 0;
            return 0;
        }
        return c_smart_connect_flag;
    }

    public boolean hasScanQrConWifi() {
        switch (c_smart_connect_flag) {
            case 10:
            case 11:
            case 12:
            case 13:
            case 19:
                return true;
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder()//
                .append(getClass().getSimpleName())//
                .append(":act=").append(action)//
                .append(",cap=").append(capability)//
                .append(",id=").append(szDevID)//
                .append(",mac=").append(szDevMAC)//
                .append(",usr=").append(szUser)//
                .append(",pwd=").append(szPwd)//
                .append(",wifissid=").append(szWifiSSID)//
                .append(",wifipwd=").append(szWifiPwd);//
        // .append(",resver=").append(reserve)//
        if (c_device_type != 0) {
            sb.append(",c_device_type=").append(c_device_type);
            sb.append(",un_resolution_0_flag=").append(un_resolution_0_flag);
            sb.append(",un_resolution_1_flag=").append(un_resolution_1_flag);
            sb.append(",un_resolution_2_flag=").append(un_resolution_2_flag);
            sb.append(",c_encrypted_ic_flag=").append(c_encrypted_ic_flag);
            sb.append(",c_pir_flag=").append(c_pir_flag);
            sb.append(",c_ptz_flag=").append(c_ptz_flag);
            sb.append(",c_mic_flag=").append(c_mic_flag);
            sb.append(",c_speaker_flag=").append(c_speaker_flag);
            sb.append(",c_sd_flag=").append(c_sd_flag);
            sb.append(",c_temperature_flag=").append(c_temperature_flag);
            sb.append(",c_timezone_flag=").append(c_timezone_flag);
            sb.append(",c_night_vison_flag=").append(c_night_vison_flag);
            sb.append(",c_ethernet_flag=").append(c_ethernet_flag).append(",eth=" + hasEthernetSlot());
            sb.append(",c_smart_connect_flag=").append(c_smart_connect_flag).append(",smart=" + c_smart_connect_flag);
            sb.append(",c_motion_detection_flag=").append(c_motion_detection_flag);
            sb.append(",c_record_duration_flag=").append(c_record_duration_flag);
        }
        return sb.toString();
    }

    public static boolean hasEthernetSlot(int abilities) {
        return checkAbilityInt(abilities, QRC_BIT_ETHERNET_SLOT, QRC_OPTION_QUERY);
    }

    // public static final void parse(long CGetQrCode, QRCodec qrcfg) {
    // qrcfg.action = getAction(CGetQrCode);
    // qrcfg.capability = getDevCap(CGetQrCode);
    // qrcfg.szDevID = getString(CGetQrCode, QRC_TAG_IPC_ID);
    // qrcfg.szDevMAC = getString(CGetQrCode, QRC_TAG_IPC_MAC);
    // qrcfg.szUser = getString(CGetQrCode, QRC_TAG_STREAM_USR);
    // qrcfg.szPwd = getString(CGetQrCode, QRC_TAG_STREAM_PWD);
    // qrcfg.szWifiSSID = getString(CGetQrCode, QRC_TAG_SMART_SSID);
    // qrcfg.szWifiPwd = getString(CGetQrCode, QRC_TAG_SMART_PWD);
    // freeCGetQrCode(CGetQrCode);
    // }

    public static native void checkValidApp(Application app);

    public static native String getSharingQRTextV1(String strDevID, String strStreamUser, String strStreamPwd);

    public static native String getSmartCfgQRTextV1(String strDevID, String strWiFiSSID, String strWiFiPwd);

    public static native void recognizeCompat(String qrtext, int[] v0Flags, String[] v0Strings, int[] v1Flags, char[] v1AbiFlags);

    // /**
    // * create a CGetQrCode pointer in C by action
    // *
    // * @param action
    // * @return
    // */
    // public static native long newCGetQrCode(int action);
    //
    // /**
    // * create a CGetQrCode pointer in C by qrtext
    // *
    // * @param qrtext
    // * @return CGetQrCode pointer in C
    // */
    // public static native long recognize(String qrtext);
    //
    // public static native void setString(long CGetQrCode, int tag, String
    // val);
    //
    // public static native String getString(long CGetQrCode, int tag);
    //
    public static native boolean checkAbilityInt(int abilities, int bitFlag, int optFlag);
    //
    // public static native boolean checkAbility(long CGetQrCode, int bitFlag,
    // int optFlag);
    //
    // /**
    // * build the QrText by a CGetQrCode pointer in C
    // *
    // * @param CGetQrCode
    // * @return
    // */
    // public static native String buildQRText(long CGetQrCode);
    //
    // public static native void freeCGetQrCode(long CGetQrCode);
    //
    // private static native void setAction(long CGetQrCode, int action);
    //
    // private static native void setDevCap(long CGetQrCode, int capability);
    //
    // private static native void setReserve(long CGetQrCode, byte[] reserve);
    //
    // private static native int getAction(long CGetQrCode);
    //
    // private static native int getDevCap(long CGetQrCode);
    //
    // private static native byte[] getReserve(long CGetQrCode);
}
