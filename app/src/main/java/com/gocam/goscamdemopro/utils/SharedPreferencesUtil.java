package com.gocam.goscamdemopro.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.gocam.goscamdemopro.GApplication;

import ulife.goscam.com.loglib.dbg;

public class SharedPreferencesUtil {
    private static final String TAG = "SharedPreferencesUtil";
    private static String NAME = "Ulifeplus";

    public static class SpreContant {
        public static String SP_USER_NAME = "USER_NAME";
        //public static String SP_SAVE_PSW = "SAVE_PSW_";
        public static String SP_SAVE_PSW_N = "SAVE_PSW_N";
        public static String SP_COUNTRY_NO = "COUNTRY_NO";
        public static String SP_SAVE_TIME = "SaveTime";
        public static String SP_IS_SAVE = "IsSavePsw";
        public static String SP_IS_LOGOUT = "IsLogout";
        public static String SP_REMENBER_PSW = "REMENBER_PSW_";
        public static String SP_CODE_PROMT = "SP_CODE_PROMT_";
        public static String SP_TALK_TYPE = "SP_TALK_TYPE_";
        public final static String SP_SAVE_ACCOUNT = "SP_SAVE_ACCOUNT";//保存账号
        public final static String SP_ACCESS_TOKEN = "SP_ACCESS_TOKEN_";
        public final static String SP_REFRESH_TOKEN = "SP_REFRESH_TOKEN_";

        public static String SP_UPDATE_DOWNLOAD_ID = "SP_UPDATE_DOWNLOAD_ID";
        public static String SP_UPDATE_FILE_MD5 = "SP_UPDATE_FILE_MD5";

        public static String SP_VERSION_CODE = "versionCode";
        public static String SP_SERVER_TYPE = "SERVER_TYPE";
        public static String SP_Full_DUPLEX = "SP_FUll_DUPLEX";
        public static final String SP_ = "_";

        public static final String SP_LOGIN_TOKEN = "loginToken";
        public static final String IS_FIRST_DOWNLOAD_APP = "IS_FIRST_DOWNLOAD_APP_V5.0.55";
        public static final String IS_FIRST_ADD_DEVICE = "IS_FIRST_ADD_DEVICE";

        public static final String APP_VERSION_CODE = "APP_VERSION_CODE";
        public static final String UPGRADE_INIT_TIME = "UPGRADE_INIT_TIME"; // 首次提示升级的时间
        public static final String UPGRADE_INIT_COUNT = "UPGRADE_INIT_COUNT"; // 提示升级次数
        public static final String UPLOAD_IM_TOKEN = "UPLOAD_IM_TOKEN"; // im token

        public static final String SP_UUID = "sp_uuid";

        public static final String SP_SL_SOFT_VERSION = "sp_sl_soft_version_";

        public static final String BT_LOCK_WIFI_BT = "bt_lock_wifi_bt";

        public static final String V_PHOTO_DEVICE_CAM = "V_PHOTO_DEVICE_CAM";

        public static final String IS_GANT_ALERT_WINDOW = "IS_GANT_ALERT_WINDOW";

    }

    private static Context mContext;

    public static void setContext(Context context) {
        mContext = context;
    }

    private static SharedPreferences getSharedPreferences() {
        Context ct = GApplication.app;
        if (ct == null)
            ct = mContext;
        return ct.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static void putString(String key, String val) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, val);
        editor.commit();
    }

    public static void putInt(String key, int val) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(key, val);
        editor.commit();
    }

    public static void putLong(String key, long val) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putLong(key, val);
        editor.commit();
    }

    public static void putBoolean(String key, boolean val) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, val);
        editor.commit();
    }

    public static String getString(String key, String defaultVal) {
        SharedPreferences sp = getSharedPreferences();
        return sp.getString(key, defaultVal);
    }

    public static int getInt(String key, int defaultVal) {
        SharedPreferences sp = getSharedPreferences();
        return sp.getInt(key, defaultVal);
    }

    public static long getLong(String key, long defaultVal) {
        SharedPreferences sp = getSharedPreferences();
        return sp.getLong(key, defaultVal);
    }

    public static boolean getBoolean(String key, boolean defaultVal) {
        SharedPreferences sp = getSharedPreferences();
        return sp.getBoolean(key, defaultVal);
    }

    public static void remove(String key) {
        getSharedPreferences().edit().remove(key).commit();
    }


    public static String getKey(String sp, String... strings) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(sp);
        for (String s : strings) {
            stringBuilder.append(SpreContant.SP_).append(s);
        }
        String str = stringBuilder.toString();
        dbg.D(TAG, "getKey=" + str);
        return str;
    }
}