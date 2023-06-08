package com.gocam.goscamdemopro.cloud.data.entity;

public enum EventType {
    Normal(-1), Motion(-1), Sound(-1), Temp(-1),Cry(-1);
    private int color;

    EventType(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
