package com.gocam.goscamdemopro.cloud.data.entity;

import com.gos.platform.api.contact.AlarmType;

public class AlarmEvent extends CameraEvent {
    private int eventTypeNum;
    private String eventDesc;

    public AlarmEvent(long startTime, long endTime, int eventTypeNum, String eventDesc) {
        super(startTime, endTime);
        this.setEventTypeNum(eventTypeNum);
        this.setEventDesc(eventDesc);
    }

    public int getEventTypeNum() {
        return eventTypeNum;
    }

    public void setEventTypeNum(int eventTypeNum) {
        switch (eventTypeNum) {
            case AlarmType.E_CRY_ALARM:
                this.setEventType(EventType.Cry);
                break;
            case AlarmType.VIDEO_MOTION:
                this.setEventType(EventType.Motion);
                break;
            case AlarmType.AUDIO_MOTION:
                this.setEventType(EventType.Sound);
                break;
            case AlarmType.HIGH_TEMP_ALARM:
            case AlarmType.LOW_TEMP_ALARM:
                this.setEventType(EventType.Temp);
                break;
            default:
                this.setEventType(EventType.Motion);
                break;
        }
        this.eventTypeNum = eventTypeNum;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    @Override
    public String toString() {
        return "AlarmEvent{" + super.toString() +
                "eventTypeNum=" + eventTypeNum +
                ", eventDesc='" + eventDesc + '\'' +
                '}';
    }
}
