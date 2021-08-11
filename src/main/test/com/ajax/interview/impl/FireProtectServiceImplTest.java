package com.ajax.interview.impl;

import com.ajax.interview.FireDepartment;
import com.ajax.interview.FireProtectService;
import com.ajax.interview.State;
import edu.umd.cs.mtc.MultithreadedTestCase;

import edu.umd.cs.mtc.TestFramework;
import org.junit.Test;
import org.mockito.Mockito;

public class FireProtectServiceImplTest extends MultithreadedTestCase {

    private FireDepartment stub1 = Mockito.mock(FireDepartment.class);

    private FireProtectService underTest;

    @Override
    public void initialize() {
        underTest = new FireProtectServiceImpl();
        underTest.registerFireDepartment(stub1);
    }

    public void thread1() throws InterruptedException {
        underTest.changeState("1", State.ARMED);
    }

    public void thread2() throws InterruptedException {
        underTest.changeState("1", State.DISARMED);
    }

    public void thread3() throws InterruptedException {
        underTest.changeTemperature("1", 30);
    }

    public void thread4() throws InterruptedException {
        underTest.changeTemperature("1", 20);
    }

    public void thread5() throws InterruptedException {
        underTest.changeTemperature("1", 20);
    }


    @Override
    public void finish() {
        Mockito.verify(stub1, Mockito.times(2)).alarm("1");
    }

    @Test
    public void testCounter() throws Throwable {
        TestFramework.runManyTimes(new FireProtectServiceImplTest(), 10);
    }


}