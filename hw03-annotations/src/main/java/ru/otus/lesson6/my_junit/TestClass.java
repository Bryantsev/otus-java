package ru.otus.lesson6.my_junit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Alexander Bryantsev on 23.07.2019.
 */
public class TestClass {

    private Class<?> testClass;
    private List<String> beforeAll = new ArrayList<>();
    private List<String> afterAll = new ArrayList<>();
    private List<String> beforeEach = new ArrayList<>();
    private List<String> afterEach = new ArrayList<>();
    private List<Test> tests = new ArrayList<>();

    public TestClass() {
    }

    public List<String> getBeforeAll() {
        return beforeAll;
    }

    public void addBeforeAll(String beforeAll) {
        this.beforeAll.add(beforeAll);
    }

    public List<String> getAfterAll() {
        return afterAll;
    }

    public void addAfterAll(String afterAll) {
        this.afterAll.add(afterAll);
    }

    public List<String> getBeforeEach() {
        return beforeEach;
    }

    public void addBeforeEach(String beforeEach) {
        this.beforeEach.add(beforeEach);
    }

    public List<String> getAfterEach() {
        return afterEach;
    }

    public void addAfterEach(String afterEach) {
        this.afterEach.add(afterEach);
    }

    public TestClass(Class<?> testClass) {
        this.testClass = testClass;
    }

    public Class<?> getTestClass() {
        return testClass;
    }

    public void setTestClass(Class<?> testClass) {
        this.testClass = testClass;
    }

    public List<Test> getTests() {
        return tests;
    }

    public void addTest(Test testAll) {
        this.tests.add(testAll);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestClass that = (TestClass) o;
        return Objects.equals(testClass, that.testClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testClass);
    }


    public static class Test {
        private String name;
        private int times;
        private boolean ignored;
        private List<TestExec> execs = new ArrayList<>();

        public Test() {
        }

        public Test(String name, int times, boolean ignored) {
            this.name = name;
            this.times = times;
            this.ignored = ignored;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getTimes() {
            return times;
        }

        public void setTimes(int times) {
            this.times = times;
        }

        public boolean isIgnored() {
            return ignored;
        }

        public void setIgnored(boolean ignored) {
            this.ignored = ignored;
        }

        public List<TestExec> getExecs() {
            return execs;
        }

        public void setExecs(List<TestExec> execs) {
            this.execs = execs;
        }
    }

    public static class TestExec {
        private int numExec;
        private boolean isSuccess;
        private String error;

        public TestExec() {
        }

        public TestExec(int numExec, boolean isSuccess, String error) {
            this.numExec = numExec;
            this.isSuccess = isSuccess;
            this.error = error;
        }

        public int getNumExec() {
            return numExec;
        }

        public void setNumExec(int numExec) {
            this.numExec = numExec;
        }

        public boolean isSuccess() {
            return isSuccess;
        }

        public void setSuccess(boolean success) {
            isSuccess = success;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

}
