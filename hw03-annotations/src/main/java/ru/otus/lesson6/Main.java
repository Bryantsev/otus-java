package ru.otus.lesson6;

import ru.otus.lesson6.my_junit.TestsExecutor;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Alexander Bryantsev on 23.07.2019.
 */
public class Main {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        System.out.println("Урок 6: аннотации и рефлексия");

        TestsExecutor.execute(args.length > 0 ? args :
            new String[]{"ru.otus.lesson6.TestedClassTest", "ru.otus.lesson6.TestedClassTest22", "ru.otus.lesson6.TestedClass22Test"});
    }

}
