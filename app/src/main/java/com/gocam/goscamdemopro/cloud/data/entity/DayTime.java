package com.gocam.goscamdemopro.cloud.data.entity;

public class DayTime {
    private long startTime;
    private long endTime;
    private String formatTime;

    public DayTime() {

    }

    public DayTime(long startTime, long endTime, String formatTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.formatTime = formatTime;
    }

    public void setDayTime(long startTime, long endTime, String formatTime){
        this.startTime = startTime;
        this.endTime = endTime;
        this.formatTime = formatTime;
    }

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

    public String getFormatTime() {
        return formatTime;
    }

    public void setFormatTime(String formatTime) {
        this.formatTime = formatTime;
    }

    @Override
    public String toString() {
        return "DayTime{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", formatTime='" + formatTime + '\'' +
                '}';
    }
}
