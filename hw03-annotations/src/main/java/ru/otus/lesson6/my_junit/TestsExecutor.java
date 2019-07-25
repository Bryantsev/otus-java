package ru.otus.lesson6.my_junit;

import ru.otus.lesson6.my_junit.annotations.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestsExecutor {

    private static int successedTests, failedTests, ignoredTests;

    public static void execute(String[] classes) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        List<String> errors = new ArrayList<>();
        List<TestClass> results = new ArrayList<>();

        for (String className : classes) {
            if (!className.endsWith("Test")) {
                errors.add("Имя класса для тестирования должно оканчиваться на Test! Класс " + className + " не соответствует данному формату!");
                continue;
            }

            prepareTestClassData(errors, results, className);
        }

        if (!results.isEmpty()) {
            printTestPlan(results);
            executeTests(results);
            printTestResults(results);
        } else {
            System.out.println("Не найдено тестов!");
        }

        printErrors(errors);
    }

    private static void prepareTestClassData(List<String> errors, List<TestClass> results, String className) {
        Class<?> testClass;
        try {
            testClass = Class.forName(className);
        } catch (ClassNotFoundException ex) {
            errors.add("Класс " + className + " не найден!");
            return;
        }

        TestClass testResult = new TestClass(testClass);
        for (Method method : testClass.getMethods()) {
            if (method.isAnnotationPresent(BeforeAll.class)) {
                testResult.addBeforeAll(method.getName());
            } else if (method.isAnnotationPresent(AfterAll.class)) {
                testResult.addAfterAll(method.getName());
            } else if (method.isAnnotationPresent(BeforeEach.class)) {
                testResult.addBeforeEach(method.getName());
            } else if (method.isAnnotationPresent(AfterEach.class)) {
                testResult.addAfterEach(method.getName());
            } else if (method.isAnnotationPresent(Test.class)) {
                testResult.addTest(new TestClass.Test(method.getName(), method.getAnnotation(Test.class).times(), method.isAnnotationPresent(Ignore.class)));
            }
        }

        if (!testResult.getTests().isEmpty()) {
            results.add(testResult);
        }
    }

    private static void printErrors(List<String> errors) {
        if (!errors.isEmpty()) {
            System.out.println("\n*** Ошибки во время тестирования ***");
            errors.forEach(System.out::println);
        }
    }

    private static void executeTests(List<TestClass> results) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        System.out.println("== Тестирование начинается ==");
        for (TestClass testResult : results) {
            Class<?> testClass = testResult.getTestClass();
            Object testObj = testClass.getDeclaredConstructor().newInstance();

            // Выполняем все методы beforeAll в порядке их расположения в тестовом классе
            for (String beforeAllMethod : testResult.getBeforeAll()) {
                Method method = testClass.getMethod(beforeAllMethod);
                method.invoke(testObj);
            }

            for (TestClass.Test test : testResult.getTests()) {
                if (test.isIgnored()) {
                    ignoredTests++;
                    continue;
                }

                Method testMethod = testClass.getMethod(test.getName());
                for (int i = 0; i < test.getTimes(); i++) {
                    // Выполняем все методы beforeEach в порядке их расположения в тестовом классе
                    for (String beforeEachMethod : testResult.getBeforeEach()) {
                        Method method = testClass.getMethod(beforeEachMethod);
                        method.invoke(testObj);
                    }

                    try {
                        testMethod.invoke(testObj);
                        test.getExecs().add(new TestClass.TestExec(i, true, null));
                        successedTests++;

                    } catch (InvocationTargetException e) {
                        test.getExecs().add(new TestClass.TestExec(i, false, e.getCause().getMessage()));
                        failedTests++;
                    }

                    // Выполняем все методы afterEach в порядке их расположения в тестовом классе
                    for (String afterEachMethod : testResult.getAfterEach()) {
                        Method method = testClass.getMethod(afterEachMethod);
                        method.invoke(testObj);
                    }
                }
            }

            // Выполняем все методы beforeAll в порядке их расположения в тестовом классе
            for (String afterAllMethod : testResult.getAfterAll()) {
                Method method = testClass.getMethod(afterAllMethod);
                method.invoke(testObj);
            }
        }
        System.out.println("\n== Тестирование окончено ==\n");
    }

    private static void printTestPlan(List<TestClass> results) {
        System.out.println("== План тестирования ==");
        for (TestClass testResult : results) {
            System.out.println("Класс: " + testResult.getTestClass().getName());
            testResult.getTests().forEach((test) ->
                System.out.println(
                    String.format("Тест %s будет %s", test.getName(), (!test.isIgnored() ? "выполнен " + test.getTimes() + " раз(а)" : "пропущен"))
                )
            );
        }
        System.out.println();
    }

    private static void printTestResults(List<TestClass> results) {
        System.out.println("== Результаты тестирования ==");
        for (TestClass testResult : results) {
            System.out.println("Класс: " + testResult.getTestClass().getName());
            testResult.getTests().forEach(
                (test) -> test.getExecs().forEach(
                    (exec) -> System.out.println(String.format("Тест %s (%s) пройден %s",
                        test.getName(), exec.getNumExec() + 1, (exec.isSuccess() ? "успешно" : "с ошибками:\n" + exec.getError())))
                )
            );
        }
        System.out.println(String.format("Пройдено тестов: %s, из них %s успешно и %s с ошибками. Пропущено тестов: %s\n",
            successedTests + failedTests, successedTests, failedTests, ignoredTests));
    }

}
