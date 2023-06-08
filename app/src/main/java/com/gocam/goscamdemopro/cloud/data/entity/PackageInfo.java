package com.gocam.goscamdemopro.cloud.data.entity;

import java.io.Serializable;

public class PackageInfo implements Serializable {
    String planName;//套餐名称
    String enable;//  是否可用  	 0 = 禁用，1=可用
    String renewEnable;// 是否可延续  	0=不可延续，1=可延续
    String dataLife;//数据保存生命周期
    String serviceLife;//服务时长
    String price;//套餐现价格
    String originalPrice;//套餐原价格
    String createTime;//套餐创建时间
    String planId;//套餐ID

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getRenewEnable() {
        return renewEnable;
    }

    public void setRenewEnable(String renewEnable) {
        this.renewEnable = renewEnable;
    }

    public String getDataLife() {
        return dataLife;
    }

    public void setDataLife(String dataLife) {
        this.dataLife = dataLife;
    }

    public String getServiceLife() {
        return serviceLife;
    }

    public void setServiceLife(String serviceLife) {
        this.serviceLife = serviceLife;
    }


    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }
}
