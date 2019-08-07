package ru.otus.lesson8;

import ru.otus.lesson8.annotations.LogMethods;
import ru.otus.lesson8.annotations.LogType;

/**
 * Created by Alexander Bryantsev on 01.08.2019.
 */
@LogMethods(LogType.ALL)
public class TestLogging2 {

    public void emptyArgsMethod() {

    }

    public void calculation(int param) {
        System.out.println("test: " + param);
    }

    public void add(int value1, int value2, Integer value3, Integer value4) {
        System.out.println("add ints: " + value1 + " + " + value2 + " = " + (value1 + value2));
    }

    public static void add(double value1, double value2) {
        System.out.println("add doubles: " + value1 + " + " + value2 + " = " + (value1 + value2));
    }

}
