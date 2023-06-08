package com.gocam.goscamdemopro.cloud.data.entity;

/**
 * 当前正在使用的套餐
 */

public class PackageDetail {
    private String planId;
    private String planName;
    private String dateLife;
    private String serviceLife;
    private String id;
    private String orderNo;
    private String deviceId;
    private String status;
    private String startTime;
    private String preinvalidTime;
    private String switchEnable;

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getDateLife() {
        return dateLife;
    }

    public void setDateLife(String dateLife) {
        this.dateLife = dateLife;
    }

    public String getServiceLife() {
        return serviceLife;
    }

    public void setServiceLife(String serviceLife) {
        this.serviceLife = serviceLife;
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
}
