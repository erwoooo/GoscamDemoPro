package com.gocam.goscamdemopro.utils;

import android.text.TextUtils;

import com.gocam.goscamdemopro.entity.Device;
import com.gocam.goscamdemopro.vphoto.data.SelectDeviceResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 用于管理设备
 */
public class DeviceManager {
    private static DeviceManager MANAGER = new DeviceManager();
    private List<Device> deviceList = Collections.synchronizedList(new ArrayList<Device>());

    private List<SelectDeviceResult.Data> photoDeviceList = Collections.synchronizedList(new ArrayList<SelectDeviceResult.Data>());

    private DeviceManager() {
    }

    public static DeviceManager getInstance() {
        return MANAGER;
    }

    public synchronized Device findDeviceById(String deviceId) {
        for (Device device : deviceList) {
            if (TextUtils.equals(deviceId, device.devId)) {
                return device;
            }
        }
        return null;
    }

    public void setPhotoDeviceList(List<SelectDeviceResult.Data> photoDeviceList) {
        this.photoDeviceList = photoDeviceList;
    }

    public boolean isOnline(String deviceId) {
        for (SelectDeviceResult.Data data : photoDeviceList) {
            if (deviceId.equals(data.getDevice_id())) {
                return data.getOnline() == 1;
            }
        }
        return false;
    }

    public synchronized List<Device> getDeviceList() {
        return deviceList;
    }

    public synchronized List<Device> saveDevice(List<Device> list) {
        //过滤出来已经存在的设备
        List<Device> tempList = new ArrayList<>(list);
        Iterator<Device> iterator1 = deviceList.iterator();
        while (iterator1.hasNext()) {
            Device next = iterator1.next();
            boolean isExist = false;
            Device dev = null;
            for (Device u : tempList) {
                if (TextUtils.equals(u.devId, next.devId)) {//已经有了， 更新信息
                    next.devName = u.devName;
                    next.isOnline = u.isOnline;
                    next.streamUser = u.streamUser;
                    next.streamPassword = u.streamPassword;
                    next.deviceSfwVer = u.deviceSfwVer;
                    next.deviceHdwVer = u.deviceHdwVer;
                    next.deviceHdType = u.deviceHdType;
                    next.isOwn = u.isOwn;
                    dev = u;
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {//不存在,已经被解绑
                next.release();
                iterator1.remove();
            } else {//存在
                tempList.remove(dev);
            }
        }
        deviceList.addAll(tempList);
        for (Device device : deviceList) {
            device.getConnection().setPlatDevOnline(device.isPlatDevOnline());
            device.getConnection().connect(0);
        }
        return deviceList;
    }

    public synchronized void deleteDevice(Device device) {
        deviceList.remove(device);
        if (device != null) {
            device.release();
        }
    }

    public synchronized void clear() {
        Iterator<Device> iterator1 = deviceList.iterator();
        while (iterator1.hasNext()) {
            Device next = iterator1.next();
            next.release();
            iterator1.remove();
        }
    }
}
