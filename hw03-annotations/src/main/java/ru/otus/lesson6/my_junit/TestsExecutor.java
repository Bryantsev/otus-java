package ru.otus.lesson6.my_junit;

import ru.otus.lesson6.my_junit.annotations.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestsExecutor {

    private int succeedTests;
    private int failedTests;
    private int ignoredTests;

    public void execute(String[] classes) throws Exception {

        List<String> errors = new ArrayList<>();
        List<TestClass> testClasses = new ArrayList<>();

        for (String className : classes) {
            if (!className.endsWith("Test")) {
                errors.add("Имя класса для тестирования должно оканчиваться на Test! Класс " + className + " не соответствует данному формату!");
                continue;
            }

            prepareTestClassData(errors, testClasses, className);
        }

        try {
            if (!testClasses.isEmpty()) {
                printTestPlan(testClasses);
                executeTests(errors, testClasses);
                printTestResults(testClasses);
            } else {
                System.out.println("Не найдено тестов!");
            }

        } finally {
            printErrors(errors);
        }
    }

    private void prepareTestClassData(List<String> errors, List<TestClass> testClasses, String testClassName) {
        Class<?> clazz;
        try {
            clazz = Class.forName(testClassName);
        } catch (ClassNotFoundException ex) {
            errors.add("Класс " + testClassName + " не найден!");
            return;
        }

        TestClass testClass = new TestClass(clazz);
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(BeforeAll.class)) {
                testClass.addBeforeAllMethod(method);
            } else if (method.isAnnotationPresent(AfterAll.class)) {
                testClass.addAfterAllMethod(method);
            } else if (method.isAnnotationPresent(BeforeEach.class)) {
                testClass.addBeforeEachMethod(method);
            } else if (method.isAnnotationPresent(AfterEach.class)) {
                testClass.addAfterEachMethod(method);
            } else if (method.isAnnotationPresent(Test.class)) {
                testClass.addTest(new TestMethod(method, method.getAnnotation(Test.class).times(), method.isAnnotationPresent(Ignore.class)));
            }
        }

        if (!testClass.getTestMethods().isEmpty()) {
            testClasses.add(testClass);
        }
    }

    private void printErrors(List<String> errors) {
        if (!errors.isEmpty()) {
            System.out.println("\n*** Ошибки во время тестирования ***");
            errors.forEach(System.out::println);
        }
    }

    private void executeTests(List<String> errors, List<TestClass> testClasses) throws Exception {
        System.out.println("== Тестирование начинается ==");
        for (TestClass testClass : testClasses) {
            Class<?> clazz = testClass.getClazz();
            Object testObj = clazz.getDeclaredConstructor().newInstance();

            try {
                executeMethods(errors, testClass.getBeforeAllMethods(), testObj, true);

                for (TestMethod test : testClass.getTestMethods()) {
                    if (test.isIgnored()) {
                        ignoredTests++;
                        continue;
                    }

                    for (int i = 0; i < test.getTimes(); i++) {
                        try {
                            executeMethods(errors, testClass.getBeforeEachMethods(), testObj, true);

                            try {
                                test.getMethod().invoke(testObj);
                                test.getExecs().add(new TestExec(i, true, null));
                                succeedTests++;

                            } catch (InvocationTargetException e) {
                                test.getExecs().add(new TestExec(i, false, e.getCause().getMessage()));
                                failedTests++;
                            }

                        } finally {
                            executeMethods(errors, testClass.getAfterEachMethods(), testObj, false);
                        }
                    }
                }

            } finally {
                executeMethods(errors, testClass.getAfterAllMethods(), testObj, false);
            }
        }
        System.out.println("\n== Тестирование окончено ==\n");
    }

    /**
     * Всегда выполняем методы в порядке их добавления в список.
     * Исключения перехватываем и сохраняем в списке ошибок, не прерывая выполнения всего списка
     *
     * @param errors  Список ошибок
     * @param methods Список методов для выполнения
     * @param testObj Объект, методы которого необходимо выполнить
     */
    private void executeMethods(List<String> errors, List<Method> methods, Object testObj, boolean breakExecutingOnException) throws Exception {
        for (Method method : methods) {
            try {
                method.invoke(testObj);
            } catch (Exception ex) {
                errors.add("Ошибка выполнения метода " + method.getName() + ":\n" + ex.getCause().getMessage());
                if (breakExecutingOnException) {
                    throw new Exception("Ошибка выполнения списка методов!");
                }
            }
        }
    }

    private void printTestPlan(List<TestClass> testClasses) {
        System.out.println("== План тестирования ==");
        for (TestClass testClass : testClasses) {
            System.out.println("Класс: " + testClass.getClazz().getName());
            testClass.getTestMethods().forEach(test ->
                System.out.println(
                    String.format("Тест %s будет %s", test.getMethod().getName(), (!test.isIgnored() ? "выполнен " + test.getTimes() + " раз(а)" : "пропущен"))
                )
            );
        }
        System.out.println();
    }

    private void printTestResults(List<TestClass> testClasses) {
        System.out.println("== Результаты тестирования ==");
        for (TestClass testClass : testClasses) {
            System.out.println("Класс: " + testClass.getClazz().getName());
            testClass.getTestMethods().forEach(
                test -> test.getExecs().forEach(
                    exec -> System.out.println(String.format("Тест %s (%s) пройден %s",
                        test.getMethod().getName(), exec.getNumExec() + 1, (exec.isSuccess() ? "успешно" : "с ошибками:\n" + exec.getError())))
                )
            );
        }
        System.out.println(String.format("Пройдено тестов: %s, из них %s успешно и %s с ошибками. Пропущено тестов: %s",
            succeedTests + failedTests, succeedTests, failedTests, ignoredTests));
    }

}
