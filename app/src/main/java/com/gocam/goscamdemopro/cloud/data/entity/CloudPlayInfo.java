package com.gocam.goscamdemopro.cloud.data.entity;

import java.io.Serializable;
import java.lang.ref.WeakReference;

public class CloudPlayInfo implements Serializable {

    /**
     * startTime : 1508380397
     * endTime : 1508380411
     * bucket : gos-media
     * key : oss_test/201710191033170185.H264
     */
    public final Object lock = new Object();
    private long startTime;
    private long endTime;
    private int alarmType;
    private String bucket;
    private String key;//获取视频
    private String dateLife;
    private String cycle;

    private String picKey;//获取缩略图
    private String filePath;
    private String picPath;
    private String url;
    private String picUrl;
    private boolean isCorrupted = false;//文件是否损坏

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getFileSeekPosition(long seekTo) {
        return (int) (seekTo - startTime);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isCorrupted() {
        return isCorrupted;
    }

    public void setCorrupted(boolean corrupted) {
        isCorrupted = corrupted;
    }

    public String getDateLife() {
        return dateLife;
    }

    public void setDateLife(String dateLife) {
        this.dateLife = dateLife;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public String getPicKey() {
        return picKey;
    }

    public void setPicKey(String picKey) {
        this.picKey = picKey;
    }

    public int getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }



    private transient WeakReference<CameraEvent> cameraEvent;//绑定的g
    public CameraEvent getCameraEvent(){
        return cameraEvent == null ? null : cameraEvent.get();
    }
    public void setCameraEvent(CameraEvent cameraEvent){
        this.cameraEvent = new WeakReference<CameraEvent>(cameraEvent);
    }


    @Override
    public String toString() {
        return "CloudPlayInfo{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", bucket='" + bucket + '\'' +
                ", key='" + key + '\'' +
                ", filePath='" + filePath + '\'' +
                ", url='" + url + '\'' +
                ", isCorrupted=" + isCorrupted +
                '}';
    }
}
