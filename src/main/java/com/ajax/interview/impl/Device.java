package com.ajax.interview.impl;

import com.ajax.interview.State;

import java.util.concurrent.atomic.AtomicInteger;

public class Device {
    private volatile State state;
    private AtomicInteger lastTemperature;

    public Device() {
        this.state = State.DISARMED;
        this.lastTemperature = new AtomicInteger(20);
    }

    public int changeTemperature(int temperatureDiff) {
        return lastTemperature.addAndGet(temperatureDiff);
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public int getTemperature() {
        return lastTemperature.get();
    }
}
