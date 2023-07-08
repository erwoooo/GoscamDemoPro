package com.gocam.goscamdemopro.cloud.data.entity;


import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhaolong on 2017/8/30.
 */

public class  CameraEvent implements Cloneable, Comparable {
    public final Object lock = new Object();
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private EventType eventType = EventType.Normal;
    private long startTime;
    private long endTime;
    private String startTextTime;
    private String endTextTime;
    protected int eventTypeNum;
    public String prePicPath;//预览图

    //用于保存云存储告警信息文件下载信息
    private Object cloudPlayInfos;
    public void setCloudPlayInfos(Object infos){
        if(cloudPlayInfos == null)
            cloudPlayInfos = infos;
    }
    public Object getCloudPlayInfos(){
        return cloudPlayInfos;
    }

    public int getEventTypeNum() {
        return eventTypeNum;
    }


    //当前正在播放事件
    private boolean isCurrentPlay;
    public boolean isCurrentPlay(){
        return isCurrentPlay;
    }
    public void setCurrentPlay(boolean currentPlay){
        isCurrentPlay = currentPlay;
    }

    //已经播放过的事件
    private boolean isPlayed;

    public boolean isPlayed(){
        return isPlayed;
    }
    public void setPlayed(boolean isPlayed){
        this.isPlayed = isPlayed;
    }


    private Object obj;
    public void setObj(Object obj){
        this.obj = obj;
    }
    public Object getObj(){
        return obj;
    }

    public CameraEvent() {
    }

    public CameraEvent(long startTime, long endTime) {
        this.setStartTime(startTime);
        this.setEndTime(endTime);
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
        this.setStartTextTime(formatTime(startTime * 1000));
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
        this.setEndTextTime(formatTime(endTime * 1000));
    }

    public String getStartTextTime() {
        return startTextTime;
    }

    public void setStartTextTime(String startTextTime) {
        this.startTextTime = startTextTime;
    }

    public String getEndTextTime() {
        return endTextTime;
    }

    public void setEndTextTime(String endTextTime) {
        this.endTextTime = endTextTime;
    }

    public static String formatTime(long time) {
        return simpleDateFormat.format(new Date(time));
    }

    public int getStartPosition(long startTime, long longTimeScaleDuration) {
        return (int) ((this.startTime - startTime) / longTimeScaleDuration);
    }

    public int getEndPosition(long startTime, long longTimeScaleDuration) {
        return (int) ((this.endTime - startTime) / longTimeScaleDuration);
    }

//    @Override
//    public CameraEvent clone() {
//        // TODO Auto-generated method stub
//        try {
//            return (CameraEvent) super.clone();
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    @Override
    public String toString() {
        return "CameraEvent{" +
                "eventType=" + eventType +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", startTextTime='" + startTextTime + '\'' +
                ", endTextTime='" + endTextTime + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull Object obj) {
        if (obj instanceof CameraEvent) {
            CameraEvent cameraEvent = (CameraEvent) obj;
            return ((Long) this.getStartTime()).compareTo(cameraEvent.getStartTime());
        }
        return -1;
    }
}
