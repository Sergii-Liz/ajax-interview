package com.ajax.interview.impl;

import com.ajax.interview.FireDepartment;
import com.ajax.interview.FireProtectService;
import com.ajax.interview.State;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class FireProtectServiceImpl implements FireProtectService {

    private final ExecutorService updateReceiver = Executors.newSingleThreadExecutor();
    private final ExecutorService alarmSender = Executors.newSingleThreadExecutor();

    private final Map<String, Device> devices = new HashMap<>();
    private final List<FireDepartment> departments = new CopyOnWriteArrayList<>();

    @Override
    public void changeState(String deviceId, State state) {
        updateReceiver.submit(new RegisterDeviceIfAbsentTask(deviceId));
        updateReceiver.submit(new ChangeDeviceStateTask(deviceId, state));
    }

    @Override
    public void changeTemperature(String deviceId, int temperatureDiff) {
        updateReceiver.submit(new RegisterDeviceIfAbsentTask(deviceId));
        updateReceiver.submit(new ChangeDeviceTempTask(deviceId, temperatureDiff));
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
        updateReceiver.submit(new AddDepartmentTask(fireDepartment));
    }

    private class RegisterDeviceIfAbsentTask implements Runnable {

        private final String deviceId;

        private RegisterDeviceIfAbsentTask(String deviceId) {
            this.deviceId = deviceId;
        }

        @Override
        public void run() {
            devices.putIfAbsent(deviceId, new Device());
        }
    }

    private class ChangeDeviceStateTask implements Runnable {

        private final String deviceId;
        private final State state;

        private ChangeDeviceStateTask(String deviceId, State state) {
            this.deviceId = deviceId;
            this.state = state;
        }

        @Override
        public void run() {
            devices.get(deviceId).setState(state);
        }
    }

    private class ChangeDeviceTempTask implements Runnable {

        private final String deviceId;
        private final int tempDiff;

        private ChangeDeviceTempTask(String deviceId, int tempDiff) {
            this.deviceId = deviceId;
            this.tempDiff = tempDiff;
        }

        @Override
        public void run() {
            Device device = devices.get(deviceId);
            int currentTemp = device.changeTemperature(tempDiff);
            if (device.getState() == State.ARMED && (currentTemp > 80 || tempDiff > 20)) {
                alarmSender.submit(new AlarmTask(deviceId));
            }
        }
    }


    private class AddDepartmentTask implements Runnable {

        private final FireDepartment fireDepartment;

        private AddDepartmentTask(FireDepartment fireDepartment) {
            this.fireDepartment = fireDepartment;
        }

        @Override
        public void run() {
            departments.add(fireDepartment);
        }
    }


    private class AlarmTask implements Runnable {

        private final String deviceId;

        private AlarmTask(String deviceId) {
            this.deviceId = deviceId;
        }

        @Override
        public void run() {
            departments.forEach(fireDepartment -> fireDepartment.alarm(deviceId));
        }
    }

    void stop() {
        updateReceiver.shutdown();
        alarmSender.shutdown();
    }
}
