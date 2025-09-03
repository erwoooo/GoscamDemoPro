package com.gocam.goscamdemopro.utils;

import com.gos.platform.api.devparam.PeripheralElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PeripheralManager {
    private static PeripheralManager instance;

    private HashMap<String, List<PeripheralElement>> peripheral = new HashMap<>();

    public PeripheralManager() {
    }

    public static PeripheralManager getInstance() {
        if (instance == null) {
            synchronized (PeripheralManager.class) {
                if (instance == null) {
                    instance = new PeripheralManager();
                }
            }
        }
        return instance;
    }

    public synchronized PeripheralElement getElement(String deviceId, String subDevId) {
        List<PeripheralElement> elements = peripheral.get(deviceId);
        if (elements == null) {
            return null;
        }

        for (PeripheralElement element : elements) {
            if (subDevId.equals(element.getSubDevId())) {
                return element;
            }
        }
        return null;
    }

    public synchronized List<PeripheralElement> getPeripheral(String deviceId) {
        return peripheral.get(deviceId);
    }

    public synchronized void saveDevice(String deviceId, List<PeripheralElement> list) {
        if (list == null) {
            return;
        }
        List<PeripheralElement> deviceList = Collections.synchronizedList(new ArrayList<PeripheralElement>());
        deviceList.addAll(list);
        peripheral.put(deviceId, deviceList);
    }

    public synchronized void setStatus(String deviceId, String subDevId, int online, int battery) {
        List<PeripheralElement> elements = peripheral.get(deviceId);
        if (elements == null) {
            return;
        }

        for (PeripheralElement element : elements) {
            if (subDevId.equals(element.getSubDevId())) {
                element.setOnline(online);
                element.setBattery_level(battery);
            }
        }
    }
}
