package ru.otus.lesson6.my_junit;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestMethod {
    private Method method;
    private int times;
    private boolean ignored;
    private List<TestExec> execs = new ArrayList<>();

    public TestMethod() {
    }

    TestMethod(Method method, int times, boolean ignored) {
        this.method = method;
        this.times = times;
        this.ignored = ignored;
    }

    Method getMethod() {
        return method;
    }

    void setMethod(Method method) {
        this.method = method;
    }

    int getTimes() {
        return times;
    }

    void setTimes(int times) {
        this.times = times;
    }

    boolean isIgnored() {
        return ignored;
    }

    void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }

    List<TestExec> getExecs() {
        return execs;
    }

    void setExecs(List<TestExec> execs) {
        this.execs = execs;
    }
}
