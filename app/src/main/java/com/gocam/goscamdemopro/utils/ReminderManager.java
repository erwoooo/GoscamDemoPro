package com.gocam.goscamdemopro.utils;

import com.gos.platform.api.devparam.PeripheralElement;

import java.util.ArrayList;
import java.util.List;

public class ReminderManager {
    private static ReminderManager instance;

    private List<PeripheralElement.MethodBean> mMethodBeans = new ArrayList<>();

    public ReminderManager() {
    }

    public static ReminderManager getInstance() {
        if (instance == null) {
            synchronized (ReminderManager.class) {
                if (instance == null) {
                    instance = new ReminderManager();
                }
            }
        }
        return instance;
    }

    public List<PeripheralElement.MethodBean> getMethodBeans() {
        return mMethodBeans;
    }

    public synchronized PeripheralElement.MethodBean getMethodBean(int type) {
        for (PeripheralElement.MethodBean bean : mMethodBeans) {
            if (type == bean.getEvent_type()) {
                return bean;
            }
        }
        return null;
    }

    public synchronized void clearList() {
        this.mMethodBeans.clear();
    }

    public synchronized void saveMethod(List<PeripheralElement.MethodBean> methodBeans) {
        this.mMethodBeans.clear();
        this.mMethodBeans.addAll(methodBeans);
    }
}
