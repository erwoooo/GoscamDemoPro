package com.gocam.goscamdemopro.cloud.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceCloudServiceInfo implements Parcelable {
    private String mDeviceId;
    private String mDeviceName;
    private String mDevCap;
    private boolean isDeviceOnline = false;
    private CloudSetMenuInfo mCloudSetMenuInfo;
    public boolean isSelect=false;


    protected DeviceCloudServiceInfo(Parcel in) {
        mDeviceId = in.readString();
        mDeviceName = in.readString();
        mDevCap = in.readString();
        isDeviceOnline = in.readByte() != 0;
        mCloudSetMenuInfo = (CloudSetMenuInfo) in.readSerializable();
    }

    public DeviceCloudServiceInfo() {
    }

    public DeviceCloudServiceInfo(String deviceId, String deviceName, boolean isDeviceOnline) {
        mDeviceId = deviceId;
        mDeviceName = deviceName;
        this.isDeviceOnline = isDeviceOnline;
    }

    public static final Creator<DeviceCloudServiceInfo> CREATOR = new Creator<DeviceCloudServiceInfo>() {
        @Override
        public DeviceCloudServiceInfo createFromParcel(Parcel in) {
            return new DeviceCloudServiceInfo(in);
        }

        @Override
        public DeviceCloudServiceInfo[] newArray(int size) {
            return new DeviceCloudServiceInfo[size];
        }
    };

    public String getDeviceId() {
        return mDeviceId;
    }

    public void setDeviceId(String deviceId) {
        mDeviceId = deviceId;
    }

    public String getDeviceName() {
        return mDeviceName;
    }

    public void setDeviceName(String deviceName) {
        mDeviceName = deviceName;
    }

    public boolean isDeviceOnline() {
        return isDeviceOnline;
    }

    public void setDeviceOnline(boolean deviceOnline) {
        isDeviceOnline = deviceOnline;
    }

    public String getDevCap() {
        return mDevCap;
    }

    public void setDevCap(String devCap) {
        mDevCap = devCap;
    }

    public CloudSetMenuInfo getCloudSetMenuInfo() {
        return mCloudSetMenuInfo;
    }

    public void setCloudSetMenuInfo(CloudSetMenuInfo cloudSetMenuInfo) {
        mCloudSetMenuInfo = cloudSetMenuInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDeviceId);
        dest.writeString(mDeviceName);
        dest.writeString(mDevCap);
        dest.writeByte((byte) (isDeviceOnline ? 1 : 0));
        dest.writeSerializable(mCloudSetMenuInfo);
    }

    @Override
    public String toString() {
        return "DeviceCloudServiceInfo{" +
                "mDeviceId='" + mDeviceId + '\'' +
                ", mDeviceName='" + mDeviceName + '\'' +
                ", isDeviceOnline=" + isDeviceOnline +
                ", mCloudSetMenuInfo=" + mCloudSetMenuInfo +
                ", mDevCap='" + mDevCap + '\'' +
                '}';
    }
}
