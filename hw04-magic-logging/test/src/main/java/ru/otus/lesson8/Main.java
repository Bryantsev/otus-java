package ru.otus.lesson8;

/**
 * Created by Alexander Bryantsev on 01.08.2019.
 * c:\app\java\jdk-12.0.2\bin\java --enable-preview -javaagent:target/agent.jar -jar target/test.jar
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Start testing magic logging");

        new TestLogging().calculation(7);
        new TestLogging().add(999, 1111);

        new TestLogging2().emptyArgsMethod();
        new TestLogging2().calculation(99);
        new TestLogging2().add(7, 15, 999, 53);
        TestLogging2.add(7.33, 15.156);
        new TestLogging2().add(700, 1500, 0, 0);

    }

}
