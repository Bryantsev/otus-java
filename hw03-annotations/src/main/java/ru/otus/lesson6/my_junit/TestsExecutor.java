package ru.otus.lesson6.my_junit;

import ru.otus.lesson6.my_junit.annotations.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestsExecutor {

    private static int succeedTests, failedTests, ignoredTests;

    public static void execute(String[] classes) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        List<String> errors = new ArrayList<>();
        List<TestClass> testClasses = new ArrayList<>();

        for (String className : classes) {
            if (!className.endsWith("Test")) {
                errors.add("Имя класса для тестирования должно оканчиваться на Test! Класс " + className + " не соответствует данному формату!");
                continue;
            }

            prepareTestClassData(errors, testClasses, className);
        }

        if (!testClasses.isEmpty()) {
            printTestPlan(testClasses);
            executeTests(testClasses);
            printTestResults(testClasses);
        } else {
            System.out.println("Не найдено тестов!");
        }

        printErrors(errors);
    }

    private static void prepareTestClassData(List<String> errors, List<TestClass> testClasses, String className) {
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
                testResult.addBeforeAll(method);
            } else if (method.isAnnotationPresent(AfterAll.class)) {
                testResult.addAfterAll(method);
            } else if (method.isAnnotationPresent(BeforeEach.class)) {
                testResult.addBeforeEach(method);
            } else if (method.isAnnotationPresent(AfterEach.class)) {
                testResult.addAfterEach(method);
            } else if (method.isAnnotationPresent(Test.class)) {
                testResult.addTest(new TestMethod(method, method.getAnnotation(Test.class).times(), method.isAnnotationPresent(Ignore.class)));
            }
        }

        if (!testResult.getTests().isEmpty()) {
            testClasses.add(testResult);
        }
    }

    private static void printErrors(List<String> errors) {
        if (!errors.isEmpty()) {
            System.out.println("\n*** Ошибки во время тестирования ***");
            errors.forEach(System.out::println);
        }
    }

    private static void executeTests(List<TestClass> testClasses) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        System.out.println("== Тестирование начинается ==");
        for (TestClass testClass : testClasses) {
            Class<?> clazz = testClass.getClazz();
            Object testObj = clazz.getDeclaredConstructor().newInstance();

            try {
                // Выполняем все методы beforeAll в порядке их расположения в тестовом классе
                for (Method method : testClass.getBeforeAll()) {
                    method.invoke(testObj);
                }

                for (TestMethod test : testClass.getTests()) {
                    if (test.isIgnored()) {
                        ignoredTests++;
                        continue;
                    }

                    for (int i = 0; i < test.getTimes(); i++) {
                        try {
                            // Выполняем все методы beforeEach в порядке их расположения в тестовом классе
                            for (Method method : testClass.getBeforeEach()) {
                                method.invoke(testObj);
                            }

                            try {
                                test.getMethod().invoke(testObj);
                                test.getExecs().add(new TestExec(i, true, null));
                                succeedTests++;

                            } catch (InvocationTargetException e) {
                                test.getExecs().add(new TestExec(i, false, e.getCause().getMessage()));
                                failedTests++;
                            }

                        } finally {
                            // Всегда выполняем методы afterEach в порядке их расположения в тестовом классе
                            for (Method method : testClass.getAfterEach()) {
                                method.invoke(testObj);
                            }
                        }
                    }
                }

            } finally {
                // Всегда выполняем методы afterAll в порядке их расположения в тестовом классе
                for (Method method : testClass.getAfterAll()) {
                    method.invoke(testObj);
                }
            }
        }
        System.out.println("\n== Тестирование окончено ==\n");
    }

    private static void printTestPlan(List<TestClass> testClasses) {
        System.out.println("== План тестирования ==");
        for (TestClass testClass : testClasses) {
            System.out.println("Класс: " + testClass.getClazz().getName());
            testClass.getTests().forEach((test) ->
                System.out.println(
                    String.format("Тест %s будет %s", test.getMethod().getName(), (!test.isIgnored() ? "выполнен " + test.getTimes() + " раз(а)" : "пропущен"))
                )
            );
        }
        System.out.println();
    }

    private static void printTestResults(List<TestClass> testClasses) {
        System.out.println("== Результаты тестирования ==");
        for (TestClass testClass : testClasses) {
            System.out.println("Класс: " + testClass.getClazz().getName());
            testClass.getTests().forEach(
                (test) -> test.getExecs().forEach(
                    (exec) -> System.out.println(String.format("Тест %s (%s) пройден %s",
                        test.getMethod().getName(), exec.getNumExec() + 1, (exec.isSuccess() ? "успешно" : "с ошибками:\n" + exec.getError())))
                )
            );
        }
        System.out.println(String.format("Пройдено тестов: %s, из них %s успешно и %s с ошибками. Пропущено тестов: %s\n",
            succeedTests + failedTests, succeedTests, failedTests, ignoredTests));
    }

}
