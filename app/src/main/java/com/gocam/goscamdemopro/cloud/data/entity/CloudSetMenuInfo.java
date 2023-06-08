package com.gocam.goscamdemopro.cloud.data.entity;

import java.io.Serializable;

public class CloudSetMenuInfo implements Serializable {
    /**
     * planId : 3
     * planName : 月套餐23
     * dateLife : 12
     * serviceLife : 30
     * count : 1
     * payTime : 20170921095344
     * id : 207
     * orderNo : 201709210953254180
     * deviceId : A9996100NR618DMYH2JZJ8ST111A
     * status : 1:正在使用  0：过期   2：续费  7：移除
     * startTime : 20170921095344
     * preinvalidTime : 20171021095344
     * switchEnable : 1
     * dataExpiredTime : 20171102095344
     * orderCount : 1
     * serviceTime : 7842244
     */

    private int planId;
    private String planName;
    private int dateLife;
    private int serviceLife;
    private int count;
    private String payTime;
    private String id;
    private String orderNo;
    private String deviceId;
    private String status;
    private String startTime;
    private String preinvalidTime;
    private String switchEnable;
    private String dataExpiredTime;
    private long serviceStartTime;
    private long serviceInvalidTime;
    private long serviceDataExpiredTime;
    private long timeStamp;

    private int orderCount;
    private String serviceTime;

    public long getServiceStartTime() {
        return serviceStartTime;
    }

    public void setServiceStartTime(long serviceStartTime) {
        this.serviceStartTime = serviceStartTime;
    }

    public long getServiceInvalidTime() {
        return serviceInvalidTime;
    }

    public void setServiceInvalidTime(long serviceInvalidTime) {
        this.serviceInvalidTime = serviceInvalidTime;
    }

    public long getServiceDataExpiredTime() {
        return serviceDataExpiredTime;
    }

    public void setServiceDataExpiredTime(long serviceDataExpiredTime) {
        this.serviceDataExpiredTime = serviceDataExpiredTime;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public int getDateLife() {
        return dateLife;
    }

    public void setDateLife(int dateLife) {
        this.dateLife = dateLife;
    }

    public int getServiceLife() {
        return serviceLife;
    }

    public void setServiceLife(int serviceLife) {
        this.serviceLife = serviceLife;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getPreinvalidTime() {
        return preinvalidTime;
    }

    public void setPreinvalidTime(String preinvalidTime) {
        this.preinvalidTime = preinvalidTime;
    }

    public String getSwitchEnable() {
        return switchEnable;
    }

    public void setSwitchEnable(String switchEnable) {
        this.switchEnable = switchEnable;
    }

    public String getDataExpiredTime() {
        return dataExpiredTime;
    }

    public void setDataExpiredTime(String dataExpiredTime) {
        this.dataExpiredTime = dataExpiredTime;
    }

    @Override
    public String toString() {
        return "CloudSetMenuInfo{" +
                "planId=" + planId +
                ", planName='" + planName + '\'' +
                ", dateLife=" + dateLife +
                ", serviceLife=" + serviceLife +
                ", count=" + count +
                ", payTime='" + payTime + '\'' +
                ", id='" + id + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", status='" + status + '\'' +
                ", startTime='" + startTime + '\'' +
                ", preinvalidTime='" + preinvalidTime + '\'' +
                ", switchEnable='" + switchEnable + '\'' +
                ", dataExpiredTime='" + dataExpiredTime + '\'' +
                ", serviceStartTime=" + serviceStartTime +
                ", serviceInvalidTime=" + serviceInvalidTime +
                ", serviceDataExpiredTime=" + serviceDataExpiredTime +
                ", timeStamp=" + timeStamp +
                ", orderCount=" + orderCount +
                ", serviceTime='" + serviceTime + '\'' +
                '}';
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public String getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(String serviceTime) {
        this.serviceTime = serviceTime;
    }
}
