package ru.otus.lesson6;

import ru.otus.lesson6.my_junit.annotations.*;

/**
 * Created by Alexander Bryantsev on 23.07.2019.
 */
public class TestedClassTest {

    private int eachExecuteCount;

    @BeforeAll
    public void beforeAll() {
        System.out.println("beforeAll executed");
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
