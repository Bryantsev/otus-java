package ru.otus.lesson6;

import ru.otus.lesson6.my_junit.annotations.AfterAll;
import ru.otus.lesson6.my_junit.annotations.AfterEach;
import ru.otus.lesson6.my_junit.annotations.BeforeAll;
import ru.otus.lesson6.my_junit.annotations.BeforeEach;
import ru.otus.lesson6.my_junit.annotations.Ignore;
import ru.otus.lesson6.my_junit.annotations.Test;

/**
 * Created by Alexander Bryantsev on 23.07.2019.
 */
public class TestedClassTest {

    private int eachExecuteCount;

    @BeforeAll
    public void beforeAll() throws Exception {
        System.out.println("beforeAll executed");
        // throw new Exception("Ошибка в методе beforeAll");
    }

    @AfterAll
    public void afterAll() {
        System.out.println("afterAll executed");
    }

    @BeforeEach
    public void beforeEach() {
        eachExecuteCount++;
        System.out.println("beforeEach " + eachExecuteCount + " executed");
    }

    @AfterEach
    public void afterEach() {
        System.out.println("afterEach " + eachExecuteCount + " executed");
    }

    @Test
    @Ignore
    public void test1_failed() throws Exception {
        System.out.println("test1_failed");
        throw new Exception("Что-то пошло не так!");
    }

    @Test
    public void test2_failed() throws Exception {
        System.out.println("test2_failed");
        throw new Exception("Что-то пошло не так!");
    }

    @Test(times = 3)
    public void test3_success() {
        System.out.println("test3_success");
    }

}
