package ru.otus.lesson1;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import java.io.Console;
import java.util.Map;

public class HelloOtus {


    public static void main(String[] args) {

        Console console = System.console();
        if (console == null) {
            System.out.println("Консоль недоступна! Запустите приложение из терминала или командной строки!\n");
            return;
        }

        Map<String, String> users = ImmutableMap.of("user1", "pass1", "user2", "pass2");

        String userName;
        do {
            userName = console.readLine("Имя пользователя: ");
            if (Strings.isNullOrEmpty(userName)) {
                console.printf("Задайте имя пользователя, пожалуйста!\n");
            } else {
                break;
            }
        } while (true);

        String password = new String(console.readPassword("Пароль: "));
        if (!Strings.isNullOrEmpty(password) &&
                users.containsKey(userName) &&
                users.getOrDefault(userName, "").equals(password)) {
            console.printf("Добро пожаловать, %s!", userName);
        } else {
            console.printf("Неверное имя пользователя или пароль!");
        }

    }

}
