package com.gocam.goscamdemopro.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;


import com.gocam.goscamdemopro.GApplication;
import com.gocam.goscamdemopro.R;
import com.gocam.goscamdemopro.entity.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {
    private static String MainDir = "Ulifeplus";
    private static final String VideoDir = "Video";
    private static final String SnapshotDir = "Pic";
    private static final String alarm = "Alarm";
    private static final String DownloadDir = "Download";
    private static final String CacheDir = "Cache";
    private static final String Temp = "Temp";
    private static final String Preset = "Preset";

    static {
        MainDir = GApplication.app.getString(R.string.app_name);
    }

    /**
     * 获取根目录
     */
    public static File getMainDir() {
        String mainDir = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            File externalFilesDir = GApplication.app.getExternalFilesDir(GApplication.app.getString(R.string.app_name));
            if(externalFilesDir!=null) {
                mainDir = externalFilesDir.getAbsolutePath();
            }
        }else{
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                mainDir=Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + MainDir;
            }
        }
        File file = null;
        file = new File(mainDir);
        if (!file.exists() || !file.isDirectory()) {
            boolean f = file.mkdirs();
        }
        return file;
    }

    /**
     * 获取指定用户名下的录像目录
     */
    public static File getRecordDir(String account, String iRouterID) {
        String videoDir = null;
        videoDir = getMainDir() + File.separator + account + File.separator + iRouterID + File.separator + VideoDir;
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            videoDir = Environment.getExternalStorageDirectory().getAbsolutePath() +
//                    File.separator + MainDir + File.separator + account + File.separator + iRouterID + File.separator + VideoDir;
//        }
        File file = null;
        file = new File(videoDir);
        if (!file.exists() || !file.isDirectory()) {
            boolean b = file.mkdirs();
        }
        return file;
    }

    public static File getLogPath(String fileName){
        try{
            String logDir = getMainDir().getAbsolutePath() + File.separator + "log";
            File file = new File(logDir);
            boolean b = true;
            if (!file.exists() || !file.isDirectory()) {
                b = file.mkdirs();
            }
            if(b){
                File f = new File(logDir + File.separator + fileName);
                if(!f.exists() || !file.isDirectory()) {
                    return f.createNewFile() ? f : null;
                }else{
                    return f;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getFileDownloadPath() {
        return getMainDir() + File.separator + DownloadDir + File.separator;
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            return Environment.getExternalStorageDirectory().getAbsolutePath() +
//                    File.separator + MainDir + File.separator + DownloadDir + File.separator;
//        }
//        return null;
    }

    public static String getRecordPath(String account, String iRouterID) {
        return getRecordPath(account, iRouterID, ".mp4");
    }

    public static String getDoubleRecordPath(String account, String iRouterID,int channel) {
        return getRecordPath(account, iRouterID, channel + ".mp4");
    }

    public static String getDoubleRecordPath(String account, String iRouterID, int channel,String suffix) {
        String path = getRecordDir(account, iRouterID).getAbsolutePath();
//        return path + File.separator + iRouterID + "_" + System.currentTimeMillis() + ".mp4";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HHmmssZ");
        return path + File.separator + format.format(new Date()) + channel+ suffix;
    }

    public static String getRecordPath(String account, String iRouterID, String suffix) {
        String path = getRecordDir(account, iRouterID).getAbsolutePath();
//        return path + File.separator + iRouterID + "_" + System.currentTimeMillis() + ".mp4";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HHmmssZ");
        return path + File.separator + format.format(new Date()) + suffix;
    }

    public static String getSnapshotPath(String account, String iRouterID) {
        String path = getPicDir(account, iRouterID).getAbsolutePath();
//        return path + File.separator + iRouterID + "_" + System.currentTimeMillis() + ".jpg";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HHmmssSSSZ");
        return path + File.separator + format.format(new Date()) + ".jpg";
    }

    //双幕
    public static String getSnapshotPath(String account, String iRouterID,int channel) {
        String path = getPicDir(account, iRouterID).getAbsolutePath();
//        return path + File.separator + iRouterID + "_" + System.currentTimeMillis() + ".jpg";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HHmmssSSSZ");
        return path + File.separator + format.format(new Date()) + "_"+ channel +".jpg";
    }

    //保存预置位图片路径
    public static String getPresetPath(String account, String devId) {
        String presetDir = null;
        presetDir = getMainDir() + File.separator + account + File.separator + devId + File.separator + Preset;
        mkdirs(presetDir);
        return presetDir;
    }

    public static String getAlarmPicPath(String account) {
        String snapshotDir = null;
        snapshotDir = getMainDir() + File.separator + account + File.separator + alarm;
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            snapshotDir = Environment.getExternalStorageDirectory().getAbsolutePath() +
//                    File.separator + MainDir + File.separator + account + File.separator + alarm;
//        }
        File file = new File(snapshotDir);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }


    /**
     * 获取指定用户名下的抓拍目录
     */
    public static File getPicDir(String account, String devId) {
        String snapshotDir = null;
        snapshotDir = getMainDir() + File.separator + account + File.separator + devId + File.separator + SnapshotDir;
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            snapshotDir = Environment.getExternalStorageDirectory().getAbsolutePath() +
//                    File.separator + MainDir + File.separator + account + File.separator + devId + File.separator + SnapshotDir;
//        }
        File file = new File(snapshotDir);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getDownloadDir(String account, String deviceId) {
        String downloadDir = null;
        downloadDir = getMainDir() + File.separator + account + File.separator + deviceId + File.separator + DownloadDir;
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            downloadDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
//                    MainDir + File.separator + account + File.separator + deviceId + File.separator + DownloadDir;
//        }
        File file = new File(downloadDir);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getDownloadDir(String deviceId) {
        User info = GApplication.app.user;
        return getDownloadDir(info == null ? "goscam" : info.getUserName(), deviceId);
    }

    public static String getDownloadPath(String deviceId) {
        String path = getDownloadDir(deviceId).getAbsolutePath();
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getDownloadVideoPath(String deviceId) {
        String path = getDownloadDir(deviceId).getAbsolutePath() + File.separator + VideoDir;
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getDownloadPhotoPath(String deviceId) {
        String path = getDownloadDir(deviceId).getAbsolutePath() + File.separator + SnapshotDir;
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return path;
    }


    /**
     * 获取指定用户名下的缓存目录
     */
    public static File getCacheDir(String account) {
        String cacheDir = null;
        cacheDir = getMainDir() + File.separator + account + File.separator + CacheDir;
        File file = new File(cacheDir);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 获取云存储视频缓存路径
     */
    public static String getPlayFileCacheDir(String account, String deviceId) {
        String videoCacheDir = getCacheDir(account) + File.separator + deviceId + File.separator + VideoDir;
        File file = new File(videoCacheDir);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return videoCacheDir;
    }

    /**
     * 获取云存储缩略图缓存路径
     */
    public static String getPicCacheDir(String account, String deviceId) {
        String videoCacheDir = getCacheDir(account) + File.separator + deviceId + File.separator + SnapshotDir;
        File file = new File(videoCacheDir);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return videoCacheDir;
    }

    /**
     * 获取设备图像缩略路径
     */
    public static String getDeviceSanpshot(String userName, String deviceID) {
        String filePath = getCacheDir(userName).getAbsolutePath();
        String tmp = filePath + File.separator + deviceID + ".jpg";
        mkdirs(new File(tmp).getParentFile());
        return tmp;
    }



    public static String getTempPath() {
        String cacheDir = null;
        cacheDir = getMainDir() + File.separator + Temp;
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            cacheDir = Environment.getExternalStorageDirectory().getAbsolutePath() +
//                    File.separator + MainDir + File.separator + Temp;
//        }
        File file = new File(cacheDir);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * 删除文件
     *
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        return deleteFile(new File(filePath));
    }

    /**
     * 删除文件
     *
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }


    /**
     * 更新文件到媒体库(文件操作完成后，将其在媒体库内更新)
     *
     * @param context
     * @param filePath 文件全路径
     */
    public static void scanFile(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(filePath);
        android.util.Log.e("scanFile", "scanFile: file.exit" + file.exists() + "\n" + filePath );
        scanIntent.setData(Uri.fromFile(file));
        context.sendBroadcast(scanIntent);
    }

    /**
     * 通知媒体库更新文件夹
     *
     * @param context
     * @param filePath 文件夹路径
     */
    public static void scanFiles(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }

    /**
     * 文件大小转换
     *
     * @param size
     * @return
     */
    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }

    public static boolean isFileExists(File file) {
        return (file.exists() && file.length() > 0);
    }

    public static boolean mkdirs(String path) {
        File file = new File(path);
        return mkdirs(file);
    }

    public static boolean mkdirs(File file) {
        if (!file.exists() || !file.isDirectory()) {
            return file.mkdirs();
        }
        return true;
    }

    public static String getDoorbellPath(String account, String deviceId) {
//        account=encode(account);
//        deviceId = encode(deviceId);
        String filePath = getCacheDir(account) + File.separator + deviceId + File.separator + "Doorbell";
        File file = new File(filePath);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return filePath;
    }

    public static String getTfPreviewPicPath(String account, String deviceId){
        String filePath = getCacheDir(account) + File.separator + deviceId + File.separator + "Preview";
        File file = new File(filePath);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return filePath;
    }

    public static File createFile(File file) {
        if (file.exists()) {
            return file;
        } else {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }



    //检查SD卡是否有足够的空间
    public static boolean checkFreeSpace() {
        long minimum = 30; //要求sd卡最少可用空间已M为单位
        long size = minimum * 1024L * 1024L;
        long sdFreeSpace = getSDFreeSpace();
        return sdFreeSpace > size || sdFreeSpace == -1;
    }
    public static long getSDFreeSpace() {
        try{
            String path = getMainDir().getAbsolutePath();
            StatFs stat = new StatFs(path);
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            return availableBlocks * blockSize;
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    //获取某个设备的文件路径
    public static String getDevicePath(String account, String devId){
        String path = getMainDir() + File.separator + account + File.separator + devId;
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return path;
    }

    //获取某个设备的缓存文件路径
    public static String getDeviceCachePath(String account, String devId){
        String path = getMainDir() + File.separator + account + File.separator + CacheDir + File.separator + devId;
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getUserPath(String account){
        String path = getMainDir() + File.separator + account;
        mkdirs(path);
        return path;
    }
}
