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
    private List<Method> beforeAllMethods = new ArrayList<>();
    private List<Method> afterAllMethods = new ArrayList<>();
    private List<Method> beforeEachMethods = new ArrayList<>();
    private List<Method> afterEachMethods = new ArrayList<>();
    private List<TestMethod> testMethods = new ArrayList<>();

    public TestClass() {
    }

    List<Method> getBeforeAllMethods() {
        return beforeAllMethods;
    }

    void addBeforeAllMethod(Method method) {
        this.beforeAllMethods.add(method);
    }

    List<Method> getAfterAllMethods() {
        return afterAllMethods;
    }

    void addAfterAllMethod(Method method) {
        this.afterAllMethods.add(method);
    }

    List<Method> getBeforeEachMethods() {
        return beforeEachMethods;
    }

    void addBeforeEachMethod(Method method) {
        this.beforeEachMethods.add(method);
    }

    List<Method> getAfterEachMethods() {
        return afterEachMethods;
    }

    void addAfterEachMethod(Method method) {
        this.afterEachMethods.add(method);
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

    List<TestMethod> getTestMethods() {
        return testMethods;
    }

    void addTest(TestMethod testAll) {
        this.testMethods.add(testAll);
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
