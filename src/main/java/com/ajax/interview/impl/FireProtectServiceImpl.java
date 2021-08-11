package com.ajax.interview.impl;

import com.ajax.interview.FireDepartment;
import com.ajax.interview.FireProtectService;
import com.ajax.interview.State;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class FireProtectServiceImpl implements FireProtectService {

    private Map<String, Device> devices = new ConcurrentHashMap<>();
    private List<FireDepartment> departments = new CopyOnWriteArrayList<>();

    @Override
    public void changeState(String deviceId, State state) {
        devices.putIfAbsent(deviceId, new Device());
        devices.get(deviceId).setState(state);
    }

    @Override
    public void changeTemperature(String deviceId, int temperatureDiff) {
        devices.putIfAbsent(deviceId, new Device());
        Device device = devices.get(deviceId);
        int currentTemp = device.changeTemperature(temperatureDiff);
        if (device.getState() == State.ARMED && (currentTemp > 80 || temperatureDiff > 20)) {
            departments.forEach(fireDepartment -> fireDepartment.alarm(deviceId));
        }
    }

    @Override
    public State getState(String deviceId) {
        return devices.get(deviceId).getState();
    }

    @Override
    public int getTemperature(String deviceId) {
        return devices.get(deviceId).getTemperature();
    }

    @Override
    public void registerFireDepartment(FireDepartment fireDepartment) {
        departments.add(fireDepartment);
    }
}
