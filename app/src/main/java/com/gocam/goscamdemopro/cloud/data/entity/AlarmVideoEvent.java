package com.gocam.goscamdemopro.cloud.data.entity;

public class AlarmVideoEvent extends AlarmEvent {

    public AlarmVideoEvent(long startTime, long endTime, int eventTypeNum) {
        super(startTime, endTime, eventTypeNum, "");
    }

}
