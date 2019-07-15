package ru.otus.lesson1;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import java.io.Console;
import java.util.Map;

public class HelloOtus {


    public static void main(String[] args) {

        Console console = getConsole();
        if (console == null) {
            return;
        }

        String userName = readUserName(console);
        if (!checkUserPassword(console, userName, new String(console.readPassword("Пароль: ")))) {
            return;
        }

        // Useful work next
    }

    private static boolean checkUserPassword(Console console, String userName, String password) {
        Map<String, String> users = ImmutableMap.of("user1", "pass1", "user2", "pass2");

        if (!Strings.isNullOrEmpty(password) &&
                users.containsKey(userName) &&
                users.getOrDefault(userName, "").equals(password)) {
            console.printf("Добро пожаловать, %s!", userName);
            return true;
        } else {
            console.printf("Неверное имя пользователя или пароль!");
            return false;
        }
    }

    private static String readUserName(Console console) {
        String userName;
        do {
            userName = console.readLine("Имя пользователя: ");
            if (Strings.isNullOrEmpty(userName)) {
                console.printf("Задайте имя пользователя, пожалуйста!\n");
            } else {
                break;
            }
        } while (true);

        return userName;
    }

    private static Console getConsole() {
        Console console = System.console();
        if (console == null) {
            System.out.println("Консоль недоступна! Запустите приложение из терминала или командной строки!\n");
        }
        return console;
    }

}
