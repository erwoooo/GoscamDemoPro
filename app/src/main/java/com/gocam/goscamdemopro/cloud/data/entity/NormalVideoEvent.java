package com.gocam.goscamdemopro.cloud.data.entity;

public class NormalVideoEvent extends CameraEvent {

    public NormalVideoEvent(long startTime, long endTime) {
        super(startTime, endTime);
        this.setEventType(EventType.Normal);
    }

}
