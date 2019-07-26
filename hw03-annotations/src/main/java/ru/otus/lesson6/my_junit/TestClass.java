package ru.otus.lesson6.my_junit;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Alexander Bryantsev on 23.07.2019.
 */
public class TestClass {

    private Class<?> clazz;
    private List<Method> beforeAll = new ArrayList<>();
    private List<Method> afterAll = new ArrayList<>();
    private List<Method> beforeEach = new ArrayList<>();
    private List<Method> afterEach = new ArrayList<>();
    private List<TestMethod> tests = new ArrayList<>();

    public TestClass() {
    }

    List<Method> getBeforeAll() {
        return beforeAll;
    }

    void addBeforeAll(Method beforeAll) {
        this.beforeAll.add(beforeAll);
    }

    List<Method> getAfterAll() {
        return afterAll;
    }

    void addAfterAll(Method afterAll) {
        this.afterAll.add(afterAll);
    }

    List<Method> getBeforeEach() {
        return beforeEach;
    }

    void addBeforeEach(Method beforeEach) {
        this.beforeEach.add(beforeEach);
    }

    List<Method> getAfterEach() {
        return afterEach;
    }

    void addAfterEach(Method afterEach) {
        this.afterEach.add(afterEach);
    }

    TestClass(Class<?> clazz) {
        this.clazz = clazz;
    }

    Class<?> getClazz() {
        return clazz;
    }

    void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    List<TestMethod> getTests() {
        return tests;
    }

    void addTest(TestMethod testAll) {
        this.tests.add(testAll);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestClass that = (TestClass) o;
        return Objects.equals(clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz);
    }

}
