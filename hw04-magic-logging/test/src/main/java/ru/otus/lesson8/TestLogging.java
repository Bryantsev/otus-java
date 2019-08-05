package ru.otus.lesson8;

import ru.otus.lesson8.annotations.Log;
import ru.otus.lesson8.annotations.LogMethods;

/**
 * Created by Alexander Bryantsev on 01.08.2019.
 */
@LogMethods
public class TestLogging {

    @Log
    public void calculation(int param) {
        System.out.println("must be logged");
    }

    public void add(int value1, int value2) {
        System.out.println("add (no logging): " + value1 + " + " + value2 + " = " + (value1 + value2));
    }

}
